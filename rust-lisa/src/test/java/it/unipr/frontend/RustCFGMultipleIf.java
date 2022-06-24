package it.unipr.frontend;

import org.junit.Test;

import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;

public class RustCFGMultipleIf extends RustLiSATestExecutor {

	@Test
	public void testMultipleIf() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true).setJsonOutput(true);
		perform("cfg/multiple-if", "multiple-if.rs", conf);
	}
	
}
