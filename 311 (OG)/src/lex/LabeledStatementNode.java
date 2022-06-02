package lex;

public class LabeledStatementNode extends StatementNode{
	private String labelName;
	private StatementNode labelStatement;
	//Constructor 
	public LabeledStatementNode(String name, StatementNode statement) {
		this.labelName = name;
		this.labelStatement = statement;
	}
	//Accessors 
	public String getLabelName() {
		return labelName;
	}
	public StatementNode getLabelStatement() {
		return labelStatement;
	}
	
	
	@Override
	public String toString() {
		return "LabeledStatementNode(" + getLabelName() + ", " + getLabelStatement() +  ")";
	}
}
