package it.unipr.frontend;

import org.junit.Test;

import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;

public class RustCFGIfElse extends RustLiSATestExecutor {

	@Test
	public void testIfElse() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true).setJsonOutput(true);
		perform("cfg/if-else", "if-else.rs", conf);
	}
	
}
