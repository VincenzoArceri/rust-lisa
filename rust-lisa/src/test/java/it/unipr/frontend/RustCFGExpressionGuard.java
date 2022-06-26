package it.unipr.frontend;

import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import org.junit.Test;

public class RustCFGExpressionGuard extends RustLiSATestExecutor {

	@Test
	public void testExpressionGuards() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true).setJsonOutput(true);
		perform("cfg/expression-guard", "expression-guard.rs", conf);
	}

}
