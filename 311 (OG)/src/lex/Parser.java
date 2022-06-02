package lex;

import java.util.ArrayList;
import java.util.List;

import lex.MathOpNode.Operation;

public class Parser {
	private static List<String> List;
	//constructor that accepts the list of tokens
	public Parser(List<String> tokenList) {
		Parser.List = tokenList; //set the List passed as current list
	}
	
	
	public static StatementsNode parse() throws Exception {
		StatementsNode ast = statements(); 
		return ast; //return new AST Node 
	}
	
	public static String matchAndRemove(String tokenType) {
		//Checks if the token matches the first element in List,
		if (List.get(0).equals(tokenType)) {
			List.remove(0);
			return tokenType; //return token because it matched
		}
		return null; //return null because it did not match
	}
	public static StatementsNode statements() throws Exception {
		//accept any number of statements until statement returns null
		//check if statement is null
		List<StatementNode> statementList = new ArrayList<StatementNode>();
		
		StatementNode checkStatement = statement();
		if (checkStatement == null) {
			return null;
		}
		//--> statement is not null !
		//keep calling statement until it returns null
		StatementNode temp = checkStatement; 
		while (temp != null) {
			statementList.add(temp);
			temp = statement(); //have temp update before checkStatement
		}
		//return checkStatement; 
		return new StatementsNode(statementList);
	}
	
	public static StatementNode statement() throws Exception {
		//handle a single statement and return its node
		
		//Look for a label before checking any thing else 
		LabeledStatementNode label = labelStatement();
		if (label != null) {
			return label;
		}
		//Print statement or 
		StatementNode printStatement = printStatement();
		if (printStatement != null) {
			return printStatement;
		}
		//Data or 
		StatementNode dataNode = dataStatement();
		if (dataNode != null) {
			return dataNode;
		}
		//Read or 
		StatementNode readNode = readStatement();
		if (readNode != null) {
			return readNode;
		}
		//Input or 
		StatementNode inputNode = inputStatement();
		if (inputNode != null) {
			return inputNode;
		}
		//GoSub or 
		StatementNode goSubNode = goSubStatement();
		if (goSubNode != null) {
			return goSubNode;
		}
		//Return or 
		StatementNode returnNode = returnStatement();
		if (returnNode != null) {
			return returnNode;
		}
		//For or 
		StatementNode forNode = forStatement();
		if (forNode != null) {
			return forNode;
		}
		//Next or 
		StatementNode nextNode = nextStatement();
		if (nextNode != null) {
			return nextNode;
		}
		//If or 
		StatementNode ifNode = ifStatement();
		if (ifNode != null) {
			return ifNode;
		}
		
		//assignment 
		StatementNode assignNode = assignmentStatement();
		if (assignNode != null) {
			return assignNode;
		}
		//None, so return null
		return null;
	}
	public static LabeledStatementNode labelStatement() throws Exception {
		String nameOfLabel = findLabel();
		if (nameOfLabel == null) {
			return null;
		}
		//Label Name is found !
		//Now, check for statements 
		StatementNode statement = statement();
		if (statement != null) {
			//Check if statement is a label statement 
			if (statement instanceof LabeledStatementNode) {
				throw new Exception ("Can't take LabelStatement as Statement in LabelNode");
			}
			//Statement is not labelStatement !
			return new LabeledStatementNode(nameOfLabel, statement);
		}
		
		//if no statements after label, throw error
		throw new Exception ("No Statement after Label"); 
	}
	public static PrintNode printStatement() throws Exception {
		if (matchAndRemove("PRINT") != null) {
			//call printList to get the list to pass to PrintNode()
			List<Node> expList = printList();
			return new PrintNode(expList);
		}
		return null;
	}

	public static List<Node> printList() throws Exception {
		//Create the list to store expressions/STRINGs
		List<Node> listOfNode = new ArrayList<Node>();
		//comma separated expressions/STRINGs
		Node exp = expression();
		Node str = findString();
		if (exp == null && str == null) {
			return null;
		}		
		//expression/stringNode is found !
		//Loop the expression/stringNode and commas until end of line
		while (exp != null || str != null) {
			if (exp != null) {
				//Expression is found !
				listOfNode.add(exp);
			} else {
				//expression is not found. . . but stringNode is found !
				listOfNode.add(str);
			}
			//if there is no comma, it's end of list so return the current list
			if (matchAndRemove("COMMA") == null) {
				break;
			}
			exp = expression(); //update the expression
			str = findString(); //update the stringNode
		}
		return listOfNode; //final list of nodes
	}
	public static DataNode dataStatement() throws Exception{
		if (matchAndRemove("DATA") != null) {
			//call dataList to get the list to pass to dataNode()
			List<Node> nodeList = dataList();
			if (nodeList == null) {
				throw new Exception ("No Interger/Float/String Found . . . ");
			}
			return new DataNode(nodeList);
		}
		return null;
	}
	public static List<Node> dataList() {
		//Create the list to store nodes
		List<Node> nodesList = new ArrayList<Node>();
		//comma separated list of IntegerNode/FloatNode/stringNode
		Node intNode = findInt();
		Node floatNode = findFloat();
		Node strNode = findString();
		//If none of these are found, return null
		if (intNode == null && floatNode == null && strNode == null) {
			return null;
		}	
		//integer or float or string node was found here!
		//Loop the expression and commas until end of line
		while (intNode != null || floatNode != null || strNode != null) {
			if (intNode != null) {
				//IntegerNode is found !
				nodesList.add(intNode);

			} else if (floatNode != null) {
				//IntegerNode is not found. . . but floatNode is found !
				nodesList.add(floatNode);
			} else {
				//IntegerNode is not found. . . and floatNode is not found. . . but stringNode is found !
				nodesList.add(strNode);
			}
			//if there is no comma, it's end of list so return the current list
			if (matchAndRemove("COMMA") == null) {
				break;
			}
			intNode = findInt(); //update the IntegerNode
			floatNode = findFloat(); //update the floatNode
			strNode = findString(); //update the stringNode
		}
		//final list of nodes
		return nodesList;
	}
	
	public static ReadNode readStatement() throws Exception{
		if (matchAndRemove("READ") != null) {
			//call printList to get the list to pass to readNode()
			List<VariableNode> nodeList = readList();
			if (nodeList == null) {
				throw new Exception ("No variable found . . .");
			}
			return new ReadNode(nodeList);
		}
		return null;
	}
	public static List<VariableNode> readList() {
		//Create the list to store VariableNodes
		List<VariableNode> varList = new ArrayList<VariableNode>();
		//comma separated list of VariableNodes
		VariableNode varNode = findId();
		//If no variable is found, return null
		if (varNode == null) {
			return null;
		}
		//variableNode is found !
		//Loop for more variableNodes and commas
		while (varNode != null) {
			//Add to list
			varList.add(varNode);
			//Check for more commas
			if (matchAndRemove("COMMA") == null) {
				break;
			}
			varNode = findId(); //update the variableNode
		}
		return varList;
	}
	public static InputNode inputStatement() throws Exception{
		//Check for the keyword "INPUT"
		if (matchAndRemove("INPUT") != null) {
			//Check for string and variable 
			StringNode strTest = findString();
			VariableNode varTest = findId();
			if (strTest == null && varTest == null) {
				throw new Exception ("No String or Variable as first parameter");
			}
			if (strTest != null) {
				//string is found !
				//Now, make sure to check for comma
				if (matchAndRemove("COMMA") == null) {
					throw new Exception ("No COMMA after StringNode");
				}
				//Finally, Find the variable list 
				List<VariableNode> varList = readList();
				if (varList == null) {
					throw new Exception ("No variable(s) in InputNode . . .");
				}
				return new InputNode(strTest, varList);
			} 
			//Variable is found !
			//Now, make sure to check for comma
			if (matchAndRemove("COMMA") == null) {
				throw new Exception ("No COMMA after VariableNode");
			}
			//Finally, Find the variable list 
			List<VariableNode> varList = readList();
			if (varList == null) {
				throw new Exception ("No variable(s) in InputNode . . .");
			}
			return new InputNode(varTest, varList);
		}
		//There is no INPUT 
		return null;
	}
	public static GoSubNode goSubStatement() throws Exception{
		//Check for the keyword "GOSUB"
		if (matchAndRemove("GOSUB") != null) {
			VariableNode id = findId();
			if (id != null) {
				return new GoSubNode(id);
			}
			//GOSUB keyword was found but no identifier --> exception
			throw new Exception ("No IDENTIFIER after GOSUB keyword . . .");
		}
		return null;
	}
	public static ReturnNode returnStatement() {
		//Check for the keyword "RETURN"
		if (matchAndRemove("RETURN") != null) {
			//Return is found !
			return new ReturnNode();
		}
		//Return was node found
		return null;
	}
	public static ForNode forStatement() throws Exception {
		//Check for the keyword "FOR"
		if (matchAndRemove("FOR") != null) {
			VariableNode variable = findId();
			//Check for variable 
			if (variable == null) {
				throw new Exception ("No variable after FOR"); 
			}
			//Check for EQUALS 
			if (matchAndRemove("EQUALS") == null) {
				throw new Exception ("No EQUALS operator after variable"); 
			}
			IntegerNode initialValue = findInt();
			//Check for initial value to set variable 
			if (initialValue == null) {
				throw new Exception ("No initial value for variable assignment"); 
			}
			//Check for TO 
			if (matchAndRemove("TO") == null) {
				throw new Exception ("No TO after inital value"); 
			}
			IntegerNode limitValue = findInt();
			//Check for limit value 
			if (limitValue == null) {
				throw new Exception ("No limit value"); 
			}
			//Check for STEP (optional)
			if (matchAndRemove("STEP") == null) {
				//Step isn't found, so set step to 1 and return the ForNode
				IntegerNode step = new IntegerNode(1);
				return new ForNode(variable, initialValue, limitValue, step);
			}
			//Step is found !
			IntegerNode step = findInt();
			//Check for increment value 
			if (step == null) {
				throw new Exception ("No increment value after STEP"); 
			}
			//Increment found ! 
			return new ForNode(variable, initialValue, limitValue, step);
		}
		//FOR isn't found 
		return null;
	}
	public static NextNode nextStatement() throws Exception {
		//Check for NEXT keyword
		if (matchAndRemove("NEXT") != null) {
			//Next was found !
			VariableNode var = findId();
			if (var != null) {
				return new NextNode(var);
			}
			//No variable was found
			throw new Exception ("No variable after NEXT");
		}
		//Next is not found
		return null;
	}
	
	public static IfNode ifStatement() throws Exception {
		if (matchAndRemove("IF") != null) {
			//if is found !
			//check for Boolean Operation Node
			BooleanOperationNode boolOp = boolOperation();
			if (boolOp == null) {
				throw new Exception ("No Boolean Operation found after expression");
			}
			//BooleanOperationNode found !
			//Check for THEN
			if (matchAndRemove("THEN") == null) {
				throw new Exception ("No THEN keyword found after expression");
			}
			//THEN is found !
			//Check for Label
			VariableNode labelNode = findId();
			if (labelNode == null) {
				throw new Exception ("No label found after THEN keyword");
			}
			//Return IfNode()
			return new IfNode(boolOp, labelNode);
		}
		//keyword IF not found !
		return null;
	}
	public static BooleanOperationNode boolOperation() throws Exception {
		//check for first expression
		Node expression1 = expression();
		if (expression1 == null) {
			throw new Exception ("No expression found after IF");
		}
		//found expression !
		//Now, check for boolean operator 
		String operator = findBooleanOp();
		if (operator == null) {
			throw new Exception ("No operator found after expression");
		}
		//boolean operation found !
		//Now, check for second expression
		Node expression2 = expression();
		if (expression2 == null) {
			throw new Exception ("No expression found after operator");
		}
		//Second expression found !
		//return BooleanOperationNode
		return new BooleanOperationNode(expression1, operator, expression2);
	}
	public static FunctionNode functionInvocation() throws Exception {
		String functionName = findFunctionName();
		//Check for function name
		if (functionName != null) {
			//functionName is found !
			//Now, check for LPAREN 
			if (matchAndRemove("LPAREN") == null) {
				throw new Exception ("No LPAREN after function name");
			}
			//LPAREN is found !
			//Now, check for parameter list -- param list can be empty !!
			List<Node> paramList = paramList();
			
			//parameter list is found !
			//Now, check for RPAREN 
			if (matchAndRemove("RPAREN") == null) {
				throw new Exception ("No RPAREN after param list");
			}
			//RPAREN is found !
			//return FunctionNode()
			return new FunctionNode(functionName, paramList);
		}
		//No function name found !
		return null;
	}
	public static List<Node> paramList() throws Exception {
		//List of expression or string or it can be empty
		//Create list
		List<Node> listOfParameters = new ArrayList<Node>();
		//Check for expression and string
		Node exp = expression();
		StringNode str = findString();
		//Check if list is empty
		if (exp == null && str == null) {
			//empty list is returned
			return listOfParameters;
		}
		//Expression or StringNode is found !
		//Loop until they are both null or there are no more commas
		while (exp == null || str == null) {
			if (exp != null) {
				//expression is found !
				listOfParameters.add(exp);
			} else {
				//String is found !
				listOfParameters.add(str);
			}
			if (matchAndRemove("COMMA") == null) {
				break;
			}
			exp = expression(); //update expression
			str = findString(); //update string
		}
		//Final list of parameters
		return listOfParameters;
	}
	
	public static AssignmentNode assignmentStatement() throws Exception {
		//Assignment is a variable = expression or variable$ = "string"
		//Check for variable name 
		VariableNode varNode = findId();
		if (varNode == null) {
			return null;
		}
		//Check for equals sign 
		if (matchAndRemove("EQUALS") == null) {
			throw new Exception ("No Equals after variable name . . .");
		}
		//Check for expression
		Node expressNode = expression();
		Node strNode = findString();
		if (expressNode == null && strNode == null) {
			throw new Exception ("No Expression after Equals . . .");
		} else if (expressNode != null) {
			return new AssignmentNode(varNode, expressNode);
		} 
		//Must be a string if we get to here . . .
		//Assignment creation successful !
		return new AssignmentNode(varNode, strNode);
	}
	//determine if expression contains term or term(s)
	public static Node expression() throws Exception {
		Node t = term(); 
		FunctionNode func = functionInvocation();
		
		//If there is no term and no function, return null
		if (t == null && func == null) {
			return null;
		} 
		//there is either a term or a function !
		//check for function; if there is a function, return the function
		if (func != null) {
			return func;
		}
		//There is no function, so there has to be a term !
		//Pass in the Node value so we don't have to check again in termAndTerm()
		Node tAndT = termAndTerm(t);
		if (tAndT != null) {
			//Once I'm here, Keep calling factorAndFactor until I get a null
			Node temp = tAndT; //Create a temp to place value of factorAndFactor()
			while (temp != null) {
				temp = termAndTerm(tAndT); //update temp with factorAndFactor() value
				//Update fAndF value only if factorAndFactor is not null
				if (temp != null) {
					tAndT = temp;
				} else {
					return tAndT; //Once temp is null, it means there is no more times/divide symbols, so return what we have
				}
			}
		}
		
		return t; 
	}
	//determine if term contains factor or factor(s)
	public static Node term() throws Exception {
		//Check if factor or expression value 
		Node f = factor();
		if (f == null) {
			return null;
		}
		/*
		 	*** IMPORTANT INFO ON HOW TO HANDLE CURLY BRACES {} ***
		//Check for Factor Times/Divide Factor AT LEAST ONCE, if it is null --> just return factor 
		//BUT IF IT IS A VALUE, pass the value to factorAndFactor() and call function. Once factorAndFactor() is null return final MathOpNode()
		//This makes sure that term() includes all times(*) and divides (/) symbols */
		Node fAndF = factorAndFactor(f); // 5 * 11
		if (fAndF != null) {
			//Once I'm here, Keep calling factorAndFactor until I get a null
			Node temp = fAndF; //Create a temp to place value of factorAndFactor()
			while (temp != null) {
				temp = factorAndFactor(fAndF); //update temp with factorAndFactor() value
				//Update fAndF value only if factorAndFactor is not null
				if (temp != null) {
					fAndF = temp;
				} else {
					return fAndF; //Once temp is null, it means there is no more times/divide symbols, so return what we have
				}
			}
		}
		
		return f;  //Return factor because factorAndFactor is null
	}
	
	public static Node factor() throws Exception{
		//Check for int and float and Identifier
		IntegerNode factorIsInt = findInt();
		VariableNode factorIsId = findId();
		FloatNode factorIsFloat = findFloat();
		if (factorIsId != null) {
			return factorIsId; //return new VariableNode
		}
		if (factorIsInt != null) {
			return factorIsInt; //return new IntegerNode
		}
		if (factorIsFloat != null) {
			return factorIsFloat;//return new FloatNode
		}
		
		//Check for start of Expression : LPAREN
		if(matchAndRemove("LPAREN") == null) {
			return null;
		}
		//Check if valid Expression
		Node exp = expression();
		if(exp == null) {
			throw new Exception ("Invalid Expression");
		}
		//Check for end of Expression : RPAREN
		if(matchAndRemove("RPAREN") == null) {
			throw new Exception ("Invalid: NO RPAREN");
		}
		//Return expression value
		return exp; 
	}
	
	public static Node termAndTerm(Node term) throws Exception {
		//Checks for Term +/- Term
		Operation oper;
		String plus = matchAndRemove("PLUS");
		String minus = matchAndRemove("MINUS");
		//If matchAndRemove returns plus or minus, assign appropriate operation value
		if (plus != null) {
			oper = Operation.Add;		
		} else if (minus != null) {
			oper = Operation.Subtract;
		} else {
			//matchAndRemove returned null for both cases so return null 
			return null;
		}
		//check second term 
		Node term2 = term();
		if (term2 == null) {
			throw new Exception ("NO SECOND TERM");
		} 
		//return the operation done on the left and right node using MathOpNode
		return new MathOpNode(oper, term, term2);
	}
	
	public static MathOpNode factorAndFactor(Node factor) throws Exception {
		//Checks for Factor +/- Factor
		Operation oper;
		String times = matchAndRemove("TIMES");
		String divide = matchAndRemove("DIVIDE");
		//If matchAndRemove returns times or divide, assign appropriate operation value		
		if (times != null) {
			oper = Operation.Multiply;
		} else if (divide != null) {
			oper = Operation.Divide;
		} else {
			//matchAndRemove returned null for both cases so return null
			return null;
		}
		//second factor
		Node factor2 = factor();
		if (factor2 == null) {
			throw new Exception ("NO SECOND FACTOR");
		} 
		//return the operation done on the left and right node using MathOpNode
		return new MathOpNode(oper, factor, factor2);
	}
	
	//Method to find (int) number 
	
	//Method to find (int) number 
	public static IntegerNode findInt() {
		//Initialize answer string
		String answer = "";
		//Make first element a char array
		String str = List.get(0);
		char[] c = str.toCharArray();
		//Traverse through every char 
		for (char ch : c) {
			String value = Character.toString(ch);
			//Add to answer string if char is a number or negative sign (-)
			if(Character.isDigit(ch) || ch == '-') {
				answer += value;
			} 
		}
		//There was no number in first element, return null
		if (answer == "") {
			return null;
		}
		//Return IntegerNode if string matched first element
		if (matchAndRemove("NUMBER(" + answer + ")") != null) {
			return new IntegerNode(Integer.parseInt(answer));
		}
		//matchAndRemove returned null
		return null;
	}
	//Method to find (float) number 
	public static FloatNode findFloat() {
		//Initialize answer string
		String answer = "";
		//Make first element a char array
		String str = List.get(0);
		char [] c = str.toCharArray();
		//Traverse through every char 
		for (char ch : c) {
			String value = Character.toString(ch);
			//Add to answer string if char is a number or decimal (.) or negative sign (-) 
			if(Character.isDigit(ch) || ch == '.' || ch == '-') {
				answer += value;
			} 
		}
		//There is no number in first element
		if (answer == "") {
			return null;
		}
		//Return IntegerNode if string matched first element
		if(matchAndRemove("NUMBER(" + answer + ")") != null) {
			return new FloatNode(Float.parseFloat(answer)); //Note : parseFloat() automatically rounds the value
		}
		//matchAndRemove returned null
		return null;
	}
	public static VariableNode findId() {
		//Initialize answer string
		String answer = "";
		//Make first element a char array
		String str = List.get(0);
		char[] c = str.toCharArray();
		
		//Find the last occurrence of the colon 
		int colon = str.lastIndexOf(":");
		
		// If the colon is right before the closing parenthesis then it is a label and not an identifier so return null
		if (colon == c.length - 1) {
			//Must be a LABEL !
			return null;
		}
		
		//Create a for loop that loops from the start of '(' to the end of the char array
		for (int lParen = str.indexOf('('); lParen < c.length; lParen++) {
			if (lParen == -1) {
				break;
			}
			
			//Add to answer string if char is a letter
			if(Character.isLetter(c[lParen]) || c[lParen] == '$' || c[lParen] == '%') {
				String value = Character.toString(c[lParen]);
				answer += value;
			} 
		}
		
		//There were no letters in first element, return null
		if (answer == "") {
			return null;
		}
		//Return VariableNode if string matched first element
		//IDENTIFIERS ARE OUR VARIABLES !
		if (matchAndRemove("IDENTIFIER(" + answer + ")") != null) {
			return new VariableNode(answer); 
		} 
		//matchAndRemove returned null
		return null;
	}
	public static StringNode findString() {
		//Initialize answer string
		String answer = "";
		//Make first element a char array
		String str = List.get(0);
		char[] c = str.toCharArray();
		//Add anything that isn't a double quote to the answer string because we want the value inside 
		//Add 1 to the initial index to exclude '(' from being checked 
		//Subtracted 1 from the last index to exclude ')' from being checked
		for (int lParen = str.indexOf('(') + 1; lParen < c.length - 1; lParen++) {
			//Not found
			if (lParen == -1) {
				break;
			}
			//Add anything to answer string as long as it isn't double quote
			if(c[lParen] != '"') {
				String value = Character.toString(c[lParen]);
				answer += value;
			} 
		}

		//There were no letters in first element, return null
		if (answer == "") {
			return null;
		}
		
		//Return StringNode if string matched first element
		if (matchAndRemove("String(" + answer + ")") != null) {
			return new StringNode(answer); 
		} 
		//matchAndRemove returned null
		return null;
	}
	//used to find label name 
	public static String findLabel() {
		//Initialize answer string
		String answer = "";
		//Make first element a char array
		String str = List.get(0);
		char[] c = str.toCharArray();
		
		//Create a for loop that loops from the start of '(' to the end of the char array
		for (int lParen = str.indexOf('('); lParen < c.length; lParen++) {
			if (lParen == -1) {
				break;
			}
			//Add to answer string if char is a letter
			if(Character.isLetter(c[lParen]) || c[lParen] == '$' || c[lParen] == '%') {
				String value = Character.toString(c[lParen]);
				answer += value;
			} 
		}
		
		//There were no letters in first element, return null
		if (answer == "") {
			return null;
		}
		//Return the string if it matched first element
		if (matchAndRemove("LABEL(" + answer + ")") != null) {
			return answer; 
		} 
		//matchAndRemove returned null
		return null;
	}
	public static String findBooleanOp() {
		//Check for < 
		if (matchAndRemove("LESSTHAN") != null) {
			return "LESSTHAN";
		}
		//Check for <=
		if (matchAndRemove("LESSTHANEQUALS") != null) {
			return "LESSTHANEQUALS";
		}
		//Check for >
		if (matchAndRemove("GREATERTHAN") != null) {
			return"GREATERTHAN";
		}
		//Check for >=
		if (matchAndRemove("GREATERTHANEQUALS") != null) {
			return "GREATERTHANEQUALS";
		}
		//Check for <>
		if (matchAndRemove("NOTEQUALS") != null) {
			return "NOTEQUALS";
		}
		//Check for =
		if (matchAndRemove("EQUALS") != null) {
			return "EQUALS";
		}
		//No valid operators
		return null;
	}
	public static String findFunctionName() {
		//Check for RANDOM
		if (matchAndRemove("RANDOM") != null) {
			return "RANDOM";
		}
		//Check for LEFT$
		if (matchAndRemove("LEFT$") != null) {
			return "LEFT$";
		}
		//Check for RIGHT$
		if (matchAndRemove("RIGHT$") != null) {
			return "RIGHT$";
		}
		//Check for MID$
		if (matchAndRemove("MID$") != null) {
			return "MID$";
		}
		//Check for NUM
		if (matchAndRemove("NUM$") != null) {
			return "NUM$";
		}
		//Check for VAL
		if (matchAndRemove("VAL") != null) {
			return "VAL";
		}
		//Check for VAL%
		if (matchAndRemove("VAL%") != null) {
			return "VAL%";
		}
		//No valid function name
		return null;
	}
}
	

