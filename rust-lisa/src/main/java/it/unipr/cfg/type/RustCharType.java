package it.unipr.cfg.type;

import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import java.util.Collection;
import java.util.Collections;

/**
 * Unique instance of the Rust char type.
 *
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustCharType implements Type {

	/**
	 * Unique instance of Rust char type.
	 */
	public static final RustCharType INSTANCE = new RustCharType();

	private RustCharType() {
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		return other instanceof RustCharType || other instanceof Untyped;
	}

	@Override
	public Type commonSupertype(Type other) {
		// Rust cast ought to be explicit by design
		// https://doc.rust-lang.org/rust-by-example/types/cast.html
		if (other instanceof RustCharType)
			return other;
		return Untyped.INSTANCE;
	}

	@Override
	public Collection<Type> allInstances() {
		return Collections.singleton(INSTANCE);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof RustCharType;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(INSTANCE);
	}

	@Override
	public String toString() {
		return "char";
	}

}
