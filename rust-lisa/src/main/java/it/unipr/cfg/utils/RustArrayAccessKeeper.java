package it.unipr.cfg.utils;

import it.unive.lisa.program.cfg.statement.Expression;

/**
 * Rust information keeper for accessing array.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustArrayAccessKeeper implements RustAccessResolver {

	private final Expression expr;

	/**
	 * Constructs a {@link RustArrayAccessKeeper}.
	 * 
	 * @param expr the expression used to access the array
	 */
	public RustArrayAccessKeeper(Expression expr) {
		this.expr = expr;
	}

	/**
	 * Yields the expression kept inside this type.
	 * 
	 * @return {@link Expression} kept inside this type
	 */
	public Expression getExpr() {
		return expr;
	}

}
