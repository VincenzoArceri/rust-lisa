package it.unipr.cfg.utils;

import java.util.List;

import it.unive.lisa.program.cfg.statement.Expression;

public class RustAttributeNameKeeper implements RustAccessResolver {
	private String name;
	
	public RustAttributeNameKeeper(String name) {
		this.name = name;
	}
	
}
