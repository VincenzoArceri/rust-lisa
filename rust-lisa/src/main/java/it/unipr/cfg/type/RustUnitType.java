package it.unipr.cfg.type;

import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import java.util.Collection;
import java.util.Collections;

/**
 * Unique instance of the Rust unit type.
 *
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustUnitType implements RustType {

	private static final RustUnitType INSTANCE = new RustUnitType();

	/**
	 * Yields the singleton instance based on mutability.
	 * 
	 * @return the correct instance based on the type mutability
	 */
	public static RustUnitType getInstance() {
		return INSTANCE;
	}

	private RustUnitType() {
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		return other instanceof RustUnitType || other instanceof Untyped;
	}

	@Override
	public Type commonSupertype(Type other) {
		if (other instanceof RustUnitType)
			return other;
		return Untyped.INSTANCE;
	}

	@Override
	public Collection<Type> allInstances() {
		return Collections.singleton(INSTANCE);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof RustUnitType;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(INSTANCE);
	}

	@Override
	public String toString() {
		return "()";
	}

}
