package lex;

import java.util.List;

public class FunctionNode extends Node{
	private String functionName;
	private List<Node> nodeList;
	//Constructor
	public FunctionNode(String name, List<Node> List) {
		this.functionName = name;
		this.nodeList = List;
	}
	
	//Accessors
	public String getFunctionName() {
		return functionName;
	}
	public List<Node> getNodeList() {
		return nodeList;
	}
	
	@Override
	public String toString() {
		return getFunctionName() + "(" + getNodeList() + ")";
	}
}
