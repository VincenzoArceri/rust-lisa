package it.unipr.cfg.utils;

import java.util.List;

import it.unive.lisa.program.cfg.statement.Expression;

public class RustFunctionCallKeeper implements RustAccessResolver {
	
	private final List<Expression> parameters;
	
	public RustFunctionCallKeeper(List<Expression> parameters) {
		this.parameters = parameters;
	}
	
	public List<Expression> getParameters() {
		return parameters;
	}
}
