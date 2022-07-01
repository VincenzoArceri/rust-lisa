package it.unipr.frontend;

import it.unipr.rust.antlr.RustBaseVisitor;
import it.unipr.rust.antlr.RustParser.TyContext;
import it.unipr.rust.antlr.RustParser.Ty_pathContext;
import it.unipr.rust.antlr.RustParser.Ty_path_mainContext;
import it.unipr.rust.antlr.RustParser.Ty_path_segment_no_superContext;
import it.unipr.rust.antlr.RustParser.Ty_path_tailContext;
import it.unive.lisa.type.Type;

/**
 * Type visitor for Rust, managing the parsing of Rust types.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class RustTypeVisitor extends RustBaseVisitor<Type> {

	@Override
	public Type visitTy(TyContext ctx) {
		// TODO: we are currently handling just the production ty_path
		// macro_tail?
		// (and skipping macro_tail)
		return visitTy_path(ctx.ty_path());
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
		// TODO: here you can find basic types
		return null;
	}
}
