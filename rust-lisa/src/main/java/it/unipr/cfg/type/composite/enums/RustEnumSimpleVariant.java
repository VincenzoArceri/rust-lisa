package it.unipr.cfg.type.composite.enums;

import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;

/**
 * Rust enum simple variant.
 *
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustEnumSimpleVariant implements RustEnumVariant {

	private String name;

	/**
	 * Construct the {@link RustEnumSimpleVariant} object.
	 * 
	 * @param name the name of the variant
	 */
	public RustEnumSimpleVariant(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Statement match(Expression toMatch) {
		// TODO too coarse
		return null;
	}
}
