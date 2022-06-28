package it.unipr.cfg.type.composite;

import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Unique instance of the Rust array type.
 *
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustTupleType implements Type {

	/**
	 * Collection of all parse arrays.
	 */
	public static final Set<RustTupleType> INSTANCE = new HashSet<>();

	/**
	 * If the same array type was already parsed, return the instance; otherwise
	 * add it and return it.
	 */
	public static RustTupleType lookup(RustTupleType type) {
		if (!INSTANCE.contains(type))
			INSTANCE.add(type);

		return INSTANCE.stream().filter(x -> x.equals(type)).findFirst().get();
	}

	private List<Type> types;

	public RustTupleType(List<Type> types) {
		this.types = types;
	}

	public boolean checkAssignment(Object other) {
		if (other instanceof RustTupleType) {
			RustTupleType o = (RustTupleType) other;

			if (o.types.size() != this.types.size())
				return false;

			return this.types.equals(o.types);
		}

		return false;
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
		for (RustTupleType tuple : INSTANCE)
			instances.add(tuple);

		return instances;
	}

	@Override
	public int hashCode() {
		int accumulator = 0;
		for (Type t : types)
			accumulator += t.hashCode();

		return accumulator;
	}

	@Override
	public boolean equals(Object obj) {
		return types.equals(obj);
	}

}
