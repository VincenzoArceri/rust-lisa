package it.unipr.cfg.type.composite;

import it.unipr.cfg.type.RustType;
import it.unive.lisa.type.ArrayType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Instance of the Rust array type.
 *
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustArrayType implements ArrayType, RustType {

	/**
	 * Collection of all arrays.
	 */
	private static final Set<RustArrayType> INSTANCES = new HashSet<>();

	/**
	 * Yields the first instance that matches array type requested or adds it if
	 * not present.
	 * 
	 * @param type the {@link RustArrayType} to look for
	 * 
	 * @return the first {@link RustArrayType} inserted of the same kind
	 */
	public static RustArrayType lookup(RustArrayType type) {
		if (!INSTANCES.contains(type))
			INSTANCES.add(type);

		return INSTANCES.stream().filter(x -> x.equals(type)).findFirst().get();
	}

	/**
	 * Remove all instances of Rust tuple types.
	 */
	public static void clearAll() {
		INSTANCES.clear();
	}

	/**
	 * Yields all instances of Rust tuple types.
	 * 
	 * @return all instances of a Rust tuple types
	 */
	public static Collection<Type> all() {
		Collection<Type> result = new HashSet<>();
		for (Type t : INSTANCES.toArray(new RustArrayType[0])) {
			result.add(t);
		}
		return result;
	}

	/**
	 * In Rust an array is characterized by type of its elements length, and its
	 * mutability.
	 */
	private final Type contentType;
	private final Integer length;

	/**
	 * Construct the {@link RustArrayType} object.
	 * 
	 * @param contentType the type of the element in the array
	 * @param length      the length of the array
	 */
	public RustArrayType(Type contentType, Integer length) {
		this.contentType = Objects.requireNonNull(contentType);
		this.length = Objects.requireNonNull(length);
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		if (other instanceof RustArrayType)
			return contentType.canBeAssignedTo(((RustArrayType) other).contentType)
					&& length.equals(((RustArrayType) other).length);

		return other instanceof Untyped;
	}

	@Override
	public Type commonSupertype(Type other) {
		if (other instanceof RustArrayType)
			if (contentType.canBeAssignedTo(((RustArrayType) other).contentType)
					&& length.equals(((RustArrayType) other).length))
				return other;
		return Untyped.INSTANCE;
	}

	@Override
	public Collection<Type> allInstances() {
		Collection<Type> instances = new HashSet<>();
		for (RustArrayType array : INSTANCES)
			instances.add(array);

		return instances;
	}

	@Override
	public Type getInnerType() {
		return contentType;
	}

	@Override
	public Type getBaseType() {
		if (contentType instanceof RustArrayType)
			return ((RustArrayType) contentType).getBaseType();

		return contentType;
	}

	@Override
	public int hashCode() {
		return contentType.hashCode() + length;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		RustArrayType other = (RustArrayType) obj;

		if (contentType == null) {
			if (other.contentType != null)
				return false;
		} else if (!contentType.equals(other.contentType))
			return false;

		if (length == null) {
			if (other.length != null)
				return false;
		} else if (!length.equals(other.length))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "[" + contentType.toString() + "; " + length.toString() + "]";
	}

}
