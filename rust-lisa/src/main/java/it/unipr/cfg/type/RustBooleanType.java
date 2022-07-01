package it.unipr.cfg.type;

import it.unive.lisa.type.BooleanType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import java.util.Collection;
import java.util.Collections;

/**
 * Unique instance of the Rust boolean type.
 *
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class RustBooleanType implements BooleanType {

	/**
	 * Unique instance of RustBoolean type.
	 */
	public static final RustBooleanType INSTANCE = new RustBooleanType();

	private RustBooleanType() {
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		return other instanceof RustBooleanType || other instanceof Untyped;
	}

	@Override
	public Type commonSupertype(Type other) {
		if (other instanceof RustBooleanType)
			return other;
		return Untyped.INSTANCE;
	}

	@Override
	public Collection<Type> allInstances() {
		return Collections.singleton(INSTANCE);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof RustBooleanType;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(INSTANCE);
	}
}
