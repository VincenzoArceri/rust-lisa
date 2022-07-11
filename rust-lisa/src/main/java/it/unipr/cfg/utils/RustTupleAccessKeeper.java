package it.unipr.cfg.utils;

import it.unive.lisa.program.cfg.statement.Expression;

public class RustTupleAccessKeeper implements RustAccessResolver {
	private final Expression expr;
	
	public RustTupleAccessKeeper(Expression expr) {
		this.expr = expr;
	}
	
	public Expression getExpr() {
		return expr;
	}
}
