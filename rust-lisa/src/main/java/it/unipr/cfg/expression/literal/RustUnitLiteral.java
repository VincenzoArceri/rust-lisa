package it.unipr.cfg.expression.literal;

import it.unipr.cfg.type.RustUnitType;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.literal.Literal;

/**
 * Rust unit literal.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustUnitLiteral extends Literal<Void> {

	/**
	 * Build the unit literal.
	 * 
	 * @param cfg      the {@link CFG} where this literal lies
	 * @param location the location where this literal is defined
	 */
	public RustUnitLiteral(CFG cfg, CodeLocation location) {
		super(cfg, location, null, RustUnitType.getInstance());
	}

}
