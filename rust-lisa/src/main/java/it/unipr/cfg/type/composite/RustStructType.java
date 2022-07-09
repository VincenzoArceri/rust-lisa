package it.unipr.cfg.type.composite;

import it.unipr.cfg.type.RustType;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.UnitType;
import it.unive.lisa.type.Untyped;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Builds the Rust struct type.
 *
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustStructType implements UnitType, RustType {

	private static final Map<String, RustStructType> structTypes = new HashMap<>();

	/**
	 * Yields a unique instance (either an existing one or a fresh one) of
	 * {@link RustStructType} representing a struct type with the given
	 * {@code name}, representing the given {@code unit}.
	 * 
	 * @param name       the name of the struct type
	 * @param unit       the unit underlying this type
	 * @param mutability the mutability of the struct type
	 * @param types      an ordered list of types inside the struct
	 * 
	 * @return the unique instance of {@link RustStructType} representing the
	 *             struct type with the given name
	 */
	public static RustStructType lookup(String name, CompilationUnit unit, boolean mutability, Type... types) {
		return structTypes.computeIfAbsent(name, x -> new RustStructType(name, unit, mutability, types));
	}

	private final String name;
	private final CompilationUnit unit;
	private final boolean mutable;
	private final List<Type> types;

	/**
	 * Builds the struct type.
	 * 
	 * @param name    name of the struct type
	 * @param unit    the compilation unit of the struct type
	 * @param mutable the mutability of the struct type
	 * @param types   an ordered list of types inside the struct
	 */
	private RustStructType(String name, CompilationUnit unit, boolean mutable, Type... types) {
		this.name = name;
		this.unit = unit;
		this.mutable = true;
		this.types = Arrays.asList(types);
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		if (other instanceof RustArrayType) {
			RustStructType o = (RustStructType) other;
			return (name.equals(o.name) && unit.equals(o.unit) && types.equals(o.types));
		}
		return other instanceof Untyped;
	}

	@Override
	public Type commonSupertype(Type other) {
		if (other instanceof RustStructType) {
			RustStructType o = (RustStructType) other;
			if (name.equals(o.name) && unit.equals(o.unit) && types.equals(o.types))
				return o;
		}
		return Untyped.INSTANCE;
	}

	@Override
	public Collection<Type> allInstances() {
		Collection<Type> instances = new HashSet<>();
		for (RustStructType in : structTypes.values())
			instances.add(in);
		return instances;
	}

	@Override
	public boolean isMutable() {
		return mutable;
	}

	@Override
	public CompilationUnit getUnit() {
		return unit;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		RustStructType other = (RustStructType) obj;

		if (name == null) {
			if (other.name != null)
				return false;

		} else if (!name.equals(other.name))
			return false;

		if (unit == null) {
			if (other.unit != null)
				return false;

		} else if (!unit.equals(other.unit))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

}
