package it.unipr.cfg.statement;

import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.Assignment;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.Untyped;

/**
 * Rust assigment expression (e.g., let x = y).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustLetAssignment extends Assignment {

	/**
	 * Builds the let assigment expression.
	 * 
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param left     the left-hand side of this expression
	 * @param right    the right-hand side of this expression
	 */
	public RustLetAssignment(CFG cfg, CodeLocation location,
			Expression left, Expression right) {
		// TODO: need to change type of this expression
		// once we have modeled Rust types
		super(cfg, location, Untyped.INSTANCE, left, right);
	}

}
