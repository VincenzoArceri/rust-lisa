package it.unipr.cfg.expression.literal;

import it.unipr.cfg.type.numeric.floating.RustF64Type;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.literal.Literal;

/**
 * Rust float literal.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class RustFloat extends Literal<Float> {

	/**
	 * Build the float literal.
	 * 
	 * @param cfg      the {@link CFG} where this literal lies
	 * @param location the location where this literal is defined
	 * @param value    the float value
	 */
	public RustFloat(CFG cfg, CodeLocation location, Float value) {
		// TODO: need to change type of this expression
		// once we have modeled Rust types
		super(cfg, location, value, RustF64Type.getInstance(false));
	}
}