package lex;

public class Token {
	
	//All possible states
	public enum State {
		Start ,
		Number ,
		Addition ,
		Subtraction,
		Division ,
		Multiplication ,
		Decimal ,
		EndOfLine ,
		Equals,
		LessThan,
		LessThanEquals,
		GreaterThan, 
		GreaterThanEquals,
		NotEquals,
		LParen,
		RParen,
		String,
		PRINT,
		IDENTIFIER, 
		LABEL, 
		COMMA;
	}
	
	private State type;
	private String value;
	//accessors
	public State getType() {
		return type;
	}
	public String getValue(String str) {
		if (str.equals("num")) {
			value = "NUMBER(";
		} else if (str.equals("str")) {
			value = "String(";
		} else if (str.equals("id")) {
			value = "IDENTIFIER(";
		} else if (str.equals("lbl")) {
			value = "LABEL(";
		}
		
		return value;
	} 
	
}
