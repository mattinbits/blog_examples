package com.mjlivesey.evilmacros

import scala.annotation.{compileTimeOnly, StaticAnnotation}
import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros

object EvilMacro {

  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    annottees match {
      case List(annottee @ Expr(
                  mod@ModuleDef(m, n, Template(p, s, b)))) =>
        //annottee.tree.children.foreach(println)
        val newTemplateChildren = b.map{
          case d @ DefDef(mods, name, types, params,
                      t @ Ident(TypeName("Int")), body) =>
            val amendedBody = q"($body) + 1"
            DefDef(mods, name, types, params, t, amendedBody)

          case other => other
        }
        val amendedModule =
          ModuleDef(m, n, Template(p, s, newTemplateChildren))
        c.Expr(q"{$amendedModule}")

      case other =>
        Expr
        println(s"Expecting single module")
        c.Expr(Block(annottees.map(_.tree).toList, Literal(Constant(()))))
    }
  }
}

@compileTimeOnly("enable macro paradise to expand macro annotations")
class evil extends StaticAnnotation {
  def macroTransform(annottees: Any*) = macro EvilMacro.impl
}


