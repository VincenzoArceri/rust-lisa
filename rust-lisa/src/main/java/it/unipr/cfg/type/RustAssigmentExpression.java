package it.unipr.cfg.type;

import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.Assignment;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.Untyped;

/**
 * Rust assigments expression (e.g., let x = y;).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustAssigmentExpression extends Assignment {

	/**
	 * Builds the and expression.
	 * 
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param left     the left-hand side of this expression
	 * @param right    the right-hand side of this expression
	 */
	public RustAssigmentExpression(CFG cfg, CodeLocation location,
			Expression left, Expression right) {
		// TODO: need to change type of this expression
		// once we have modeled Rust types
		super(cfg, location, Untyped.INSTANCE, left, right);
	}

}
