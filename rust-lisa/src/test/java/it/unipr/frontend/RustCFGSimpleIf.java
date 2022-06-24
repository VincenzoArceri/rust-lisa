package it.unipr.frontend;

import org.junit.Test;

import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;

public class RustCFGSimpleIf extends RustLiSATestExecutor {

	@Test
	public void testSimpleIf() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true).setJsonOutput(true);
		perform("cfg/simple-if", "simple-if.rs", conf);
	}
	
}
