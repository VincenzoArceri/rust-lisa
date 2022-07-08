package it.unipr.cfg.expression.literal;

import it.unipr.cfg.type.RustStrType;
import it.unipr.cfg.type.composite.RustReferenceType;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.literal.Literal;
import it.unive.lisa.type.ReferenceType;

/**
 * Rust string literal.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustString extends Literal<String> {

	/**
	 * Builds the string literal.
	 * 
	 * @param cfg      the {@link CFG} where this literal lies
	 * @param location the location where this literal is defined
	 * @param value    the string value
	 */
	public RustString(CFG cfg, CodeLocation location, String value) {
		// TODO: need to change type of this expression
		// once we have modeled Rust types
		super(cfg, location, value, new RustReferenceType(RustStrType.getInstance(false), false));
	}
}
