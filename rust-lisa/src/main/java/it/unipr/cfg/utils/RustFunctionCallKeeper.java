package it.unipr.cfg.utils;

import java.util.List;

import it.unive.lisa.program.cfg.statement.Expression;

public class RustFunctionCallKeeper implements RustAccessResolver {
	
	private final List<Expression> accessParameter;
	
	public RustFunctionCallKeeper(List<Expression> parameters) {
		this.accessParameter = parameters;
	}
	
}
