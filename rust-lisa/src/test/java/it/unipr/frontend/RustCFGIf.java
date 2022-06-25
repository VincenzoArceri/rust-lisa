package it.unipr.frontend;

import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import org.junit.Test;

public class RustCFGIf extends RustLiSATestExecutor {

	@Test
	public void testSimpleIf() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true).setJsonOutput(true);
		perform("cfg/simple-if", "simple-if.rs", conf);
	}

	@Test
	public void testIfElse() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true).setJsonOutput(true);
		perform("cfg/if-else", "if-else.rs", conf);
	}

	@Test
	public void testIfElseif() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true).setJsonOutput(true);
		perform("cfg/if-elseif", "if-elseif.rs", conf);
	}

	@Test
	public void testIfElseifElse() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true).setJsonOutput(true);
		perform("cfg/if-elseif-else", "if-elseif-else.rs", conf);
	}

	@Test
	public void testMultipleIf() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true).setJsonOutput(true);
		perform("cfg/multiple-if", "multiple-if.rs", conf);
	}

}
