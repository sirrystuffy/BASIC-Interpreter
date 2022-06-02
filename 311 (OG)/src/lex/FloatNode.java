package lex;

public class FloatNode extends Node{
	private float num;
	//insert constructor 
	public FloatNode(float i) {
		num = i;
	}
	public float getValue() {
		return num;
	}
	@Override
	public String toString() {
		return "FloatNode(" + getValue() + ")";
	}
}
