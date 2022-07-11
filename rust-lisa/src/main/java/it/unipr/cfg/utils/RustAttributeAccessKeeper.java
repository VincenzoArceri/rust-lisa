package it.unipr.cfg.utils;

import java.util.List;

import it.unive.lisa.program.cfg.statement.Expression;

public class RustAttributeAccessKeeper implements RustAccessResolver {
	private Expression expr;
	
	public RustAttributeAccessKeeper(Expression expr) {
		this.expr = expr;
	}
	
	public Expression getExpr() {
		return expr;
	}
	
}
