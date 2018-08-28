package com.pharbers.macros.convert.jsonapi

import scala.reflect.macros.whitebox
import com.pharbers.util.log.phLogTrait
import scala.language.experimental.macros
import com.pharbers.macros.api.JsonapiConvert

object JsonapiMacro extends phLogTrait {
    implicit def jsonapiMacroMaterialize[T]: JsonapiConvert[T] = macro impl[T]

    def impl[T](c: whitebox.Context)(ttag: c.WeakTypeTag[T]): c.Expr[JsonapiConvert[T]] = {
        import c.universe._

        println("woclalcalnalkdfjaldkfajfsl;ai")

        val t_symbol = ttag.tpe match {
            case TypeRef(_, str, _) => str
        }
        phLog("t_symbol = " + t_symbol)
        val t_name = t_symbol.asClass.name.toString
        phLog("t_name = " + t_name)
        val t_type = TypeName(t_name)
        val c_name = TypeName(c.freshName("eval$"))
        phLog("c_name = " + c_name)


        println(ttag.tpe)



//    v match {
//        case Some(reo: ResourceObject) =>
//
//        case Some(reos: ResourceObjects) =>
//            field_mirror.set(Some())
//            val NullaryMethodType(TypeRef(_, _, TypeRef(_, _, tpe_str :: Nil) :: Nil)) = field_type.typeSignature
//            val conn_many = reos.array.map(fromResourceObject[Order])
//            field_mirror.set(Some(conn_many.toList))
//        case None => ???
//    }


        val q"..$clsdef" = q"""{
        class $c_name extends JsonapiConvert[$t_type] {

            import com.pharbers.jsonapi.model._
            import scala.reflect.runtime.{universe => ru}
            import com.pharbers.jsonapi.model.RootObject._
            import com.pharbers.jsonapi.model.JsonApiObject._
            import com.pharbers.macros.convert.jsonapi.ResourceObjectReader
            import com.pharbers.macros.convert.jsonapi.ResourceObjectReader._
            import ru._

            def extract_abc(rd: Option[RootObject.Data]): Option[abc] = {
                rd match {
                    case Some(reo: ResourceObject) => Some(fromResourceObject[abc](reo)(ResourceReaderMaterialize))
                    case _ => None
                }
            }

            def extract_Order(rd: Option[RootObject.Data]): Option[List[Order]] = {
                rd match {
                    case Some(reos: ResourceObjects) => Some(reos.array.map(fromResourceObject[Order](_)(ResourceReaderMaterialize)).toList)
                    case _ => None
                }
            }

            override def fromJsonapi(jsonapi: RootObject, package_local: String): $t_type = {
                val entity_type = ru.typeOf[$t_type]
                val runtime_mirror = ru.runtimeMirror(getClass.getClassLoader)

                /** 解析attributes 到基础数据 **/
                val jsonapi_data = jsonapi.data.get.asInstanceOf[ResourceObject]
                val entity = fromResourceObject[$t_type](jsonapi_data)(ResourceReaderMaterialize)

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
                    val field_type = entity_type.member(ru.TermName(k))
                    val field_mirror = inst_mirror.reflectField(field_type.asTerm)
                    k match {
                        case "abc" => field_mirror.set(extract_abc(v))
                        case "orders" => field_mirror.set(extract_Order(v))
                    }
                }
                entity
            }

            override def toJsonapi(obj: profile): RootObject = ???

        }
        }"""

        val reVal =q""" new $c_name """
        println("11wocalcaoswedflkj")

        c.Expr[JsonapiConvert[T]](Block(clsdef.toList.asInstanceOf[List[c.universe.Tree]], reVal))
    }
}
