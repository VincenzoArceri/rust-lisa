package it.unipr.cfg.expression.literal.enums;

import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.literal.Literal;
import it.unive.lisa.type.Type;

public abstract class RustEnumLiteral<E> extends Literal<E> {
	
	@Override
	public abstract String toString();
	
	/**
	 * Builds the enum literal.
	 * 
	 * @param cfg        the cfg that this expression belongs to
	 * @param location   the location where the expression is defined within the
	 *                       program
	 * @param value      the value of this literal
	 * @param staticType the type of this literal
	 */
	public RustEnumLiteral(CFG cfg, CodeLocation location, E value, Type staticType) {
		super(cfg, location, value, staticType);
	}
}
