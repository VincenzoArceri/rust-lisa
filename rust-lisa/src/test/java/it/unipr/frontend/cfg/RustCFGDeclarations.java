package it.unipr.frontend.cfg;

import it.unipr.frontend.RustLiSATestExecutor;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import org.junit.Test;

public class RustCFGDeclarations extends RustLiSATestExecutor {

	@Test
	public void testMutableDeclaration() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true).setJsonOutput(true);
		perform("cfg/mutable-variable-declaration", "mutable-variable-declaration.rs", conf);
	}

	@Test
	public void testSimpleDeclaration() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true).setJsonOutput(true);
		perform("cfg/variable-declaration", "variable-declaration.rs", conf);
	}

	@Test
	public void testSimpleOperationDeclaration() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true).setJsonOutput(true);
		perform("cfg/simple-operation-variable-declaration", "simple-operation-variable-declaration.rs", conf);
	}

}
