package com.pharbers.macros.common.resulting

import com.pharbers.jsonapi.JsonapiRootObjectFormat
import com.pharbers.jsonapi.model.{Attributes, RootObject}

import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros

trait Resultable[T] extends JsonapiRootObjectFormat[T] {
    override def toJsonapi(a : T) : RootObject
    override def fromJsonapi(obj : RootObject) : T
}

object Resultable {
    implicit def materializeResultable[T] : Resultable[T] = macro impl[T]

    def impl[T](c: Context)(ttag: c.WeakTypeTag[T]) : c.Expr[Resultable[T]] = {
        import c.universe._
        import c.universe.Flag._

        val t_name = ttag.tpe match { case TypeRef(_, str, _) => str }
        val weak_type_name = t_name.asClass.name.toString
        val fresh_class_name = TypeName(c.freshName("eval$"))

        val q"..$clsdef" =
        q"""
            import com.pharbers.jsonapi.model.JsonApiObject.{JsArrayValue, JsObjectValue, NullValue, NumberValue, StringValue}
            import com.pharbers.jsonapi.model.{Attribute, Attributes, Links, RootObject}
            import com.pharbers.jsonapi.model.RootObject.{ResourceObject, ResourceObjects}
            import com.pharbers.macros.common.resulting.Resultable
            import com.pharbers.model.detail._

            import scala.reflect.runtime.universe._
            import scala.reflect.runtime.{universe => ru}

            class $fresh_class_name extends Resultable[${TypeName(weak_type_name)}] {

                override def toJsonapi(p : ${TypeName(weak_type_name)}) = {
                    val mirror = ru.runtimeMirror(getClass.getClassLoader)
                    val inst_mirror = mirror.reflect(p)
                    val class_symbol = inst_mirror.symbol
                    val class_field = class_symbol.typeSignature.members.filter(p => p.isTerm && ! p.isMethod).toList

                    val companion_symbol = class_symbol.companion.asModule
                    val companion_mirror = mirror.reflectModule(companion_symbol)
                    val companion_instance = mirror.reflect(companion_mirror.instance)

                    val opt = typeOf[Option[_]].typeSymbol
                    val ltp = typeOf[List[_]].typeSymbol
                    def isConnectionInject(f : ru.Symbol) =
                        f.info.baseType(opt) != NoType &&
                            f.info.typeArgs.length == 1 &&
                            f.info.typeArgs.head.baseClasses.
                            map(_.name.toString).contains("commonresult")

                    def isConnectionManyInject(f : ru.Symbol) =
                        f.info.baseType(ltp) != NoType &&
                            f.info.typeArgs.length == 1 &&
                            f.info.typeArgs.head.baseClasses.
                            map(_.name.toString).contains("commonresult")

                    val attrs =
                        class_field.map { f =>
                            val attr_mirror = inst_mirror.reflectField(f.asTerm)
                            val attr_val = attr_mirror.get

                            Attribute(f.name.toString,
                                if (f.info =:= typeOf[String]) StringValue(attr_val.toString)
                                else if (f.info <:< typeOf[Number]) NumberValue(BigDecimal(attr_val.asInstanceOf[Number].doubleValue))
                                else if (isConnectionInject(f) || isConnectionManyInject(f))  {
                                    val companion_implicit =
                                        companion_symbol.typeSignature.members.
                                            find(p => p.name.toString == f.name.toString).
                                            map (x => x).getOrElse(throw new Exception(""))

                                    val compaion_field_mirror = companion_instance.reflectField(companion_implicit.asTerm)

                                    attr_val match {
                                        case Some(x) => JsObjectValue(asJsonApi(x)(compaion_field_mirror.get.asInstanceOf[Expandable[Any]]))
                                        case Nil => NullValue
                                        case lst : List[Any] => JsArrayValue(lst.map (x => JsObjectValue(asJsonApi(x)(compaion_field_mirror.get.asInstanceOf[Expandable[Any]]))))
                                        case _ => ???
                                    }
                                }
                                else NullValue)
                            }.filterNot(it => NullValue == it.value).asInstanceOf[Attributes]

                    RootObject(data = Some(ResourceObject(
                               `type` = $weak_type_name,
                                id = Some(p.id.toString),
                                attributes = Some(
                                    attrs.toList
                                ), links = Some(List(Links.Self("http://test.link/person/42", None))))))
                }

                    override def fromJsonapi(rootObject: RootObject): ${TypeName(weak_type_name)} = ???
            }
         """
        val reVal =
            q"""
                new $fresh_class_name
             """

        val rt = c.Expr[Resultable[T]](Block(clsdef.toList.asInstanceOf[List[c.universe.Tree]], reVal))
        println(rt)
        rt
//        c.Expr[Resultable[T]](Block(clsdef.toList.asInstanceOf[List[c.universe.Tree]], reVal))
    }
}

