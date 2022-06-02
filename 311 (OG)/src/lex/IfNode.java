package lex;

public class IfNode extends StatementNode{
	private BooleanOperationNode operation;
	private VariableNode label;
	//Constructor
	public IfNode(BooleanOperationNode oper, VariableNode lbl) {
		this.operation = oper;
		this.label = lbl;
	}
	//Accessors 
	
	public BooleanOperationNode getOperation() {
		return operation;
	}
	
	public VariableNode getLabel() {
		return label;
	}
	@Override
	public String toString() {
		return "IfNode(" + getOperation() + ", " + getLabel() + ")";
	}
}
