//package com.pharbers.macros.convert.jsonapi
//
//import com.pharbers.macros.api.JsonapiConvert
//
//import scala.reflect.macros.whitebox
//
//object JsonapiMacro {
//    implicit def jsonapiMacroMaterialize[T] : JsonapiConvert[T] = macro impl[T]
//
//    def impl[T](c: whitebox.Context)(ttag: c.WeakTypeTag[T]): c.Expr[JsonapiConvert[T]] = {
//        import c.universe._
//
//        val t_name = ttag.tpe match { case TypeRef(_, str, _) => str }
//        println("wocao = " + t_name)
//        val weak_type_name = t_name.asClass.name.toString
//        println("weak_type_name = " + t_name)
//        val fresh_class_name = TypeName(c.freshName("eval$"))
//        println("fresh_class_name = " + t_name)
//
////        val q"..$clsdef" =
////            q"""
////            {
////                import com.pharbers.jsonapi.model.JsonApiObject.{JsObjectValue, NullValue, NumberValue, StringValue}
////                import com.pharbers.jsonapi.model.{Attribute, Attributes, Links, RootObject}
////                import com.pharbers.jsonapi.model.RootObject.{ResourceObject, ResourceObjects}
////                import com.pharbers.macros.common.expending.Expandable
////
////                import scala.reflect.runtime.universe._
////                import scala.reflect.runtime.{universe => ru}
////
////                import com.pharbers.model.detail._
////
////                class ${fresh_class_name} extends Expandable[${TypeName(weak_type_name)}] {
////                    override def toJsonapi(p : ${TypeName(weak_type_name)}) : Attributes = {
////                        val mirror = ru.runtimeMirror(getClass.getClassLoader)
////                        val inst_mirror = mirror.reflect(p)
////                        val class_symbol = inst_mirror.symbol
////                        val class_field = class_symbol.typeSignature.members.filter(p => p.isTerm && ! p.isMethod).toList
////
////                        class_field.map { f =>
////                        val attr_mirror = inst_mirror.reflectField(f.asTerm)
////                        val attr_val = attr_mirror.get
////
////                        Attribute(f.name.toString,
////                            if (f.info =:= typeOf[String]) StringValue(attr_val.toString)
////                            else if (f.info <:< typeOf[Number]) NumberValue(BigDecimal(attr_val.asInstanceOf[Number].doubleValue))
////                            else NullValue)
////                        }.filterNot(it => NullValue == it.value).asInstanceOf[Attributes]
////                    }
////                    override def fromJsonapi(obj : ResourceObject) : ${TypeName(weak_type_name)} = ???
////                }
////            }
////             """
//
//        val q"..$clsdef" =
//            q"""
//                import com.pharbers.macros.api.JsonapiConvert
//                class ${fresh_class_name} extends JsonapiConvert[${TypeName(weak_type_name)}] {
//                   def fromJsonapi(jsonapi: RootObject): ${TypeName(weak_type_name)} = {
//                        println("aaaaaaaaabbbbbbbb")
//                        profile("test")
//                   }
//                   def toJsonapi(obj: ${TypeName(weak_type_name)}): RootObject = {
//                        println("toJsonapi")
//                        ???
//                   }
//                }
//             """
//println("aaaaaaaaaa")
//        val reVal =
//            q"""
//                new $fresh_class_name
//             """
//
//        val rt = c.Expr[JsonapiConvert[T]](Block(clsdef.toList.asInstanceOf[List[c.universe.Tree]], reVal))
//        println("rt = " + rt)
//        rt
////        c.Expr[Expandable[T]](Block(clsdef.toList.asInstanceOf[List[c.universe.Tree]], reVal))
//    }
//}
