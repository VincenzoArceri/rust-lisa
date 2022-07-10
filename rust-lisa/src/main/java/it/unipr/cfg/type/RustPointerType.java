package it.unipr.cfg.type;

import it.unive.lisa.caches.Caches;
import it.unive.lisa.type.PointerType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import it.unive.lisa.util.collections.externalSet.ExternalSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Unique instance of the Rust pointer type.
 *
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustPointerType implements PointerType, RustType {

	/**
	 * Collection of all pointer types.
	 */
	private static final Set<RustPointerType> INSTANCES = new HashSet<>();

	/**
	 * Yields the first instance that matches pointer type requested or adds it
	 * if not present.
	 * 
	 * @param type the {@link RustPointerType} to look for
	 * 
	 * @return the first {@link RustPointerType} inserted of the same kind
	 */
	public static RustPointerType lookup(RustPointerType type) {
		if (!INSTANCES.contains(type))
			INSTANCES.add(type);

		return INSTANCES.stream().filter(x -> x.equals(type)).findFirst().get();
	}

	/**
	 * Clear all instances of Rust pointer types.
	 */
	public static void clearAll() {
		INSTANCES.clear();
	}

	/**
	 * Yields all instances of Rust pointer types.
	 * 
	 * @return all instances of a Rust pointer types
	 */
	public static Collection<Type> all() {
		Collection<Type> result = new HashSet<>();
		for (Type t : INSTANCES.toArray(new RustPointerType[0])) {
			result.add(t);
		}
		return result;
	}

	private final Type innerType;
	private final boolean mutable;

	/**
	 * Constructor for {@link RustPointerType}.
	 * 
	 * @param innerType  inner type on which this pointer points
	 * @param mutable 	 true if this is an instance of *mut pointer, false if this is an instance of *const
	 */
	public RustPointerType(Type innerType, boolean mutable) {
		this.innerType = Objects.requireNonNull(innerType);
		this.mutable = mutable;
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		if (other instanceof RustPointerType)
			return innerType.canBeAssignedTo(((RustPointerType) other).innerType) && ((RustPointerType) other).mutable == this.mutable;
		return other instanceof Untyped;
	}

	@Override
	public Type commonSupertype(Type other) {
		if (other instanceof RustPointerType)
			if (innerType.canBeAssignedTo(((RustPointerType) other).innerType) && ((RustPointerType) other).mutable == this.mutable)
				return other;
		return Untyped.INSTANCE;
	}

	@Override
	public Collection<Type> allInstances() {
		Collection<Type> instances = new HashSet<>();
		for (RustPointerType array : INSTANCES)
			instances.add(array);

		return instances;
	}

	@Override
	public int hashCode() {
		return innerType.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		RustPointerType other = (RustPointerType) obj;

		if (innerType == null) {
			if (other.innerType != null)
				return false;
		} else if (!innerType.equals(other.innerType))
			return false;
		
		if (other.mutable != this.mutable)
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "*" + (mutable? "mut " : "const ") + innerType.toString();
	}

	@Override
	public ExternalSet<Type> getInnerTypes() {
		return Caches.types().mkSingletonSet(innerType);
	}

}
