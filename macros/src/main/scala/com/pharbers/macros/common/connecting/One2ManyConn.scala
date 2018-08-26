package com.pharbers.macros.common.connecting

import scala.reflect.macros.whitebox
import com.pharbers.util.log.phLogTrait
import scala.language.experimental.macros
import scala.annotation.{StaticAnnotation, compileTimeOnly}

@compileTimeOnly("enable macro paradis to expand macro annotations")
class One2ManyConn[C](param_name: String) extends StaticAnnotation {
    def macroTransform(annottees: Any*): Any = macro One2ManyConn.impl
}

object One2ManyConn extends phLogTrait {
    def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
        import c.universe._

        val class_tree = annottees.map(_.tree).toList match {
            case q"$mods class $tpname[..$tparams] $ctorMods(...$paramss) extends commonEntity with ..$parents { $self => ..$stats }" :: Nil =>
//                phLog("mods = " + mods)
//                phLog("tpname = " + tpname)
//                phLog("tparams = " + tparams)
//                phLog("ctorMods = " + ctorMods)
//                phLog("paramss = " + paramss)
//                phLog("parents = " + parents)
//                phLog("self = " + self)
//                phLog("stats = " + stats)

                val (conn_type, conn_name) = c.prefix.tree match {
                    case q"new One2ManyConn[$conn_type]($conn_name)" =>
                        (conn_type.toString, conn_name.toString.replace("\"", ""))
                    case _ => c.abort(c.enclosingPosition, "Annotation @One2ManyConn must provide conn_type and conn_name !")
                }
//                phLog("conn_name = " + conn_name)
//                phLog("conn_type = " + conn_type)

                val fields = paramss.flatMap { params =>
                    val q"..$trees" = q"..$params"
                    trees.map {
                        case q"$mods val $tname: $tpt = $expr" =>
                            q"$mods var $tname: $tpt = $expr"

                        case q"$mods var $tname: $tpt = $expr" =>
                            q"$mods var $tname: $tpt = $expr"
                    }
                }
                val conn_tree = q"var ${TermName(conn_name)}: Option[List[${TypeName(conn_type)}]] = None"
                val ctor_fields = fields ++ Seq(conn_tree)
//                phLog("ctor_fields = " + ctor_fields)

                q"""
                    $mods class $tpname[..$tparams] $ctorMods(..$ctor_fields) extends commonEntity with ..$parents { $self => ..$stats }
                """

            case _ => c.abort(c.enclosingPosition, "Annotation @One2ManyConn can be used only with class")
        }

        c.Expr[Any](class_tree)
    }
}
