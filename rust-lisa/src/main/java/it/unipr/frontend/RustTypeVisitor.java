package it.unipr.frontend;

import static it.unipr.frontend.RustFrontendUtilities.locationOf;

import it.unipr.cfg.type.RustBooleanType;
import it.unipr.cfg.type.RustCharType;
import it.unipr.cfg.type.RustPointerType;
import it.unipr.cfg.type.RustStrType;
import it.unipr.cfg.type.RustType;
import it.unipr.cfg.type.RustUnitType;
import it.unipr.cfg.type.composite.RustArrayType;
import it.unipr.cfg.type.composite.RustReferenceType;
import it.unipr.cfg.type.composite.RustStructType;
import it.unipr.cfg.type.composite.RustTupleType;
import it.unipr.cfg.type.composite.enums.RustEnumSimpleVariant;
import it.unipr.cfg.type.composite.enums.RustEnumVariant;
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
import it.unipr.rust.antlr.RustParser.Enum_field_declContext;
import it.unipr.rust.antlr.RustParser.Enum_field_decl_listContext;
import it.unipr.rust.antlr.RustParser.Enum_tuple_fieldContext;
import it.unipr.rust.antlr.RustParser.Enum_tuple_field_listContext;
import it.unipr.rust.antlr.RustParser.Enum_variantContext;
import it.unipr.rust.antlr.RustParser.Enum_variant_listContext;
import it.unipr.rust.antlr.RustParser.Enum_variant_mainContext;
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
import it.unive.lisa.program.CompilationUnit;
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
	private final CompilationUnit unit;

	/**
	 * Constructs a {@link RustTypeVisitor} instance.
	 * 
	 * @param filePath the filePath String of reference
	 * @param unit     the compilation unit of reference
	 */
	public RustTypeVisitor(String filePath, CompilationUnit unit) {
		this.filePath = filePath;
		this.unit = unit;
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

					RustTupleType tuple = new RustTupleType(remainingTypes);
					return RustTupleType.lookup(tuple);
				}

				return type;
			}

			return RustUnitType.getInstance();

		case "[":
			Type arrayType = visitTy_sum(ctx.ty_sum());

			if (ctx.expr() != null) {
				RustArrayType array = new RustArrayType(arrayType, getConstantValue(ctx.expr()));
				return RustArrayType.lookup(array);
			}

		case "&":
			// TODO Ignoring lifetimes for now
			if (ctx.getChild(2) != null && ctx.getChild(2).getText().equals("mut"))
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
		case "str":
			return RustStrType.getInstance();
		case "char":
			return RustCharType.getInstance();
		default:
			if (RustStructType.has(ctx.Ident().getText())) {
				return RustStructType.lookup(filePath, unit);
			} else
				throw new IllegalAccessError("The name of this type was not found");
		}
	}

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

	@Override
	public RustEnumVariant visitEnum_variant(Enum_variantContext ctx) {
		// TODO skipping attr*
		return visitEnum_variant_main(ctx.enum_variant_main());
	}

	@Override
	public List<RustEnumVariant> visitEnum_variant_list(Enum_variant_listContext ctx) {
		List<RustEnumVariant> variants = new ArrayList<>();
		for (Enum_variantContext variantCtx : ctx.enum_variant())
			variants.add(visitEnum_variant(variantCtx));

		return variants;
	}

	@Override
	public RustEnumVariant visitEnum_variant_main(Enum_variant_mainContext ctx) {
		String name = ctx.ident().getText();

		if (ctx.expr() != null) {
			// TODO This is a custom discriminant for a fieldless enumeration.
			// This could be cast to an integer later on. This is too coarse for
			// now.
			// Please see
			// "https://doc.rust-lang.org/reference/items/enumerations.html#custom-discriminant-values-for-fieldless-enumerations"
		} else if (ctx.children.size() > 1 && ctx.children.get(1).getText().equals("(")) {
			if (ctx.enum_tuple_field_list() != null) {
				List<Type> tupleFieldList = visitEnum_tuple_field_list(ctx.enum_tuple_field_list());
				RustTupleType tuple = new RustTupleType(tupleFieldList);
				RustTupleType.lookup(tuple);

				return tuple;
			}
		} else if (ctx.children.size() > 1 && ctx.children.get(1).getText().equals("{")) {
			if (ctx.enum_field_decl_list() != null) {
				List<Global> fieldList = visitEnum_field_decl_list(ctx.enum_field_decl_list());

				CompilationUnit structUnit = new CompilationUnit(locationOf(ctx, filePath),
						unit.toString() + "::" + name, true);
				RustStructType struct = RustStructType.lookup(name, structUnit);

				for (Global f : fieldList)
					structUnit.addInstanceGlobal(f);

				return struct;
			}
		}

		return new RustEnumSimpleVariant(name);
	}

	@Override
	public Type visitEnum_tuple_field(Enum_tuple_fieldContext ctx) {
		return visitTy_sum(ctx.ty_sum());
	}

	@Override
	public List<Type> visitEnum_tuple_field_list(Enum_tuple_field_listContext ctx) {
		List<Type> types = new ArrayList<>();
		for (Enum_tuple_fieldContext typeCtx : ctx.enum_tuple_field())
			types.add(visitEnum_tuple_field(typeCtx));

		return types;
	}

	@Override
	public Global visitEnum_field_decl(Enum_field_declContext ctx) {
		String name = ctx.ident().getText();
		Type type = visitTy_sum(ctx.ty_sum());

		return new Global(RustFrontendUtilities.locationOf(ctx, filePath), name, type);
	}

	@Override
	public List<Global> visitEnum_field_decl_list(Enum_field_decl_listContext ctx) {
		List<Global> globals = new ArrayList<>();
		for (Enum_field_declContext typeCtx : ctx.enum_field_decl())
			globals.add(visitEnum_field_decl(typeCtx));

		return globals;
	}

}
