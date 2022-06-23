package it.unipr.cfg.expression.literal;

import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.literal.Literal;
import it.unive.lisa.type.Untyped;

/**
 * Rust boolean literal (true, false).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class RustBoolean extends Literal<Boolean> {

	/**
	 * Build the boolean literal.
	 * 
	 * @param cfg      the {@link CFG} where this literal lies
	 * @param location the location where this literal is defined
	 * @param value    the boolean value
	 */
	public RustBoolean(CFG cfg, CodeLocation location, Boolean value) {
		// TODO: need to change type of this expression
		// once we have modeled Rust types
		super(cfg, location, value, Untyped.INSTANCE);
	}
}
