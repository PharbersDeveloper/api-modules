package com.pharbers.macros.common.connecting

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.reflect.macros.whitebox
import scala.language.experimental.macros

@compileTimeOnly("enable macro paradis to expand macro annotations")
class ConnOne2Many(para_name : String, class_name : String) extends StaticAnnotation {
    def macroTransform(annottees : Any*) : Any = macro ConnOne2One.impl
}

object ConnOne2Many {
    def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {

        import c.universe._
        val inputs = annottees.map (_.tree).toList

        val (s, annottee, expandees) = inputs match {
            case (param : ValDef) :: (rest @ (_ :: _)) => ("val", param, rest)
            case (param : TypeDef) :: (rest @ (_ :: _)) => ("type", param, rest)
            case _ => ("", EmptyTree, inputs)
        }
        //        println((s, annottee, expandees))
        val outputs = expandees

        c.Expr[Any](Block(outputs, Literal(Constant(()))))
    }
}