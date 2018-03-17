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
// Builtin '$signed'
////////////////////////////////////////////////////////////////////////////////

package com.argondesign.alogic.builtins

import com.argondesign.alogic.ast.Trees._
import com.argondesign.alogic.core.CompilerContext
import com.argondesign.alogic.core.Types._

object DollarSigned extends BuiltinPolyFunc {

  protected def name = "$signed"

  protected def retType(args: List[Expr])(implicit cc: CompilerContext): Type = {
    TypeSInt(args.head.tpe.width)
  }

  protected def validArgs(args: List[Expr])(implicit cc: CompilerContext) = {
    args.lengthCompare(1) == 0 && args.head.tpe.isPacked
  }
}
