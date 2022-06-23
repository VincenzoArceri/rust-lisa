package it.unipr.frontend;

import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import org.junit.Test;

public class DummyCFGTest
		extends RustLiSATestExecutor {

	@Test
	public void dummyTest() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true).setJsonOutput(true);
		perform("dummy", "example.rs", conf);
	}
}
