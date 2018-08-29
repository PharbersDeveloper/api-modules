package com.pharbers.macros.convert.jsonapi

import scala.reflect.macros.whitebox
import com.pharbers.util.log.phLogTrait
import scala.language.experimental.macros
import com.pharbers.macros.api.JsonapiConvert

object JsonapiMacro extends phLogTrait {
    implicit def jsonapiMacroMaterialize[T]: JsonapiConvert[T] = macro impl[T]

    def impl[T](c: whitebox.Context)(ttag: c.WeakTypeTag[T]): c.Expr[JsonapiConvert[T]] = {
        import c.universe._

        val t_type = ttag.tpe

        val t_symbol = t_type match {
            case TypeRef(_, str, _) => str
        }
//        phLog("t_symbol = " + t_symbol)
        val t_name = t_symbol.asClass.name.toString
//        phLog("t_name = " + t_name)
        val t_type_name = TypeName(t_name)
//        phLog("t_type_name = " + t_type_name)
        val tmp_class_name = TypeName(c.freshName("eval$"))
//        phLog("tmp_class_name = " + tmp_class_name)

        val q"..$clsdef" = q"""{
        class $tmp_class_name extends JsonapiConvert[$t_type_name] {

            import com.pharbers.jsonapi.model._
            import com.pharbers.macros.convert.jsonapi._
            import scala.reflect.runtime.{universe => ru}
            import com.pharbers.jsonapi.model.RootObject._
            import com.pharbers.macros.convert.jsonapi.ResourceObjectReader._

            override def fromJsonapi(jsonapi: RootObject, package_local: String): $t_type_name = {
                val entity_type = ru.typeOf[$t_type_name]
                val runtime_mirror = ru.runtimeMirror(getClass.getClassLoader)

                /** 解析attributes 到基础数据 **/
                val jsonapi_data = jsonapi.data.get.asInstanceOf[ResourceObject]
                val entity = fromResourceObject[$t_type_name](jsonapi_data)(ResourceReaderMaterialize)

                /** 根据relationships 找到关联的includeds **/
                val relationships = jsonapi_data.relationships
                val includeds = jsonapi.included.get.resourceObjects.array
                val expandInfo = relationships.get.map { case (k, v) =>
                    val tmp = v.data match {
                        case Some(reo: ResourceObject) =>
                            Some(includeds.find(y => y.id == reo.id && y.`type` == reo.`type`)
                                    .getOrElse(throw new Exception("not found " + reo.id + "&" + reo.`type` + " in includeds")))
                        case Some(reos: ResourceObjects) =>
                            Some(ResourceObjects(reos.array.map { reo =>
                                includeds.find(y => y.id == reo.id && y.`type` == reo.`type`)
                                        .getOrElse(throw new Exception(s"not found " + reo.id + "&" + reo.`type` + " in includeds"))
                            }))
                        case None => None
                    }
                    k -> tmp
                }

                /** 将展开的 relationships 赋值到实体中 **/
                val inst_mirror = runtime_mirror.reflect(entity)
                expandInfo.foreach { case (k, v) =>
                    val field_symbol = entity_type.member(ru.TermName(k)).asTerm
                    val field_mirror = inst_mirror.reflectField(field_symbol)
                    val extract_symbol = entity_type.member(ru.TermName("jsonapi_to_" + k)).asMethod
                    val extract_mirror = inst_mirror.reflectMethod(extract_symbol)
                    field_mirror.set(extract_mirror(v))
                }
                entity
            }

            override def toJsonapi(obj: $t_type_name): RootObject = ???

        }
        }"""

        val reVal =q""" new $tmp_class_name """

        c.Expr[JsonapiConvert[T]](Block(clsdef.toList.asInstanceOf[List[c.universe.Tree]], reVal))
    }
}
