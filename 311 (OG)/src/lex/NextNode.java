package lex;

public class NextNode extends StatementNode {
	StatementNode forReference;
	private static VariableNode var;
	//Constructor takes a variableNode
	public NextNode(VariableNode varName) {
		this.var = varName;
	}
	//Accessor
	public static VariableNode getVar() {
		return var;
	}
	@Override
	public String toString() {
		return "NextNode(" + getVar() + ")";
	}
	
}
