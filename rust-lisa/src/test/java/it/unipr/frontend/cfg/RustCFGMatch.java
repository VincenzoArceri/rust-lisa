package it.unipr.frontend.cfg;

import it.unipr.frontend.RustLiSATestExecutor;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import org.junit.Test;

public class RustCFGMatch extends RustLiSATestExecutor {

	@Test
	public void testExprMatch() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true).setJsonOutput(true);
		perform("cfg/expr-match", "expr-match.rs", conf);
	}
	
	@Test
	public void testBlockyMatch() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true).setJsonOutput(true);
		perform("cfg/blocky-match", "blocky-match.rs", conf);
	}
	
	@Test
	public void testMixAndMatch() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true).setJsonOutput(true);
		perform("cfg/mix-and-match", "mix-and-match.rs", conf);
	}

}
