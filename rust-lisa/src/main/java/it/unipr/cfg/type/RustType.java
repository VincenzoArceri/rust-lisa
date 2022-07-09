package it.unipr.cfg.type;

import it.unive.lisa.type.Type;

/**
 * Interface for Rust Type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public interface RustType extends Type {

	/**
	 * Yields {@code true} if and only if this type is mutable.
	 * 
	 * @return {@code true} if that type is mutable
	 */
	public boolean isMutable();
}
