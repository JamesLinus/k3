/*
* generated by Xtext
*/
package insa.parser.antlr;

import com.google.inject.Inject;

import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import insa.services.ExpArithmGrammarAccess;

public class ExpArithmParser extends org.eclipse.xtext.parser.antlr.AbstractAntlrParser {
	
	@Inject
	private ExpArithmGrammarAccess grammarAccess;
	
	@Override
	protected void setInitialHiddenTokens(XtextTokenStream tokenStream) {
		tokenStream.setInitialHiddenTokens("RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT");
	}
	
	@Override
	protected insa.parser.antlr.internal.InternalExpArithmParser createParser(XtextTokenStream stream) {
		return new insa.parser.antlr.internal.InternalExpArithmParser(stream, getGrammarAccess());
	}
	
	@Override 
	protected String getDefaultRuleName() {
		return "ExpressionArithm";
	}
	
	public ExpArithmGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}
	
	public void setGrammarAccess(ExpArithmGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
	
}
