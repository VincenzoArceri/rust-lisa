package it.unipr.cfg.utils;

import it.unive.lisa.program.cfg.statement.Expression;

/**
 * Generic Keeper used during parse of match statements.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public interface RustMatchKeeper {
	/**
	 * Yields the expression contained inside this keeper.
	 * 
	 * @return the expression contained inside
	 */
	public Expression get();
}
