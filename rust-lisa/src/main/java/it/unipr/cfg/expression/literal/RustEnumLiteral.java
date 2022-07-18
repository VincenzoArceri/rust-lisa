package it.unipr.cfg.expression.literal;

import it.unipr.cfg.type.composite.RustEnumType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;

/**
 * Interface for Rust enum literals.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustEnumLiteral extends UnaryExpression {

	private String variantName;
	
	/**
	 * Build the enum literal.
	 * 
	 * @param cfg      the {@link CFG} where this literal lies
	 * @param location the location where this literal is defined
	 * @param enumType the enum that this values corresponds to
	 * @param name     the variant name of this enum
	 * @param value    the value of this enum
	 */
	public RustEnumLiteral(CFG cfg, CodeLocation location, RustEnumType enumType, String name, Expression value) {
		super(cfg, location, enumType.toString(), enumType, value);
		this.variantName = name;
	}

	@Override
	public String toString() {
		return getStaticType() + "::" + variantName + getSubExpressions();
	}

	@Override
	protected <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> unarySemantics(
			InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
			SymbolicExpression expr, StatementStore<A, H, V, T> expressions) throws SemanticException {
		// TODO too coarse
		return state.top();
	}

}
