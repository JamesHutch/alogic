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
// Base trait for things that have a location
////////////////////////////////////////////////////////////////////////////////

package com.argondesign.alogic.core

import com.argondesign.alogic.util.unreachable

trait Locationed {

  // Can only be set once, and cannot be observed as null
  private[this] var _loc: Loc = _ // scalastyle:ignore var.field

  def hasLoc: Boolean = _loc != null

  def loc: Loc = if (hasLoc) _loc else unreachable

  def withLoc(loc: Loc): this.type = {
    if (hasLoc) {
      unreachable
    } else {
      _loc = loc
    }
    this
  }

}
