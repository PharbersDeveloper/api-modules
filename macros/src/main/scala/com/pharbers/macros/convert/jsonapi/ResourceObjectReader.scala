package com.pharbers.macros.convert.jsonapi

import scala.reflect.macros.whitebox
import com.pharbers.util.log.phLogTrait
import scala.language.experimental.macros
import com.pharbers.jsonapi.model.RootObject.ResourceObject

trait ResourceObjectReader[T] {
    def fromResourceObject(resource: ResourceObject): T

    def toResourceObject(obj: T): ResourceObject
}

object ResourceObjectReader extends phLogTrait {
    implicit def ResourceReaderMaterialize[T]: ResourceObjectReader[T] = macro impl[T]

    def impl[T](c: whitebox.Context)(ttag: c.WeakTypeTag[T]): c.Expr[ResourceObjectReader[T]] = {
        import c.universe._

        val t_symbol = ttag.tpe match {
            case TypeRef(_, str, _) => str
        }
//        phLog("t_symbol = " + t_symbol)
        val t_name = t_symbol.asClass.name.toString
//        phLog("t_name = " + t_name)
        val t_type = TypeName(t_name)
        val c_name = TypeName(c.freshName("eval$"))
//        phLog("c_name = " + c_name)


        val q"..$clsdef" = q"""{
        class $c_name extends ResourceObjectReader[$t_type] {

            import com.pharbers.jsonapi.model._
            import scala.reflect.runtime.{universe => ru}
            import com.pharbers.jsonapi.model.RootObject._
            import com.pharbers.jsonapi.model.JsonApiObject._

            override def fromResourceObject(resource: ResourceObject): $t_type = {
                val entity_type = ru.typeOf[$t_type]
                val runtime_mirror = ru.runtimeMirror(getClass.getClassLoader)
                val class_symbol = entity_type.typeSymbol.asClass
                val class_mirror = runtime_mirror.reflectClass(class_symbol)
                val ctor_symbol = entity_type.decl(ru.termNames.CONSTRUCTOR).asMethod
                val ctor_mirror = class_mirror.reflectConstructor(ctor_symbol)
                val entity = ctor_mirror().asInstanceOf[$t_type]

                entity.id = resource.id.get
                entity.`type` = resource.`type`

                val attrs = resource.attributes.get.toList
                val inst_mirror = runtime_mirror.reflect(entity)
                attrs.foreach { attr =>
                    val field_symbol = entity_type.decl(ru.TermName(attr.name)).asTerm
                    val field_mirror = inst_mirror.reflectField(field_symbol)
                    attr.value match {
                        case StringValue(str) => field_mirror.set(str)
                        case NumberValue(number) if number.isValidInt => field_mirror.set(number.toInt)
                        case NumberValue(number) if number.isBinaryDouble => field_mirror.set(number.toDouble)
                        case NumberValue(number) if number.isValidLong => field_mirror.set(number.toLong)
                        case NumberValue(_) => ???
                        case BooleanValue(number) => field_mirror.set(number)
                        case NullValue => ???
                        case _ => ???
                    }
                }

                entity
            }

            override def toResourceObject(obj: $t_type): ResourceObject = {
                val runtime_mirror = ru.runtimeMirror(getClass.getClassLoader)
                val inst_mirror = runtime_mirror.reflect(obj)
                val class_symbol = inst_mirror.symbol
                val class_field = class_symbol.typeSignature.members.filter(p => p.isTerm && !p.isMethod).toList

                val companion_symbol = class_symbol.companion.asModule
                val companion_mirror = runtime_mirror.reflectModule(companion_symbol)
                val companion_instance = runtime_mirror.reflect(companion_mirror.instance)

                def isConnOneInject(f: ru.Symbol): Boolean =
                    f.info.baseType(ru.typeOf[Option[_]].typeSymbol) != ru.NoType &&
                            f.info.typeArgs.length == 1 &&
                            f.info.typeArgs.head.baseClasses.
                                    map(_.name.toString).contains("commonEntity")

                def isConnManyInject(f: ru.Symbol): Boolean =
                    f.info.baseType(ru.typeOf[Option[List[_]]].typeSymbol) != ru.NoType &&
                            f.info.typeArgs.length == 1 &&
                            f.info.typeArgs.head.baseClasses.
                                    map(_.name.toString).contains("commonEntity")

                val attrs = class_field.map { f =>
                    val attr_mirror = inst_mirror.reflectField(f.asTerm)
                    val attr_val = attr_mirror.get

                    Attribute(f.name.toString,
                        if (f.info =:= ru.typeOf[String]) StringValue(attr_val.toString)
                        else if (f.info <:< ru.typeOf[AnyVal]) NumberValue(BigDecimal(attr_val.asInstanceOf[Number].doubleValue))
                        else if (f.info =:= ru.typeOf[Boolean]) BooleanValue(attr_val.asInstanceOf[Boolean])
                        else NullValue
                    )
                }.filterNot(NullValue == _.value).asInstanceOf[Attributes]

                ResourceObject(
                    id = Some(obj.id),
                    `type` = obj.`type`,
                    attributes = Some(
                        attrs.toList
                    ))
            }
        }
        }"""

        val reVal =q""" new $c_name """

        c.Expr[ResourceObjectReader[T]](Block(clsdef.toList.asInstanceOf[List[c.universe.Tree]], reVal))
    }
}
