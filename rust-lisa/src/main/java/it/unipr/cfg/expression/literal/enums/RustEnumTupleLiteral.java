package it.unipr.cfg.expression.literal.enums;

import it.unipr.cfg.type.composite.enums.RustEnumType;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.Expression;

/**
 * Rust enum tuple literal.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustEnumTupleLiteral extends RustEnumLiteral<Expression> {

	private String variantName;

	/**
	 * Build the enum struct literal.
	 * 
	 * @param cfg                   the {@link CFG} where this literal lies
	 * @param location              the location where this literal is defined
	 * @param variableDestructuring the values inside the literal
	 * @param variantName           the name of this variant
	 * @param enumType              the enum type of this literal
	 */
	public RustEnumTupleLiteral(CFG cfg, CodeLocation location, Expression variableDestructuring, String variantName,
			RustEnumType enumType) {
		super(cfg, location, variableDestructuring, enumType);
		this.variantName = variantName;
	}

	@Override
	public String toString() {
		return getStaticType() + "::" + variantName + "(" + getValue() + ")";
	}
}
