package it.unipr.cfg.expression.literal.enums;

import it.unipr.cfg.type.composite.enums.RustEnumType;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Rust enum tuple literal.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustEnumTupleLiteral extends RustEnumLiteral<NaryExpression> {

	/**
	 * Build the enum struct literal.
	 * 
	 * @param cfg         the {@link CFG} where this literal lies
	 * @param location    the location where this literal is defined
	 * @param assignments the values inside the literal
	 * @param enumType    the enum type of this literal
	 */
	public RustEnumTupleLiteral(CFG cfg, CodeLocation location, NaryExpression assignments, RustEnumType enumType) {
		super(cfg, location, assignments, enumType);
	}

	@Override
	public String toString() {
		return "("
				+ Arrays.asList(getValue().getSubExpressions()).stream().map(e -> e.toString())
						.collect(Collectors.joining(", "))
				+ ")";
	}
}
