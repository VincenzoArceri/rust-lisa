package it.unipr.cfg.type.composite.enums;

import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;

/**
 * Rust enum interface for implementing variants.
 *
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public interface RustEnumVariant {
	/**
	 * Operates the rust match.
	 * 
	 * @param toMatch the expression to be matched
	 * 
	 * @return the statement for the analysis
	 */
	public Statement match(Expression toMatch);

	@Override
	public String toString();
}
