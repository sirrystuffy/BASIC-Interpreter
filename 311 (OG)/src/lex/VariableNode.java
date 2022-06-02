package lex;

public class VariableNode extends Node{
	//holds a variable name 
	private String varName;
	//constructor takes a string which is the name of the variable
	public VariableNode(String nameOfVariable) {
		this.varName = nameOfVariable;
	}
	//Accessor
	public String getVarName() {
		return varName;
	}
	
	@Override
	public String toString() {
		return "VariableNode(" + getVarName() + ")";
	}
}
