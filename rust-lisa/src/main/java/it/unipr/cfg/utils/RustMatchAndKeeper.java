package it.unipr.cfg.utils;

import it.unive.lisa.program.cfg.statement.Expression;

/**
 * Keeper used during parse of match statements when if is used.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustMatchAndKeeper implements RustMatchKeeper {
	private final Expression expr;

	/**
	 * Constructor for a {@code RustMatchAndKeeper}.
	 * 
	 * @param expr the expression to keep inside
	 */
	public RustMatchAndKeeper(Expression expr) {
		this.expr = expr;
	}

	@Override
	public Expression get() {
		return expr;
	}

}
