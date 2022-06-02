package lex;

public class StringNode extends Node{
	private String str;
	//constructor takes a string
	public StringNode(String stringer) {
		this.str = stringer;
	}
	//Accessor
	public String getString() {
		return str;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "StringNode(" + getString() + ")";
	}
}
