package it.unipr.cfg.type.numeric.signed;

import it.unipr.cfg.type.RustType;
import it.unive.lisa.type.NumericType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import java.util.Collection;
import java.util.Collections;

/**
 * Unique instance of the Rust i16 type.
 *
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustI16Type implements NumericType, RustType {

	private static final RustI16Type INSTANCE = new RustI16Type(false);
	private static final RustI16Type MUTABLE_INSTANCE = new RustI16Type(true);

	public static RustI16Type getInstance(boolean mutability) {
		return mutability? INSTANCE : MUTABLE_INSTANCE;
	}
	
	private final boolean mutable;

	private RustI16Type(boolean mutability) {
		mutable = mutability;
	}
	@Override
	public boolean canBeAssignedTo(Type other) {
		return other instanceof RustI16Type || other instanceof Untyped;
	}

	@Override
	public Type commonSupertype(Type other) {
		// Rust cast ought to be explicit by design
		// https://doc.rust-lang.org/rust-by-example/types/cast.html
		if (other instanceof RustI16Type)
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
		return true;
	}

	@Override
	public boolean is32Bits() {
		return false;
	}

	@Override
	public boolean is64Bits() {
		return false;
	}

	@Override
	public boolean isUnsigned() {
		return false;
	}

	@Override
	public boolean isIntegral() {
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof RustI16Type && ((RustI16Type)obj).mutable == this.mutable;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(INSTANCE);
	}

	@Override
	public String toString() {
		return (mutable? "mut " : "") + "i16";
	}
	
	@Override
	public boolean isMutable() {
		return mutable;
	}

}
