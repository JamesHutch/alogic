////////////////////////////////////////////////////////////////////////////////
// Argon Design Ltd. Project P8009 Alogic
// Copyright (c) 2017-2018 Argon Design Ltd. All rights reserved.
//
// This file is covered by the BSD (with attribution) license.
// See the LICENSE file for the precise wording of the license.
//
// Module: Alogic Compiler
// Author: Peter de Rivaz/Geza Lore
//
// DESCRIPTION:
//
// AST representation used throughout the compiler
////////////////////////////////////////////////////////////////////////////////

package com.argondesign.alogic.ast

import com.argondesign.alogic.core.Symbols.Symbol
import com.argondesign.alogic.core.Symbols.TermSymbol
import com.argondesign.alogic.core.Types._
import com.argondesign.alogic.lib.StructuredTree
import com.argondesign.alogic.core.SourceAttributes
import com.argondesign.alogic.core.Locationed

// scalastyle:off number.of.types
// scalastyle:off number.of.methods

object Trees {

  ///////////////////////////////////////////////////////////////////////////////
  // AST root type
  ///////////////////////////////////////////////////////////////////////////////

  sealed trait Tree extends StructuredTree with Locationed with TreeOps

  object Tree extends ObjectTreeOps

  ///////////////////////////////////////////////////////////////////////////////
  // Root node for a file
  ///////////////////////////////////////////////////////////////////////////////

  case class Root(typeDefinitions: List[TypeDefinition], entity: Entity) extends Tree

  ///////////////////////////////////////////////////////////////////////////////
  // References refer to definitions
  ///////////////////////////////////////////////////////////////////////////////

  sealed trait Ref extends Tree
  case class Ident(name: String) extends Ref with SourceAttributes
  case class Sym(symbol: Symbol) extends Ref

  ///////////////////////////////////////////////////////////////////////////////
  // Type definition nodes
  ///////////////////////////////////////////////////////////////////////////////

  sealed trait TypeDefinition extends Tree

  case class TypeDefinitionStruct(ref: Ref, fieldNames: List[String], fieldTypes: List[Type])
      extends TypeDefinition
  case class TypeDefinitionTypedef(ref: Ref, kind: Type) extends TypeDefinition

  ///////////////////////////////////////////////////////////////////////////////
  // Entity (module) nodes
  ///////////////////////////////////////////////////////////////////////////////

  case class Entity(
      // Entity being defined
      ref: Ref,
      // Declarations in entity scope
      declarations: List[Declaration],
      // Instantiations
      instances: List[Instance],
      // Port connections
      connects: List[Connect],
      // Functions
      functions: List[Function],
      // Sates
      states: List[State],
      // fence statements
      fenceStmts: List[Stmt],
      // Nested entity definitions
      entities: List[Entity],
      // Verbatim sections. Map from language to string to insert into output
      verbatim: Map[String, String]
  ) extends Tree
      with EntityOps

  ///////////////////////////////////////////////////////////////////////////////
  // Entity contents
  ///////////////////////////////////////////////////////////////////////////////

  sealed trait Declaration extends Tree
  case class DeclIdent(ident: Ident, kind: Type, init: Option[Expr]) extends Declaration
  case class Decl(symbol: TermSymbol, init: Option[Expr]) extends Declaration

  case class Instance(ref: Ref, module: Ref, paramNames: List[String], paramExprs: List[Expr])
      extends Tree
  case class Connect(lhs: Expr, rhs: List[Expr]) extends Tree
  case class Function(ref: Ref, body: List[Stmt]) extends Tree

  ///////////////////////////////////////////////////////////////////////////////
  // Tree representing FSM states after control statement conversion
  ///////////////////////////////////////////////////////////////////////////////

  case class State(expr: Expr, body: List[Stmt]) extends Tree

  ///////////////////////////////////////////////////////////////////////////////
  // Thicket
  ///////////////////////////////////////////////////////////////////////////////

  // Thicket is used where a node needs to be transformed into a list of nodes.
  // Thickets are transient and are flattened into the receiving list during traversal.
  case class Thicket(trees: List[Tree]) extends Tree

  ///////////////////////////////////////////////////////////////////////////////
  // Statements
  ///////////////////////////////////////////////////////////////////////////////

  sealed trait Stmt extends Tree

  case class StmtBlock(body: List[Stmt]) extends Stmt
  case class StmtIf(cond: Expr, thenStmt: Stmt, elseStmt: Option[Stmt]) extends Stmt
  case class StmtCase(expr: Expr, cases: List[CaseClause], default: List[Stmt]) extends Stmt
  case class CaseClause(cond: List[Expr], body: Stmt) extends Tree

  case class StmtLoop(body: List[Stmt]) extends Stmt
  case class StmtWhile(cond: Expr, body: List[Stmt]) extends Stmt
  case class StmtFor(inits: List[Stmt], cond: Option[Expr], step: List[Stmt], body: List[Stmt])
      extends Stmt
  case class StmtDo(cond: Expr, body: List[Stmt]) extends Stmt

  case class StmtLet(inits: List[Stmt], body: Stmt) extends Stmt

  case class StmtFence() extends Stmt
  case class StmtBreak() extends Stmt
  case class StmtGoto(expr: Expr) extends Stmt
  case class StmtReturn() extends Stmt

  case class StmtAssign(lhs: Expr, rhs: Expr) extends Stmt
  case class StmtUpdate(lhs: Expr, op: String, rhs: Expr) extends Stmt
  case class StmtPost(expr: Expr, op: String) extends Stmt
  case class StmtExpr(expr: Expr) extends Stmt
  case class StmtDecl(decl: Declaration) extends Stmt

  case class StmtRead() extends Stmt
  case class StmtWrite() extends Stmt

  case class StmtDollarComment(str: String) extends Stmt // TODO: remove

  case class StmtStall(cond: Expr) extends Stmt

  case class StmtError() extends Stmt // placeholder when errors happened

  ///////////////////////////////////////////////////////////////////////////////
  // Expression nodes
  ///////////////////////////////////////////////////////////////////////////////

  sealed trait Expr extends Tree with ExprOps
  object Expr extends ObjectExprOps

  case class ExprCall(expr: Expr, args: List[Expr]) extends Expr

  // Operators
  case class ExprUnary(op: String, expr: Expr) extends Expr
  case class ExprBinary(lhs: Expr, op: String, rhs: Expr) extends Expr
  case class ExprTernary(cond: Expr, thenExpr: Expr, elseExpr: Expr) extends Expr
  case class ExprRep(count: Expr, expr: Expr) extends Expr
  case class ExprCat(parts: List[Expr]) extends Expr
  case class ExprIndex(expr: Expr, index: Expr) extends Expr
  case class ExprSlice(expr: Expr, lidx: Expr, op: String, ridx: Expr) extends Expr

  case class ExprSelect(expr: Expr, selector: String) extends Expr
  case class ExprIdent(name: String) extends Expr
  case class ExprRef(symbol: Symbol) extends Expr
  case class ExprType(kind: Type) extends Expr

  // Literals
  case class ExprInt(signed: Boolean, width: Int, value: BigInt) extends Expr {
    require(width > 0)
    require(signed || value >= 0)
    require(if (value >= 0) (value >> width) == 0 else (value >> width) == -1)
  }
  case class ExprNum(signed: Boolean, value: BigInt) extends Expr {
    require(signed || value >= 0)
  }
  case class ExprStr(value: String) extends Expr

  case class ExprError() extends Expr // Placeholder when errors happened

}
