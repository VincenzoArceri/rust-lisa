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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * The Rust front-end for Rust.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class RustLiSAFrontend extends RustBaseVisitor<Object> {

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

	private RustLiSAFrontend(String filePath) {
		this.filePath = filePath;
		this.program = new Program();
	}

	/**
	 * Yields the {@link Program} corresponding to the Rust program located at
	 * {@code filePath}
	 * 
	 * @param filePath the file path where the Rust program to be analyzed
	 * 
	 * @return the {@link Program} corresponding to the Rust program located at
	 *             {@code filePath}
	 * 
	 * @throws IOException if anything goes wrong during reading the file
	 */
	public static Program processFile(String filePath) throws IOException {
		return new RustLiSAFrontend(filePath).toLiSAProgram();
	}

	/**
	 * Yields the {@link Program} corresponding to the Rust program located at
	 * {@code filePath}
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
			visitItem(i);

		return null;
	}

	@Override
	public Object visitItem(ItemContext ctx) {
		// TODO: skipping for the moment attr and visibility
		if (ctx.pub_item() != null)
			return visitPub_item(ctx.pub_item());
		else if (ctx.impl_block() != null)
			return visitImpl_block(ctx.impl_block());
		else if (ctx.extern_mod() != null)
			return visitExtern_mod(ctx.extern_mod());
		else
			return visitItem_macro_use(ctx.item_macro_use());
	}

	@Override
	public Object visitPub_item(Pub_itemContext ctx) {
		// TODO: for the moment we are just interested in function declaration
		return new RustCodeMemberVisitor(filePath, program, currentUnit).visitFn_decl(ctx.fn_decl());
	}

}
