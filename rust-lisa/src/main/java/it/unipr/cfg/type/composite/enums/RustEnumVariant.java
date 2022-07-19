package it.unipr.cfg.type.composite.enums;

import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;

public interface RustEnumVariant {
	
	public Statement match(Expression toMatch);
	
	@Override
	public String toString();
}
