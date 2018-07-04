package com.pharbers.macros.common.connecting

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.reflect.macros.whitebox
import scala.language.experimental.macros
import scala.reflect.runtime.{universe => ru}

@compileTimeOnly("enable macro paradis to expand macro annotations")
class ConnectionsMacro extends StaticAnnotation {
    def macroTransform(annottees : Any*) : Any = macro ConnectionsMacro.impl
}

object ConnectionsMacro {
    def impl(c : whitebox.Context)(annottees : c.Expr[Any]*) : c.Expr[Any] = {

        import c.universe._
        import c.universe.Flag._

        val inputs = annottees.map (_.tree).toList
        assert(inputs.length == 1, message = "only can handle one class def")

        val (s, annottee, expandees) = inputs match {
            case (param : ValDef) :: (rest @ (_ :: _)) => ("val", param, rest)
            case (param : TypeDef) :: (rest @ (_ :: _)) => ("type", param, rest)
            case _ => ("", EmptyTree, inputs)
        }

        assert(annottee == EmptyTree, message = "please")

        val tmp = inputs.head

        class traverser extends Traverser {
            var valdef = List[(c.universe.TermName, c.universe.Tree)]()
            var paradef = List[(c.universe.TermName, c.universe.Tree)]()
            var one2onedef = List[(String, String)]()
            var one2manydef = List[(String, String)]()
            var _tmn : c.universe.TypeName = null
            override def traverse(tree: Tree): Unit = tree match {
                case ValDef(mods, tnm, tpy, rhs) =>
                    valdef = if ((mods.flags.hashCode & Flag.PARAM.hashCode) == 0) (tnm, tpy) :: valdef else valdef
                    paradef = if ((mods.flags.hashCode & Flag.PARAM.hashCode) != 0) (tnm, tpy) :: paradef else paradef
                    super.traverseModifiers(mods)
                    super.traverseName(tnm)
                    super.traverse(tpy)
                    super.traverse(rhs)
                case ClassDef(mods, tnm, param, impl) =>
                    _tmn = tnm
                    this.traverseClasssAnnotations(mods.annotations)
                    super.traverseModifiers(mods)
                    super.traverseName(tnm)
                    super.traverseTrees(param)
                    super.traverse(impl)
                case _ => super.traverse(tree)
            }

            def traverseClasssAnnotations(annots: List[c.universe.Tree]) = {
                annots.map { iter =>
//                    println(showRaw(iter))
                    iter match {
                        case _ @ Apply(Select(New(Ident(tpm)), _), List(Literal(Constant(para)), Literal(Constant(cons)))) =>
                            tpm toString match {
                                case "ConnOne2One" => one2onedef = (para.toString, cons.toString) :: one2onedef
                                case "ConnOne2Many" => one2manydef = (para.toString, cons.toString) :: one2manydef
                                case _ => ???
                            }
                        case _ => ???
                    }
                }
            }
        }

        val t = new traverser
        t.traverse(tmp)

        val valdefFunc = t.valdef.map { iter =>
            ValDef(Modifiers(PARAMACCESSOR | MUTABLE),
                iter._1,
                iter._2,
                EmptyTree)
        }.reverse

        val paradefFunc = t.paradef.map { iter =>
            ValDef(Modifiers(PARAM | PARAMACCESSOR),
                iter._1,
                iter._2,
                EmptyTree)
        }.reverse

        val moduleApplyFunc = t.paradef.map { iter =>
            ValDef(Modifiers(PARAM | PARAMACCESSOR),
                iter._1,
                iter._2,
                EmptyTree)
        }.reverse

        val queyparadefFunc = paradefFunc.map { iter =>
            iter match { case ValDef(_, tpn, _, _) => Ident(tpn) }
        }

        val addConnectFunc = t.one2onedef.map { iter =>
            ValDef(Modifiers(MUTABLE),
                TermName(iter._1),
                AppliedTypeTree(Ident(TypeName("Option")),
                    List(Ident(TypeName(iter._2)))),
                Ident(TermName("None")))
        }.reverse

        val moduleAddConnectFunc = t.one2onedef.map { iter =>
            ValDef(Modifiers(PARAM | DEFAULTPARAM),
                TermName(iter._1),
                AppliedTypeTree(Ident(TypeName("Option")),
                    List(Ident(TypeName(iter._2)))),
                Ident(TermName("None")))
        }.reverse

        val applyAddConnectFunc = t.one2onedef.map { iter =>
            Assign(Select(Ident(TermName("t")), TermName(iter._1)), Ident(TermName(iter._1)))
        }.reverse

        val addConnectManyFunc = t.one2manydef.map { iter =>
            ValDef(Modifiers(MUTABLE),
                TermName(iter._1),
                AppliedTypeTree(Ident(TypeName("List")),
                    List(Ident(TypeName(iter._2)))),
                Ident(TermName("Nil")))
        }.reverse

        val moduleAddConnectManyFunc = t.one2manydef.map { iter =>
            ValDef(Modifiers(PARAM | DEFAULTPARAM),
                TermName(iter._1),
                AppliedTypeTree(Ident(TypeName("List")),
                    List(Ident(TypeName(iter._2)))),
                Ident(TermName("Nil")))
        }.reverse

        val applyAddConnectManyFunc = t.one2manydef.map { iter =>
            Assign(Select(Ident(TermName("t")), TermName(iter._1)), Ident(TermName(iter._1)))
        }.reverse

        val implicitExpendingFun =
            t.one2onedef.map { iter =>
                ValDef(Modifiers(IMPLICIT), TermName(iter._1), TypeTree(),
                    TypeApply(Select(Ident(TermName("Expandable")),
                        TermName("materializeExpandable")), List(Ident(TypeName(iter._2)))))
            } :::
            t.one2manydef.map { iter =>
                ValDef(Modifiers(IMPLICIT), TermName(iter._1), TypeTree(),
                    TypeApply(Select(Ident(TermName("Expandable")),
                        TermName("materializeExpandable")), List(Ident(TypeName(iter._2)))))
            }

        val cls_tree =
        ClassDef(Modifiers(NoFlags), t._tmn, List(),
            Template(List(Ident(TypeName("commonresult")),
                Select(Ident("scala"), TypeName("Serializable"))), noSelfType,
                valdefFunc ::: addConnectFunc ::: addConnectManyFunc :::
                    (DefDef(Modifiers(), termNames.CONSTRUCTOR, List(),
                        List(paradefFunc), TypeTree(),
                        Block(List(pendingSuperCall), Literal(Constant(())))) :: Nil)
            ))

        val module_tree =
            ModuleDef(Modifiers(), TermName(t._tmn.toString), Template(List(Select(Ident("scala"), TypeName("AnyRef"))), noSelfType,
                List(DefDef(Modifiers(), termNames.CONSTRUCTOR, List(), List(List()), TypeTree(),
                    Block(List(pendingSuperCall), Literal(Constant(())))),
                    DefDef(Modifiers(), TermName("apply"), List(),
                        List(
                            moduleApplyFunc ::: moduleAddConnectFunc ::: moduleAddConnectManyFunc
                        ), TypeTree(),
                        Block(
                            (ValDef(Modifiers(), TermName("t"), TypeTree(),
                                Apply(Select(New(Ident(t._tmn)), termNames.CONSTRUCTOR),
                                    queyparadefFunc
                                )) :: Nil
                            )  ::: applyAddConnectFunc ::: applyAddConnectManyFunc
                            , Ident(TermName("t"))
                        )
                    )) ::: implicitExpendingFun
            ))


        c.Expr[Any](Block(cls_tree :: module_tree :: Nil, Literal(Constant(()))))
    }
}
