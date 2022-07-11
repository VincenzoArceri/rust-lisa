package it.unipr.cfg.expression;

import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

/**
 * A Rust reference to a variable of the current CFG, identified by its name.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustVariableRef extends VariableRef {

	private final String name;
	private final boolean mutable;

	/**
	 * Builds the untyped variable reference, identified by its name. The type
	 * of this variable reference is {@link Untyped#INSTANCE}.
	 * 
	 * @param cfg      the cfg that this expression belongs to
	 * @param location the location of this variable reference
	 * @param name     the name of this variable reference
	 * @param mutable  the mutability of this variable reference
	 */
	public RustVariableRef(CFG cfg, CodeLocation location, String name, boolean mutable) {
		this(cfg, location, name, mutable, Untyped.INSTANCE);
	}

	/**
	 * Builds the variable reference, identified by its name, happening at the
	 * given location in the program.
	 * 
	 * @param cfg      the cfg that this expression belongs to
	 * @param location the location where the expression is defined within the
	 *                     program
	 * @param name     the name of this variable
	 * @param mutable  the mutability of this variable reference
	 * @param type     the type of this variable
	 */
	public RustVariableRef(CFG cfg, CodeLocation location, String name, boolean mutable, Type type) {
		super(cfg, location, name, type);
		this.name = name;
		this.mutable = mutable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode()) + ((mutable ? 1 : 0));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!super.equals(obj))
			return false;

		if (getClass() != obj.getClass())
			return false;

		RustVariableRef other = (RustVariableRef) obj;

		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;

		if (other.mutable != this.mutable)
			return false;

		return true;
	}

	/**
	 * Yields true if this variable is mutable.
	 * 
	 * @return true if this variable is mutable
	 */
	public boolean isMutable() {
		return mutable;
	}

	@Override
	public String toString() {
		return (mutable ? "mut " : "") + name;
	}

}
