package lex;

public class AssignmentNode extends StatementNode{
	//holds a list of Node (things to print)
	private VariableNode varName;
	private Node nodeValue;
	//constructor -- holds variable name and node value 
	public AssignmentNode(VariableNode variable, Node value) {
		this.varName = variable;
		this.nodeValue = value;
	}
	//Accessors
	public VariableNode getVarName() {
		return varName;
	}
	public Node getNodeValue() {
		return nodeValue;
	}
	@Override
	public String toString() {
		return "AssignmentNode(" + getVarName() + ", " + getNodeValue() + ")";
	}
}
