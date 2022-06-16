package it.unipr.frontend;

import it.unipr.rust.antlr.RustBaseVisitor;
import it.unipr.rust.antlr.RustParser.*;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CFGDescriptor;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.edge.SequentialEdge;
import it.unive.lisa.program.cfg.statement.Assignment;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Code member visitor for Rust.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class RustCodeMemberVisitor extends RustBaseVisitor<Object> {

	/**
	 * File path of the Rust program to be analyzed
	 */
	private final String filePath;

	/**
	 * Reference to the LiSA program that it is currently analyzed
	 */
	private final Program program;

	/**
	 * Current compilation unit to which code members should be added
	 */
	private final CompilationUnit unit;

	/**
	 * Current control-flow graph to which code members should be added
	 */
	private CFG currentCfg;

	/**
	 * Builds a code member visitor for Rust.
	 * 
	 * @param filePath file path of the Rust program to be analyzed
	 * @param program  reference to the LiSA program that it is currently
	 *                     analyzed
	 * @param unit     current compilation unit to whic code members should be
	 *                     added
	 */
	public RustCodeMemberVisitor(String filePath, Program program, CompilationUnit unit) {
		this.filePath = filePath;
		this.program = program;
		this.unit = unit;
	}

	/**
	 * Yields the line position of a parse rule
	 * 
	 * @param ctx the parse rule
	 * 
	 * @return yields the line position of a parse rule
	 */
	static protected int getLine(ParserRuleContext ctx) {
		return ctx.getStart().getLine();
	}

	/**
	 * Yields the line position of a terminal node
	 * 
	 * @param ctx the terminal node
	 * 
	 * @return yields the line position of a terminal node
	 */
	static protected int getLine(TerminalNode ctx) {
		return ctx.getSymbol().getLine();
	}

	/**
	 * Yields the column position of a parse rule
	 * 
	 * @param ctx the parse rule
	 * 
	 * @return yields the column position of a parse rule
	 */
	static protected int getCol(ParserRuleContext ctx) {
		return ctx.getStop().getCharPositionInLine();
	}

	/**
	 * Yields the column position of a terminal node
	 * 
	 * @param ctx the terminal node
	 * 
	 * @return yields the column position of a terminal node
	 */
	static protected int getCol(TerminalNode ctx) {
		return ctx.getSymbol().getCharPositionInLine();
	}

	/**
	 * Yields the source code location of a parse rule
	 * 
	 * @param ctx the parse rule
	 * 
	 * @return yields the source code location of a parse rule
	 */
	protected SourceCodeLocation locationOf(ParserRuleContext ctx) {
		return new SourceCodeLocation(filePath, getLine(ctx), getCol(ctx));
	}

	/**
	 * Yields the source code location of a terminal node
	 * 
	 * @param ctx the terminal node
	 * 
	 * @return yields the source code location of a terminal node
	 */
	protected SourceCodeLocation locationOf(TerminalNode ctx) {
		return new SourceCodeLocation(filePath, getLine(ctx), getCol(ctx));
	}

	@Override
	public CFG visitFn_decl(Fn_declContext ctx) {
		String fnName = getFnName(ctx.fn_head());
		CFGDescriptor cfgDesc = new CFGDescriptor(locationOf(ctx), unit, false, fnName, new Parameter[0]);
		currentCfg = new CFG(cfgDesc);
		visitBlock_with_inner_attrs(ctx.block_with_inner_attrs());
		return currentCfg;
	}

	private String getFnName(Fn_headContext fnHead) {
		return fnHead.ident().getText();
	}

	@Override
	public Object visitVisibility(VisibilityContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitVisibility_restriction(Visibility_restrictionContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitItem_macro_use(Item_macro_useContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitItem_macro_path(Item_macro_pathContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitItem_macro_path_parent(Item_macro_path_parentContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitItem_macro_path_segment(Item_macro_path_segmentContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitItem_macro_tail(Item_macro_tailContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitExtern_crate(Extern_crateContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitUse_decl(Use_declContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitUse_path(Use_pathContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitUse_suffix(Use_suffixContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitUse_item(Use_itemContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitUse_item_list(Use_item_listContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitRename(RenameContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitMod_decl_short(Mod_decl_shortContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitMod_decl(Mod_declContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitExtern_mod(Extern_modContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitForeign_item(Foreign_itemContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitForeign_item_tail(Foreign_item_tailContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitStatic_decl(Static_declContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitConst_decl(Const_declContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitMethod_decl(Method_declContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTrait_method_decl(Trait_method_declContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitForeign_fn_decl(Foreign_fn_declContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitFn_head(Fn_headContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitParam(ParamContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitParam_ty(Param_tyContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitParam_list(Param_listContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitVariadic_param_list(Variadic_param_listContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitVariadic_param_list_names_optional(Variadic_param_list_names_optionalContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitSelf_param(Self_paramContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitMethod_param_list(Method_param_listContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTrait_method_param(Trait_method_paramContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitRestricted_pat(Restricted_patContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTrait_method_param_list(Trait_method_param_listContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitRtype(RtypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitFn_rtype(Fn_rtypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitType_decl(Type_declContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitStruct_decl(Struct_declContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitStruct_tail(Struct_tailContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTuple_struct_field(Tuple_struct_fieldContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTuple_struct_field_list(Tuple_struct_field_listContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitField_decl(Field_declContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitField_decl_list(Field_decl_listContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitEnum_decl(Enum_declContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitEnum_variant(Enum_variantContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitEnum_variant_list(Enum_variant_listContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitEnum_variant_main(Enum_variant_mainContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitEnum_tuple_field(Enum_tuple_fieldContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitEnum_tuple_field_list(Enum_tuple_field_listContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitEnum_field_decl(Enum_field_declContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitEnum_field_decl_list(Enum_field_decl_listContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitUnion_decl(Union_declContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTrait_decl(Trait_declContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTrait_item(Trait_itemContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTy_default(Ty_defaultContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitConst_default(Const_defaultContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitImpl_block(Impl_blockContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitImpl_what(Impl_whatContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitImpl_item(Impl_itemContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitImpl_item_tail(Impl_item_tailContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitAttr(AttrContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitInner_attr(Inner_attrContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTt(TtContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTt_delimited(Tt_delimitedContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTt_parens(Tt_parensContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTt_brackets(Tt_bracketsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTt_block(Tt_blockContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitMacro_tail(Macro_tailContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitPath(PathContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitPath_parent(Path_parentContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitAs_trait(As_traitContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitPath_segment(Path_segmentContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitPath_segment_no_super(Path_segment_no_superContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitSimple_path_segment(Simple_path_segmentContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTy_path(Ty_pathContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitFor_lifetime(For_lifetimeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLifetime_def_list(Lifetime_def_listContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLifetime_def(Lifetime_defContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLifetime_bound(Lifetime_boundContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTy_path_main(Ty_path_mainContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTy_path_tail(Ty_path_tailContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTy_path_parent(Ty_path_parentContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTy_path_segment(Ty_path_segmentContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTy_path_segment_no_super(Ty_path_segment_no_superContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitWhere_clause(Where_clauseContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitWhere_bound_list(Where_bound_listContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitWhere_bound(Where_boundContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitColon_bound(Colon_boundContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitBound(BoundContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitPrim_bound(Prim_boundContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTy(TyContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitMut_or_const(Mut_or_constContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitExtern_abi(Extern_abiContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTy_args(Ty_argsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLifetime_list(Lifetime_listContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTy_sum(Ty_sumContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTy_sum_list(Ty_sum_listContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTy_arg(Ty_argContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTy_arg_list(Ty_arg_listContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTy_params(Ty_paramsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLifetime_param(Lifetime_paramContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLifetime_param_list(Lifetime_param_listContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTy_param(Ty_paramContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTy_param_list(Ty_param_listContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitPat(PatContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitPat_no_mut(Pat_no_mutContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitPat_range_end(Pat_range_endContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitPat_lit(Pat_litContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitPat_list(Pat_listContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitPat_list_with_dots(Pat_list_with_dotsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitPat_list_dots_tail(Pat_list_dots_tailContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitPat_elt(Pat_eltContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitPat_elt_list(Pat_elt_listContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitPat_fields(Pat_fieldsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitPat_field(Pat_fieldContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitExpr(ExprContext ctx) {
		return visitAssign_expr(ctx.assign_expr());
	}

	@Override
	public Object visitExpr_no_struct(Expr_no_structContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitExpr_list(Expr_listContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitBlock(BlockContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pair<Statement, Statement> visitBlock_with_inner_attrs(Block_with_inner_attrsContext ctx) {
		// TODO: skipping inner attributes for the moment
		Statement lastStmt = null;
		Statement entryNode = null;

		for (StmtContext stmt : ctx.stmt()) {
			Pair<Statement, Statement> currentStmt = visitStmt(stmt);

			if (lastStmt != null)
				currentCfg.addEdge(new SequentialEdge(lastStmt, currentStmt.getLeft()));
			else
				entryNode = currentStmt.getLeft();
			lastStmt = currentStmt.getRight();
		}
		return Pair.of(entryNode, lastStmt);
	}

	@Override
	public Pair<Statement, Statement> visitStmt(StmtContext ctx) {
		if (ctx.item() != null)
			// TODO: not considered for the moment
			return null;
		else
			return visitStmt_tail(ctx.stmt_tail());
	}

	@Override
	public Pair<Statement, Statement> visitStmt_tail(Stmt_tailContext ctx) {
		// TODO: for the moment we just parse expr, skipping the rest
		Statement expr = visitExpr(ctx.expr());
		return Pair.of(expr, expr);
	}

	@Override
	public Object visitBlocky_expr(Blocky_exprContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitCond_or_pat(Cond_or_patContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLoop_label(Loop_labelContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitMatch_arms(Match_armsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitMatch_arm_intro(Match_arm_introContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitMatch_pat(Match_patContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitMatch_if_clause(Match_if_clauseContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitExpr_attrs(Expr_attrsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitExpr_inner_attrs(Expr_inner_attrsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitPrim_expr(Prim_exprContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitPrim_expr_no_struct(Prim_expr_no_structContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLit(LitContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitClosure_params(Closure_paramsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitClosure_param(Closure_paramContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitClosure_param_list(Closure_param_listContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitClosure_tail(Closure_tailContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLifetime_or_expr(Lifetime_or_exprContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitFields(FieldsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitStruct_update_base(Struct_update_baseContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitField(FieldContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitField_name(Field_nameContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitPost_expr(Post_exprContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitPost_expr_tail(Post_expr_tailContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitPre_expr(Pre_exprContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitCast_expr(Cast_exprContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitMul_expr(Mul_exprContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitAdd_expr(Add_exprContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitShift_expr(Shift_exprContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitBit_and_expr(Bit_and_exprContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitBit_xor_expr(Bit_xor_exprContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitBit_or_expr(Bit_or_exprContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitCmp_expr(Cmp_exprContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitAnd_expr(And_exprContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitOr_expr(Or_exprContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression visitRange_expr(Range_exprContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitAssign_expr(Assign_exprContext ctx) {
		if (ctx.assign_expr() == null)
			return visitRange_expr(ctx.range_expr());

		Expression lhs = visitRange_expr(ctx.range_expr());
		Expression rhs = (Expression) visitAssign_expr(ctx.assign_expr());
		Assignment asg = new Assignment(currentCfg, locationOf(ctx), lhs, rhs);
		return asg;
	}

	@Override
	public Object visitPost_expr_no_struct(Post_expr_no_structContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitPre_expr_no_struct(Pre_expr_no_structContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitCast_expr_no_struct(Cast_expr_no_structContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitMul_expr_no_struct(Mul_expr_no_structContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitAdd_expr_no_struct(Add_expr_no_structContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitShift_expr_no_struct(Shift_expr_no_structContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitBit_and_expr_no_struct(Bit_and_expr_no_structContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitBit_xor_expr_no_struct(Bit_xor_expr_no_structContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitBit_or_expr_no_struct(Bit_or_expr_no_structContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitCmp_expr_no_struct(Cmp_expr_no_structContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitAnd_expr_no_struct(And_expr_no_structContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitOr_expr_no_struct(Or_expr_no_structContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitRange_expr_no_struct(Range_expr_no_structContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitAssign_expr_no_struct(Assign_expr_no_structContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitIdent(IdentContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitAny_ident(Any_identContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

}
