package it.unipr.cfg.type.composite.enums;

import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;

/**
 * Rust enum interface for implementing variants.
 *
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:simone.gazza@studenti.unipr.it">Simone Gazza</a>
 */
public interface RustEnumVariant {
	/**
	 * Used during match statement parsing. This function should check if the
	 * type of the variant and, if required, the values are correct or not, and
	 * in that case it builds the CFG. For example, in this code, every branch
	 * in the match statement should call the function to be constructed: enum
	 * Color { Red, Blue, Green, RGB(u32, u32, u32), } fn main() { let color =
	 * Color::RGB(122, 17, 40); match color { Color::Red => println!("The color
	 * is Red!"), Color::Blue => println!("The color is Blue!"), Color::Green =>
	 * println!("The color is Green!"), Color::RGB(r, g, b) => println!("Red:
	 * {}, green: {}, and blue: {}!", r, g, b), } }
	 * 
	 * @param toMatch the expression to be matched
	 *
	 * @return the statement for the analysis
	 */
	public Statement match(Expression toMatch);

	@Override
	public String toString();
}
