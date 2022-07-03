package it.unipr.cfg;

import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

public class RustTyper {
	public static Type resultType(Expression left, Expression right) {
		if (left.getStaticType().equals(right.getStaticType()))
			return left.getStaticType();
		
		return Untyped.INSTANCE;
	}
	
	public static Type resultType(Expression expr) {
		if (expr.getStaticType() instanceof Untyped)
			return Untyped.INSTANCE;
		
		return expr.getStaticType();
	}
}
