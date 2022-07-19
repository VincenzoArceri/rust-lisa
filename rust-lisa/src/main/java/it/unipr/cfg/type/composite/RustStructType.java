package it.unipr.cfg.type.composite;

import it.unipr.cfg.type.RustType;
import it.unipr.cfg.type.composite.enums.RustEnumVariant;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.UnitType;
import it.unive.lisa.type.Untyped;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Builds the Rust struct type.
 *
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustStructType implements UnitType, RustType, RustEnumVariant {

	private static final Map<String, RustStructType> INSTANCES = new HashMap<>();

	/**
	 * Yields a unique instance (either an existing one or a fresh one) of
	 * {@link RustStructType} representing a struct type with the given
	 * {@code name}, representing the given {@code unit}.
	 * 
	 * @param name the name of the struct type
	 * @param unit the unit underlying this type
	 * 
	 * @return the unique instance of {@link RustStructType} representing the
	 *             struct type with the given name
	 */
	public static RustStructType lookup(String name, CompilationUnit unit) {
		return INSTANCES.computeIfAbsent(name, x -> new RustStructType(name, unit));
	}

	/**
	 * Retrieve a single instance of a Rust struct types.
	 * 
	 * @param name the name of the struct
	 * 
	 * @return all instances of a Rust struct types, null otherwise
	 */
	public static RustStructType get(String name) {
		return INSTANCES.get(name);
	}

	/**
	 * Remove all instances of Rust struct types.
	 */
	public static void clearAll() {
		INSTANCES.clear();
	}

	/**
	 * Checks whether a struct type named {@code name} has been already built.
	 * 
	 * @param name the name of the struct type
	 * 
	 * @return whether a struct type named {@code name} has been already built.
	 */
	public static boolean has(String name) {
		return INSTANCES.containsKey(name);
	}

	/**
	 * Yields all instances of Rust struct types.
	 * 
	 * @return all instances of a Rust struct types
	 */
	public static Collection<Type> all() {
		Collection<Type> result = new HashSet<>();
		for (Type t : INSTANCES.values()) {
			result.add(t);
		}
		return result;
	}

	private final String name;
	private final CompilationUnit unit;

	/**
	 * Builds the struct type.
	 * 
	 * @param name  the name of the struct type
	 * @param unit  the compilation unit of the struct type
	 * @param types an ordered list of types inside the struct
	 */
	private RustStructType(String name, CompilationUnit unit) {
		this.name = name;
		this.unit = unit;
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		if (other instanceof RustStructType) {
			RustStructType o = (RustStructType) other;
			return (name.equals(o.name) && unit.equals(o.unit));
		}
		return other instanceof Untyped;
	}

	@Override
	public Type commonSupertype(Type other) {
		if (other instanceof RustStructType) {
			RustStructType o = (RustStructType) other;
			if (name.equals(o.name) && unit.equals(o.unit))
				return o;
		}
		return Untyped.INSTANCE;
	}

	@Override
	public Collection<Type> allInstances() {
		Collection<Type> instances = new HashSet<>();
		for (RustStructType in : INSTANCES.values())
			instances.add(in);
		return instances;
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

	@Override
	public Statement match(Expression toMatch) {
		// TODO too coarse
		return null;
	}

}
