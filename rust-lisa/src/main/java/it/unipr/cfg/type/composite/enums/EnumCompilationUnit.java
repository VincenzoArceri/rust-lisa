package it.unipr.cfg.type.composite.enums;

import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.cfg.CodeLocation;
import java.util.Collection;
import java.util.HashSet;

/**
 * Rust compilation unit for enums.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class EnumCompilationUnit extends CompilationUnit {

	private Collection<RustEnumVariant> variants;

	/**
	 * Construct the {@link EnumCompilationUnit} object.
	 * 
	 * @param location the type of the element in the array
	 * @param name     the name of the compilation unit
	 * @param sealed   true if the compilation unit is sealead
	 */
	public EnumCompilationUnit(CodeLocation location, String name, boolean sealed) {
		super(location, name, sealed);
		variants = new HashSet<>();
	}

	/**
	 * Yields a list containing the variants of the enum.
	 * 
	 * @return a collection of {@link RustEnumVariant}
	 */
	public Collection<RustEnumVariant> getVariants() {
		Collection<RustEnumVariant> result = new HashSet<>();
		for (RustEnumVariant variant : variants)
			result.add(variant);

		return result;
	}

	/**
	 * Add an {@link RustEnumVariant} to the collection of this compilation
	 * unit.
	 * 
	 * @param variant the {@link RustEnumVariant} to be put inside
	 */
	public void addVariant(RustEnumVariant variant) {
		variants.add(variant);
	}

}
