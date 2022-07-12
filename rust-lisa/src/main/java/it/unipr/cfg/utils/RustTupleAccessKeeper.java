package it.unipr.cfg.utils;

import it.unive.lisa.program.cfg.statement.Expression;

/**
 * Rust information keeper for accessing tuples.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustTupleAccessKeeper implements RustAccessResolver {
	private final Expression expr;

	/**
	 * Constructs a {@link RustTupleAccessKeeper}.
	 * 
	 * @param expr the expression use to access the tuple
	 */
	public RustTupleAccessKeeper(Expression expr) {
		this.expr = expr;
	}

	/**
	 * Yields the expression kept inside.
	 * 
	 * @return {@link Expression} kept inside
	 */
	public Expression getExpr() {
		return expr;
	}
}
