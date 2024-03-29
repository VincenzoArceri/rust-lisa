package it.unipr.cfg.expression.literal;

import it.unipr.cfg.type.RustCharType;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.literal.Literal;

/**
 * Rust char literal.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustChar extends Literal<Character> {

	/**
	 * Build the char literal.
	 * 
	 * @param cfg      the {@link CFG} where this literal lies
	 * @param location the location where this literal is defined
	 * @param value    the char value
	 */
	public RustChar(CFG cfg, CodeLocation location, Character value) {
		// TODO: need to change type of this expression
		// once we have modeled Rust types
		super(cfg, location, value, RustCharType.getInstance());
	}

}
