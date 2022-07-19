package it.unipr.cfg.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.abego.treelayout.internal.util.java.lang.string.StringUtil;
import org.apache.commons.lang3.StringUtils;

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

/**
 * Rust multiple name resolution for destructuring (e.g., A::B(a, b, c).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustMulitpleNameResolutionExpression extends NaryExpression {

	/**
	 * Build the multiple name resolution.
	 * 
	 * @param cfg      the {@link CFG} where this literal lies
	 * @param location the location where this literal is defined
	 * @param values   the values inside the literal with the first element the path resolution
	 */
	public RustMulitpleNameResolutionExpression(CFG cfg, CodeLocation location, Expression[] values) {
		super(cfg, location, "::", values);
	}

	@Override
	public String toString() {
		List<String> exprs = Arrays.asList(getSubExpressions())
				.stream()
				.map(x -> x.toString())
				.collect(Collectors.toList());
		
		return exprs.get(0) + "(" + StringUtils.join(exprs.subList(1, exprs.size()), ",") + ")";
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
