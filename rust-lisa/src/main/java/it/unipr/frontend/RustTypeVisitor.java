package it.unipr.frontend;

import it.unipr.cfg.type.numeric.signed.RustI32Type;
import it.unipr.rust.antlr.RustBaseVisitor;
import it.unipr.rust.antlr.RustParser.IdentContext;
import it.unipr.rust.antlr.RustParser.TyContext;
import it.unipr.rust.antlr.RustParser.Ty_pathContext;
import it.unipr.rust.antlr.RustParser.Ty_path_mainContext;
import it.unipr.rust.antlr.RustParser.Ty_path_segment_no_superContext;
import it.unipr.rust.antlr.RustParser.Ty_path_tailContext;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

/**
 * Type visitor for Rust, managing the parsing of Rust types.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public class RustTypeVisitor extends RustBaseVisitor<Type> {

	@Override
	public Type visitTy(TyContext ctx) {
		if (ctx.ty_path() != null) {
			// TODO Skipping macro_tail? part
			return visitTy_path(ctx.ty_path());
		}

		switch (ctx.getChild(0).getText()) {
		case "_":
			return Untyped.INSTANCE;

		default: // TODO When parsing in this function will be complete, delete
					// me
			return Untyped.INSTANCE;
		}
	}

	@Override
	public Type visitTy_path(Ty_pathContext ctx) {
		// TODO: we skip for the moment for_lifetime?
		return super.visitTy_path_main(ctx.ty_path_main());
	}

	@Override
	public Type visitTy_path_main(Ty_path_mainContext ctx) {
		// TODO: we are currently handling just the production ty_path_tail
		return super.visitTy_path_tail(ctx.ty_path_tail());
	}

	@Override
	public Type visitTy_path_tail(Ty_path_tailContext ctx) {
		// TODO: we are currently handling just the production
		// ty_path_segment_no_super
		return visitTy_path_segment_no_super(ctx.ty_path_segment_no_super());
	}

	@Override
	public Type visitTy_path_segment_no_super(Ty_path_segment_no_superContext ctx) {
		// TODO skipping ty_args?
		return ctx.ident() == null ? Untyped.INSTANCE : visitIdent(ctx.ident());
	}

	@Override
	public Type visitIdent(IdentContext ctx) {
		// TODO skipping "auto", "default" and "union"
		switch (ctx.Ident().getText()) {
		case "i32":
			System.out.println("SONO QUA");
			return RustI32Type.INSTANCE;
		default:
			return Untyped.INSTANCE;
		}
	}
}
