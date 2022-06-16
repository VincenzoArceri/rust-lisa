import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import it.unipr.rust.antlr.RustLexer;
import it.unipr.rust.antlr.RustParser;

public class RustLiSAFrontend {

	public static void parseContract(String filePath) throws IOException {
		InputStream is = new FileInputStream(filePath);
		RustLexer lexer = new RustLexer(CharStreams.fromStream(is, StandardCharsets.UTF_8));
		RustParser parser = new RustParser(new CommonTokenStream(lexer));

		parser.crate();

		is.close();
	}
}