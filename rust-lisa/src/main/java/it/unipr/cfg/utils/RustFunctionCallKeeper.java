package it.unipr.cfg.utils;

import it.unive.lisa.program.cfg.statement.Expression;
import java.util.List;

/**
 * Rust information keeper for function calls.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustFunctionCallKeeper implements RustAccessResolver {

	private final List<Expression> parameters;

	/**
	 * Constructs a {@link RustFunctionCallKeeper}.
	 * 
	 * @param parameters the parameters kept inside this type
	 */
	public RustFunctionCallKeeper(List<Expression> parameters) {
		this.parameters = parameters;
	}

	/**
	 * Yields the list of parameters as expressions.
	 * 
	 * @return expressions kept inside this type
	 */
	public List<Expression> getParameters() {
		return parameters;
	}
}
