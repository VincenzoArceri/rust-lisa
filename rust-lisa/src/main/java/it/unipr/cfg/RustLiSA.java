package it.unipr.cfg;

import it.unipr.frontend.RustFrontend;
import it.unive.lisa.AnalysisException;
import it.unive.lisa.LiSA;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.program.Program;
import java.io.IOException;

public class RustLiSA {

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
