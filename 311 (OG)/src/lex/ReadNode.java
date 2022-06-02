package lex;

import java.util.List;

public class ReadNode extends StatementNode {
	private List<VariableNode> varList;
	//constructor takes a variable list 
	public ReadNode(List<VariableNode> variableList) {
		this.varList = variableList;
	}
	//Accessor
	public List<VariableNode> getVarList() {
		return varList;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "ReadNode(" + getVarList() + ")";
	}
}
