package it.unipr.cfg.utils;

import it.unive.lisa.program.cfg.statement.Expression;

/**
 * Rust information keeper for accessing attributes.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustAttributeAccessKeeper implements RustAccessResolver {
	private Expression expr;

	/**
	 * Constructs a {@link RustAttributeAccessKeeper}.
	 * 
	 * @param expr the expression used to access the attribute
	 */
	public RustAttributeAccessKeeper(Expression expr) {
		this.expr = expr;
	}

	/**
	 * Yields the expression used to accessing the attribute.
	 * 
	 * @return {@link Expression} kept inside this type
	 */
	public Expression getExpr() {
		return expr;
	}

}
