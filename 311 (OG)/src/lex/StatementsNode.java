package lex;

import java.util.List;

public class StatementsNode{
	//holds a list of statementNodes
	private static List<StatementNode> statementList;
	//constructor takes a list of statements
	public StatementsNode(List<StatementNode> listOfStatements) {
		StatementsNode.statementList = listOfStatements;
	}
	//accessor
	public List<StatementNode> getList() {
		return statementList;
	}
	
	@Override
	public String toString() {
		return "StatementsNode(" + getList() + ")";
	}

	
}
