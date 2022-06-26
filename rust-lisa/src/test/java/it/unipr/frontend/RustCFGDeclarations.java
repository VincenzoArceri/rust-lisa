package it.unipr.frontend;

import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import org.junit.Test;

public class RustCFGDeclarations extends RustLiSATestExecutor {

	@Test
	public void testSimpleIf() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true).setJsonOutput(true);
		perform("cfg/mutable-variable-declaration", "mutable-variable-declaration.rs", conf);
	}

}
