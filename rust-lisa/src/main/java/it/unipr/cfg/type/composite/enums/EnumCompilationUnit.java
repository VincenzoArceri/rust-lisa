package it.unipr.cfg.type.composite.enums;

import java.util.Collection;
import java.util.HashSet;

import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.cfg.CodeLocation;

public class EnumCompilationUnit extends CompilationUnit {

	private Collection<RustEnumVariant> variants;
	
	public EnumCompilationUnit(CodeLocation location, String name, boolean sealed) {
		super(location, name, sealed);
		variants = new HashSet<>();
	}
	
	public Collection<RustEnumVariant> getVariants() {
		Collection<RustEnumVariant> result = new HashSet<>();
		for (RustEnumVariant variant : variants)
			result.add(variant);
		
		return result;
	}
	
	public void addVariant(RustEnumVariant variant) {
		variants.add(variant);
	}

}
