package it.unipr.frontend;

import org.junit.Test;

import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;

public class RustCFGIfElseifElse extends RustLiSATestExecutor {

	@Test
	public void testIfElseifElse() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true).setJsonOutput(true);
		perform("cfg/if-elseif-else", "if-elseif-else.rs", conf);
	}
	
}
