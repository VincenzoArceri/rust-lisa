package it.unipr.cfg.utils;

import java.util.List;

import it.unive.lisa.program.cfg.statement.Expression;

public class RustArrayAccessKeeper implements RustAccessResolver {
	
	private final Expression expr;
	
	public RustArrayAccessKeeper(Expression expr) {
		this.expr = expr;
	}

}
