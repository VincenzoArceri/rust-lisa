package it.unipr.frontend;

import it.unipr.cfg.expression.RustDoubleRefExpression;
import it.unipr.cfg.type.RustBooleanType;
import it.unipr.cfg.type.RustCharType;
import it.unipr.cfg.type.RustPointerType;
import it.unipr.cfg.type.RustStrType;
import it.unipr.cfg.type.RustType;
import it.unipr.cfg.type.RustUnitType;
import it.unipr.cfg.type.composite.RustArrayType;
import it.unipr.cfg.type.composite.RustReferenceType;
import it.unipr.cfg.type.composite.RustTupleType;
import it.unipr.cfg.type.numeric.floating.RustF32Type;
import it.unipr.cfg.type.numeric.floating.RustF64Type;
import it.unipr.cfg.type.numeric.signed.RustI128Type;
import it.unipr.cfg.type.numeric.signed.RustI16Type;
import it.unipr.cfg.type.numeric.signed.RustI32Type;
import it.unipr.cfg.type.numeric.signed.RustI64Type;
import it.unipr.cfg.type.numeric.signed.RustI8Type;
import it.unipr.cfg.type.numeric.signed.RustIsizeType;
import it.unipr.cfg.type.numeric.unsigned.RustU128Type;
import it.unipr.cfg.type.numeric.unsigned.RustU16Type;
import it.unipr.cfg.type.numeric.unsigned.RustU32Type;
import it.unipr.cfg.type.numeric.unsigned.RustU64Type;
import it.unipr.cfg.type.numeric.unsigned.RustU8Type;
import it.unipr.cfg.type.numeric.unsigned.RustUsizeType;
import it.unipr.rust.antlr.RustBaseVisitor;
import it.unipr.rust.antlr.RustParser.ExprContext;
import it.unipr.rust.antlr.RustParser.Field_declContext;
import it.unipr.rust.antlr.RustParser.Field_decl_listContext;
import it.unipr.rust.antlr.RustParser.Fn_rtypeContext;
import it.unipr.rust.antlr.RustParser.IdentContext;
import it.unipr.rust.antlr.RustParser.Struct_tailContext;
import it.unipr.rust.antlr.RustParser.TyContext;
import it.unipr.rust.antlr.RustParser.Ty_pathContext;
import it.unipr.rust.antlr.RustParser.Ty_path_mainContext;
import it.unipr.rust.antlr.RustParser.Ty_path_segment_no_superContext;
import it.unipr.rust.antlr.RustParser.Ty_path_tailContext;
import it.unipr.rust.antlr.RustParser.Ty_sumContext;
import it.unipr.rust.antlr.RustParser.Ty_sum_listContext;
import it.unive.lisa.program.Global;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Type visitor for Rust, managing the parsing of Rust types.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustTypeVisitor extends RustBaseVisitor<Object> {

	private final String filePath;

	/**
	 * Constructs a {@link RustTypeVisitor} instance.
	 * 
	 * @param filePath the filePath String of reference
	 */
	public RustTypeVisitor(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public Type visitTy(TyContext ctx) {
		if (ctx.ty_path() != null) {
			// TODO Skipping macro_tail? part
			return visitTy_path(ctx.ty_path());
		}

		boolean mutable = false;

		switch (ctx.getChild(0).getText()) {
		case "_":
			return Untyped.INSTANCE;
		case "(":
			if (ctx.ty_sum() != null) {

				Type type = visitTy_sum(ctx.ty_sum());

				if (ctx.ty_sum_list() != null) {
					List<Type> remainingTypes = visitTy_sum_list(ctx.ty_sum_list());
					remainingTypes.add(0, type);

					RustTupleType tuple = new RustTupleType(remainingTypes, false);
					return RustTupleType.lookup(tuple);
				}

				return type;
			}

			return RustUnitType.getInstance();

		case "[":
			Type arrayType = visitTy_sum(ctx.ty_sum());

			if (ctx.expr() != null) {
				RustArrayType array = new RustArrayType(arrayType, getConstantValue(ctx.expr()), false);
				return RustArrayType.lookup(array);
			}

		case "&":
			// TODO Ignoring lifetimes for now
			if (ctx.getChild(2).getText().equals("mut"))
				mutable = true;

			return new RustReferenceType(visitTy(ctx.ty()), mutable);

		case "&&":
			// TODO Ignoring lifetimes for now
			if (ctx.getChild(2).getText().equals("mut"))
				mutable = true;
			
			return new RustReferenceType(new RustReferenceType(visitTy(ctx.ty()), mutable), false);

		case "*":
			if (ctx.mut_or_const().getText().equals("mut"))
				mutable = true;

			RustPointerType pointer = new RustPointerType(visitTy(ctx.ty()), mutable);
			return RustPointerType.lookup(pointer);

		default: // TODO skipping the other productions
			return null;
		}
	}

	private boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	private Integer getConstantValue(ExprContext expr) {
		// TODO We are just parsing the simple literal integers, the rest for
		// now is out of our scope
		if (isInteger(expr.getText()))
			return Integer.parseInt(expr.getText());

		return null;
	}

	@Override
	public RustType visitTy_path(Ty_pathContext ctx) {
		// TODO: we skip for the moment for_lifetime?
		return visitTy_path_main(ctx.ty_path_main());
	}

	@Override
	public RustType visitTy_path_main(Ty_path_mainContext ctx) {
		// TODO: we are currently handling just the production ty_path_tail
		return visitTy_path_tail(ctx.ty_path_tail());
	}

	@Override
	public RustType visitTy_path_tail(Ty_path_tailContext ctx) {
		// TODO: we are currently handling just the production
		// ty_path_segment_no_super
		return visitTy_path_segment_no_super(ctx.ty_path_segment_no_super());
	}

	@Override
	public RustType visitTy_path_segment_no_super(Ty_path_segment_no_superContext ctx) {
		// TODO skipping ty_args?
		return ctx.ident() == null ? null : visitIdent(ctx.ident());
	}

	@Override
	public RustType visitIdent(IdentContext ctx) {
		// TODO skipping "auto", "default" and "union"
		switch (ctx.Ident().getText()) {
		case "f32":
			return RustF32Type.getInstance();
		case "f64":
			return RustF64Type.getInstance();
		case "i8":
			return RustI8Type.getInstance();
		case "i16":
			return RustI16Type.getInstance();
		case "i32":
			return RustI32Type.getInstance();
		case "i64":
			return RustI64Type.getInstance();
		case "i128":
			return RustI128Type.getInstance();
		case "isize":
			return RustIsizeType.getInstance();
		case "u8":
			return RustU8Type.getInstance();
		case "u16":
			return RustU16Type.getInstance();
		case "u32":
			return RustU32Type.getInstance();
		case "u64":
			return RustU64Type.getInstance();
		case "u128":
			return RustU128Type.getInstance();
		case "usize":
			return RustUsizeType.getInstance();
		case "bool":
			return RustBooleanType.getInstance();
		case "&str":
			return RustStrType.getInstance();
		case "char":
			return RustCharType.getInstance();
		default:
			// TODO as of now, more complex types than the simple ones are not
			// parsed
			return null;
		}
	}

	// AGGIUNGI UN METOO has che passa il nome del tipo tracciato e fa la lookup
	// altrimento non è tracciato e se non lo è lancia una exception

	@Override
	public Type visitTy_sum(Ty_sumContext ctx) {
		// TODO skipping ('+' bound)? grammar branch
		return visitTy(ctx.ty());
	}

	@Override
	public List<Type> visitTy_sum_list(Ty_sum_listContext ctx) {
		List<Type> types = new LinkedList<>();
		for (Ty_sumContext tyCtx : ctx.ty_sum()) {
			types.add(visitTy_sum(tyCtx));
		}
		return types;
	}

	@Override
	public Type visitFn_rtype(Fn_rtypeContext ctx) {
		// TODO Ignoring "impl" and "!" types
		if (ctx.ty() != null)
			return visitTy(ctx.ty());

		return RustUnitType.getInstance();
	}

	@Override
	public List<Global> visitStruct_tail(Struct_tailContext ctx) {
		// TODO skipping first and second
		if (ctx.field_decl_list() != null)
			return visitField_decl_list(ctx.field_decl_list());

		// This is a struct with no declaration of types inside
		return new ArrayList<Global>();
	}

	@Override
	public Global visitField_decl(Field_declContext ctx) {
		// TODO skipping attr* and visibility?
		String name = ctx.ident().getText();
		Type type = visitTy_sum(ctx.ty_sum());

		return new Global(RustFrontendUtilities.locationOf(ctx, filePath), name, type);
	}

	@Override
	public List<Global> visitField_decl_list(Field_decl_listContext ctx) {
		List<Global> declarations = new ArrayList<>();
		for (Field_declContext fdCtx : ctx.field_decl())
			declarations.add(visitField_decl(fdCtx));

		return declarations;
	}

}
