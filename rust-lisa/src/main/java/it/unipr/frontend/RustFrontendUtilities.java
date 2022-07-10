package it.unipr.frontend;

import it.unive.lisa.program.SourceCodeLocation;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * Utility class useful in multiple {@link RustBaseVisitor}.
 */
public class RustFrontendUtilities {

	/**
	 * Yields the line position of a parse rule.
	 * 
	 * @param ctx the parse rule
	 * 
	 * @return yields the line position of a parse rule
	 */
	static public int getLine(ParserRuleContext ctx) {
		return ctx.getStart().getLine();
	}

	/**
	 * Yields the line position of a terminal node.
	 * 
	 * @param ctx the terminal node
	 * 
	 * @return yields the line position of a terminal node
	 */
	static public int getLine(TerminalNode ctx) {
		return ctx.getSymbol().getLine();
	}

	/**
	 * Yields the column position of a parse rule.
	 * 
	 * @param ctx the parse rule
	 * 
	 * @return yields the column position of a parse rule
	 */
	static public int getCol(ParserRuleContext ctx) {
		return ctx.getStop().getCharPositionInLine();
	}

	/**
	 * Yields the column position of a terminal node.
	 * 
	 * @param ctx the terminal node
	 * 
	 * @return yields the column position of a terminal node
	 */
	static public int getCol(TerminalNode ctx) {
		return ctx.getSymbol().getCharPositionInLine();
	}

	/**
	 * Yields the source code location of a parse rule.
	 * 
	 * @param ctx      the parsing rule context
	 * @param filePath the parsing file name
	 * 
	 * @return yields the source code location of a parse rule
	 */
	static public SourceCodeLocation locationOf(ParserRuleContext ctx, String filePath) {
		return new SourceCodeLocation(filePath, getLine(ctx), getCol(ctx));
	}
}
