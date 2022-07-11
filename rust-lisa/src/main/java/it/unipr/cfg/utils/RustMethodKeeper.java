package it.unipr.cfg.utils;

import java.util.List;

import it.unive.lisa.program.cfg.statement.Expression;

public class RustMethodKeeper implements RustAccessResolver {
	
	private final List<Expression> methodParameters;
	private String methodName;
	
	public RustMethodKeeper(String methodName, List<Expression> parameters) {
		this.methodName = methodName;
		this.methodParameters = parameters;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public List<Expression> getAccessParameter() {
		return methodParameters;
	}
}
