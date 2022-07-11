package it.unipr.cfg.utils;

import it.unive.lisa.program.cfg.statement.Expression;
import java.util.List;

/**
 * Rust information keeper for calling methods.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustMethodKeeper implements RustAccessResolver {

	private final List<Expression> methodParameters;
	private String methodName;

	/**
	 * Constructs a {@link RustFunctionCallKeeper}.
	 * 
	 * @param methodName the name of the method to be called
	 * @param parameters the parameters kept inside this type
	 */
	public RustMethodKeeper(String methodName, List<Expression> parameters) {
		this.methodName = methodName;
		this.methodParameters = parameters;
	}

	/**
	 * Yields the name of the method.
	 * 
	 * @return the name of the function called
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Yields the list of parameters as expressions.
	 * 
	 * @return expressions kept inside this type
	 */
	public List<Expression> getAccessParameter() {
		return methodParameters;
	}
}
