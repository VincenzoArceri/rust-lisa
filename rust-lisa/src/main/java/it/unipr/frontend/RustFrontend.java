package it.unipr.frontend;

import static it.unipr.frontend.RustFrontendUtilities.locationOf;

import it.unipr.cfg.type.RustBooleanType;
import it.unipr.cfg.type.RustCharType;
import it.unipr.cfg.type.RustPointerType;
import it.unipr.cfg.type.RustStrType;
import it.unipr.cfg.type.RustUnitType;
import it.unipr.cfg.type.composite.RustArrayType;
import it.unipr.cfg.type.composite.RustStructType;
import it.unipr.cfg.type.composite.RustTupleType;
import it.unipr.cfg.type.numeric.floating.RustF32Type;
import it.unipr.cfg.type.numeric.floating.RustF64Type;
import it.unipr.cfg.type.numeric.signed.RustI128Type;
import it.unipr.cfg.type.numeric.signed.RustI16Type;
import it.unipr.cfg.type.numeric.signed.RustI32Type;
import it.unipr.cfg.type.numeric.signed.RustI64Type;
import it.unipr.cfg.type.numeric.signed.RustI8Type;
import it.unipr.cfg.type.numeric.signed.RustIsizeType;
import it.unipr.cfg.type.numeric.unsigned.RustU128Type;
import it.unipr.cfg.type.numeric.unsigned.RustU16Type;
import it.unipr.cfg.type.numeric.unsigned.RustU32Type;
import it.unipr.cfg.type.numeric.unsigned.RustU64Type;
import it.unipr.cfg.type.numeric.unsigned.RustU8Type;
import it.unipr.cfg.type.numeric.unsigned.RustUsizeType;
import it.unipr.rust.antlr.RustBaseVisitor;
import it.unipr.rust.antlr.RustLexer;
import it.unipr.rust.antlr.RustParser;
import it.unipr.rust.antlr.RustParser.CrateContext;
import it.unipr.rust.antlr.RustParser.ItemContext;
import it.unipr.rust.antlr.RustParser.Mod_bodyContext;
import it.unipr.rust.antlr.RustParser.Pub_itemContext;
import it.unipr.rust.antlr.RustParser.Struct_declContext;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.call.assignment.OrderPreservingAssigningStrategy;
import it.unive.lisa.program.cfg.statement.call.assignment.ParameterAssigningStrategy;
import it.unive.lisa.program.cfg.statement.call.resolution.ParameterMatchingStrategy;
import it.unive.lisa.program.cfg.statement.call.resolution.RuntimeTypesMatchingStrategy;
import it.unive.lisa.program.cfg.statement.call.traversal.HierarcyTraversalStrategy;
import it.unive.lisa.program.cfg.statement.call.traversal.SingleInheritanceTraversalStrategy;
import it.unive.lisa.program.cfg.statement.evaluation.EvaluationOrder;
import it.unive.lisa.program.cfg.statement.evaluation.LeftToRightEvaluation;
import it.unive.lisa.type.Type;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * The Rust front-end for LiSA.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustFrontend extends RustBaseVisitor<Object> {

	/**
	 * The strategy of traversing super-unit to search for target call
	 * implementation.
	 */
	public static final HierarcyTraversalStrategy HIERARCY_TRAVERSAL_STRATEGY = SingleInheritanceTraversalStrategy.INSTANCE;

	/**
	 * The parameter assigning strategy for calls.
	 */
	public static final ParameterAssigningStrategy PARAMETER_ASSIGN_STRATEGY = OrderPreservingAssigningStrategy.INSTANCE;

	/**
	 * The parameter matching strategy for matching method and function calls.
	 */
	public static final ParameterMatchingStrategy METHOD_MATCHING_STRATEGY = RuntimeTypesMatchingStrategy.INSTANCE;

	/**
	 * The parameter evaluation order strategy.
	 */
	public static final EvaluationOrder EVALUATION_ORDER = LeftToRightEvaluation.INSTANCE;

	/**
	 * Reference to the parser
	 */
	private static RustParser parser;

	/**
	 * File path of the Rust program to be analyzed
	 */
	private final String filePath;

	/**
	 * LiSA program corresponding to the Rust program located at
	 * {@code filePath}
	 */
	private final Program program;

	/**
	 * Reference to the current unit
	 */
	private CompilationUnit currentUnit;

	private RustFrontend(String filePath) {
		this.filePath = filePath;
		this.program = new Program();
	}

	private void registerTypes() {
		program.registerType(RustF32Type.getInstance());
		program.registerType(RustF64Type.getInstance());
		program.registerType(RustI8Type.getInstance());
		program.registerType(RustI16Type.getInstance());
		program.registerType(RustI32Type.getInstance());
		program.registerType(RustI64Type.getInstance());
		program.registerType(RustI128Type.getInstance());
		program.registerType(RustIsizeType.getInstance());
		program.registerType(RustU8Type.getInstance());
		program.registerType(RustU16Type.getInstance());
		program.registerType(RustU32Type.getInstance());
		program.registerType(RustU64Type.getInstance());
		program.registerType(RustU128Type.getInstance());
		program.registerType(RustUsizeType.getInstance());
		program.registerType(RustBooleanType.getInstance());
		program.registerType(RustCharType.getInstance());
		program.registerType(RustStrType.getInstance());
		program.registerType(RustUnitType.getInstance());
		RustPointerType.all().forEach(program::registerType);
		RustStructType.all().forEach(program::registerType);
		RustArrayType.all().forEach(program::registerType);
		RustTupleType.all().forEach(program::registerType);
	}

	/**
	 * Yields the {@link Program} corresponding to the Rust program located at
	 * {@code filePath}.
	 * 
	 * @param filePath the file path where the Rust program to be analyzed
	 * 
	 * @return the {@link Program} corresponding to the Rust program located at
	 *             {@code filePath}
	 * 
	 * @throws IOException if anything goes wrong during reading the file
	 */
	public static Program processFile(String filePath) throws IOException {
		return new RustFrontend(filePath).toLiSAProgram();
	}

	/**
	 * Yields the instance of {@link RustParser}.
	 * 
	 * @return the reference to the parser
	 */
	public static RustParser getParser() {
		return parser;
	}

	/**
	 * Yields the {@link Program} corresponding to the Rust program located at
	 * {@code filePath}.
	 * 
	 * @return the {@link Program} corresponding to the Rust program located at
	 *             {@code filePath}
	 * 
	 * @throws IOException if anything goes wrong during reading the file
	 */
	private Program toLiSAProgram() throws IOException {

		InputStream is = new FileInputStream(filePath);
		RustLexer lexer = new RustLexer(CharStreams.fromStream(is, StandardCharsets.UTF_8));
		RustParser parser = new RustParser(new CommonTokenStream(lexer));
		RustFrontend.parser = parser;

		ParseTree tree = parser.crate();
		visitCrate((CrateContext) tree);
		return program;
	}

	@Override
	public Object visitCrate(CrateContext ctx) {
		CompilationUnit mainUnit = new CompilationUnit(new SourceCodeLocation(filePath, 0, 0), filePath, false);
		currentUnit = mainUnit;
		program.addCompilationUnit(mainUnit);
		return visitMod_body(ctx.mod_body());
	}

	@Override
	public Void visitMod_body(Mod_bodyContext ctx) {
		// TODO: skipping for the moment inner_attr
		for (ItemContext i : ctx.item())
			visitItem(i);

		registerTypes();
		return null;
	}

	@Override
	public Void visitItem(ItemContext ctx) {
		if (ctx.pub_item() != null && ctx.pub_item().struct_decl() != null)
			visitPub_item(ctx.pub_item());

		for (Type t : RustStructType.all())
			program.addCompilationUnit(((RustStructType) t).getUnit());

		if (ctx.impl_block() != null) {
			RustStructType struct = RustStructType.get(ctx.impl_block().impl_what().getText());
			CompilationUnit u = struct.getUnit();

			List<CFG> implCfg = new RustCodeMemberVisitor(filePath, program, u).visitImpl_block(ctx.impl_block());

			for (CFG cfg : implCfg)
				u.addInstanceCFG(cfg);
		}

		if (ctx.pub_item() != null && ctx.pub_item().fn_decl() != null)
			program.addCFG(new RustCodeMemberVisitor(filePath, program, currentUnit)
					.visitFn_decl(ctx.pub_item().fn_decl()));

		if (ctx.item_macro_use() != null)
			// Both macro definitions and calls are here
			// TODO parsing only calls for now
			new RustCodeMemberVisitor(filePath, program, currentUnit).visitItem_macro_use(ctx.item_macro_use());

		return null;
	}

	@Override
	public Void visitPub_item(Pub_itemContext ctx) {
		if (ctx.struct_decl() != null) {
			String name = ctx.struct_decl().ident().getText();
			CompilationUnit structUnit = new CompilationUnit(locationOf(ctx, filePath), name, true);

			RustStructType.lookup(name, structUnit);

			List<Global> fields = visitStruct_decl(ctx.struct_decl());

			for (Global f : fields)
				structUnit.addInstanceGlobal(f);
		}
		return null;
	}

	@Override
	public List<Global> visitStruct_decl(Struct_declContext ctx) {
		// TODO skipping ty_params? production
		return new RustTypeVisitor(filePath, currentUnit).visitStruct_tail(ctx.struct_tail());
	}

}
