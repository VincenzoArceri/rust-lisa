package it.unipr.cfg.expression.literal.enums;

import it.unipr.cfg.type.composite.enums.RustEnumType;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;

/**
 * Interface for Rust enum literals.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustEnumSimpleLiteral extends RustEnumLiteral<String> {
	/**
	 * Build the enum literal.
	 * 
	 * @param cfg      the {@link CFG} where this literal lies
	 * @param location the location where this literal is defined
	 * @param value    the value of the expression
	 * @param enumType the variant type of the expression
	 */
	public RustEnumSimpleLiteral(CFG cfg, CodeLocation location, String value, RustEnumType enumType) {
		super(cfg, location, value, enumType);
	}

	@Override
	public String toString() {
		return getStaticType() + "::" + getValue();
	}
}
