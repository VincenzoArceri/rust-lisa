package it.unipr.cfg.type.composite;

import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Instance of the Rust tuple type.
 *
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustTupleType implements Type {

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

	/**
	 * Construct the {@link RustTupleType} object.
	 * 
	 * @param types an ordered list of types inside the tuple
	 */
	public RustTupleType(List<Type> types) {
		this.types = types;
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
