package it.unipr.cfg.utils;

import java.util.List;

import it.unive.lisa.program.cfg.statement.Expression;

public class RustMethodKeeper implements RustAccessResolver {
	
	private final List<Expression> accessParameter;
	private String methodName;
	
	public RustMethodKeeper(String methodName, List<Expression> parameters) {
		this.methodName = methodName;
		this.accessParameter = parameters;
	}
	
}
