package it.unipr.cfg.expression.literal.enums;

import it.unipr.cfg.type.RustType;
import it.unipr.cfg.type.composite.RustTupleType;
import it.unipr.cfg.type.composite.enums.RustEnumType;
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
 * Rust tuple literal.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustEnumTupleLiteral extends RustEnumLiteral<NaryExpression> {

	/**
	 * Build the enum struct literal.
	 * 
	 * @param cfg      		the {@link CFG} where this literal lies
	 * @param location 		the location where this literal is defined
	 * @param assignments   the values inside the literal
	 * @param enumType 		the enum type of this literal
	 */
	public RustEnumTupleLiteral(CFG cfg, CodeLocation location, NaryExpression assignments, RustEnumType enumType) {
		super(cfg, location, assignments, enumType);
	}

	@Override
	public String toString() {
		return "("
				+ Arrays.asList(getValue().getSubExpressions()).stream().map(e -> e.toString()).collect(Collectors.joining(", "))
				+ ")";
	}
}
