package it.unipr.cfg.type.numeric.floating;

import it.unive.lisa.type.NumericType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import java.util.Collection;
import java.util.Collections;

/**
 * Unique instance of the Rust f32 type.
 *
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustF32Type implements NumericType {

	/**
	 * Unique instance of Rust f32 type.
	 */
	public static final RustF32Type INSTANCE = new RustF32Type();

	private RustF32Type() {
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		return other instanceof RustF32Type || other instanceof Untyped;
	}

	@Override
	public Type commonSupertype(Type other) {
		// Rust cast ought to be explicit by design
		// https://doc.rust-lang.org/rust-by-example/types/cast.html
		if (other instanceof RustF32Type)
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
		return false;
	}

	@Override
	public boolean isUnsigned() {
		return false;
	}

	@Override
	public boolean isIntegral() {
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof RustF32Type;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(INSTANCE);
	}
	
	@Override
	public String toString() {
		return "f32";
	}

}
