package it.unipr.cfg.type.numeric.unsigned;

import it.unipr.cfg.type.RustType;
import it.unive.lisa.type.NumericType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import java.util.Collection;
import java.util.Collections;

/**
 * Unique instance of the Rust usize type.
 *
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustUsizeType implements NumericType, RustType {

	private static final RustUsizeType INSTANCE = new RustUsizeType(false);
	private static final RustUsizeType MUTABLE_INSTANCE = new RustUsizeType(true);

	/**
	 * Yields the singleton instance based on mutability.
	 * 
	 * @param mutability the mutability of the type
	 * 
	 * @return the correct instance based on the type mutability
	 */
	public static RustUsizeType getInstance(boolean mutability) {
		return mutability ? MUTABLE_INSTANCE : INSTANCE;
	}

	private final boolean mutable;

	private RustUsizeType(boolean mutability) {
		mutable = mutability;
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		return other instanceof RustUsizeType || other instanceof Untyped;
	}

	@Override
	public Type commonSupertype(Type other) {
		// Rust cast ought to be explicit by design
		// https://doc.rust-lang.org/rust-by-example/types/cast.html
		if (other instanceof RustUsizeType)
			return other;
		return Untyped.INSTANCE;
	}

	@Override
	public Collection<Type> allInstances() {
		return Collections.singleton(INSTANCE);
	}

	@Override
	public boolean is8Bits() {
		return false;
	}

	@Override
	public boolean is16Bits() {
		return false;
	}

	@Override
	public boolean is32Bits() {
		return true;
	}

	@Override
	public boolean is64Bits() {
		return true;
	}

	@Override
	public boolean isUnsigned() {
		return true;
	}

	@Override
	public boolean isIntegral() {
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof RustUsizeType && ((RustUsizeType) obj).mutable == this.mutable;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(INSTANCE);
	}

	@Override
	public String toString() {
		return (mutable ? "mut " : "") + "usize";
	}

	@Override
	public boolean isMutable() {
		return mutable;
	}

}
