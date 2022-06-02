package lex;

import java.util.List;
//IS VARIABLE LIST ITS OWN CONSTRUCTOR 
public class InputNode extends StatementNode {
	private StringNode str;
	private VariableNode varNode;
	private List<VariableNode> varList;
	//First param is either a string or variableNode, then take a variable list 
	public InputNode(StringNode string, List<VariableNode> variableList) {
		this.str = string;
		this.varList = variableList;
	}
	public InputNode(VariableNode variableNode, List<VariableNode> variableList) {
		this.varNode = variableNode;
		this.varList = variableList;
	}
	//Accessors
	public StringNode getStringNode() {
		return str;
	}
	public VariableNode getVarNode() {
		return varNode;
	}
	public List<VariableNode> getVarList() {
		return varList;
	}
	
	@Override
	public String toString() {
		//Check if string is there
		if (str != null) {
			return "InputNode(" + getStringNode() + "," + getVarList()+ ")";
		} 
		//else variableNode is there !
		return "InputNode(" + getVarNode() + "," + getVarList()+ ")";
	}
	
}
