package it.unipr.frontend;

import static it.unipr.frontend.RustFrontendUtilities.locationOf;

import it.unipr.cfg.expression.RustAccessMemberExpression;
import it.unipr.cfg.expression.RustArrayAccess;
import it.unipr.cfg.expression.RustBoxExpression;
import it.unipr.cfg.expression.RustCastExpression;
import it.unipr.cfg.expression.RustDerefExpression;
import it.unipr.cfg.expression.RustDoubleRefExpression;
import it.unipr.cfg.expression.RustRangeExpression;
import it.unipr.cfg.expression.RustRangeFromExpression;
import it.unipr.cfg.expression.RustRefExpression;
import it.unipr.cfg.expression.RustReturnExpression;
import it.unipr.cfg.expression.RustTupleAccess;
import it.unipr.cfg.expression.RustVariableRef;
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
import it.unipr.cfg.expression.literal.RustStructLiteral;
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
import it.unipr.cfg.type.composite.RustStructType;
import it.unipr.cfg.utils.RustAccessResolver;
import it.unipr.cfg.utils.RustArrayAccessKeeper;
import it.unipr.cfg.utils.RustAttributeAccessKeeper;
import it.unipr.cfg.utils.RustFunctionCallKeeper;
import it.unipr.cfg.utils.RustMethodKeeper;
import it.unipr.cfg.utils.RustTupleAccessKeeper;
import it.unipr.rust.antlr.RustBaseVisitor;
import it.unipr.rust.antlr.RustParser.*;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CFGDescriptor;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.edge.Edge;
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
import it.unive.lisa.program.cfg.statement.global.AccessGlobal;
import it.unive.lisa.program.cfg.statement.literal.NullLiteral;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import it.unive.lisa.util.datastructures.graph.AdjacencyMatrix;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
	 * Yields the current file path.
	 * 
	 * @return the current file path
	 */
	public String getFilePath() {
		return filePath;
	}

	private void switchLeafNodes(Statement oldNode, Statement newNode) {
		AdjacencyMatrix<Statement, Edge, CFG> adj = currentCfg.getAdjacencyMatrix();

		for (Statement predecessor : adj.predecessorsOf(oldNode)) {
			Edge e = adj.getEdgeConnecting(predecessor, oldNode);

			Edge newEdge = new SequentialEdge(predecessor, newNode);
			if (e instanceof TrueEdge)
				newEdge = new TrueEdge(predecessor, newNode);
			else if (e instanceof FalseEdge)
				newEdge = new FalseEdge(predecessor, newNode);

			adj.removeEdge(e);
			currentCfg.addEdge(newEdge);
		}

		adj.removeNode(oldNode);
	}

	@Override
	public CFG visitFn_decl(Fn_declContext ctx) {
		String fnName = getFnName(ctx.fn_head());

		Type returnType = RustUnitType.getInstance();
		if (ctx.fn_rtype() != null)
			returnType = new RustTypeVisitor(filePath, unit).visitFn_rtype(ctx.fn_rtype());

		CFGDescriptor cfgDesc = new CFGDescriptor(locationOf(ctx, filePath), unit, false, fnName, returnType,
				new Parameter[0]);
		currentCfg = new CFG(cfgDesc);

		Pair<Statement, Statement> block = visitBlock_with_inner_attrs(ctx.block_with_inner_attrs());
		currentCfg.getEntrypoints().add(block.getLeft());

		Collection<Statement> nodes = currentCfg.getNodes();

		if (returnType instanceof RustUnitType) {
			Ret ret = new Ret(currentCfg, locationOf(ctx, filePath));

			if (currentCfg.getAllExitpoints().isEmpty()) {
				Set<Statement> exitNodes = nodes
						.stream()
						.filter(n -> currentCfg.getAdjacencyMatrix().followersOf(n).isEmpty())
						.collect(Collectors.toSet());

				for (Statement exit : exitNodes) {
					currentCfg.addNode(ret);
					currentCfg.addEdge(new SequentialEdge(exit, ret));
				}
			} else
				currentCfg.addNode(ret);

			// Substitute return with ret nodes
			for (Statement node : nodes) {
				if (node instanceof RustReturnExpression) {
					NoOp noOp = new NoOp(currentCfg, locationOf(ctx, filePath));
					currentCfg.addNode(noOp);

					switchLeafNodes(node, noOp);
					currentCfg.addEdge(new SequentialEdge(noOp, ret));
				}
			}
		}
		// Substitute inner RustExplicitReturn with return statements
		else {
			// Add return to exit node if it's the only stmt
			if (currentCfg.getNodes().size() == 1) {
				Statement onlyNode = nodes.stream().findFirst().get();

				NoOp noOp = new NoOp(currentCfg, locationOf(ctx, filePath));
				currentCfg.addNode(noOp, true);
				currentCfg.getEntrypoints().remove(onlyNode);

				currentCfg.addEdge(new SequentialEdge(noOp, onlyNode));
				currentCfg.getAllExitpoints().add(onlyNode);
			}

			for (Statement stmt : nodes)
				if (stmt instanceof RustReturnExpression) {
					Expression value = ((RustReturnExpression) stmt).getSubExpression();

					Return ret = new Return(currentCfg, locationOf(ctx, filePath), value);
					currentCfg.addNode(ret);

					switchLeafNodes(stmt, ret);
				}
		}

		currentCfg.simplify();
		return currentCfg;
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

		Type returnType = RustUnitType.getInstance();
		if (ctx.fn_rtype() != null)
			returnType = new RustTypeVisitor(filePath, unit).visitFn_rtype(ctx.fn_rtype());

		CFGDescriptor cfgDesc = new CFGDescriptor(locationOf(ctx, filePath), unit, false, methodName, returnType,
				new Parameter[0]);
		currentCfg = new CFG(cfgDesc);
		NoOp initPoint = new NoOp(currentCfg, locationOf(ctx, filePath));
		currentCfg.addNode(initPoint, true);

		Pair<Statement, Statement> block = visitBlock_with_inner_attrs(ctx.block_with_inner_attrs());
		currentCfg.getEntrypoints().add(block.getLeft());

		Collection<Statement> nodes = currentCfg.getNodes();

		// Substitute exit points wit
		if (returnType instanceof RustUnitType) {
			Ret ret = new Ret(currentCfg, locationOf(ctx, filePath));

			// Add possible missing ret as final instruction
			if (currentCfg.getAllExitpoints().isEmpty()) {
				Set<Statement> exitNodes = nodes
						.stream()
						.filter(n -> currentCfg.getAdjacencyMatrix().followersOf(n).isEmpty())
						.collect(Collectors.toSet());

				for (Statement exit : exitNodes) {
					currentCfg.addNode(ret);
					currentCfg.addEdge(new SequentialEdge(exit, ret));
				}
			} else
				currentCfg.addNode(ret);

			// Substitute return with ret nodes
			for (Statement node : nodes) {
				if (node instanceof RustReturnExpression) {
					NoOp noOp = new NoOp(currentCfg, locationOf(ctx, filePath));
					currentCfg.addNode(noOp);

					switchLeafNodes(node, noOp);
					currentCfg.addEdge(new SequentialEdge(noOp, ret));
				}
			}
		}
		// Substitute inner RustExplicitReturn with return statements
		else {
			List<Statement> nonNoOpNodes = nodes.stream().filter(n -> !(n instanceof NoOp))
					.collect(Collectors.toList());
			if (nonNoOpNodes.size() == 1) {
				AdjacencyMatrix<Statement, Edge, CFG> adj = currentCfg.getAdjacencyMatrix();
				Statement node = nonNoOpNodes.get(0);
				adj.getEdges().forEach(e -> adj.removeEdge(e));
				adj.getNodes().forEach(n -> adj.removeNode(n));
				currentCfg.addNode(node);
			}

			// Add return to exit node if it's the only stmt
			if (currentCfg.getNodes().size() == 1) {
				Statement onlyNode = nodes.stream().findFirst().get();

				NoOp noOp = new NoOp(currentCfg, locationOf(ctx, filePath));
				currentCfg.addNode(noOp, true);
				currentCfg.getEntrypoints().remove(onlyNode);

				currentCfg.addEdge(new SequentialEdge(noOp, onlyNode));
				currentCfg.getAllExitpoints().add(onlyNode);
			}

			for (Statement stmt : nodes)
				if (stmt instanceof RustReturnExpression) {
					Expression value = ((RustReturnExpression) stmt).getSubExpression();

					Return ret = new Return(currentCfg, locationOf(ctx, filePath), value);
					currentCfg.addNode(ret);

					switchLeafNodes(stmt, ret);
				}
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

		return new Parameter(locationOf(ctx, filePath), expression.toString(), type);
	}

	@Override
	public Type visitParam_ty(Param_tyContext ctx) {
		// TODO skipping second production
		return new RustTypeVisitor(filePath, unit).visitTy_sum(ctx.ty_sum());
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

		Type type = new RustTypeVisitor(filePath, unit).visitTy_sum(ctx.ty_sum());

		return new Parameter(locationOf(ctx, filePath), "self", type);
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

		List<CFG> impls = new ArrayList<>();
		for (Impl_itemContext fdCtx : ctx.impl_item()) {
			CFG visitedCfg = visitImpl_item(fdCtx);
			impls.add(visitedCfg);
		}

		return impls;
	}

	@Override
	public Type visitImpl_what(Impl_whatContext ctx) {
		// TODO Skipping trait implementation for now and parsing only the last
		// rule
		return new RustTypeVisitor(filePath, unit).visitTy_sum(ctx.ty_sum(0));
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
		if (ctx.path_parent() != null) {
			Expression parent = visitPath_parent(ctx.path_parent());
			Expression child = visitPath_segment_no_super(ctx.path_segment_no_super());

			if (child instanceof RustVariableRef) {
				Global global = new Global(locationOf(ctx, filePath), child.toString());
				// TODO check if the toString is enough or it need something
				// else
				Unit unit = program.getUnit(parent.toString());
				return new AccessGlobal(currentCfg, locationOf(ctx, filePath), unit, global);
			}

			throw new UnsupportedOperationException(ctx.getText());
		}

		return visitPath_segment_no_super(ctx.path_segment_no_super());
	}

	@Override
	public Expression visitPath_parent(Path_parentContext ctx) {
		// TODO skipping "'<' ty_sum as_trait? '>'" production
		if (ctx.getChild(0).getText().equals("self")) {
			return new RustVariableRef(currentCfg, locationOf(ctx, filePath), "self", false);
		} else if (ctx.path_parent() == null) {
			return visitPath_segment(ctx.path_segment());
		}

		Expression child = visitPath_segment(ctx.path_segment());
		Expression parent = visitPath_parent(ctx.path_parent());

		if (child instanceof RustVariableRef) {
			Global global = new Global(locationOf(ctx, filePath), child.toString());
			// TODO check if the toString is enough or it need something else
			Unit unit = program.getUnit(parent.toString());
			return new AccessGlobal(currentCfg, locationOf(ctx, filePath), unit, global);
		}

		throw new UnsupportedOperationException(ctx.getText());
	}

	@Override
	public Object visitAs_trait(As_traitContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression visitPath_segment(Path_segmentContext ctx) {
		if (ctx.path_segment_no_super() != null)
			return visitPath_segment_no_super(ctx.path_segment_no_super());

		return new RustVariableRef(currentCfg, locationOf(ctx, filePath), "super", false);
	}

	@Override
	public Expression visitPath_segment_no_super(Path_segment_no_superContext ctx) {
		// TODO: skipping ('::' ty_args)?
		return visitSimple_path_segment(ctx.simple_path_segment());
	}

	@Override
	public Expression visitSimple_path_segment(Simple_path_segmentContext ctx) {
		if (ctx.getChild(0).getText().equals("Self"))
			return new RustVariableRef(currentCfg, locationOf(ctx, filePath), "Self", false);

		return new RustVariableRef(currentCfg, locationOf(ctx, filePath), ctx.ident().getText(), false);
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
		return new RustTypeVisitor(filePath, unit).visitTy(ctx);
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

		String name = ctx.ident().getText();

		if (ctx.pat() != null) {
			// TODO Ignoring the meaning of ('@' pat)? part for now
			return visitPat(ctx.pat());
		}

		return new RustVariableRef(currentCfg, locationOf(ctx, filePath), name, true);
	}

	@Override
	public Expression visitPat_no_mut(Pat_no_mutContext ctx) {
		if (ctx.pat_lit() != null) {
			return visitPat_lit(ctx.pat_lit());
		}

		if (ctx.ident() != null) {
			String name = ctx.ident().getText();
			Expression var = new RustVariableRef(currentCfg, locationOf(ctx, filePath), name, false);

			if (ctx.pat() != null) {
				// TODO skipping the meaning of ('@' pat)?
				Expression pat = visitPat(ctx.pat());
			}

			if (ctx.getChild(0) != null && ctx.getChild(0).getText().equals("ref"))
				if (ctx.getChild(1) != null && ctx.getChild(1).getText().equals("mut"))
					return new RustRefExpression(
							currentCfg,
							locationOf(ctx, filePath),
							new RustVariableRef(currentCfg, locationOf(ctx, filePath), name, true),
							false);
				else
					return new RustRefExpression(
							currentCfg,
							locationOf(ctx, filePath),
							new RustVariableRef(currentCfg, locationOf(ctx, filePath), name, false),
							false);
			else
				return var;
		}

		if (ctx.pat() != null) {
			if (ctx.pat_fields() != null) {
				Expression name = visitPath(ctx.path());
				List<Expression> values = visitPat_fields(ctx.pat_fields());

				Type[] types = values
						.stream()
						.map(p -> p.getStaticType())
						.collect(Collectors.toList())
						.toArray(new Type[0]);

				RustStructType struct = RustStructType.lookup(name.toString(), unit);

				return new RustStructLiteral(
						currentCfg,
						locationOf(ctx, filePath),
						struct,
						values.toArray(new Expression[0]));
			}

			// The grammar says there is a bug here, skipping
			return null;
		}

		switch (ctx.getChild(0).getText()) {
		case "_":
			return new VariableRef(currentCfg, locationOf(ctx, filePath), "_");
		case "(":
			return visitPat_list_with_dots(ctx.pat_list_with_dots());
		case "[":
			return visitPat_elt_list(ctx.pat_elt_list());
		case "&":
			if (ctx.getChild(1).getText().equals("mut"))
				return new RustRefExpression(currentCfg, locationOf(ctx, filePath), visitPat(ctx.pat()), true);

			return visitPat_no_mut(ctx.pat_no_mut());
		case "&&":
			if (ctx.getChild(1).getText().equals("mut"))
				return new RustDoubleRefExpression(currentCfg, locationOf(ctx, filePath),
						visitPat(ctx.pat()), true);

			return new RustDoubleRefExpression(currentCfg, locationOf(ctx, filePath),
					visitPat_no_mut(ctx.pat_no_mut()), false);
		case "box":
			return new RustBoxExpression(currentCfg, locationOf(ctx, filePath), visitPat(ctx.pat()));
		default:
			// TODO need to implement the other cases:
			// pat_no_mut
			// : pat_range_end '...' pat_range_end
			// | pat_range_end '..' pat_range_end
			// | path macro_tail
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
			return new RustMinusExpression(currentCfg, locationOf(ctx, filePath), lit);

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
	public List<Expression> visitPat_fields(Pat_fieldsContext ctx) {
		// TODO Skipping ".." first production
		// TODO Skipping all the terminal symbols in the second production

		List<Expression> result = new ArrayList<>();
		for (Pat_fieldContext pfCtx : ctx.pat_field()) {
			result.add(visitPat_field(pfCtx));
		}

		return result;
	}

	@Override
	public Expression visitPat_field(Pat_fieldContext ctx) {
		String name = ctx.ident().getText();
		Expression value = null;

		if (ctx.pat() != null)
			value = visitPat(ctx.pat());

		if (ctx.getChild(3) != null && ctx.getChild(3).getText().equals("mut"))
			value = new RustVariableRef(currentCfg, locationOf(ctx, filePath), name, true);
		else
			value = new RustVariableRef(currentCfg, locationOf(ctx, filePath), name, false);

		// The "ref" on the lhs of an expression is equivalent to a "&" on the
		// rhs
		// https://doc.rust-lang.org/rust-by-example/scope/borrow/ref.html
		if (ctx.getChild(2) != null && ctx.getChild(2).getText().equals("ref"))
			value = new RustRefExpression(currentCfg, locationOf(ctx, filePath), value, false);

		if (ctx.getChild(1) != null && ctx.getChild(1).getText().equals("box"))
			value = new RustBoxExpression(currentCfg, locationOf(ctx, filePath), value);

		return value;
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
		Statement entryStmt = null;
		Statement lastStmt = null;

		for (StmtContext stmt : ctx.stmt()) {
			// Note: since we are not sure what to return from visitStmt, we
			// are sure that this function returns a pair of Statement
			@SuppressWarnings("unchecked")
			Pair<Statement, Statement> currentStmt = (Pair<Statement, Statement>) visitStmt(stmt);

			if (lastStmt != null)
				currentCfg.addEdge(new SequentialEdge(lastStmt, currentStmt.getLeft()));
			else
				entryStmt = currentStmt.getLeft();

			lastStmt = currentStmt.getRight();
		}

		if (ctx.expr() != null) {
			Expression expr = visitExpr(ctx.expr());
			RustReturnExpression ret = new RustReturnExpression(currentCfg, locationOf(ctx, filePath), expr);
			currentCfg.addNode(ret);

			if (entryStmt == null)
				return Pair.of(ret, ret);

			currentCfg.addEdge(new SequentialEdge(lastStmt, ret));
		}

		if (ctx.expr() == null && ctx.stmt().size() == 0) {
			NoOp noOp = new NoOp(currentCfg, locationOf(ctx, filePath));
			currentCfg.addNode(noOp);

			entryStmt = noOp;
			lastStmt = noOp;
		}

		return Pair.of(entryStmt, lastStmt);
	}

	@Override
	public Pair<Statement, Statement> visitBlock_with_inner_attrs(Block_with_inner_attrsContext ctx) {
		// TODO: skipping inner attributes for the moment
		Statement entryStmt = null;
		Statement lastStmt = null;

		for (StmtContext stmt : ctx.stmt()) {
			// Note: since we are not sure what to return from visitStmt, we are
			// sure that this function returns a pair of Statement
			@SuppressWarnings("unchecked")
			Pair<Statement, Statement> currentStmt = (Pair<Statement, Statement>) visitStmt(stmt);

			if (lastStmt != null)
				currentCfg.addEdge(new SequentialEdge(lastStmt, currentStmt.getLeft()));
			else
				entryStmt = currentStmt.getLeft();

			lastStmt = currentStmt.getRight();
		}

		// This expr is the one of return from a function
		if (ctx.expr() != null) {
			Expression expr = visitExpr(ctx.expr());
			RustReturnExpression ret = new RustReturnExpression(currentCfg, locationOf(ctx, filePath), expr);
			currentCfg.addNode(ret);

			if (entryStmt == null) {
				return Pair.of(ret, ret);
			}

			currentCfg.addEdge(new SequentialEdge(lastStmt, ret));
		}

		if (ctx.expr() == null && ctx.stmt().size() == 0) {
			NoOp noOp = new NoOp(currentCfg, locationOf(ctx, filePath));
			currentCfg.addNode(noOp);

			entryStmt = noOp;
			lastStmt = noOp;
		}

		return Pair.of(entryStmt, lastStmt);
	}

	@Override
	public Object visitStmt(StmtContext ctx) {
		// TODO I am not sure on what to return here exactly. So for now this is
		// implemented as returning an Object and a (safe) cast as needed

		if (ctx.getText().equals(";")) {
			NoOp noOp = new NoOp(currentCfg, locationOf(ctx, filePath));

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
			Expression lhs = visitPat(ctx.pat());

			Type type = (ctx.ty() == null ? Untyped.INSTANCE : visitTy(ctx.ty()));

			// TODO do not take into account the attr part for now
			if (ctx.expr() != null) {
				Expression rhs = visitExpr(ctx.expr());

				VariableRef var = new VariableRef(currentCfg, locationOf(ctx, filePath), lhs.toString(), type);

				RustLetAssignment assigment = new RustLetAssignment(currentCfg, locationOf(ctx, filePath), type, var,
						rhs);
				currentCfg.addNode(assigment);

				return Pair.of(assigment, assigment);
			}

			VariableRef var = new VariableRef(currentCfg, locationOf(ctx, filePath), lhs.toString(), type);
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
			NoOp noOp = new NoOp(currentCfg, locationOf(ctx, filePath));
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

			NoOp noOp = new NoOp(currentCfg, locationOf(ctx, filePath));
			currentCfg.addNode(noOp);

			firstStmt = guard;
			currentCfg.addEdge(new TrueEdge(guard, body.getLeft()));
			currentCfg.addEdge(new FalseEdge(guard, noOp));
			currentCfg.addEdge(new SequentialEdge(body.getRight(), guard));
			lastStmt = noOp;

		} else if ((loop_label == null && ctx.children.get(0).getText().equals("for"))
				|| ctx.children.get(1).getText().equals("for")) {

			// Note that this is enforced by the semantics to be a (list of)
			// identifiers.
			// TODO For now we can assume this is just a single identifier.
			String name = visitPat(ctx.pat()).toString();
			VariableRef forVariable = new RustVariableRef(currentCfg, locationOf(ctx, filePath), name, true);

			Expression range = visitExpr_no_struct(ctx.expr_no_struct());
			Pair<Statement, Statement> body = visitBlock_with_inner_attrs(ctx.block_with_inner_attrs());

			VariableRef fresh = new RustVariableRef(currentCfg, locationOf(ctx, filePath), "RUSTLISA_FRESH", false);
			Expression freshAssignment = new RustLetAssignment(currentCfg, locationOf(ctx, filePath), Untyped.INSTANCE,
					fresh,
					range);
			currentCfg.addNode(freshAssignment);

			UnresolvedCall nextCall = new UnresolvedCall(currentCfg, locationOf(ctx, filePath),
					RustFrontend.PARAMETER_ASSIGN_STRATEGY, RustFrontend.METHOD_MATCHING_STRATEGY,
					RustFrontend.HIERARCY_TRAVERSAL_STRATEGY, CallType.INSTANCE, fresh.getName(), "next",
					RustFrontend.EVALUATION_ORDER, Untyped.INSTANCE, new Expression[0]);

			Expression forVarAssignment = new RustLetAssignment(currentCfg, locationOf(ctx, filePath), Untyped.INSTANCE,
					forVariable,
					nextCall);
			// TODO Keep in mind that this is also a function
			// call to pat.next() which
			// returns a std::ops::Option which is Some(n) if n
			// is the next iterator in the
			// sequence and None otherwise.

			currentCfg.addNode(forVarAssignment);
			currentCfg.addEdge(new SequentialEdge(freshAssignment, forVarAssignment));

			// TODO NullLiteral here is to represent the None type, change this
			// in the future
			Expression guard = new RustNotEqualExpression(currentCfg, locationOf(ctx, filePath), forVariable,
					new NullLiteral(currentCfg, locationOf(ctx, filePath)));
			currentCfg.addNode(guard);

			currentCfg.addEdge(new SequentialEdge(forVarAssignment, guard));

			NoOp noOp = new NoOp(currentCfg, locationOf(ctx, filePath));
			currentCfg.addNode(noOp);

			currentCfg.addEdge(new TrueEdge(guard, body.getLeft()));
			currentCfg.addEdge(new FalseEdge(guard, noOp));

			Expression increment = new RustAssignment(currentCfg, locationOf(ctx, filePath), forVariable, nextCall);
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
		if (ctx.prim_expr_no_struct() == null) {
			// TODO skipping expr_inner_attrs? part
			Expression path = visitPath(ctx.path());
			if (ctx.fields() != null) {
				List<Pair<Expression, Expression>> fields = visitFields(ctx.fields());

				RustStructType structType = RustStructType.lookup(path.toString(), unit);

				return new RustStructLiteral(
						currentCfg,
						locationOf(ctx, filePath),
						structType,
						fields.stream()
								.map(e -> e.getRight())
								.collect(Collectors.toList())
								.toArray(new Expression[0]));
			}
		}
		return visitPrim_expr_no_struct(ctx.prim_expr_no_struct());
	}

	@Override
	public Expression visitPrim_expr_no_struct(Prim_expr_no_structContext ctx) {
		// TODO remaining production to parse:
		// | 'move'? closure_params closure_tail
		// | blocky_expr
		// | 'break' lifetime_or_expr?
		// | 'continue' Lifetime?
		// | 'return' expr?

		if (ctx.getChild(0).getText().equals("(")) {
			// TODO Ignoring expr_inner_attrs? part

			if (ctx.expr().get(0) != null) {
				Expression expr = visitExpr(ctx.expr(0));

				if (ctx.expr_list() != null) {
					List<Expression> exprs = visitExpr_list(ctx.expr_list());
					exprs.add(0, expr);

					return new RustTupleLiteral(
							currentCfg,
							locationOf(ctx, filePath),
							exprs.stream()
									.map(e -> e.getStaticType())
									.collect(Collectors.toList())
									.toArray(new RustType[0]),
							exprs.toArray(new Expression[0]));
				}

				return expr;
			}

			return new RustUnitLiteral(currentCfg, locationOf(ctx, filePath));
		} else if (ctx.getChild(0).getText().equals("[")) {
			// TODO Ignoring expr_inner_attrs? part

			if (ctx.expr_list() != null) {
				List<Expression> exprs = visitExpr_list(ctx.expr_list());
				return new RustArrayLiteral(currentCfg, locationOf(ctx, filePath), Untyped.INSTANCE,
						exprs.toArray(new Expression[0]));

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

				return new RustArrayLiteral(currentCfg, locationOf(ctx, filePath), Untyped.INSTANCE, exprs);
			}

			return new RustArrayLiteral(currentCfg, locationOf(ctx, filePath), Untyped.INSTANCE, new Expression[0]);

		} else if (ctx.getChild(0).getText().equals("self")) {
			return new RustVariableRef(currentCfg, locationOf(ctx, filePath), "self", false);
		} else if (ctx.getChild(0).getText().equals("return")) {
			Expression returnValue = new RustUnitLiteral(currentCfg, locationOf(ctx, filePath));
			if (ctx.expr(0) != null)
				returnValue = visitExpr(ctx.expr(0));

			return new RustReturnExpression(currentCfg, locationOf(ctx, filePath), returnValue);
		} else if (ctx.blocky_expr() != null) {
			// TODO watch out for expression and statements
			// return visitBlocky_expr(ctx.blocky_expr());
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
			return new RustBoolean(currentCfg, locationOf(ctx, filePath), true);
		else if (ctx.getText().equals("false"))
			return new RustBoolean(currentCfg, locationOf(ctx, filePath), false);
		else if (ctx.BareIntLit() != null)
			return new RustInteger(currentCfg, locationOf(ctx, filePath), Integer.parseInt(ctx.getText()));
		else if (ctx.FloatLit() != null)
			return new RustFloat(currentCfg, locationOf(ctx, filePath), Float.parseFloat(ctx.getText()));
		else if (ctx.StringLit() != null) {
			String strValue = ctx.StringLit().getText();
			return new RustString(currentCfg, locationOf(ctx, filePath), strValue.substring(1, strValue.length()));
		} else if (ctx.CharLit() != null) {
			char charValue = ctx.CharLit().getText().charAt(1);
			return new RustChar(currentCfg, locationOf(ctx, filePath), charValue);
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
	public List<Pair<Expression, Expression>> visitFields(FieldsContext ctx) {
		// TODO Skipping the first production and struct_update_base
		List<Pair<Expression, Expression>> fields = new ArrayList<>();
		for (FieldContext fctx : ctx.field())
			fields.add(visitField(fctx));

		return fields;
	}

	@Override
	public Object visitStruct_update_base(Struct_update_baseContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pair<Expression, Expression> visitField(FieldContext ctx) {
		if (ctx.field_name() != null) {
			return Pair.of(visitField_name(ctx.field_name()), visitExpr(ctx.expr()));
		} else {
			Expression expr = visitIdent(ctx.ident());
			return Pair.of(expr, expr);
		}
	}

	@Override
	public Expression visitField_name(Field_nameContext ctx) {
		if (ctx.ident() != null)
			return visitIdent(ctx.ident());
		else
			return new RustInteger(currentCfg, locationOf(ctx, filePath), Integer.parseInt(ctx.getText()));
	}

	@Override
	public Expression visitPost_expr(Post_exprContext ctx) {
		if (ctx.prim_expr() != null)
			return visitPrim_expr(ctx.prim_expr());

		Expression head = visitPost_expr(ctx.post_expr());

		RustAccessResolver tail = visitPost_expr_tail(ctx.post_expr_tail());

		if (tail instanceof RustArrayAccessKeeper) {
			RustArrayAccessKeeper right = (RustArrayAccessKeeper) tail;
			return new RustArrayAccess(currentCfg, locationOf(ctx, filePath), head, right.getExpr());

		} else if (tail instanceof RustTupleAccessKeeper) {
			RustTupleAccessKeeper right = (RustTupleAccessKeeper) tail;

			return new RustTupleAccess(currentCfg, locationOf(ctx, filePath), head, right.getExpr());
		} else if (tail instanceof RustMethodKeeper) {
			RustMethodKeeper right = (RustMethodKeeper) tail;

			List<Expression> parameters = new ArrayList<>();
			parameters.add(head);
			parameters.addAll(right.getAccessParameter());

			UnresolvedCall methodCall = new UnresolvedCall(currentCfg, locationOf(ctx, filePath),
					RustFrontend.PARAMETER_ASSIGN_STRATEGY, RustFrontend.METHOD_MATCHING_STRATEGY,
					RustFrontend.HIERARCY_TRAVERSAL_STRATEGY, CallType.INSTANCE, "", right.getMethodName(),
					RustFrontend.EVALUATION_ORDER, Untyped.INSTANCE, parameters.toArray(new Expression[0]));
			return methodCall;

		} else if (tail instanceof RustAttributeAccessKeeper) {
			RustAttributeAccessKeeper right = (RustAttributeAccessKeeper) tail;
			return new RustAccessMemberExpression(currentCfg, locationOf(ctx, filePath), head, right.getExpr());

		} else {
			RustFunctionCallKeeper right = (RustFunctionCallKeeper) tail;

			String receiverName = (head instanceof AccessGlobal ? ((AccessGlobal) head).getContainer().getName() : "");
			String targetName = (head instanceof AccessGlobal ? ((AccessGlobal) head).getTarget().getName()
					: head.toString());

			UnresolvedCall functionCall = new UnresolvedCall(currentCfg, locationOf(ctx, filePath),
					RustFrontend.PARAMETER_ASSIGN_STRATEGY, RustFrontend.METHOD_MATCHING_STRATEGY,
					RustFrontend.HIERARCY_TRAVERSAL_STRATEGY, CallType.STATIC, receiverName, targetName,
					RustFrontend.EVALUATION_ORDER, Untyped.INSTANCE, right.getParameters().toArray(new Expression[0]));

			return functionCall;
		}

	}

	@Override
	public RustAccessResolver visitPost_expr_tail(Post_expr_tailContext ctx) {
		switch (ctx.getChild(0).getText()) {
		case "?":
			// TODO should return the correct error handling operator on the
			// correct type
			return null;
		case "[":
			return new RustArrayAccessKeeper(visitExpr(ctx.expr()));
		case ".":
			if (ctx.BareIntLit() != null) {
				Expression position = new RustInteger(currentCfg, locationOf(ctx, filePath),
						Integer.parseInt(ctx.BareIntLit().getText()));

				return new RustTupleAccessKeeper(position);
			}

			// TODO skipping ty_args?

			String ident = ctx.ident().getText();

			// Method call with parameters
			if (ctx.expr_list() != null) {
				List<Expression> parameters = visitExpr_list(ctx.expr_list());
				return new RustMethodKeeper(ident, parameters);

				// Method call without parameters
			} else if (ctx.getChild(2) != null && ctx.getChild(2).getText().equals("("))
				return new RustMethodKeeper(ident, new ArrayList<Expression>());

			// Attribute access
			else {
				RustVariableRef attributeName = new RustVariableRef(currentCfg, locationOf(ctx, filePath), ident,
						false);
				return new RustAttributeAccessKeeper(attributeName);
			}
		case "(":
			if (ctx.expr_list() != null)
				return new RustFunctionCallKeeper(visitExpr_list(ctx.expr_list()));
			return new RustFunctionCallKeeper(new ArrayList<Expression>());
		}
		// Unreachable
		return null;
	}

	@Override
	public Expression visitPre_expr(Pre_exprContext ctx) {
		// TODO Skipping every production: "in" etc. and expr_attrs pre_expr
		if (ctx.post_expr() != null)
			return visitPost_expr(ctx.post_expr());

		boolean mutable = false;
		if (ctx.getChild(1) != null && ctx.getChild(1).getText().equals("mut"))
			mutable = true;

		switch (ctx.children.get(0).getText()) {
		case "-":
			return new RustMinusExpression(currentCfg, locationOf(ctx, filePath), visitPre_expr(ctx.pre_expr()));
		case "!":
			return new RustNotExpression(currentCfg, locationOf(ctx, filePath), visitPre_expr(ctx.pre_expr()));
		case "&":
			return new RustRefExpression(currentCfg, locationOf(ctx, filePath), visitPre_expr(ctx.pre_expr()), mutable);
		case "&&":
			return new RustDoubleRefExpression(currentCfg, locationOf(ctx, filePath), visitPre_expr(ctx.pre_expr()),
					mutable);
		case "*":
			return new RustDerefExpression(currentCfg, locationOf(ctx, filePath), visitPre_expr(ctx.pre_expr()));
		case "box":
			return new RustBoxExpression(currentCfg, locationOf(ctx, filePath), visitPre_expr(ctx.pre_expr()));
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
			return new RustMulExpression(currentCfg, locationOf(ctx, filePath), left, right);
		else if (symbol.equals("/"))
			return new RustDivExpression(currentCfg, locationOf(ctx, filePath), left, right);
		else
			return new RustModExpression(currentCfg, locationOf(ctx, filePath), left, right);
	}

	@Override
	public Expression visitAdd_expr(Add_exprContext ctx) {
		if (ctx.add_expr() == null)
			return visitMul_expr(ctx.mul_expr());

		Expression left = visitAdd_expr(ctx.add_expr());
		Expression right = visitMul_expr(ctx.mul_expr());
		String symbol = ctx.children.get(1).getText();
		if (symbol.equals("+"))
			return new RustAddExpression(currentCfg, locationOf(ctx, filePath), left, right);
		else
			return new RustSubExpression(currentCfg, locationOf(ctx, filePath), left, right);
	}

	@Override
	public Expression visitShift_expr(Shift_exprContext ctx) {
		if (ctx.shift_expr() == null)
			return visitAdd_expr(ctx.add_expr());

		Expression left = visitShift_expr(ctx.shift_expr());
		Expression right = visitAdd_expr(ctx.add_expr());
		String symbol = ctx.children.get(1).getText();
		if (symbol.equals("<"))
			return new RustLeftShiftExpression(currentCfg, locationOf(ctx, filePath), left, right);
		else
			return new RustRightShiftExpression(currentCfg, locationOf(ctx, filePath), left, right);
	}

	@Override
	public Expression visitBit_and_expr(Bit_and_exprContext ctx) {
		if (ctx.bit_and_expr() == null)
			return visitShift_expr(ctx.shift_expr());

		Expression left = visitBit_and_expr(ctx.bit_and_expr());
		Expression right = visitShift_expr(ctx.shift_expr());
		return new RustAndBitwiseExpression(currentCfg, locationOf(ctx, filePath), left, right);
	}

	@Override
	public Expression visitBit_xor_expr(Bit_xor_exprContext ctx) {
		if (ctx.bit_xor_expr() == null)
			return visitBit_and_expr(ctx.bit_and_expr());

		Expression left = visitBit_xor_expr(ctx.bit_xor_expr());
		Expression right = visitBit_and_expr(ctx.bit_and_expr());
		return new RustOrBitwiseExpression(currentCfg, locationOf(ctx, filePath), left, right);
	}

	@Override
	public Expression visitBit_or_expr(Bit_or_exprContext ctx) {
		if (ctx.bit_or_expr() == null)
			return visitBit_xor_expr(ctx.bit_xor_expr());

		Expression left = visitBit_or_expr(ctx.bit_or_expr());
		Expression right = visitBit_xor_expr(ctx.bit_xor_expr());
		return new RustOrBitwiseExpression(currentCfg, locationOf(ctx, filePath), left, right);
	}

	@Override
	public Expression visitCmp_expr(Cmp_exprContext ctx) {
		Expression left = visitBit_or_expr(ctx.bit_or_expr(0));

		if (ctx.getChildCount() > 1) {
			Expression right = visitBit_or_expr(ctx.bit_or_expr(1));

			switch (ctx.getChild(1).getText()) {
			case "==":
				return new RustEqualExpression(currentCfg, locationOf(ctx, filePath), left, right);
			case "!=":
				return new RustNotEqualExpression(currentCfg, locationOf(ctx, filePath), left, right);
			case "<":
				return new RustLessExpression(currentCfg, locationOf(ctx, filePath), left, right);
			case "<=":
				return new RustLessEqualExpression(currentCfg, locationOf(ctx, filePath), left, right);
			case ">":
				// Since the greater equal sign is split in the g4 grammar, a
				// check is necessary
				if (ctx.getChild(ctx.getChildCount() - 2).getText().equals("="))
					return new RustGreaterEqualExpression(currentCfg, locationOf(ctx, filePath), left, right);

				return new RustGreaterExpression(currentCfg, locationOf(ctx, filePath), left, right);
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
		return new RustOrExpression(currentCfg, locationOf(ctx, filePath), left, right);
	}

	@Override
	public Expression visitOr_expr(Or_exprContext ctx) {
		if (ctx.or_expr() == null)
			return visitAnd_expr(ctx.and_expr());

		Expression left = visitOr_expr(ctx.or_expr());
		Expression right = visitAnd_expr(ctx.and_expr());
		return new RustOrExpression(currentCfg, locationOf(ctx, filePath), left, right);
	}

	@Override
	public Expression visitRange_expr(Range_exprContext ctx) {
		if (ctx.children.size() == 1) { // First production
			return visitOr_expr(ctx.or_expr(0));

		} else if (ctx.children.size() == 3) { // Second full production
			Expression left = visitOr_expr(ctx.or_expr().get(0));
			Expression right = visitOr_expr(ctx.or_expr().get(1));

			return new RustRangeExpression(currentCfg, locationOf(ctx, filePath), left, right);

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
				return new RustRangeFromExpression(currentCfg, locationOf(ctx, filePath), left);
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
			return new RustAssignment(currentCfg, locationOf(ctx, filePath), rangeExpr, right);
		case "*=":
			return new RustAssignment(currentCfg, locationOf(ctx, filePath), rangeExpr,
					new RustMulExpression(currentCfg, locationOf(ctx, filePath), rangeExpr, right));
		case "/=":
			return new RustAssignment(currentCfg, locationOf(ctx, filePath), rangeExpr,
					new RustDivExpression(currentCfg, locationOf(ctx, filePath), rangeExpr, right));
		case "%=":
			return new RustAssignment(currentCfg, locationOf(ctx, filePath), rangeExpr,
					new RustModExpression(currentCfg, locationOf(ctx, filePath), rangeExpr, right));
		case "+=":
			return new RustAssignment(currentCfg, locationOf(ctx, filePath), rangeExpr,
					new RustAddExpression(currentCfg, locationOf(ctx, filePath), rangeExpr, right));
		case "-=":
			return new RustAssignment(currentCfg, locationOf(ctx, filePath), rangeExpr,
					new RustSubExpression(currentCfg, locationOf(ctx, filePath), rangeExpr, right));
		case "<<=":
			return new RustAssignment(currentCfg, locationOf(ctx, filePath), rangeExpr,
					new RustLeftShiftExpression(currentCfg, locationOf(ctx, filePath), rangeExpr, right));
		case ">": // catches only ">" which is separated in the g4 grammar
			return new RustAssignment(currentCfg, locationOf(ctx, filePath), rangeExpr,
					new RustRightShiftExpression(currentCfg, locationOf(ctx, filePath), rangeExpr, right));
		case "&=":
			return new RustAssignment(currentCfg, locationOf(ctx, filePath), rangeExpr,
					new RustAndBitwiseExpression(currentCfg, locationOf(ctx, filePath), rangeExpr, right));
		case "^=":
			return new RustAssignment(currentCfg, locationOf(ctx, filePath), rangeExpr,
					new RustXorBitwiseExpression(currentCfg, locationOf(ctx, filePath), rangeExpr, right));
		default: // operator "|="
			return new RustAssignment(currentCfg, locationOf(ctx, filePath), rangeExpr,
					new RustOrBitwiseExpression(currentCfg, locationOf(ctx, filePath), rangeExpr, right));
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
			// TODO skipping expr_attrs pre_expr_no_struct production
			return null;
		}

		boolean mutable = (ctx.getChild(1).getText().equals("mut") ? true : false);

		switch (ctx.getChild(0).getText()) {
		case "-":
			return new RustMinusExpression(currentCfg, locationOf(ctx, filePath), expr);
		case "!":
			return new RustNotExpression(currentCfg, locationOf(ctx, filePath), expr);
		case "&":
			return new RustRefExpression(currentCfg, locationOf(ctx, filePath), expr, mutable);
		case "&&":
			return new RustDoubleRefExpression(currentCfg, locationOf(ctx, filePath), expr, mutable);
		case "*":
			return new RustDerefExpression(currentCfg, locationOf(ctx, filePath), expr);
		case "box":
			return new RustBoxExpression(currentCfg, locationOf(ctx, filePath), expr);
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
		Type type = new RustTypeVisitor(filePath, unit).visitTy_sum(ctx.ty_sum());

		return new RustCastExpression(currentCfg, locationOf(ctx, filePath), type, left);
	}

	@Override
	public Expression visitMul_expr_no_struct(Mul_expr_no_structContext ctx) {
		if (ctx.mul_expr_no_struct() == null)
			return visitCast_expr_no_struct(ctx.cast_expr_no_struct());

		if (ctx.getChild(1).getText().equals("*")) {
			Expression left = visitMul_expr_no_struct(ctx.mul_expr_no_struct());
			Expression right = visitCast_expr_no_struct(ctx.cast_expr_no_struct());

			return new RustMulExpression(currentCfg, locationOf(ctx, filePath), left, right);
		} else if (ctx.getChild(1).getText().equals("/")) {
			Expression left = visitMul_expr_no_struct(ctx.mul_expr_no_struct());
			Expression right = visitCast_expr_no_struct(ctx.cast_expr_no_struct());

			return new RustDivExpression(currentCfg, locationOf(ctx, filePath), left, right);
		} else {
			Expression left = visitMul_expr_no_struct(ctx.mul_expr_no_struct());
			Expression right = visitCast_expr_no_struct(ctx.cast_expr_no_struct());

			return new RustModExpression(currentCfg, locationOf(ctx, filePath), left, right);
		}
	}

	@Override
	public Expression visitAdd_expr_no_struct(Add_expr_no_structContext ctx) {
		if (ctx.add_expr_no_struct() == null)
			return visitMul_expr_no_struct(ctx.mul_expr_no_struct());

		if (ctx.getChild(1).getText().equals("+")) {
			Expression left = visitAdd_expr_no_struct(ctx.add_expr_no_struct());
			Expression right = visitMul_expr_no_struct(ctx.mul_expr_no_struct());

			return new RustAddExpression(currentCfg, locationOf(ctx, filePath), left, right);
		} else {
			Expression left = visitAdd_expr_no_struct(ctx.add_expr_no_struct());
			Expression right = visitMul_expr_no_struct(ctx.mul_expr_no_struct());

			return new RustSubExpression(currentCfg, locationOf(ctx, filePath), left, right);
		}
	}

	@Override
	public Expression visitShift_expr_no_struct(Shift_expr_no_structContext ctx) {
		if (ctx.shift_expr_no_struct() == null)
			return visitAdd_expr_no_struct(ctx.add_expr_no_struct());

		if (ctx.getChild(1).getText().equals("<")) {
			Expression left = visitShift_expr_no_struct(ctx.shift_expr_no_struct());
			Expression right = visitAdd_expr_no_struct(ctx.add_expr_no_struct());

			return new RustLeftShiftExpression(currentCfg, locationOf(ctx, filePath), left, right);
		} else {
			Expression left = visitShift_expr_no_struct(ctx.shift_expr_no_struct());
			Expression right = visitAdd_expr_no_struct(ctx.add_expr_no_struct());

			return new RustRightShiftExpression(currentCfg, locationOf(ctx, filePath), left, right);
		}
	}

	@Override
	public Expression visitBit_and_expr_no_struct(Bit_and_expr_no_structContext ctx) {
		if (ctx.bit_and_expr_no_struct() == null)
			return visitShift_expr_no_struct(ctx.shift_expr_no_struct());

		Expression left = visitBit_and_expr_no_struct(ctx.bit_and_expr_no_struct());
		Expression right = visitShift_expr_no_struct(ctx.shift_expr_no_struct());

		return new RustAndBitwiseExpression(currentCfg, locationOf(ctx, filePath), left, right);
	}

	@Override
	public Expression visitBit_xor_expr_no_struct(Bit_xor_expr_no_structContext ctx) {
		if (ctx.bit_xor_expr_no_struct() == null)
			return visitBit_and_expr_no_struct(ctx.bit_and_expr_no_struct());

		Expression left = visitBit_xor_expr_no_struct(ctx.bit_xor_expr_no_struct());
		Expression right = visitBit_and_expr_no_struct(ctx.bit_and_expr_no_struct());

		return new RustXorBitwiseExpression(currentCfg, locationOf(ctx, filePath), left, right);
	}

	@Override
	public Expression visitBit_or_expr_no_struct(Bit_or_expr_no_structContext ctx) {
		if (ctx.bit_or_expr_no_struct() == null)
			return visitBit_xor_expr_no_struct(ctx.bit_xor_expr_no_struct());

		Expression left = visitBit_or_expr_no_struct(ctx.bit_or_expr_no_struct());
		Expression right = visitBit_xor_expr_no_struct(ctx.bit_xor_expr_no_struct());

		return new RustOrBitwiseExpression(currentCfg, locationOf(ctx, filePath), left, right);
	}

	@Override
	public Expression visitCmp_expr_no_struct(Cmp_expr_no_structContext ctx) {
		Expression left = visitBit_or_expr_no_struct(ctx.bit_or_expr_no_struct(0));

		if (ctx.getChildCount() > 1) {
			Expression right = visitBit_or_expr_no_struct(ctx.bit_or_expr_no_struct(1));

			switch (ctx.getChild(1).getText()) {
			case "==":
				return new RustEqualExpression(currentCfg, locationOf(ctx, filePath), left, right);
			case "!=":
				return new RustNotEqualExpression(currentCfg, locationOf(ctx, filePath), left, right);
			case "<":
				return new RustLessExpression(currentCfg, locationOf(ctx, filePath), left, right);
			case "<=":
				return new RustLessEqualExpression(currentCfg, locationOf(ctx, filePath), left, right);
			case ">":
				// Since the greater equal sign is split in the g4 grammar, a
				// check is necessary
				if (ctx.getChild(ctx.getChildCount() - 2).getText().equals("="))
					return new RustGreaterEqualExpression(currentCfg, locationOf(ctx, filePath), left, right);

				return new RustGreaterExpression(currentCfg, locationOf(ctx, filePath), left, right);
			}
		}

		return left;
	}

	@Override
	public Expression visitAnd_expr_no_struct(And_expr_no_structContext ctx) {
		if (ctx.and_expr_no_struct() != null) {
			Expression and = visitAnd_expr_no_struct(ctx.and_expr_no_struct());
			Expression cmp = visitCmp_expr_no_struct(ctx.cmp_expr_no_struct());

			return new RustAndExpression(currentCfg, locationOf(ctx, filePath), and, cmp);
		}

		return visitCmp_expr_no_struct(ctx.cmp_expr_no_struct());
	}

	@Override
	public Expression visitOr_expr_no_struct(Or_expr_no_structContext ctx) {
		if (ctx.or_expr_no_struct() != null) {
			Expression or = visitOr_expr_no_struct(ctx.or_expr_no_struct());
			Expression and = visitAnd_expr_no_struct(ctx.and_expr_no_struct());

			return new RustOrExpression(currentCfg, locationOf(ctx, filePath), or, and);
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

			return new RustRangeExpression(currentCfg, locationOf(ctx, filePath), left, right);

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
				return new RustRangeFromExpression(currentCfg, locationOf(ctx, filePath), left);
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
				return new RustAssignment(currentCfg, locationOf(ctx, filePath), rangeExpr, right);
			case "*=":
				return new RustAssignment(currentCfg, locationOf(ctx, filePath), rangeExpr,
						new RustMulExpression(currentCfg, locationOf(ctx, filePath), rangeExpr, right));
			case "/=":
				return new RustAssignment(currentCfg, locationOf(ctx, filePath), rangeExpr,
						new RustDivExpression(currentCfg, locationOf(ctx, filePath), rangeExpr, right));
			case "%=":
				return new RustAssignment(currentCfg, locationOf(ctx, filePath), rangeExpr,
						new RustModExpression(currentCfg, locationOf(ctx, filePath), rangeExpr, right));
			case "+=":
				return new RustAssignment(currentCfg, locationOf(ctx, filePath), rangeExpr,
						new RustAddExpression(currentCfg, locationOf(ctx, filePath), rangeExpr, right));
			case "<<=":
				return new RustAssignment(currentCfg, locationOf(ctx, filePath), rangeExpr,
						new RustLeftShiftExpression(currentCfg, locationOf(ctx, filePath), rangeExpr, right));
			case ">":
				return new RustAssignment(currentCfg, locationOf(ctx, filePath), rangeExpr,
						new RustRightShiftExpression(currentCfg, locationOf(ctx, filePath), rangeExpr, right));
			case "&=":
				return new RustAssignment(currentCfg, locationOf(ctx, filePath), rangeExpr,
						new RustAndBitwiseExpression(currentCfg, locationOf(ctx, filePath), rangeExpr, right));
			case "^=":
				return new RustAssignment(currentCfg, locationOf(ctx, filePath), rangeExpr,
						new RustXorBitwiseExpression(currentCfg, locationOf(ctx, filePath), rangeExpr, right));
			case "|=":
				return new RustAssignment(currentCfg, locationOf(ctx, filePath), rangeExpr,
						new RustOrBitwiseExpression(currentCfg, locationOf(ctx, filePath), rangeExpr, right));
			}
		}

		return rangeExpr;
	}

	@Override
	public Expression visitIdent(IdentContext ctx) {
		// TODO: everything is mapped as a variable reference, included auto,
		// default, union
		return new RustVariableRef(currentCfg, locationOf(ctx, filePath), ctx.getText(), false);
	}

	@Override
	public Object visitAny_ident(Any_identContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

}
