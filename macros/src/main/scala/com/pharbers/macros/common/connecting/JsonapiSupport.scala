package com.pharbers.macros.common.connecting

import scala.reflect.macros.whitebox
import scala.language.experimental.macros
import scala.annotation.{StaticAnnotation, compileTimeOnly}

@compileTimeOnly("enable macro paradis to expand macro annotations")
class JsonapiSupport extends StaticAnnotation {
    def macroTransform(annottees: Any*): Any = macro JsonapiSupport.impl
}

object JsonapiSupport extends com.pharbers.util.log.phLogTrait {
    def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
        import c.universe._

        val class_tree = {
            annottees.map(_.tree).toList match {
                case re@q"$_ class $_[..$_] $_(...$_) extends { ..$_ } with ..$_ { $_ => ..$_ }" :: Nil => re.head
                case _ => c.abort(c.enclosingPosition, "Annotation @JsonapiSupport can be used only with class")
            }
        }

        val q"$mods class $tpname[..$tparams] $ctorMods(...$paramss) extends { ..$earlydefns } with ..$parents { $self => ..$stats }" = class_tree
//        phLog("mods = " + mods)
//        phLog("tpname = " + tpname)
//        phLog("tparams = " + tparams)
//        phLog("ctorMods = " + ctorMods)
//        phLog("paramss = " + paramss)
//        phLog("earlydefns = " + earlydefns)
//        phLog("parents = " + parents)
//        phLog("self = " + self)
//        phLog("stats = " + stats)

        val jc_name = TermName(tpname + "JsonapiConvert")
        val mc_name = TermName(tpname + "MongoDBConvert")
        val entity_type = tq"$tpname"

        val fields = paramss.flatMap { params =>
            val q"..$trees" = q"..$params"
            trees.map {
                case q"$mods val $tname: $tpt = $expr" =>
                    (tname, tpt)
            }
        }.map{ x =>
            x._1.toString() + "  ->  " + x._2.toString()
        }

val aab = q"$fields"

        val clsdef =
            q"""{
                $mods class $tpname[..$tparams] $ctorMods(...$paramss) extends { ..$earlydefns } with ..$parents { $self => ..$stats

                    import com.mongodb.DBObject
                    import com.pharbers.util.log.phLogTrait
                    import com.pharbers.macros.api.JsonapiConvert
                    import com.pharbers.macros.api.MongoDBConvert
                    import com.pharbers.jsonapi.model.JsonApiObject._
                    import com.pharbers.jsonapi.model.RootObject.ResourceObject
                    import com.pharbers.jsonapi.model.{Attribute, Links, RootObject}

                    implicit object $jc_name extends JsonapiConvert[$entity_type] with phLogTrait {
                        override def fromJsonapi(jsonapi: RootObject): $entity_type = {
                            val data = jsonapi.data.map(_.asInstanceOf[ResourceObject]).get
                            val attrs = data.attributes.get.toList
                            val included = jsonapi.included
                            val id = data.id.get
                            val tmp = new $entity_type(..$tparams)

                            println("fields = " + $aab)

                            tmp
                        }
                        override def toJsonapi(obj: $entity_type): RootObject = {
                            RootObject(data = Some(ResourceObject(
                               `type` = "request",
                               id = Some(""),
                               attributes = Some(List(
                                  Attribute("major", NumberValue(1)),
                                  Attribute("minor", NumberValue(2))
                               )), links = Some(List(Links.Self("http://com.pharbers.test.link/person/42", None))))))
                        }
                    }

                    implicit object $mc_name extends MongoDBConvert[$entity_type] with phLogTrait {
                        override def fromMongo(data: DBObject): $entity_type = {
                            phLog("fromMongo")
                            ???
                        }
                        override def toMongo(obj: $entity_type): DBObject = {
                            phLog("toMongo")
                            ???
                        }
                    }
                }
            }"""

        phLog(clsdef)
        c.Expr[Any](clsdef)
    }
}
