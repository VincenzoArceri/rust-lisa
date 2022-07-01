package it.unipr.cfg.expression;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Rust access a child expression (e.g., x.y or x.z()).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustAccessMemberFunctionExpression extends NaryExpression {

	/**
	 * Builds the cast expression.
	 * 
	 * @param cfg          the {@link CFG} where this expression lies
	 * @param location     the location where this expression is defined
	 * @param father       the father of this access
	 * @param functionName the name of the function to be accessed
	 * @param parameters   the parameters to be passed to compute the function
	 */
	public RustAccessMemberFunctionExpression(CFG cfg, CodeLocation location, Expression father, String functionName,
			Expression[] parameters) {
		// TODO: need to change type of this expression
		// once we have modeled Rust types
		super(cfg, location,
				father + "." + functionName
						+ Arrays.asList(parameters).stream().map((x) -> x.toString()).collect(Collectors.joining(",")),
				parameters);
	}

	@Override
	public <A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> AnalysisState<A, H, V, T> expressionSemantics(
					InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
					ExpressionSet<SymbolicExpression>[] params, StatementStore<A, H, V, T> expressions)
					throws SemanticException {
		// TODO too coarse
		return state.top();
	}

}