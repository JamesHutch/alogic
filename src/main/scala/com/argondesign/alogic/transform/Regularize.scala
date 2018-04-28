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
// Regularize tree by assigning loc where missing, and applying type assigner
// where tpe is missing
////////////////////////////////////////////////////////////////////////////////

package com.argondesign.alogic.transform

import com.argondesign.alogic.ast.TreeTransformer
import com.argondesign.alogic.ast.Trees._
import com.argondesign.alogic.core.CompilerContext
import com.argondesign.alogic.core.Loc
import com.argondesign.alogic.core.TreeInTypeTransformer
import com.argondesign.alogic.typer.TypeAssigner

final class Regularize(
    loc: Loc,
    assignTypes: Boolean
)(
    implicit cc: CompilerContext
) extends TreeTransformer {

  override val typed: Boolean = false

  private[this] object TypeRegularize extends TreeInTypeTransformer(this)

  override def transform(tree: Tree): Tree = {
    if (!tree.hasLoc) {
      tree withLoc loc
    }

    if (assignTypes && !tree.hasTpe) {
      TypeAssigner(tree)
    }

    tree match {
      case DeclIdent(_, kind, _) => TypeRegularize(kind)
      case ExprType(kind)        => TypeRegularize(kind)
      case _                     => ()
    }

    tree
  }
}
