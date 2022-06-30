package it.unipr.cfg.type.composite;

import it.unive.lisa.type.ArrayType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Instance of the Rust array type.
 *
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustArrayType implements ArrayType {

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
	 * In rust an array is characterized by type of its elements and length.
	 */
	private Type contentType;
	private Integer length;

	/**
	 * Construct the RustArrayType object.
	 * 
	 * @param contentType the type of the element in the array
	 * @param length      the length of the array
	 */
	public RustArrayType(Type contentType, Integer length) {
		this.contentType = contentType;
		this.length = length;
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
		if (obj instanceof RustArrayType) {
			RustArrayType other = (RustArrayType) obj;
			return this.contentType.equals(other.contentType) && this.length == other.length;
		}
		return false;
	}

}
