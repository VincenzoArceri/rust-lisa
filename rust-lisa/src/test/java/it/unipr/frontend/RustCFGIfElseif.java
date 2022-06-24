package it.unipr.frontend;

import org.junit.Test;

import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;

public class RustCFGIfElseif extends RustLiSATestExecutor {

	@Test
	public void testIfElseif() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true).setJsonOutput(true);
		perform("cfg/if-elseif", "if-elseif.rs", conf);
	}
	
}
