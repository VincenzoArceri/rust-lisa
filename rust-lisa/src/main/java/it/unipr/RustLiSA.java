package it.unipr;

import it.unipr.frontend.RustFrontend;
import it.unive.lisa.AnalysisException;
import it.unive.lisa.LiSA;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.program.Program;
import java.io.IOException;

/**
 * RustLiSA static analyzer build upon LiSA.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class RustLiSA {

	/**
	 * RustLiSA entry point.
	 * 
	 * @param args arguments
	 * 
	 * @throws AnalysisException if anything goes wrong during the analysis
	 * @throws IOException       if anything goes wrong during reading the file
	 */
	public static void main(String[] args) throws AnalysisException, IOException {
		Program program = RustFrontend.processFile(args[0]);

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.setDumpCFGs(true)
				.setJsonOutput(true)
				.setWorkdir("output");

		LiSA lisa = new LiSA(conf);
		lisa.run(program);
	}
}
