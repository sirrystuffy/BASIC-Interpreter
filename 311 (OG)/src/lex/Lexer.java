package lex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lex.Token.State;
//PROBLEM: Crerated the look ahead but it's still parsing the next char 
//MAYBE CREATE A LOOKAHEAD TO CHECK FOR THE %
public class Lexer {
	public List<String> lex(String s) throws Exception {
		//implement counter to index charArray
		int index = 0;
		//Make string into array of chars
		char[] charArray = s.toCharArray();
		
		//Put all the tokens in this and return it
		List<String> answer = new ArrayList<String>();
		//initialize current state to Start
		State currentState = State.Start;
		
		Token t = new Token();
		
		//Initialize value string
		String value = "";
		//Create Data Structure for known words and its token type
		HashMap<String, String> knownWords = new HashMap<String, String>();
		//Statements
		knownWords.put("PRINT", "PRINT"); 
		knownWords.put("DATA", "DATA");
		knownWords.put("READ", "READ");
		knownWords.put("INPUT", "INPUT");
		knownWords.put("GOSUB", "GOSUB");
		knownWords.put("RETURN", "RETURN");
		knownWords.put("FOR", "FOR");
		knownWords.put("TO", "TO");
		knownWords.put("STEP", "STEP");
		knownWords.put("NEXT", "NEXT");
		knownWords.put("IF", "IF");
		knownWords.put("THEN", "THEN");

		//Functions
		knownWords.put("RANDOM", "RANDOM");
		knownWords.put("LEFT$", "LEFT$");
		knownWords.put("RIGHT$", "RIGHT$");
		knownWords.put("MID$", "MID$");
		knownWords.put("NUM$", "NUM$");
		knownWords.put("VAL", "VAL");
		knownWords.put("VAL%", "VAL%");

		//for each char in the array
		for (char c : charArray) {
			//for known words 
			boolean known = true;
			//Make a string representation of char
			String str = Character.toString(c);
			//State Machine
			switch(currentState) {
				case Start: 
					//if the char is a number, add it to value and go to Number state
					if (Character.isDigit(c)) {
						value = t.getValue("num"); //represents NUMBER(
						value += str;
						currentState = State.Number;
					} else if (Character.isLetter(c)) {
						value = t.getValue("id"); //represents IDENTIFIER(
						value += str;
						currentState = State.IDENTIFIER;
					} else if (c == '+') {
						answer.add("PLUS");
					} else if (c == '-') {
						//Check bounds
						if (index + 1 < s.length()) {
							//create temporary char and set it to the next char
							char temp = s.charAt(index + 1);
							//If next value after '-' is a number then it is negative else emit minus
							if (Character.isDigit(temp)) {
								// add to value string
								value = t.getValue("num");
								value += str;
								currentState = State.Number;
							} else {
								answer.add("MINUS");
								currentState = State.Start;
							}
						}
						//out of bounds or end of statement
						//emit divide and go back to start to accept input
					} else if (c == '/') {
						answer.add("DIVIDE");
						//emit times and go back to start to accept input
					} else if (c == '*') {
						answer.add("TIMES");
						//add decimal to value and go to number to accept input
					} else if (c == '.') {
						value = t.getValue("num");
						value += str;
						currentState = State.Decimal;
						//allow but ignore; go back to start
					} else if (c == ' ') {
						//do nothing 
					} else if (c == '='){
						//Check for any key symbols (<, >) that was checked before and add to value
						if (value == "LESSTHAN" || value == "GREATERTHAN" || value == "NOT") {
							value += "EQUALS"; 
							answer.add(value);
							value = "";
						} else {
							answer.add("EQUALS");
							currentState = State.Start;
						}
						
					} else if (c == '<'){
						
						//check bounds and check next value matches known symbols
						if (index + 1 < s.length() && s.charAt(index + 1) == '=') {
							value = "LESSTHAN";
							currentState = State.Start;
						} else if (index + 1 < s.length() && s.charAt(index + 1) == '>') {
							value = "NOT";
							currentState = State.Start;
						} else {
							//no matches so just emit LESS THAN
							answer.add("LESSTHAN");
							currentState = State.Start;
						}
					} else if (c == '>'){
						//check bounds and check next value matches known symbols
						if (value == "NOT") {
							value += "EQUALS";
							answer.add(value);
							currentState = State.Start;
						} else if (index + 1 < s.length() && s.charAt(index + 1) == '=') {
							value = "GREATERTHAN";
							currentState = State.Start;
						} else {
							//no matches so just emit Greater Than
							answer.add("GREATERTHAN");
							currentState = State.Start;
						}
					} else if (c == '('){
						answer.add("LPAREN");
					} else if (c == ')'){
						answer.add("RPAREN");
					} else if (c == '"'){
						//Assume open quote and go to String
						value = t.getValue("str");
						currentState = State.String;
					} else if (c == ','){
						answer.add("COMMA");
						currentState = State.Start;
					} else if (c == '%'){
						//check for known word VAL%
						if (value.equals("VAL%")) {
							//known word is found ! all we need to do is reset value for other chars
							value = ""; //reset
						} else {
							//it is not the known word VAL% !
							throw new Exception ("% was found without VAL");
						}
					} else {
						//throw exception if input isn't any of the ones listed
						throw new IllegalArgumentException("Invalid Input: " + c + " at line " +  s);
					}
					break;
				case Number:
					//Takes either number or decimal. Anything else will emit number, reset value, and change state
					if (Character.isDigit(c)) {
						value += str;
						currentState = State.Number;
					} else if (c == '.') {
						value += ".";
						currentState = State.Decimal;
					} else if (Character.isLetter(c)) {
						//close string and add current value to list
						value += ")"; 
						answer.add(value);
						//Add c to string, reset value to IDENTIFIER( and switch states
						value = t.getValue("id");
						value += str;
						currentState = State.IDENTIFIER;
					} else if (c == '"') {
						//close string and add current value to list
						value += ")"; 
						answer.add(value); 
						//reset value to String( and switch states
						value = t.getValue("str");
						currentState = State.String;
					} else if (c == '<') {
						//close value and add to list
						value += ")"; 
						answer.add(value); 
						//check bounds and check next value matches known symbols
						if (index + 1 < s.length() && s.charAt(index + 1) == '=') {
							value = "LESSTHAN";
							currentState = State.Start;
						} else if (index + 1 < s.length() && s.charAt(index + 1) == '>') {
							value = "NOT";
							currentState = State.Start;
						} else {
							//no matching symbols, so emit Less than
							answer.add("LESSTHAN");
							currentState = State.Start;
						}
					} else if (c == '>') {
						//close value and add to list
						value += ")"; 
						answer.add(value);
						//check bounds and check next value matches known symbols
						if (index + 1 < s.length() && s.charAt(index + 1) == '=') {
							value = "GREATERTHAN";
							currentState = State.Start;
						} else {
							//no matches so just emit GREATHER THAN
							answer.add("GREATERTHAN");
							currentState = State.Start;
						}
					} else if (c == ','){
						//comma assumes the current number is final
						value += ")";
						answer.add(value);
						answer.add("COMMA");
						currentState = State.Start;
					} else {
						//close value and add to list
						value += ")"; 
						answer.add(value); 
						//Don't accept any other symbol other than + - / * < > ( ) =
						if (getSymbol(c) == "INVALID SYMBOL") {
							throw new Exception("ERROR: Invalid Input--> " + c + " at LINE: " + s);
						}
						//Don't get symbol if whitespace because we ignore them 
						if (c != ' ') {
							answer.add(getSymbol(c)); 
						}
						currentState = State.Start;//Go back to start 
					}
					break;
				case Decimal: 
					//Takes only numbers. Anything else will emit number, reset value, and change state
					if (Character.isDigit(c)) {
						value += str;
						currentState = State.Decimal; //stay in decimal to ensure another decimal isn't taken as input 
					} else if(Character.isLetter(c)) {
						//close value string and add to list
						value += ")"; 
						answer.add(value);
						//Add to string, set value to IDENTIFIER( and switch states
						value = t.getValue("id");
						value += str;
						currentState = State.IDENTIFIER;
					} else if (c == '"') {
						//close value string and add to list
						value += ")"; 
						answer.add(value); 
						//set to String( and switch states
						value = t.getValue("str");
						currentState = State.String;
					} else if (c == ','){
						//comma assumes the current number is final
						value += ")";
						answer.add(value);
						answer.add("COMMA");
						currentState = State.Start;
					} else {
						//close value string and add to list
						value += ")";
						answer.add(value);
						//throw exception if invalid, append to answer if not space, ignore if char is space
						if (getSymbol(c) == "INVALID SYMBOL") {
							throw new Exception("ERROR: Invalid Input--> " + c + " at LINE: " + s);
						} else if (getSymbol(c) != "") {
							answer.add(getSymbol(c));
						} else if (c == ' '){
							//do nothing
						} 
						currentState = State.Start;
					}
					break;
				case IDENTIFIER: 
					if(Character.isLetter(c)) {
						//Check if next char is ':' to anticipate LABEL
						if (index + 1 < s.length() && s.charAt(index + 1) == ':') {
							value += str; 
							currentState = State.LABEL;
						} else {
							value += str;
							currentState = State.IDENTIFIER;
						}
						//Check if the current word matches with any value in hash map
						for (String st : knownWords.values()) {
							if (value.equals(t.getValue("id") + knownWords.get(st))) {
								value = knownWords.get(st);
								//check for a '%' after the word for the VAL% case
								if (index + 1 < s.length() && s.charAt(index + 1) == '%') {
									//if the string is a known word and the next char is %, then the word MUST be VAL%
									value = "VAL%";
									answer.add(value);
									currentState = State.Start;
									break; //word is matched so exit loop
								}
								answer.add(value);
								value = ""; //reset value so ')' doesn't get added at the end
								currentState = State.Start;
								break;//exit after one match
							}
						}
						
					} else if (Character.isDigit(c)) {
						//Check if the current word matches with any value in hash map
						for (String st : knownWords.values()) {
							if (value.equals(t.getValue("id") + knownWords.get(st))) {
								value = knownWords.get(st);
								known = false; //The word matched! So no need to add ')'
								break;//exit after one match
							}
						}
						//If true, then word is identifier and needs ')'
						if (known) {
							value += ")"; 
						}
						answer.add(value); //Adding value
						//set to NUMBER(
						value = t.getValue("num");
						value += str;
						currentState = State.Number;
					} else if (c == '"') {
						//Check if the current word matches with any value in hash map
						for (String st : knownWords.values()) {
							if (value.equals(t.getValue("id") + knownWords.get(st))) {
								value = knownWords.get(st);
								known = false; //The word matched! So no need to add ')'
								break; //exit after one match
							}
						}
						//If true, then word is identifier and needs ')'
						if (known) {
							value += ")"; 
						}
						answer.add(value); //Adding value
						//set to String(
						value = t.getValue("str");
						currentState = State.String;
					} else if (c == '<') {
						//Check if the current word matches with any value in hash map
						for (String st : knownWords.values()) {
							if (value.equals(t.getValue("id") + knownWords.get(st))) {
								value = knownWords.get(st);
								known = false; //The word matched! So no need to add ')'
								break; //exit after one match
							}
						}
						//If true, then word is identifier and needs ')'
						if (known) {
							value += ")"; 
						}
						answer.add(value); 
						//check bounds and check if next value matches known symbols
						if (index + 1 < s.length() && s.charAt(index + 1) == '=') {
							value = "LESSTHAN";
							currentState = State.Start;
						} else if (index + 1 < s.length() && s.charAt(index + 1) == '>') {
							value = "NOT";
							currentState = State.Start;
						} else {
							answer.add("LESSTHAN");
							currentState = State.Start;
						}
						
					} else if (c == '>') {
						//Check if the current word matches with any value in hash map
						for (String st : knownWords.values()) {
							if (value.equals(t.getValue("id") + knownWords.get(st))) {
								value = knownWords.get(st);
								known = false; //The word matched! So no need to add ')'
								break; //exit after one match
							}
						}
						//If true, then word is identifier and needs ')'
						if (known) {
							value += ")"; 
						}
						answer.add(value); //Adding current value
						//check bounds and check next value matches known symbols
						if (index + 1 < s.length() && s.charAt(index + 1) == '=') {
							value = "GREATERTHAN";
							currentState = State.Start;
						} else {
							answer.add("GREATERTHAN");
							currentState = State.Start;
						}
						
					} else if (c == ','){
						value += ")";
						answer.add(value);
						answer.add("COMMA");
						currentState = State.Start;
					} else {
						//check the following symbols that can follow a word 
						if (c == '$' || c == '%') {
							//Check if the next char is a ':'(label)
							if (index + 1 < s.length() && s.charAt(index + 1) == ':') {
								value += str;
								currentState = State.LABEL;
							} else {
								//Not a label !
								//Add symbol to value
								value += str;
								//Check if the identifier word matches with any value in hash map
								for (String st : knownWords.values()) {
									if (value.equals(t.getValue("id") + knownWords.get(st))) {
										value = knownWords.get(st);
										known = false; //The word matched! So no need to add ')'
										break; //exit after one match
									}
								}
								//If true, then identifier is word and needs ')'
								if (known) {
									value += ")"; 
									answer.add(value); 
								} else {
									//add to list
									answer.add(value); 
								}
								//Change state
								currentState = State.Start;
							}			
						} else {
							//Check if the identifier word matches with any value in hash map
							for (String st : knownWords.values()) {
								if (value.equals(t.getValue("id") + knownWords.get(st))) {
									value = knownWords.get(st);
									known = false; //The word matched! So no need to add ')'
									break; //exit after one match
								}
							}
							//If true, then identifier is word and needs ')'
							if (known) {
								value += ")"; 
							}
							answer.add(value); 
							//Don't accept any other symbol other than + - / * < > = ( )
							if (getSymbol(c) == "INVALID SYMBOL") {
								throw new Exception("ERROR: Invalid Input--> " + c + " at LINE: " + s);
							} else if (getSymbol(c) != "") {
								answer.add(getSymbol(c));
							} else if (c == ' '){
								//do nothing
							} 
							currentState = State.Start;
						}
					}
					break;
				case String:
					if(c == '"') {
						//Assume Close quote 
						value += ")"; 
						answer.add(value); //Adding numerical value
						currentState = State.Start;
					} else if (index + 1 == s.length()) {
						//If next value is end of string and there still isn't a close quote, throw an exception
						throw new Exception ("ERROR: NO CLOSE QUOTE FOR STRING");
					} else {
						//accept all input except close quote
						value += str; 
						currentState = State.String;
					}
					break;
				case LABEL: 
					//Append all the letters of the word backwards, reverse it, add it to value, and then add it to string 
					//don't want to alter index so just take the value and store it in count
					int count = index;
					//temporary string and char
					String tempString = "";
					//IF value before it is a letter, then this is a label
					value = t.getValue("lbl"); //LABEL(
					
					//Doesn't check for out of bounds because label(':') has to be after a word so index can't be 0, 
					//if ':' is in the first index, case --> Start will throw an exception.
					char temp = s.charAt(count - 1);
					//In case these symbols are added to the word 
					if (temp == '$' || temp == '%') {
						//update temp value to skip over the $ or %
						count--;
						tempString += String.valueOf(s.charAt(count));
						temp = s.charAt(count - 1); 
					}
					//Checks for null here because it's looping, so I want it to stop if it reaches < 0 
					while (count - 1 >= 0 && Character.isLetter(temp)) {
						count --; //decrement early so temp can update properly
						tempString += String.valueOf(s.charAt(count));
						if (count - 1 >= 0) {
							temp = s.charAt(count-1);
						}
					}
					//Put tempValue into a StringBuilder object to reverse the order
					StringBuilder input = new StringBuilder();
					input.append(tempString);
					input.reverse();
					value += input;
					//close value and add to list
					value += ")";
					answer.add(value);
					currentState = State.Start;
			default:
				break;
				
			}
			index++;//increment index after every char
		}
		
		//If line is not empty and value string doesn't contain a closed parenthesis then add ')' and add to list. 
		if (value != null && value != "" && !value.matches(".*[\\)]")) {
			value += ")";//close the number
			answer.add(value);
		}
		
		//if there are no more chars in the string then add EndOfLine to list
		answer.add("EndOfLine");

		return answer;
	}
	//Function that takes in a symbol and returns the appropriate TOKEN
	public String getSymbol(char symbol) throws Exception {
	
		if (symbol == '+' ) {
			return "PLUS";
		} else if (symbol == '-') {
			return "MINUS";
		} else if (symbol == '/') {
			return "DIVIDE";
		} else if (symbol == '*') {
			return "TIMES";
		} else if (symbol == '<') {
			return "LESSTHAN";
		} else if (symbol == '>') {
			return "GREATERTHAN";
		} else if (symbol == '(') {
			return "LPAREN";
		} else if (symbol == ')') {
			return "RPAREN";
		} else if (symbol == '=') {
			return "EQUALS";
		} else if (symbol == ' ') {
			return "";
		} 
		
		return "INVALID SYMBOL";
	}
	
	
}

