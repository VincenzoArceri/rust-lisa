package it.unipr.frontend.cfg;

import it.unipr.frontend.RustLiSATestExecutor;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import org.junit.Test;

public class RustCFGEmpty extends RustLiSATestExecutor {

	@Test
	public void testEmptyFunctions() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true).setJsonOutput(true);
		perform("cfg/empty-function", "empty-function.rs", conf);
	}

	@Test
	public void testEmptyStatement() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true).setJsonOutput(true);
		perform("cfg/empty-statement", "empty-statement.rs", conf);
	}

	@Test
	public void testEmptyMethod() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true).setJsonOutput(true);
		perform("cfg/empty-method", "empty-method.rs", conf);
	}
}
