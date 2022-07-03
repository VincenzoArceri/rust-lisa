package it.unipr.cfg;

import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

/**
 * Used to statically infer the type of a expression given the types of the
 * subcomponents.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustTyper {

	/**
	 * Infers the static type of the binary expression.
	 * 
	 * @param left  the left-hand side of this expression
	 * @param right the right-hand side of this expression
	 * 
	 * @return {@link Untyped} if one of the expressions are {@link Untyped},
	 *             the common static type otherwise
	 */
	public static Type resultType(Expression left, Expression right) {
		if (left.getStaticType().equals(right.getStaticType()))
			return left.getStaticType();

		return Untyped.INSTANCE;
	}

	/**
	 * Infers the static type of the unary expression.
	 * 
	 * @param expr the left-hand side of this expression
	 * 
	 * @return {@link Untyped} if the expression is {@link Untyped}, the static
	 *             type otherwise
	 */
	public static Type resultType(Expression expr) {
		if (expr.getStaticType() instanceof Untyped)
			return Untyped.INSTANCE;

		return expr.getStaticType();
	}
}
