package it.unipr.cfg.type.composite.enums;

import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;

public class RustEnumSimpleVariant implements RustEnumVariant {

	private String name;

	public RustEnumSimpleVariant(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public Statement match(Expression toMatch) {
		// TODO too coarse
		return null;
	}
}
