package it.unipr.frontend.cfg;

import it.unipr.frontend.RustLiSATestExecutor;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import org.junit.Test;

public class RustCFGControlStructure extends RustLiSATestExecutor {

	@Test
	public void testLoop() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true).setJsonOutput(true);
		perform("cfg/loop", "loop.rs", conf);
	}
}
