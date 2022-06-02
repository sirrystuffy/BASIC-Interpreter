package lex;

public class GoSubNode extends StatementNode {
	private VariableNode identifier;
	//constructor 
	public GoSubNode(VariableNode identifierName) {
		this.identifier = identifierName;
	}
	//Accessor
	public VariableNode getGoSub() {
		return identifier;
	}
	
	@Override
	public String toString() {
		return "GOSUB(" + getGoSub() +  ")";
	}
}
