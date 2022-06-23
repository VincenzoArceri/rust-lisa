package it.unipr.cfg.expression.literal;

import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.literal.Literal;
import it.unive.lisa.type.Untyped;

/**
 * Rust integer literal.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class RustInteger extends Literal<Integer> {

	/**
	 * Builds the integer literal.
	 * 
	 * @param cfg      the {@link CFG} where this literal lies
	 * @param location the location where this literal is defined
	 * @param value    the integer value
	 */
	public RustInteger(CFG cfg, CodeLocation location, Integer value) {
		// TODO: need to change type of this expression
		// once we have modeled Rust types
		super(cfg, location, value, Untyped.INSTANCE);
	}
}