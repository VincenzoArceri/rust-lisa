package it.unipr.frontend;

import it.unipr.rust.antlr.RustBaseVisitor;
import it.unipr.rust.antlr.RustLexer;
import it.unipr.rust.antlr.RustParser;
import it.unipr.rust.antlr.RustParser.CrateContext;
import it.unipr.rust.antlr.RustParser.ItemContext;
import it.unipr.rust.antlr.RustParser.Mod_bodyContext;
import it.unipr.rust.antlr.RustParser.Pub_itemContext;
import it.unive.lisa.program.CompilationUnit;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * The Rust front-end for LiSA.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class RustFrontend extends RustBaseVisitor<Object> {

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

	private RustFrontend(String filePath) {
		this.filePath = filePath;
		this.program = new Program();
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
	public Object visitMod_body(Mod_bodyContext ctx) {
		// TODO: skipping for the moment inner_attr
		for (ItemContext i : ctx.item())
			program.addCFG(visitItem(i));

		return null;
	}

	@Override
	public CFG visitItem(ItemContext ctx) {
		// TODO: skipping for the moment attr and visibility
		// the casts below are completely wrong, they will be removed
		if (ctx.pub_item() != null)
			return visitPub_item(ctx.pub_item());
		else if (ctx.impl_block() != null)
			return (CFG) visitImpl_block(ctx.impl_block());
		else if (ctx.extern_mod() != null)
			return (CFG) visitExtern_mod(ctx.extern_mod());
		else
			return (CFG) visitItem_macro_use(ctx.item_macro_use());
	}

	@Override
	public CFG visitPub_item(Pub_itemContext ctx) {
		// TODO: for the moment we are just interested in function declaration
		return new RustCodeMemberVisitor(filePath, program, currentUnit).visitFn_decl(ctx.fn_decl());
	}

}
