package lex;

public class BooleanOperationNode extends Node{
	private Node exp1, exp2;
	String operation; 
	//constructor 
	public BooleanOperationNode(Node expression1, String oper, Node expression2) {
		this.exp1 = expression1;
		this.operation = oper;
		this.exp2 = expression2;
	}
	//Accessors
	public Node getExpression1() {
		return exp1;
	}
	public String getOperation() {
		return operation;
	}
	public Node getExpression2() {
		return exp2;
	}
	@Override
	public String toString() {
		return "BooleanOperationNode(" + getExpression1() + ", " + getOperation() + ", " + getExpression2() + ")";
	}
}
