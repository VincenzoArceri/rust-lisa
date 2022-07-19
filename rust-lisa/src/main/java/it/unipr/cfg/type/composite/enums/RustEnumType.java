package it.unipr.cfg.type.composite.enums;

import it.unipr.cfg.type.RustType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.UnitType;
import it.unive.lisa.type.Untyped;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Instance of the Rust enum type.
 *
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustEnumType implements RustType, UnitType {

	/**
	 * Collection of all enums.
	 */
	private static final Set<RustEnumType> INSTANCES = new HashSet<>();

	/**
	 * Yields the first instance that matches enum type requested or adds it if
	 * not present.
	 * 
	 * @param type the {@link RustEnumType} to look for
	 * 
	 * @return the first {@link RustEnumType} inserted of the same kind
	 */
	public static RustEnumType lookup(RustEnumType type) {
		if (!INSTANCES.contains(type))
			INSTANCES.add(type);

		return INSTANCES.stream().filter(x -> x.equals(type)).findFirst().get();
	}

	/**
	 * Remove all instances of Rust enum types.
	 */
	public static void clearAll() {
		INSTANCES.clear();
	}

	/**
	 * Yields all instances of Rust enum types.
	 * 
	 * @return all instances of a Rust enum types
	 */
	public static Collection<Type> all() {
		Collection<Type> result = new HashSet<>();
		for (Type t : INSTANCES.toArray(new RustEnumType[0])) {
			result.add(t);
		}
		return result;
	}

	/**
	 * In Rust an enum has its own values.
	 */
	private final String name;
	private final EnumCompilationUnit unit;

	/**
	 * Construct the {@link RustEnumType} object. Note that {@code variantNames}
	 * and {@code types} must have the same length.
	 * 
	 * @param name the name of this enum
	 * @param unit the compilation unit it belongs to
	 */
	public RustEnumType(String name, EnumCompilationUnit unit) {
		this.name = Objects.requireNonNull(name);
		this.unit = Objects.requireNonNull(unit);
	}

	@Override
	public boolean canBeAssignedTo(Type o) {
		if (o instanceof RustEnumType) {
			RustEnumType other = (RustEnumType) o;
			return name.equals(other.name)
					&& unit.equals(other.unit);
		}

		return o instanceof Untyped;
	}

	@Override
	public Type commonSupertype(Type o) {
		if (o instanceof RustEnumType) {
			RustEnumType other = (RustEnumType) o;
			if (name.equals(other.name)
					&& unit.equals(other.unit))
				return other;
		}

		return Untyped.INSTANCE;
	}

	@Override
	public Collection<Type> allInstances() {
		Collection<Type> instances = new HashSet<>();
		for (RustEnumType array : INSTANCES)
			instances.add(array);

		return instances;
	}

	@Override
	public int hashCode() {
		return (name.hashCode()) ^ unit.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		RustEnumType other = (RustEnumType) obj;

		if (unit == null) {
			if (other.unit != null)
				return false;
		} else if (!unit.equals(other.unit))
			return false;

		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;

		return true;
	}

	@Override
	public String toString() {
		String result = "enum " + name + "{";
		for (RustEnumVariant variant : unit.getVariants()) {
			result += variant.toString() + ",\n";
		}
		return result + "}";
	}

	/**
	 * Yields the compilation unit.
	 * 
	 * @return the compilation unit of this type
	 */
	public EnumCompilationUnit getUnit() {
		return unit;
	}
}
