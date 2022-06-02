package lex;

public class IntegerNode extends Node{
	private int num;
	//insert constructor 
	public IntegerNode(int i) {
		this.num = i;
	}
	public IntegerNode() {
		// TODO Auto-generated constructor stub
	}
	public int getValue() {
		return num;
	}
	@Override
	public String toString() {
		return "IntegerNode(" + getValue() + ")";
	}
}
