package com.pharbers.macros.common.connecting

import scala.reflect.macros.whitebox
import scala.language.experimental.macros
import scala.annotation.{StaticAnnotation, compileTimeOnly}

@compileTimeOnly("enable macro paradis to expand macro annotations")
class ConnOne2One(para_name : String, class_name : String) extends StaticAnnotation {
    def macroTransform(annottees : Any*) : Any = macro ConnOne2One.impl
}

object ConnOne2One {
    def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {

        import c.universe._
        val inputs = annottees.map (_.tree).toList

        val (s, annottee, expandees) = inputs match {
            case (param : ValDef) :: (rest @ (_ :: _)) => ("val", param, rest)
            case (param : TypeDef) :: (rest @ (_ :: _)) => ("type", param, rest)
            case _ => ("", EmptyTree, inputs)
        }
        val outputs = expandees

        c.Expr[Any](Block(outputs, Literal(Constant(()))))
    }
}
