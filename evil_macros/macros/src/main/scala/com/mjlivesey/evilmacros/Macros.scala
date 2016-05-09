package com.mjlivesey.evilmacros

import scala.annotation.StaticAnnotation
import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros

object EvilMacro {

  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    annottees match {
      //case List(annottee@ ModuleDef(m, n, t)) =>
      case List(annottee @ Expr(mod@ModuleDef(m, n, Template(p, s, b)))) =>
        println(m)
        println(n)
        println(b)
        //annottee.tree.children.foreach(println)
        val newTemplateChildren = b.map{
          case d @ DefDef(mods, name, types, params, t @ Ident(TypeName("Int")), body) =>
            val amendedBody =
              q"""
                 ($body) + 1
               """
            println(amendedBody)
            DefDef(mods, name, types, params, t, amendedBody)

          case other => other
        }
        val amendedModule = ModuleDef(m, n, Template(p, s, newTemplateChildren))
        c.Expr(q"{$amendedModule}")

      case other =>
        Expr
        println(s"Expecting single annottee ${other.head.getClass.getCanonicalName}")
        c.Expr(Block(annottees.map(_.tree).toList, Literal(Constant(()))))
    }

  }
}

class evil extends StaticAnnotation {
  def macroTransform(annottees: Any*) = macro EvilMacro.impl
}


