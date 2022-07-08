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
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustBooleanType implements BooleanType, RustType {

	private static final RustBooleanType INSTANCE = new RustBooleanType(false);
	private static final RustBooleanType MUTABLE_INSTANCE = new RustBooleanType(true);

	public static RustBooleanType getInstance(boolean mutability) {
		return mutability? MUTABLE_INSTANCE : INSTANCE;
	}
	
	private final boolean mutable;

	private RustBooleanType(boolean mutability) {
		mutable = mutability;
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
		return obj instanceof RustBooleanType && ((RustBooleanType)obj).mutable == this.mutable;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(INSTANCE);
	}

	@Override
	public String toString() {
		return (mutable? "mut " : "") + "bool";
	}
	
	@Override
	public boolean isMutable() {
		return mutable;
	}

}
