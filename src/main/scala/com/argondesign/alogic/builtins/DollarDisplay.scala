////////////////////////////////////////////////////////////////////////////////
// Argon Design Ltd. Project P8009 Alogic
// Copyright (c) 2018 Argon Design Ltd. All rights reserved.
//
// This file is covered by the BSD (with attribution) license.
// See the LICENSE file for the precise wording of the license.
//
// Module: Alogic Compiler
// Author: Geza Lore
//
// DESCRIPTION:
//
// Builtin '$display'
////////////////////////////////////////////////////////////////////////////////

package com.argondesign.alogic.builtins

import com.argondesign.alogic.ast.Trees._
import com.argondesign.alogic.core.CompilerContext
import com.argondesign.alogic.core.Loc
import com.argondesign.alogic.core.Types._

private[builtins] class DollarDisplay(implicit cc: CompilerContext) extends BuiltinPolyFunc {

  val name = "$display"

  private def nonVoidPacked(expr: Expr) = expr.tpe match {
    case TypeVoid => false
    case kind     => kind.isPacked
  }

  def returnType(args: List[Expr]) = args partialMatch {
    case Nil                                                              => TypeVoid
    case str :: rest if str.tpe == TypeStr && (rest forall nonVoidPacked) => TypeVoid
  }

  def isKnownConst(args: List[Expr]) = false

  def fold(loc: Loc, args: List[Expr]) = None
}
