package it.unipr.cfg.expression.literal.enums;

import it.unipr.cfg.expression.RustMulitpleExpression;
import it.unipr.cfg.type.composite.enums.RustEnumType;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;

/**
 * Rust enum tuple literal.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustEnumTupleLiteral extends RustEnumLiteral<RustMulitpleExpression> {

	private String variantName;

	/**
	 * Build the enum struct literal.
	 * 
	 * @param cfg         the {@link CFG} where this literal lies
	 * @param location    the location where this literal is defined
	 * @param expressions the values to be used inside the literal. During
	 *                        destructuring they can be variables, in (almost)
	 *                        any other case they can be other literals
	 * @param variantName the name of this variant
	 * @param enumType    the enum type of this literal
	 */
	public RustEnumTupleLiteral(CFG cfg, CodeLocation location, RustMulitpleExpression expressions, String variantName,
			RustEnumType enumType) {
		super(cfg, location, expressions, enumType);
		this.variantName = variantName;
	}

	@Override
	public String toString() {
		return getStaticType() + "::" + variantName + "(" + getValue() + ")";
	}
}
