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
	public static final Set<RustArrayType> INSTANCE = new HashSet<>();

	/**
	 * If the same array type was already parsed, return the instance; otherwise
	 * add it and return it.
	 */
	public static RustArrayType lookup(RustArrayType type) {
		if (!INSTANCE.contains(type))
			INSTANCE.add(type);

		return INSTANCE.stream().filter(x -> x.equals(type)).findFirst().get();
	}

	/**
	 * In rust an array is characterized by type of its elements and length.
	 */
	private Type contentType;
	private Integer length;

	public RustArrayType(Type contentType, Integer length) {
		this.contentType = contentType;
		this.length = length;
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		if (other instanceof RustArrayType)
			return contentType.canBeAssignedTo(((RustArrayType) other).contentType)
					&& length.equals(((RustArrayType) other).length);

		return false;
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
		for (RustArrayType array : INSTANCE)
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
			return this.contentType == other.contentType && this.length == other.length;
		}
		return false;
	}

}
