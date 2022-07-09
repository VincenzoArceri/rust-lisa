package it.unipr.cfg.type.composite;

import it.unipr.cfg.type.RustType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Instance of the Rust tuple type.
 *
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustTupleType implements RustType {

	/**
	 * Collection of all parse tuples.
	 */
	private static final Set<RustTupleType> INSTANCES = new HashSet<>();

	/**
	 * Yields the first instance that matches tuple type requested or adds it if
	 * not present.
	 * 
	 * @param type the {@link RustTupleType} to look for
	 * 
	 * @return the first {@link RustTupleType} inserted of the same kind
	 */
	public static RustTupleType lookup(RustTupleType type) {
		if (!INSTANCES.contains(type))
			INSTANCES.add(type);

		return INSTANCES.stream().filter(x -> x.equals(type)).findFirst().get();
	}

	private final List<Type> types;
	private final boolean mutable;

	/**
	 * Construct the {@link RustTupleType} object.
	 * 
	 * @param types      an ordered list of types inside the tuple
	 * @param mutability the mutability of the tuple
	 */
	public RustTupleType(List<Type> types, boolean mutability) {
		this.types = Objects.requireNonNull(types);
		this.mutable = mutability;
	}

	private boolean checkAssignment(Object other) {
		if (other instanceof RustTupleType) {
			RustTupleType o = (RustTupleType) other;

			if (o.types.size() != this.types.size())
				return false;

			return this.types.equals(o.types);
		}

		return other instanceof Untyped;
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		return this.checkAssignment(other);
	}

	@Override
	public Type commonSupertype(Type other) {
		return checkAssignment(other) ? other : Untyped.INSTANCE;
	}

	@Override
	public Collection<Type> allInstances() {
		Collection<Type> instances = new HashSet<>();
		for (RustTupleType tuple : INSTANCES)
			instances.add(tuple);

		return instances;
	}

	@Override
	public int hashCode() {
		return Objects.hash(types);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		RustTupleType other = (RustTupleType) obj;

		if (types == null) {
			if (other.types != null)
				return false;
		} else if (!types.equals(other.types))
			return false;

		if (mutable != other.mutable)
			return false;

		return true;
	}

	@Override
	public String toString() {
		return (mutable ? "mut " : "") + "(" + types.stream().map(t -> t.toString()).collect(Collectors.joining(", "))
				+ ")";
	}

	@Override
	public boolean isMutable() {
		return mutable;
	}
}
