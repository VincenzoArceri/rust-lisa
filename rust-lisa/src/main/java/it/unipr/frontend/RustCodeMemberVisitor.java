package it.unipr.frontend;

import it.unipr.cfg.expression.RustBoxExpression;
import it.unipr.cfg.expression.RustCastExpression;
import it.unipr.cfg.expression.RustDerefExpression;
import it.unipr.cfg.expression.RustDoubleRefExpression;
import it.unipr.cfg.expression.RustRangeExpression;
import it.unipr.cfg.expression.RustRangeFromExpression;
import it.unipr.cfg.expression.RustRefExpression;
import it.unipr.cfg.expression.bitwise.RustAndBitwiseExpression;
import it.unipr.cfg.expression.bitwise.RustLeftShiftExpression;
import it.unipr.cfg.expression.bitwise.RustNotExpression;
import it.unipr.cfg.expression.bitwise.RustOrBitwiseExpression;
import it.unipr.cfg.expression.bitwise.RustRightShiftExpression;
import it.unipr.cfg.expression.bitwise.RustXorBitwiseExpression;
import it.unipr.cfg.expression.comparison.RustAndExpression;
import it.unipr.cfg.expression.comparison.RustEqualExpression;
import it.unipr.cfg.expression.comparison.RustGreaterEqualExpression;
import it.unipr.cfg.expression.comparison.RustGreaterExpression;
import it.unipr.cfg.expression.comparison.RustLessEqualExpression;
import it.unipr.cfg.expression.comparison.RustLessExpression;
import it.unipr.cfg.expression.comparison.RustNotEqualExpression;
import it.unipr.cfg.expression.comparison.RustOrExpression;
import it.unipr.cfg.expression.literal.RustArrayLiteral;
import it.unipr.cfg.expression.literal.RustBoolean;
import it.unipr.cfg.expression.literal.RustChar;
import it.unipr.cfg.expression.literal.RustFloat;
import it.unipr.cfg.expression.literal.RustInteger;
import it.unipr.cfg.expression.literal.RustString;
import it.unipr.cfg.expression.literal.RustTupleLiteral;
import it.unipr.cfg.expression.literal.RustUnitLiteral;
import it.unipr.cfg.expression.numeric.RustAddExpression;
import it.unipr.cfg.expression.numeric.RustDivExpression;
import it.unipr.cfg.expression.numeric.RustMinusExpression;
import it.unipr.cfg.expression.numeric.RustModExpression;
import it.unipr.cfg.expression.numeric.RustMulExpression;
import it.unipr.cfg.expression.numeric.RustSubExpression;
import it.unipr.cfg.statement.RustAssignment;
import it.unipr.cfg.statement.RustLetAssignment;
import it.unipr.cfg.type.RustType;
import it.unipr.cfg.type.RustUnitType;
import it.unipr.cfg.type.composite.RustArrayType;
import it.unipr.cfg.type.composite.RustStructType;
import it.unipr.cfg.type.composite.RustTupleType;
import it.unipr.rust.antlr.RustBaseVisitor;
import it.unipr.rust.antlr.RustParser.*;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CFGDescriptor;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.edge.FalseEdge;
import it.unive.lisa.program.cfg.edge.SequentialEdge;
import it.unive.lisa.program.cfg.edge.TrueEdge;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NoOp;
import it.unive.lisa.program.cfg.statement.Ret;
import it.unive.lisa.program.cfg.statement.Return;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.program.cfg.statement.call.Call.CallType;
import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;
import it.unive.lisa.program.cfg.statement.literal.NullLiteral;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Code member visitor for Rust.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustCodeMemberVisitor extends RustBaseVisitor<Object> {

	/**
	 * File path of the Rust program to be analyzed.
	 */
	private final String filePath;

	/**
	 * Reference to the LiSA program that it is currently analyzed.
	 */
	private final Program program;

	/**
	 * Current compilation unit to which code members should be added.
	 */
	private final CompilationUnit unit;

	/**
	 * Current control-flow graph to which code members should be added.
	 */
	private CFG currentCfg;

	/**
	 * Builds a code member visitor for Rust.
	 * 
	 * @param filePath file path of the Rust program to be analyzed
	 * @param program  reference to the LiSA program that it is currently
	 *                     analyzed
	 * @param unit     current compilation unit to which code members should be
	 *                     added
	 */
	public RustCodeMemberVisitor(String filePath, Program program, CompilationUnit unit) {
		this.filePath = filePath;
		this.program = program;
		this.unit = unit;
	}
	
	/**
	 * Yields the current control flow graph.
	 * 
	 * @return the current cfg
	 */
	public CFG getCurrentCfg() {
		return currentCfg;
	}
	
	/**
	 * Yields the current compilation unit.
	 * 
	 * @return the current compilation unit
	 */
	public CompilationUnit getCompilationUnit() {
		return unit;
	}

	/**
	 * Yields the line position of a parse rule.
	 * 
	 * @param ctx the parse rule
	 * 
	 * @return yields the line position of a parse rule
	 */
	static protected int getLine(ParserRuleContext ctx) {
		return ctx.getStart().getLine();
	}

	/**
	 * Yields the line position of a terminal node.
	 * 
	 * @param ctx the terminal node
	 * 
	 * @return yields the line position of a terminal node
	 */
	static protected int getLine(TerminalNode ctx) {
		return ctx.getSymbol().getLine();
	}

	/**
	 * Yields the column position of a parse rule.
	 * 
	 * @param ctx the parse rule
	 * 
	 * @return yields the column position of a parse rule
	 */
	static protected int getCol(ParserRuleContext ctx) {
		return ctx.getStop().getCharPositionInLine();
	}

	/**
	 * Yields the column position of a terminal node.
	 * 
	 * @param ctx the terminal node
	 * 
	 * @return yields the column position of a terminal node
	 */
	static protected int getCol(TerminalNode ctx) {
		return ctx.getSymbol().getCharPositionInLine();
	}

	/**
	 * Yields the source code location of a parse rule.
	 * 
	 * @param ctx the parse rule
	 * 
	 * @return yields the source code location of a parse rule
	 */
	protected SourceCodeLocation locationOf(ParserRuleContext ctx) {
		return new SourceCodeLocation(filePath, getLine(ctx), getCol(ctx));
	}

	/**
	 * Yields the source code location of a terminal node.
	 * 
	 * @param ctx the terminal node
	 * 
	 * @return yields the source code location of a terminal node
	 */
	protected SourceCodeLocation locationOf(TerminalNode ctx) {
		return new SourceCodeLocation(filePath, getLine(ctx), getCol(ctx));
	}

	@Override
	public List<CFG> visitFn_decl(Fn_declContext ctx) {
		String fnName = getFnName(ctx.fn_head());
		
		Type returnType = RustUnitType.INSTANCE;
		if (ctx.fn_rtype() != null)
			returnType = new RustTypeVisitor(this).visitFn_rtype(ctx.fn_rtype());
		
		CFGDescriptor cfgDesc = new CFGDescriptor(locationOf(ctx), unit, false, fnName, returnType, new Parameter[0]);
		currentCfg = new CFG(cfgDesc);
		
		Pair<Statement, Statement> block = visitBlock_with_inner_attrs(ctx.block_with_inner_attrs());
		currentCfg.getEntrypoints().add(block.getLeft());

		if (currentCfg.getAllExitpoints().isEmpty()) {
			Ret ret = new Ret(currentCfg, locationOf(ctx));
			currentCfg.addNode(ret);
			currentCfg.addEdge(new SequentialEdge(block.getRight(), ret));
		}

		currentCfg.simplify();
		
		List<CFG> result = new ArrayList<>();
		result.add(currentCfg);
		return result;
	}

	private String getFnName(Fn_headContext fnHead) {
		// TODO skipping: 'const'? 'unsafe'? extern_abi? ty_params?
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
	public CFG visitMethod_decl(Method_declContext ctx) {
		String methodName = getFnName(ctx.fn_head());
		
		Type returnType = RustUnitType.INSTANCE;
		if (ctx.fn_rtype() != null)
			returnType = new RustTypeVisitor(this).visitFn_rtype(ctx.fn_rtype());
						
		CFGDescriptor cfgDesc = new CFGDescriptor(locationOf(ctx), unit, false, methodName, returnType, new Parameter[0]);
		currentCfg = new CFG(cfgDesc);
		Pair<Statement, Statement> block = visitBlock_with_inner_attrs(ctx.block_with_inner_attrs());
		currentCfg.getEntrypoints().add(block.getLeft());

		if (currentCfg.getAllExitpoints().isEmpty()) {
			Ret ret = new Ret(currentCfg, locationOf(ctx));
			currentCfg.addNode(ret);
			currentCfg.addEdge(new SequentialEdge(block.getRight(), ret));
		}
		
		currentCfg.simplify();
		return currentCfg;
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
	public Parameter visitParam(ParamContext ctx) {
		Expression expression = visitPat(ctx.pat());
		Type type = visitParam_ty(ctx.param_ty());
		
		return new Parameter(locationOf(ctx), expression.toString(), type);
	}

	@Override
	public Type visitParam_ty(Param_tyContext ctx) {
		// TODO skipping second production
		return visitTy_sum(ctx.ty_sum());
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
	public Parameter visitSelf_param(Self_paramContext ctx) {
		// TODO Skipping second production
		
		// TODO as of now, mutability in params requires more infrastructure
		boolean mutability = false;
		if (ctx.getChild(0).getText().equals("mut"))
			mutability = true;
		
		Type type = visitTy_sum(ctx.ty_sum());
		
		return new Parameter(locationOf(ctx), "self", type);
	}

	@Override
	public List<Parameter> visitMethod_param_list(Method_param_listContext ctx) {
		List<Parameter> parameters = new ArrayList<>();
		
		if (ctx.self_param() != null) {
			parameters.add(visitSelf_param(ctx.self_param()));
		}
		
		for (ParamContext pCtx : ctx.param())
			parameters.add(visitParam(pCtx));
			
		return parameters;
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
	public Type visitFn_rtype(Fn_rtypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitType_decl(Type_declContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RustStructType visitStruct_decl(Struct_declContext ctx) {
		CFGDescriptor cfgDesc = new CFGDescriptor(locationOf(ctx), unit, false, filePath, new Parameter[0]);
		currentCfg = new CFG(cfgDesc);
		
		Expression name = visitIdent(ctx.ident());
		
		// TODO skipping ty_params? production
		List<Expression> declarations = new RustTypeVisitor(this).visitStruct_tail(ctx.struct_tail());
		
		return RustStructType.lookup(
			name.toString(),
			unit, 
			false, 
			declarations
				.stream()
				.map(x -> x.getStaticType())
				.collect(Collectors.toList())
				.toArray(new RustType[0])
			);
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
	public List<CFG> visitImpl_block(Impl_blockContext ctx) {
		// TODO Ignoring: 'unsafe'?, ty_params? where_clause?
		Type struct = visitImpl_what(ctx.impl_what());
		
		List<CFG> impls = new ArrayList<>();
		for (Impl_itemContext fdCtx: ctx.impl_item()) {
			CFG visitedCfg = visitImpl_item(fdCtx);
			impls.add(visitedCfg);
			unit.addCFG(visitedCfg);
		}
		
		return impls;
	}

	@Override
	public Type visitImpl_what(Impl_whatContext ctx) {
		// TODO Skipping trait implementation for now and parsing only the last rule
		return visitTy_sum(ctx.ty_sum(0));
	}

	@Override
	public CFG visitImpl_item(Impl_itemContext ctx) {
		// TODO skipping attr* and visibility?
		return visitImpl_item_tail(ctx.impl_item_tail());
	}

	@Override
	public CFG visitImpl_item_tail(Impl_item_tailContext ctx) {
		// TODO skipping all production except "method_decl"
		return visitMethod_decl(ctx.method_decl());
	}

	@Override
	public Statement visitAttr(AttrContext ctx) {
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
	public Expression visitPath(PathContext ctx) {
		if (ctx.path_segment_no_super() != null)
			return visitPath_segment_no_super(ctx.path_segment_no_super());
		// TODO: skipping the production path_parent? '::' path_segment_no_super
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
	public Expression visitPath_segment_no_super(Path_segment_no_superContext ctx) {
		// TODO: skipping ('::' ty_args)?
		return visitSimple_path_segment(ctx.simple_path_segment());
	}

	@Override
	public Expression visitSimple_path_segment(Simple_path_segmentContext ctx) {
		// TODO: skipping Self
		return visitIdent(ctx.ident());
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
	public Type visitTy(TyContext ctx) {
		return new RustTypeVisitor(this).visitTy(ctx);
	}

	@Override
	public String visitMut_or_const(Mut_or_constContext ctx) {
		return ctx.getText().equals("mut") ? "mut" : "const";
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
	public Type visitTy_sum(Ty_sumContext ctx) {
		// TODO skipping ('+' bound)? grammar branch
		return new RustTypeVisitor(this).visitTy(ctx.ty());
	}

	@Override
	public List<Type> visitTy_sum_list(Ty_sum_listContext ctx) {
		return new RustTypeVisitor(this).visitTy_sum_list(ctx);
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
	public Expression visitPat(PatContext ctx) {
		if (ctx.pat_no_mut() != null)
			return visitPat_no_mut(ctx.pat_no_mut());

		// TODO figure out later what to do with mutability
		boolean mutable = true;

		if (ctx.pat() != null) {
			// TODO Ignoring the pat part for now
			return visitPat(ctx.pat());
		}

		Expression ident = visitIdent(ctx.ident());
		return ident;
	}

	@Override
	public Expression visitPat_no_mut(Pat_no_mutContext ctx) {
		if (ctx.pat_lit() != null) {
			return visitPat_lit(ctx.pat_lit());
		}

		if (ctx.ident() != null) {
			// TODO figure out what to do with all this stuff
			boolean isRef = (ctx.getChild(0) != null && ctx.getChild(0).getText().equals("ref") ? true : false);
			boolean isMut = (ctx.getChild(1) != null && ctx.getChild(1).getText().equals("mut") ? true : false);

			if (ctx.pat() != null) {
				// TODO figure out what to do
				Expression pat = visitPat(ctx.pat());
			}

			// Parse the identifier
			return visitIdent(ctx.ident());
		}

		switch (ctx.getChild(0).getText()) {
		case "_":
			return new VariableRef(currentCfg, locationOf(ctx), "_");
		case "(":
			return visitPat_list_with_dots(ctx.pat_list_with_dots());
		case "[":
			return visitPat_elt_list(ctx.pat_elt_list());
		case "&":
			if (ctx.getChild(1).getText().equals("mut"))
				// TODO figure out what to do with mutable 
				return new RustRefExpression(currentCfg, locationOf(ctx), visitPat(ctx.pat()));
		
			return visitPat_no_mut(ctx.pat_no_mut());
		case "&&":
			if (ctx.getChild(1).getText().equals("mut"))
				// TODO figure out what to do with mutable 
				return new RustRefExpression(currentCfg, locationOf(ctx),
					new RustRefExpression(currentCfg, locationOf(ctx),
						visitPat(ctx.pat())));

			return new RustRefExpression(currentCfg, locationOf(ctx),
				new RustRefExpression(currentCfg, locationOf(ctx),
					visitPat_no_mut(ctx.pat_no_mut())));
		case "box":
			return new RustBoxExpression(currentCfg, locationOf(ctx), visitPat(ctx.pat()));
		default:
			// TODO need to implement the other cases:
			// pat_no_mut:
			// : pat_range_end '...' pat_range_end
			// | pat_range_end '..' pat_range_end // experimental
			// `feature(exclusive_range_pattern)`
			// | path macro_tail
			// | 'ref'? ident ('@' pat)?
			// | 'ref' 'mut' ident ('@' pat)?
			// | path '(' pat_list_with_dots? ')'
			// | path '{' pat_fields? '}'
			// | path // BUG: ambiguity with bare ident case (above)
			return null;
		}
	}

	@Override
	public Object visitPat_range_end(Pat_range_endContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression visitPat_lit(Pat_litContext ctx) {
		Expression lit = visitLit(ctx.lit());
		if (ctx.getChild(0).getText().equals("-"))
			return new RustMinusExpression(currentCfg, locationOf(ctx), lit);

		return lit;
	}

	@Override
	public Expression visitPat_list(Pat_listContext ctx) {
		// TODO figure out what to do here
		for (PatContext patContext : ctx.pat()) {

		}
		return null;
	}

	@Override
	public Expression visitPat_list_with_dots(Pat_list_with_dotsContext ctx) {
		if (ctx.pat_list_dots_tail() != null) {
			return visitPat_list_dots_tail(ctx.pat_list_dots_tail());
		}

		// TODO figure out what to do in the other cases
		for (PatContext patContext : ctx.pat()) {

		}
		// TODO figure out what to do in the other cases
		if (ctx.pat_list_dots_tail() != null) {
			visitPat_list_dots_tail(ctx.pat_list_dots_tail());
		}

		return null;
	}

	@Override
	public Expression visitPat_list_dots_tail(Pat_list_dots_tailContext ctx) {
		// TODO figure out what to do here
		if (ctx.pat_list() != null)
			return visitPat_list(ctx.pat_list());
		return null;
	}

	@Override
	public Expression visitPat_elt(Pat_eltContext ctx) {
		if (ctx.pat() != null)
			return visitPat(ctx.pat());
		// TODO figure out what to do in the other case
		return null;
	}

	@Override
	public Expression visitPat_elt_list(Pat_elt_listContext ctx) {
		for (Pat_eltContext patContext : ctx.pat_elt()) {
			// TODO figure out what to do here
			Expression pat = visitPat_elt(patContext);
		}

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
	public Expression visitExpr(ExprContext ctx) {
		return visitAssign_expr(ctx.assign_expr());
	}

	@Override
	public Expression visitExpr_no_struct(Expr_no_structContext ctx) {
		return visitAssign_expr_no_struct(ctx.assign_expr_no_struct());
	}

	@Override
	public List<Expression> visitExpr_list(Expr_listContext ctx) {
		List<Expression> exprs = new ArrayList<>();
		for (ExprContext exprCtx : ctx.expr())
			exprs.add(visitExpr(exprCtx));
			
		return exprs;
	}

	@Override
	public Pair<Statement, Statement> visitBlock(BlockContext ctx) {
		Statement lastStmt = null;
		Statement entryNode = null;

		if (ctx.stmt() != null) {
			for (StmtContext stmt : ctx.stmt()) {
				@SuppressWarnings("unchecked")
				// Note: since we are not sure what to return from visitStmt, we are sure that this function returns a pair of Statement
				Pair<Statement, Statement> currentStmt = (Pair<Statement, Statement>) visitStmt(stmt);

				if (lastStmt != null)
					currentCfg.addEdge(new SequentialEdge(lastStmt, currentStmt.getLeft()));
				else
					entryNode = currentStmt.getLeft();

				lastStmt = currentStmt.getRight();
			}
		} else {
			lastStmt = new NoOp(currentCfg, locationOf(ctx));
			entryNode = new NoOp(currentCfg, locationOf(ctx));

			currentCfg.addEdge(new SequentialEdge(entryNode, lastStmt));
		}

		if (ctx.expr() != null) {
			Statement exprStmt = visitExpr(ctx.expr());
			currentCfg.addEdge(new SequentialEdge(lastStmt, exprStmt));

			return Pair.of(entryNode, exprStmt);
		}

		return Pair.of(entryNode, lastStmt);
	}

	@Override
	public Pair<Statement, Statement> visitBlock_with_inner_attrs(Block_with_inner_attrsContext ctx) {
		// TODO: skipping inner attributes for the moment
		Statement lastStmt = null;
		Statement entryNode = null;

		for (StmtContext stmt : ctx.stmt()) {
			@SuppressWarnings("unchecked")
			// Note: since we are not sure what to return from visitStmt, we are sure that this function returns a pair of Statement
			Pair<Statement, Statement> currentStmt = (Pair<Statement, Statement>) visitStmt(stmt);

			if (lastStmt != null)
				currentCfg.addEdge(new SequentialEdge(lastStmt, currentStmt.getLeft()));
			else
				entryNode = currentStmt.getLeft();

			lastStmt = currentStmt.getRight();
		}

		// This expr is the one of return from a function
		if (ctx.expr() != null) {
			Expression expr = visitExpr(ctx.expr());

			Return ret = new Return(currentCfg, locationOf(ctx), expr);
			
			currentCfg.addEdge(new SequentialEdge(lastStmt, ret));

			lastStmt = ret;
		}

		return Pair.of(entryNode, lastStmt);
	}

	@Override
	public Object visitStmt(StmtContext ctx) {
		// TODO I am not sure on what to return here exactly. So for now this is implemented as returning an Object and a (safe) cast as needed

		if (ctx.getText().equals(";")) {
			NoOp noOp = new NoOp(currentCfg, locationOf(ctx));

			currentCfg.addNode(noOp);

			return Pair.of(noOp, noOp);
		}

		if (ctx.item() != null)
			return visitItem(ctx.item());
		else
			return visitStmt_tail(ctx.stmt_tail());
	}

	@Override
	public Pair<Statement, Statement> visitStmt_tail(Stmt_tailContext ctx) {
		if (ctx.getChild(0) instanceof ExprContext) {
			Statement expr = visitExpr(ctx.expr());
			currentCfg.addNode(expr);
			return Pair.of(expr, expr);
		}

		// TODO figure out what to do here
		for (AttrContext attr : ctx.attr()) {
			break;
		}

		if (ctx.pat() != null) {
			Expression name = visitPat(ctx.pat());

			Type type = (ctx.ty() == null ? Untyped.INSTANCE : visitTy(ctx.ty()));

			// TODO do not take into account the attr part for now
			if (ctx.expr() != null) {
				Expression expr = visitExpr(ctx.expr());

				VariableRef var = new VariableRef(currentCfg, locationOf(ctx), name.toString(), type);

				RustLetAssignment assigment = new RustLetAssignment(currentCfg, locationOf(ctx), type, var, expr);
				currentCfg.addNode(assigment);

				return Pair.of(assigment, assigment);
			}

			VariableRef var = new VariableRef(currentCfg, locationOf(ctx), name.toString(), type);
			currentCfg.addNode(var);
			return Pair.of(var, var);
		}

		// TODO do not take into account the attr part for now
		return visitBlocky_expr(ctx.blocky_expr());
	}

	@Override
	public Pair<Statement, Statement> visitBlocky_expr(Blocky_exprContext ctx) {
		if (ctx.getChild(0) instanceof Block_with_inner_attrsContext && ctx.block_with_inner_attrs() != null)
			return visitBlock_with_inner_attrs(ctx.block_with_inner_attrs());

		Statement firstStmt = null;
		Statement lastStmt = null;

		// TODO Figure out what to do with the loop label
		Object loop_label = null;
		if (ctx.loop_label() != null) {
			loop_label = visitLoop_label(ctx.loop_label());
		}

		if (ctx.children.get(0).getText().equals("if")) {
			NoOp noOp = new NoOp(currentCfg, locationOf(ctx));
			currentCfg.addNode(noOp);

			List<Expression> elseIfGuardList = new ArrayList<>();
			for (int i = 0; i < ctx.cond_or_pat().size(); ++i) {
				Cond_or_patContext copc = ctx.cond_or_pat().get(i);
				BlockContext thenBlock = ctx.block(i);

				Expression guard = visitCond_or_pat(copc);
				currentCfg.addNode(guard);

				Pair<Statement, Statement> trueBlock = visitBlock(thenBlock);

				currentCfg.addEdge(new TrueEdge(guard, trueBlock.getLeft()));
				currentCfg.addEdge(new SequentialEdge(trueBlock.getRight(), noOp));

				elseIfGuardList.add(guard);
			}

			for (int i = 0; i < elseIfGuardList.size() - 1; ++i) {
				currentCfg.addEdge(
						new FalseEdge(elseIfGuardList.get(i), elseIfGuardList.get(i + 1)));
			}

			if (ctx.children.get(ctx.children.size() - 2).getText().equals("else")) {
				BlockContext elseBlock = ctx.block().get(ctx.block().size() - 1);
				Pair<Statement, Statement> parsedElseBlock = visitBlock(elseBlock);

				currentCfg.addEdge(new FalseEdge(
						elseIfGuardList.get(elseIfGuardList.size() - 1),
						parsedElseBlock.getLeft()));

				currentCfg.addEdge(new SequentialEdge(
						parsedElseBlock.getRight(),
						noOp));

			} else {
				currentCfg.addEdge(new FalseEdge(
						elseIfGuardList.get(elseIfGuardList.size() - 1),
						noOp));
			}

			firstStmt = elseIfGuardList.get(0);
			lastStmt = noOp;

		} else if ((loop_label == null && ctx.children.get(0).getText().equals("loop"))
				|| ctx.children.get(1).getText().equals("loop")) {
			Pair<Statement, Statement> stmt = visitBlock_with_inner_attrs(ctx.block_with_inner_attrs());
			currentCfg.addEdge(new SequentialEdge(stmt.getRight(), stmt.getLeft()));

			firstStmt = stmt.getLeft();
			lastStmt = stmt.getRight();

		} else if ((loop_label == null && ctx.children.get(0).getText().equals("while"))
				|| ctx.children.get(1).getText().equals("while")) {
			Expression guard = visitCond_or_pat(ctx.cond_or_pat(0));
			currentCfg.addNode(guard);

			Pair<Statement, Statement> body = visitBlock_with_inner_attrs(ctx.block_with_inner_attrs());

			NoOp noOp = new NoOp(currentCfg, locationOf(ctx));
			currentCfg.addNode(noOp);

			firstStmt = guard;
			currentCfg.addEdge(new TrueEdge(guard, body.getLeft()));
			currentCfg.addEdge(new FalseEdge(guard, noOp));
			currentCfg.addEdge(new SequentialEdge(body.getRight(), guard));
			lastStmt = noOp;

		} else if ((loop_label == null && ctx.children.get(0).getText().equals("for"))
				|| ctx.children.get(1).getText().equals("for")) {

			Expression pat = visitPat(ctx.pat());
			Expression range = visitExpr_no_struct(ctx.expr_no_struct());
			Pair<Statement, Statement> body = visitBlock_with_inner_attrs(ctx.block_with_inner_attrs());

			VariableRef fresh = new VariableRef(currentCfg, locationOf(ctx), "RUSTLISA_FRESH");
			Expression freshAssignment = new RustLetAssignment(currentCfg, locationOf(ctx), Untyped.INSTANCE, fresh,
					range);
			currentCfg.addNode(freshAssignment);

			UnresolvedCall nextCall = new UnresolvedCall(currentCfg, locationOf(ctx),
					RustFrontend.PARAMETER_ASSIGN_STRATEGY, RustFrontend.METHOD_MATCHING_STRATEGY,
					RustFrontend.HIERARCY_TRAVERSAL_STRATEGY, CallType.INSTANCE, fresh.toString(), "next",
					RustFrontend.EVALUATION_ORDER, Untyped.INSTANCE, new Expression[0]);

			// TODO This should be mutable
			Expression patAssignment = new RustLetAssignment(currentCfg, locationOf(ctx), Untyped.INSTANCE, pat,
					nextCall);
			// TODO Keep in mind that this is also a function
			// call to pat.next() which
			// returns a std::ops::Option which is Some(n) if n
			// is the next iterator in the
			// sequence and None otherwise.

			currentCfg.addNode(patAssignment);
			currentCfg.addEdge(new SequentialEdge(freshAssignment, patAssignment));

			// TODO NullLiteral here is to represent the None type, change this
			// in the future
			Expression guard = new RustNotEqualExpression(currentCfg, locationOf(ctx), pat,
					new NullLiteral(currentCfg, locationOf(ctx)));
			currentCfg.addNode(guard);

			currentCfg.addEdge(new SequentialEdge(patAssignment, guard));

			NoOp noOp = new NoOp(currentCfg, locationOf(ctx));
			currentCfg.addNode(noOp);

			currentCfg.addEdge(new TrueEdge(guard, body.getLeft()));
			currentCfg.addEdge(new FalseEdge(guard, noOp));

			Expression increment = new RustAssignment(currentCfg, locationOf(ctx), pat, nextCall);
			// TODO Keep in mind that this is also a function
			// call to pat.next() which
			// returns a std::ops::Option which is Some(n) if n
			// is the next iterator in the
			// sequence and None otherwise.
			currentCfg.addNode(increment);

			currentCfg.addEdge(new SequentialEdge(body.getRight(), increment));
			currentCfg.addEdge(new SequentialEdge(increment, guard));

			firstStmt = freshAssignment;
			lastStmt = noOp;
		}

		return Pair.of(firstStmt, lastStmt);
	}

	@Override
	public Expression visitCond_or_pat(Cond_or_patContext ctx) {
		if (ctx.getChild(0).getText().equals("let")) {
			// TODO ignoring this if branch for now
			Object o = visitPat(ctx.pat());
			Statement pair = visitExpr(ctx.expr());
			return null;
		}

		return visitExpr_no_struct(ctx.expr_no_struct());
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
	public Pair<Statement, Statement> visitExpr_attrs(Expr_attrsContext ctx) {
		Statement first = null;
		Statement previous = null;

		for (AttrContext attr : ctx.attr()) {
			Statement stmtAttr = visitAttr(attr);
			currentCfg.addNode(stmtAttr);

			if (first == null) {
				first = stmtAttr;
				previous = stmtAttr;
			} else {
				currentCfg.addEdge(new SequentialEdge(previous, stmtAttr));
				previous = stmtAttr;
			}
		}

		return Pair.of(first, previous);
	}

	@Override
	public Object visitExpr_inner_attrs(Expr_inner_attrsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression visitPrim_expr(Prim_exprContext ctx) {
		// TODO: skipping the production path '{' expr_inner_attrs? fields? '}'
		return visitPrim_expr_no_struct(ctx.prim_expr_no_struct());
	}

	@Override
	public Expression visitPrim_expr_no_struct(Prim_expr_no_structContext ctx) {
		// TODO remaining production to parse:
//		   | 'self'
//		   | 'move'? closure_params closure_tail
//		   | blocky_expr
//		   | 'break' lifetime_or_expr?
//		   | 'continue' Lifetime?
//		   | 'return' expr?
		
		if (ctx.getChild(0).getText().equals("(")) {
			// TODO Ignoring expr_inner_attrs? part
			
			if (ctx.expr().get(0) != null) {
				Expression expr = visitExpr(ctx.expr(0));
				
				if (ctx.expr_list() != null) {
					List<Expression> exprs = visitExpr_list(ctx.expr_list());
					exprs.add(0, expr);
					
					return new RustTupleLiteral(
						currentCfg,
						locationOf(ctx),
						exprs.stream()
							.map(e -> e.getStaticType())
							.collect(Collectors.toList())
							.toArray(new RustType[0]),
						exprs.toArray(new Expression[0]));
				}
				
				return expr;
			}
				
			return new RustUnitLiteral(currentCfg, locationOf(ctx));
		} else if (ctx.getChild(0).getText().equals("[")) {
			// TODO Ignoring expr_inner_attrs? part
			
			if (ctx.expr_list() != null) {
				List<Expression> exprs = visitExpr_list(ctx.expr_list());
				return new RustArrayLiteral(currentCfg, locationOf(ctx), Untyped.INSTANCE, exprs.toArray(new Expression[0]));
			
			} else if (ctx.expr() != null) {
				Expression element = visitExpr(ctx.expr(0));
				
				// TODO considering only integer literal here
				Integer length = null;
				try {
					length = Integer.parseInt(ctx.expr(1).getText());
				} catch (NumberFormatException nfe) {
					return null;
				}
				Expression[] exprs = new Expression[length];
				Arrays.fill(exprs, element);
				
				return new RustArrayLiteral(currentCfg, locationOf(ctx), Untyped.INSTANCE, exprs);
			}
			
			return new RustArrayLiteral(currentCfg, locationOf(ctx), Untyped.INSTANCE, new Expression[0]);
		
		} else if (ctx.blocky_expr() != null) {
			// TODO watch out for expression and statements
			//return visitBlocky_expr(ctx.blocky_expr());
		} else if (ctx.lit() != null) {
			return visitLit(ctx.lit());
		} else {
			// TODO: skipping macro_tail
			return visitPath(ctx.path());
		}
		
		// Unreachable
		return null;
	}

	@Override
	public Expression visitLit(LitContext ctx) {
		if (ctx.getText().equals("true"))
			return new RustBoolean(currentCfg, locationOf(ctx), true);
		else if (ctx.getText().equals("false"))
			return new RustBoolean(currentCfg, locationOf(ctx), false);
		else if (ctx.BareIntLit() != null)
			return new RustInteger(currentCfg, locationOf(ctx), Integer.parseInt(ctx.getText()));
		else if (ctx.FloatLit() != null)
			return new RustFloat(currentCfg, locationOf(ctx), Float.parseFloat(ctx.getText()));
		else if (ctx.StringLit() != null) {
			String strValue = ctx.StringLit().getText();
			return new RustString(currentCfg, locationOf(ctx), strValue.substring(1, strValue.length()));
		} else if (ctx.CharLit() != null) {
			char charValue = ctx.CharLit().getText().charAt(1);
			return new RustChar(currentCfg, locationOf(ctx), charValue);
		}

		// TODO skipping FullIntLit, ByteLit, ByteStringLit
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
	public Expression visitPost_expr(Post_exprContext ctx) {
		// TODO: skipping the production post_expr post_expr_tail
		return visitPrim_expr(ctx.prim_expr());
	}

	@Override
	public Statement visitPost_expr_tail(Post_expr_tailContext ctx) {
		switch (ctx.getChild(0).getText()) {
		case "?":
			// TODO should return the correct error handling operator on the
			// correct type
			return null;
		case "[":
			return visitExpr(ctx.expr());
		case ".":
			if (ctx.ident() == null) {
				// TODO figure out what to return here
				return null;
			}
			return visitExpr(ctx.expr());
		case "(":
			if (ctx.expr_list() != null) {
				//return visitExpr_list(ctx.expr_list());
				return null;
			}

			// TODO figure out what to return here
			NoOp noOp = new NoOp(currentCfg, locationOf(ctx));
			currentCfg.addNode(noOp);
			return noOp;
		}
		// Unreachable
		return null;
	}

	@Override
	public Expression visitPre_expr(Pre_exprContext ctx) {
		// TODO Skipping every production: "in" etc. and expr_attrs pre_expr
		if (ctx.post_expr() != null)
			return visitPost_expr(ctx.post_expr());

		switch (ctx.children.get(0).getText()) {
		case "-":
			return new RustMinusExpression(currentCfg, locationOf(ctx), visitPre_expr(ctx.pre_expr()));
		case "!":
			return new RustNotExpression(currentCfg, locationOf(ctx), visitPre_expr(ctx.pre_expr()));
		case "&":
			return new RustRefExpression(currentCfg, locationOf(ctx), visitPre_expr(ctx.pre_expr()));
		case "&&":
			return new RustDoubleRefExpression(currentCfg, locationOf(ctx), visitPre_expr(ctx.pre_expr()));
		case "*":
			return new RustDerefExpression(currentCfg, locationOf(ctx), visitPre_expr(ctx.pre_expr()));
		case "box":
			return new RustBoxExpression(currentCfg, locationOf(ctx), visitPre_expr(ctx.pre_expr()));
		default:
			return null;
		}
	}

	@Override
	public Expression visitCast_expr(Cast_exprContext ctx) {
		// TODO: skipping for the moment the cast expressions
		// we just focus on pre_expr
		return visitPre_expr(ctx.pre_expr());
	}

	@Override
	public Expression visitMul_expr(Mul_exprContext ctx) {
		if (ctx.mul_expr() == null)
			return visitCast_expr(ctx.cast_expr());

		Expression left = visitMul_expr(ctx.mul_expr());
		Expression right = visitCast_expr(ctx.cast_expr());
		String symbol = ctx.children.get(1).getText();
		if (symbol.equals("*"))
			return new RustMulExpression(currentCfg, locationOf(ctx), left, right);
		else if (symbol.equals("/"))
			return new RustDivExpression(currentCfg, locationOf(ctx), left, right);
		else
			return new RustModExpression(currentCfg, locationOf(ctx), left, right);
	}

	@Override
	public Expression visitAdd_expr(Add_exprContext ctx) {
		if (ctx.add_expr() == null)
			return visitMul_expr(ctx.mul_expr());

		Expression left = visitAdd_expr(ctx.add_expr());
		Expression right = visitMul_expr(ctx.mul_expr());
		String symbol = ctx.children.get(1).getText();
		if (symbol.equals("+"))
			return new RustAddExpression(currentCfg, locationOf(ctx), left, right);
		else
			return new RustSubExpression(currentCfg, locationOf(ctx), left, right);
	}

	@Override
	public Expression visitShift_expr(Shift_exprContext ctx) {
		if (ctx.shift_expr() == null)
			return visitAdd_expr(ctx.add_expr());

		Expression left = visitShift_expr(ctx.shift_expr());
		Expression right = visitAdd_expr(ctx.add_expr());
		String symbol = ctx.children.get(1).getText();
		if (symbol.equals("<"))
			return new RustLeftShiftExpression(currentCfg, locationOf(ctx), left, right);
		else
			return new RustRightShiftExpression(currentCfg, locationOf(ctx), left, right);
	}

	@Override
	public Expression visitBit_and_expr(Bit_and_exprContext ctx) {
		if (ctx.bit_and_expr() == null)
			return visitShift_expr(ctx.shift_expr());

		Expression left = visitBit_and_expr(ctx.bit_and_expr());
		Expression right = visitShift_expr(ctx.shift_expr());
		return new RustAndBitwiseExpression(currentCfg, locationOf(ctx), left, right);
	}

	@Override
	public Expression visitBit_xor_expr(Bit_xor_exprContext ctx) {
		if (ctx.bit_xor_expr() == null)
			return visitBit_and_expr(ctx.bit_and_expr());

		Expression left = visitBit_xor_expr(ctx.bit_xor_expr());
		Expression right = visitBit_and_expr(ctx.bit_and_expr());
		return new RustOrBitwiseExpression(currentCfg, locationOf(ctx), left, right);
	}

	@Override
	public Expression visitBit_or_expr(Bit_or_exprContext ctx) {
		if (ctx.bit_or_expr() == null)
			return visitBit_xor_expr(ctx.bit_xor_expr());

		Expression left = visitBit_or_expr(ctx.bit_or_expr());
		Expression right = visitBit_xor_expr(ctx.bit_xor_expr());
		return new RustOrBitwiseExpression(currentCfg, locationOf(ctx), left, right);
	}

	@Override
	public Expression visitCmp_expr(Cmp_exprContext ctx) {
		Expression left = visitBit_or_expr(ctx.bit_or_expr(0));

		if (ctx.getChildCount() > 1) {
			Expression right = visitBit_or_expr(ctx.bit_or_expr(1));

			switch (ctx.getChild(1).getText()) {
			case "==":
				return new RustEqualExpression(currentCfg, locationOf(ctx), left, right);
			case "!=":
				return new RustNotEqualExpression(currentCfg, locationOf(ctx), left, right);
			case "<":
				return new RustLessExpression(currentCfg, locationOf(ctx), left, right);
			case "<=":
				return new RustLessEqualExpression(currentCfg, locationOf(ctx), left, right);
			case ">":
				// Since the greater equal sign is split in the g4 grammar, a
				// check is necessary
				if (ctx.getChild(ctx.getChildCount() - 2).getText().equals("="))
					return new RustGreaterEqualExpression(currentCfg, locationOf(ctx), left, right);

				return new RustGreaterExpression(currentCfg, locationOf(ctx), left, right);
			}
		}

		return left;
	}

	@Override
	public Expression visitAnd_expr(And_exprContext ctx) {
		if (ctx.and_expr() == null)
			return visitCmp_expr(ctx.cmp_expr());

		Expression left = visitAnd_expr(ctx.and_expr());
		Expression right = visitCmp_expr(ctx.cmp_expr());
		return new RustOrExpression(currentCfg, locationOf(ctx), left, right);
	}

	@Override
	public Expression visitOr_expr(Or_exprContext ctx) {
		if (ctx.or_expr() == null)
			return visitAnd_expr(ctx.and_expr());

		Expression left = visitOr_expr(ctx.or_expr());
		Expression right = visitAnd_expr(ctx.and_expr());
		return new RustOrExpression(currentCfg, locationOf(ctx), left, right);
	}

	@Override
	public Expression visitRange_expr(Range_exprContext ctx) {
		if (ctx.children.size() == 1) { // First production
			return visitOr_expr(ctx.or_expr(0));

		} else if (ctx.children.size() == 3) { // Second full production
			Expression left = visitOr_expr(ctx.or_expr().get(0));
			Expression right = visitOr_expr(ctx.or_expr().get(1));

			return new RustRangeExpression(currentCfg, locationOf(ctx), left, right);

		} else { // Second (case with two members) and third production

			if (ctx.getChild(0).getText().equals("..")) { // Third production
				if (ctx.or_expr() != null) {
					// TODO The following here is to parse "..end" which is a
					// RangeTo type,
					// that does not have a Iterator implementation, but it is
					// used as slicing index.
					// https://doc.rust-lang.org/std/ops/struct.RangeTo.html
					// We should figure that out later
					return null;
				}

				// TODO The following here is to parse ".." which is a RangeFull
				// type,
				// that does not have a Iterator implementation, but it is used
				// as slicing index.
				// https://doc.rust-lang.org/std/ops/struct.RangeFull.html
				// We should figure that out later
				return null;

			} else { // Second (case with two members) production
				Expression left = visitOr_expr(ctx.or_expr().get(0));
				return new RustRangeFromExpression(currentCfg, locationOf(ctx), left);
			}
		}
	}

	@Override
	public Expression visitAssign_expr(Assign_exprContext ctx) {
		if (ctx.assign_expr() == null)
			return visitRange_expr(ctx.range_expr());

		Expression rangeExpr = visitRange_expr(ctx.range_expr());
		Expression right = visitAssign_expr(ctx.assign_expr());

		switch (ctx.getChild(1).getText()) {
		case "=":
			return new RustAssignment(currentCfg, locationOf(ctx), rangeExpr, right);
		case "*=":
			return new RustAssignment(currentCfg, locationOf(ctx), rangeExpr,
					new RustMulExpression(currentCfg, locationOf(ctx), rangeExpr, right));
		case "/=":
			return new RustAssignment(currentCfg, locationOf(ctx), rangeExpr,
					new RustDivExpression(currentCfg, locationOf(ctx), rangeExpr, right));
		case "%=":
			return new RustAssignment(currentCfg, locationOf(ctx), rangeExpr,
					new RustModExpression(currentCfg, locationOf(ctx), rangeExpr, right));
		case "+=":
			return new RustAssignment(currentCfg, locationOf(ctx), rangeExpr,
					new RustAddExpression(currentCfg, locationOf(ctx), rangeExpr, right));
		case "-=":
			return new RustAssignment(currentCfg, locationOf(ctx), rangeExpr,
					new RustSubExpression(currentCfg, locationOf(ctx), rangeExpr, right));
		case "<<=":
			return new RustAssignment(currentCfg, locationOf(ctx), rangeExpr,
					new RustLeftShiftExpression(currentCfg, locationOf(ctx), rangeExpr, right));
		case ">": // catches only ">" which is separated in the g4 grammar
			return new RustAssignment(currentCfg, locationOf(ctx), rangeExpr,
					new RustRightShiftExpression(currentCfg, locationOf(ctx), rangeExpr, right));
		case "&=":
			return new RustAssignment(currentCfg, locationOf(ctx), rangeExpr,
					new RustAndBitwiseExpression(currentCfg, locationOf(ctx), rangeExpr, right));
		case "^=":
			return new RustAssignment(currentCfg, locationOf(ctx), rangeExpr,
					new RustXorBitwiseExpression(currentCfg, locationOf(ctx), rangeExpr, right));
		default: // operator "|="
			return new RustAssignment(currentCfg, locationOf(ctx), rangeExpr,
					new RustOrBitwiseExpression(currentCfg, locationOf(ctx), rangeExpr, right));
		}
	}

	@Override
	public Expression visitPost_expr_no_struct(Post_expr_no_structContext ctx) {
		if (ctx.prim_expr_no_struct() != null) {
			return visitPrim_expr_no_struct(ctx.prim_expr_no_struct());
		}

		// TODO it is necessary to think about what this function returns in the
		// future
		// Pair<Statement, Statement> left =
		// visitPost_expr_no_struct(ctx.post_expr_no_struct());
		// Statement right = visitPost_expr_tail(ctx.post_expr_tail());
		//
		// currentCfg.addNode(right);
		//
		// currentCfg.addEdge(new SequentialEdge(left.getRight(), right));
		//
		// return Pair.of(left.getLeft(), right);

		return null;
	}

	@Override
	public Expression visitPre_expr_no_struct(Pre_expr_no_structContext ctx) {
		if (ctx.post_expr_no_struct() != null)
			return visitPost_expr_no_struct(ctx.post_expr_no_struct());

		Expression expr = visitPre_expr_no_struct(ctx.pre_expr_no_struct());

		if (ctx.expr_attrs() != null) {
			// TODO it is necessary to think about what this function returns in
			// the future
			// Pair<Statement, Statement> left =
			// visitExpr_attrs(ctx.expr_attrs());
			// currentCfg.addEdge(new SequentialEdge(left.getRight(),
			// expr.getLeft()));
			//
			// return Pair.of(left.getRight(), expr.getLeft());
			return null;
		}

		// TODO figure out later what to do with mutability
		boolean mutable = (ctx.getChild(1).getText().equals("mut") ? true : false);

		switch (ctx.getChild(0).getText()) {
		case "-":
			return new RustMinusExpression(currentCfg, locationOf(ctx), expr);
		case "!":
			return new RustNotExpression(currentCfg, locationOf(ctx), expr);
		case "&":
			// TODO figure out later what to do with mutability
			return new RustRefExpression(currentCfg, locationOf(ctx), expr);
		case "&&":
			// TODO figure out later what to do with mutability
			return new RustDoubleRefExpression(currentCfg, locationOf(ctx), expr);
		case "*":
			return new RustDerefExpression(currentCfg, locationOf(ctx), expr);
		case "box":
			// TODO figure out later what to do with boxes in this cases
			return new RustBoxExpression(currentCfg, locationOf(ctx), expr);
		default:
			// Preceding cases are exhaustive
			return null;
		}
	}

	@Override
	public Expression visitCast_expr_no_struct(Cast_expr_no_structContext ctx) {
		if (ctx.pre_expr_no_struct() != null)
			return visitPre_expr_no_struct(ctx.pre_expr_no_struct());

		Expression left = visitCast_expr_no_struct(ctx.cast_expr_no_struct());
		Type type = visitTy_sum(ctx.ty_sum());

		return new RustCastExpression(currentCfg, locationOf(ctx), type, left);
	}

	@Override
	public Expression visitMul_expr_no_struct(Mul_expr_no_structContext ctx) {
		if (ctx.mul_expr_no_struct() == null)
			return visitCast_expr_no_struct(ctx.cast_expr_no_struct());

		if (ctx.getChild(1).getText().equals("*")) {
			Expression left = visitMul_expr_no_struct(ctx.mul_expr_no_struct());
			Expression right = visitCast_expr_no_struct(ctx.cast_expr_no_struct());

			return new RustMulExpression(currentCfg, locationOf(ctx), left, right);
		} else if (ctx.getChild(1).getText().equals("/")) {
			Expression left = visitMul_expr_no_struct(ctx.mul_expr_no_struct());
			Expression right = visitCast_expr_no_struct(ctx.cast_expr_no_struct());

			return new RustDivExpression(currentCfg, locationOf(ctx), left, right);
		} else {
			Expression left = visitMul_expr_no_struct(ctx.mul_expr_no_struct());
			Expression right = visitCast_expr_no_struct(ctx.cast_expr_no_struct());

			return new RustModExpression(currentCfg, locationOf(ctx), left, right);
		}
	}

	@Override
	public Expression visitAdd_expr_no_struct(Add_expr_no_structContext ctx) {
		if (ctx.add_expr_no_struct() == null)
			return visitMul_expr_no_struct(ctx.mul_expr_no_struct());

		if (ctx.getChild(1).getText().equals("+")) {
			Expression left = visitAdd_expr_no_struct(ctx.add_expr_no_struct());
			Expression right = visitMul_expr_no_struct(ctx.mul_expr_no_struct());

			return new RustAddExpression(currentCfg, locationOf(ctx), left, right);
		} else {
			Expression left = visitAdd_expr_no_struct(ctx.add_expr_no_struct());
			Expression right = visitMul_expr_no_struct(ctx.mul_expr_no_struct());

			return new RustSubExpression(currentCfg, locationOf(ctx), left, right);
		}
	}

	@Override
	public Expression visitShift_expr_no_struct(Shift_expr_no_structContext ctx) {
		if (ctx.shift_expr_no_struct() == null)
			return visitAdd_expr_no_struct(ctx.add_expr_no_struct());

		if (ctx.getChild(1).getText().equals("<")) {
			Expression left = visitShift_expr_no_struct(ctx.shift_expr_no_struct());
			Expression right = visitAdd_expr_no_struct(ctx.add_expr_no_struct());

			return new RustLeftShiftExpression(currentCfg, locationOf(ctx), left, right);
		} else {
			Expression left = visitShift_expr_no_struct(ctx.shift_expr_no_struct());
			Expression right = visitAdd_expr_no_struct(ctx.add_expr_no_struct());

			return new RustRightShiftExpression(currentCfg, locationOf(ctx), left, right);
		}
	}

	@Override
	public Expression visitBit_and_expr_no_struct(Bit_and_expr_no_structContext ctx) {
		if (ctx.bit_and_expr_no_struct() == null)
			return visitShift_expr_no_struct(ctx.shift_expr_no_struct());

		Expression left = visitBit_and_expr_no_struct(ctx.bit_and_expr_no_struct());
		Expression right = visitShift_expr_no_struct(ctx.shift_expr_no_struct());

		return new RustAndBitwiseExpression(currentCfg, locationOf(ctx), left, right);
	}

	@Override
	public Expression visitBit_xor_expr_no_struct(Bit_xor_expr_no_structContext ctx) {
		if (ctx.bit_xor_expr_no_struct() == null)
			return visitBit_and_expr_no_struct(ctx.bit_and_expr_no_struct());

		Expression left = visitBit_xor_expr_no_struct(ctx.bit_xor_expr_no_struct());
		Expression right = visitBit_and_expr_no_struct(ctx.bit_and_expr_no_struct());

		return new RustXorBitwiseExpression(currentCfg, locationOf(ctx), left, right);
	}

	@Override
	public Expression visitBit_or_expr_no_struct(Bit_or_expr_no_structContext ctx) {
		if (ctx.bit_or_expr_no_struct() == null)
			return visitBit_xor_expr_no_struct(ctx.bit_xor_expr_no_struct());

		Expression left = visitBit_or_expr_no_struct(ctx.bit_or_expr_no_struct());
		Expression right = visitBit_xor_expr_no_struct(ctx.bit_xor_expr_no_struct());

		return new RustOrBitwiseExpression(currentCfg, locationOf(ctx), left, right);
	}

	@Override
	public Expression visitCmp_expr_no_struct(Cmp_expr_no_structContext ctx) {
		Expression left = visitBit_or_expr_no_struct(ctx.bit_or_expr_no_struct(0));

		if (ctx.getChildCount() > 1) {
			Expression right = visitBit_or_expr_no_struct(ctx.bit_or_expr_no_struct(1));

			switch (ctx.getChild(1).getText()) {
			case "==":
				return new RustEqualExpression(currentCfg, locationOf(ctx), left, right);
			case "!=":
				return new RustNotEqualExpression(currentCfg, locationOf(ctx), left, right);
			case "<":
				return new RustLessExpression(currentCfg, locationOf(ctx), left, right);
			case "<=":
				return new RustLessEqualExpression(currentCfg, locationOf(ctx), left, right);
			case ">":
				// Since the greater equal sign is split in the g4 grammar, a
				// check is necessary
				if (ctx.getChild(ctx.getChildCount() - 2).getText().equals("="))
					return new RustGreaterEqualExpression(currentCfg, locationOf(ctx), left, right);

				return new RustGreaterExpression(currentCfg, locationOf(ctx), left, right);
			}
		}

		return left;
	}

	@Override
	public Expression visitAnd_expr_no_struct(And_expr_no_structContext ctx) {
		if (ctx.and_expr_no_struct() != null) {
			Expression and = visitAnd_expr_no_struct(ctx.and_expr_no_struct());
			Expression cmp = visitCmp_expr_no_struct(ctx.cmp_expr_no_struct());

			return new RustAndExpression(currentCfg, locationOf(ctx), and, cmp);
		}

		return visitCmp_expr_no_struct(ctx.cmp_expr_no_struct());
	}

	@Override
	public Expression visitOr_expr_no_struct(Or_expr_no_structContext ctx) {
		if (ctx.or_expr_no_struct() != null) {
			Expression or = visitOr_expr_no_struct(ctx.or_expr_no_struct());
			Expression and = visitAnd_expr_no_struct(ctx.and_expr_no_struct());

			return new RustOrExpression(currentCfg, locationOf(ctx), or, and);
		}

		return visitAnd_expr_no_struct(ctx.and_expr_no_struct());
	}

	@Override
	public Expression visitRange_expr_no_struct(Range_expr_no_structContext ctx) {
		if (ctx.children.size() == 1) { // First production
			return visitOr_expr_no_struct(ctx.or_expr_no_struct(0));

		} else if (ctx.children.size() == 3) { // Second full production
			Expression left = visitOr_expr_no_struct(ctx.or_expr_no_struct().get(0));
			Expression right = visitOr_expr_no_struct(ctx.or_expr_no_struct().get(1));

			return new RustRangeExpression(currentCfg, locationOf(ctx), left, right);

		} else { // Second (case with two members) and third production

			if (ctx.getChild(0).getText().equals("..")) { // Third production
				if (ctx.or_expr_no_struct() != null) {
					// TODO The following here is to parse "..end" which is a
					// RangeTo type,
					// that does not have a Iterator implementation, but it is
					// used as slicing index.
					// https://doc.rust-lang.org/std/ops/struct.RangeTo.html
					// We should figure that out later
					return null;
				}

				// TODO The following here is to parse ".." which is a RangeFull
				// type,
				// that does not have a Iterator implementation, but it is used
				// as slicing index.
				// https://doc.rust-lang.org/std/ops/struct.RangeFull.html
				// We should figure that out later
				return null;

			} else { // Second (case with two members) production
				Expression left = visitOr_expr_no_struct(ctx.or_expr_no_struct().get(0));
				return new RustRangeFromExpression(currentCfg, locationOf(ctx), left);
			}
		}
	}

	@Override
	public Expression visitAssign_expr_no_struct(Assign_expr_no_structContext ctx) {
		Expression rangeExpr = visitRange_expr_no_struct(ctx.range_expr_no_struct());

		if (ctx.assign_expr_no_struct() != null) {
			Expression right = visitAssign_expr_no_struct(ctx.assign_expr_no_struct());

			switch (ctx.getChild(1).getText()) {
			case "=":
				return new RustAssignment(currentCfg, locationOf(ctx), rangeExpr, right);
			case "*=":
				return new RustAssignment(currentCfg, locationOf(ctx), rangeExpr,
						new RustMulExpression(currentCfg, locationOf(ctx), rangeExpr, right));
			case "/=":
				return new RustAssignment(currentCfg, locationOf(ctx), rangeExpr,
						new RustDivExpression(currentCfg, locationOf(ctx), rangeExpr, right));
			case "%=":
				return new RustAssignment(currentCfg, locationOf(ctx), rangeExpr,
						new RustModExpression(currentCfg, locationOf(ctx), rangeExpr, right));
			case "+=":
				return new RustAssignment(currentCfg, locationOf(ctx), rangeExpr,
						new RustAddExpression(currentCfg, locationOf(ctx), rangeExpr, right));
			case "<<=":
				return new RustAssignment(currentCfg, locationOf(ctx), rangeExpr,
						new RustLeftShiftExpression(currentCfg, locationOf(ctx), rangeExpr, right));
			case ">":
				return new RustAssignment(currentCfg, locationOf(ctx), rangeExpr,
						new RustRightShiftExpression(currentCfg, locationOf(ctx), rangeExpr, right));
			case "&=":
				return new RustAssignment(currentCfg, locationOf(ctx), rangeExpr,
						new RustAndBitwiseExpression(currentCfg, locationOf(ctx), rangeExpr, right));
			case "^=":
				return new RustAssignment(currentCfg, locationOf(ctx), rangeExpr,
						new RustXorBitwiseExpression(currentCfg, locationOf(ctx), rangeExpr, right));
			case "|=":
				return new RustAssignment(currentCfg, locationOf(ctx), rangeExpr,
						new RustOrBitwiseExpression(currentCfg, locationOf(ctx), rangeExpr, right));
			}
		}

		return rangeExpr;
	}

	@Override
	public Expression visitIdent(IdentContext ctx) {
		// TODO: everything is mapped as a variable reference, included auto,
		// default, union
		return new VariableRef(currentCfg, locationOf(ctx), ctx.getText());
	}

	@Override
	public Object visitAny_ident(Any_identContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

}
