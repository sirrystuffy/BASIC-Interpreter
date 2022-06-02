package lex;

public class MathOpNode extends Node{
	private Node left;
	private Node right;
	private Operation op;
	//insert constructor that takes two nodes and their operation
	public MathOpNode(Operation oper, Node left, Node right) {
		this.op = oper;
		this.left = left;
		this.right = right;
	}
	//enum holds possible operations
	public enum Operation {
		Add, 
		Subtract,
		Multiply, 
		Divide;
	}
	//Accessors
	public Operation getOperation() {
		return op;
	}
	public Node getLeft() {
		return left;
	}
	public Node getRight() {
		return right;
	}
	
	@Override
	public String toString() {
		return "MathOpNode(" + getOperation() + ", " + getLeft() + ", " + getRight() + ")" ;
	}
}
