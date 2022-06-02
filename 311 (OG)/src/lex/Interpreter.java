package lex;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import lex.MathOpNode.Operation;

public class Interpreter {
	List<StatementNode> statementsList = new ArrayList<StatementNode>();
	public Interpreter(List<StatementNode> listFromParser) {
		this.statementsList = listFromParser;
	}
	private static HashMap<String, Integer> intMap = new HashMap<String, Integer>();
	private static HashMap<String, Float> floatMap = new HashMap<String, Float>();
	private static HashMap<String, String> stringMap = new HashMap<String, String>();
	//Stores LabelNodes right now
	private static HashMap<String, Node> labelNodeMap = new HashMap<String, Node>();
	
	private static List<Node> readList = new ArrayList<Node>();
	
	public void initialize() throws Exception {

		visitStatements();
		setNext();
		visitForAndNext();
		visitData();
		//System.out.println("Statements List "  + statementsList);
		//Check for this because if it is a datanode statementsList will be empty
		if (statementsList.size() > 0) {
			interpret(statementsList.get(0));
		} 
	}
	
	public void visitStatements() {
		StatementNode statement = null;
		for (int i = 0; i < statementsList.size(); i++) {
			//Check for labelstatementNodes 
			StatementNode s = statementsList.get(i);
			if (s instanceof LabeledStatementNode) {
				//Label Found !
				String labelName = ((LabeledStatementNode) s).getLabelName();
				StatementNode labelStatement = ((LabeledStatementNode) s).getLabelStatement();
				labelNodeMap.put(labelName, labelStatement);
				//swap label statement with the statementNode
				statementsList.set(i, labelStatement);
			}
			/* //test
			if (statement.next == null) {
				System.out.println("next is now Pointing to NULL");
			} else {
				System.out.println("next is now pointing to: " + statement.next);
			}
			*/
		}
		//System.out.println("\t\t" + labelNodeMap.values()); //test hashmap
	}
	public void setNext() {
		//We want to set a reference to the next node so we know which node to go after the currentNode
		StatementNode statement = null;
		for (int i = 0; i < statementsList.size(); i++) {
			//Set each statement's "next reference" to the next statement in the statementsNode list . . .
			statement = statementsList.get(i);
			if (i + 1 < statementsList.size()) {
				statement.next = statementsList.get(i + 1);
			} else {
				//Getting here means there is no other statement after this 
				statement.next = null;
			}
		}

	}
	public void visitForAndNext() {
		StatementNode currentNextNode = null, currentForNode = null;
		for (int i = 0; i < statementsList.size(); i++) {
			if(statementsList.get(i) instanceof ForNode) {
				//For should have a reference to the statement after next
				//FOR is found !
				currentForNode = statementsList.get(i);
				//try to find matching NEXT after we find FOR
				for (int j = i; j < statementsList.size(); j++) {
					if(statementsList.get(j) instanceof NextNode) {
						//NEXT is found !
						currentNextNode = statementsList.get(j);
						//Node references the FOR 
						((NextNode) currentNextNode).forReference = currentForNode;
						/*----------------------------------------
						This is regarding FOR                            
						Check bounds to not get "Out of Bounds Exception" */
						if (j + 1 < statementsList.size()) {
							//Since there is a NextNode at index j, we want the statement after index j to be our next reference for ForNode
							((ForNode) currentForNode).nextReference = statementsList.get(j+1);
							
						} else {
							//There is no statement after NEXT
							((ForNode) currentForNode).nextReference = null;
							//currentForNode.next = null;
						}
						//Get out of loop since we only need to find 1 NEXT to match 1 FOR
						break;
					}
				}
				/* //Test for NEXT
				if (currentNextNode == null) {
					System.out.println("NO NEXTNODE !!!");
				} */
			}
		}
		/* //Test for FOR
		if (currentForNode == null) {
			System.out.println("\t\tNO FORNODE !!!");
		} */
	}
	public void visitData() {
		for (StatementNode t : statementsList) {
			if(t instanceof DataNode) {
				//Add to list for read and remove from statements list
				readList.addAll(((DataNode) t).getNodeList());
				statementsList.remove(t);
				//statementsList is empty and we can only take one statement per line right now 
				break;
			}
		}
	}
	//Interpreter2 Assignment  --- 
	
	public void interpret(StatementNode currentNode) throws Exception {
		Stack<Node> stack = new Stack<Node>();
		//while current.next != null
		do {
			//Check for ReadNode
			if (currentNode instanceof ReadNode) {
				System.out.println("I Am Reading . . ." + readList);
				//ReadNode is found !
				//Set each variable to the next item in the readList
				//The String is the variable name and the check whether it is int, float, string and put it in the appropriate hashmap
				//READ(a,b) means get the first thing in readList: check the type of the item vs the type of the variable.
			
				//check each variable in the read list to see if it is a $tring, F%oat, or int
				List<VariableNode> varList = ((ReadNode) currentNode).getVarList();
				int size = varList.size();
				for (int i = 0; i < varList.size(); i++) {
					//set var to the variable in the list
					String variableName = varList.get(i).getVarName();
					if (variableName.lastIndexOf('$') != -1) {
						//Variable is a string !
						//Make sure type of first item in list is String because variable is also string
						if (readList.get(i) instanceof StringNode) {
							//value is also a string -- types match !
							String value = nodeToString(readList.get(i)); 
							stringMap.put(variableName, value);
						} else {
							throw new Exception ("Value in readList { " + readList.get(i) + " }" + " is NOT the same type (String) as the variable in ReadNode");
						}
						
					} else if (variableName.lastIndexOf('%') != -1) {
						//Variable is a float !
						//Make sure type of first item in list is Float because variable is also Float
						if (readList.get(i) instanceof FloatNode) {
							//value is also a string -- types match !
							float value = nodeToFloat(readList.get(i)); 
							floatMap.put(variableName, value);
						} else {
							throw new Exception ("Value in readList { " + readList.get(i) + " }" + "is NOT the same type (Float) as the variable in ReadNode");
						}
					} else {
						//Variable is an integer
						//Make sure type of first item in list is Float because variable is also Float
						if (readList.get(i) instanceof IntegerNode) {
							//value is also a string -- types match !
							int value = nodeToInteger(readList.get(i)); 
							intMap.put(variableName, value);
						} else {
							throw new Exception ("Value in readList { " + readList.get(i) + " }" + "is NOT the same type (Integer) as the variable in ReadNode");
						}
					}
					
				}
				//removes the first element after every variable is assigned so that the next read can't assign another value to this variable
				for (int j = 0; j < size; j++) {
					readList.remove(0);
				}
				currentNode = currentNode.next;
			}
			if (currentNode instanceof AssignmentNode) {
				//AssignmentNode is found !
				Node node = ((AssignmentNode) currentNode).getNodeValue();
				//Evaluate the expression/string and set the variable to the expression/string value
				//The first getVarName is from AssignmentNode and returns a VariableNode, and the second getVarName is from VariableNode to get the string value inside the variableNode
				String varName = ((AssignmentNode) currentNode).getVarName().getVarName();
				//Value can be int or float or string so do all + test for possibility of functions being values
				if (node instanceof IntegerNode) {
					//value is int
					int intValue = evaluateIntMathOp(((AssignmentNode) currentNode).getNodeValue());
					if (intValue != 999999999 ) {
						intMap.put(varName, intValue);
					}
				} else if (node instanceof FloatNode) {
					//value is float
					float floatValue = evaluateFloatMathOp(((AssignmentNode) currentNode).getNodeValue());
					if (floatValue != 123456789) {
						floatMap.put(varName, floatValue);
					}
				} else if (node instanceof MathOpNode) {
					//value is a mathOpNode
					int intLeft = evaluateIntMathOp(node);
					float floatLeft = evaluateFloatMathOp(node);
					if (intLeft != 999999999) {
						intMap.put(varName, intLeft);
					} else if (floatLeft != 123456789){
						floatMap.put(varName, floatLeft);
					}
					//pass left operation first to determine whether this is an int or float
					/*int intLeft = evaluateIntMathOp(((MathOpNode) node).getLeft());
					
					}  */
					//must be a float mathOpNode
					
				} else if (node instanceof StringNode) {
					//value is string
					String stringValue = nodeToString(((AssignmentNode) currentNode).getNodeValue());
					if (stringValue != null) {
						stringMap.put(varName, stringValue);
					}
				} else if (node instanceof FunctionNode) {
					String base = ((FunctionNode) node).getFunctionName();
					//Get the first and second elements in the list since there can only be two params and we don't want to loop a list with 2 elements
					List<Node> parameters = ((FunctionNode) node).getNodeList();
					switch (base) { //Create a function for this switch statement 
						case "RANDOM" : 
							intMap.put(varName, random());
							break;
						case "LEFT$" :
							StringNode value = (StringNode)parameters.get(0);
							IntegerNode amount = (IntegerNode)parameters.get(1);
							stringMap.put(varName, left(value.getString(), amount.getValue()));
							break;
						case "RIGHT$" :
							StringNode value2 = (StringNode)parameters.get(0);
							IntegerNode amount2 = (IntegerNode)parameters.get(1);
							stringMap.put(varName, right(value2.getString(), amount2.getValue()));
							break;
						case "MID$" :
							//This method has 3 parameters, so we need to increment our gets by 1 from the original
							StringNode value3 = (StringNode)parameters.get(0);
							IntegerNode start = (IntegerNode) parameters.get(1);
							IntegerNode stringAmount = (IntegerNode)parameters.get(2);
							stringMap.put(varName, mid(value3.getString(), start.getValue(), stringAmount.getValue()));
							break;
						case "NUM$" : 
							if (parameters.get(0) instanceof IntegerNode) {
								IntegerNode numToString = (IntegerNode) parameters.get(0);
								stringMap.put(varName, num(numToString.getValue()));
							} else if (parameters.get(0) instanceof FloatNode) {
								FloatNode numToString = (FloatNode) parameters.get(0);
								stringMap.put(varName, num(numToString.getValue()));
							}
							
							break;
						case "VAL" :
							StringNode value4 = (StringNode)parameters.get(0);
							intMap.put(varName, val(value4.getString()));
							break;
						case "VAL%" :
							StringNode value5 = (StringNode)parameters.get(0);
							floatMap.put(varName, valPercentSign(value5.getString()));
							break;
						default:
							break;
					}
				} else {
					throw new Exception("Value is not an int/float/string");
				}
				currentNode = currentNode.next;
			}
			if (currentNode instanceof InputNode) {
				//InputNode is found !
				//print the string to console, check what variable it is, and store the variables with user data into the map
				String question = ((InputNode) currentNode).getStringNode().getString();
				System.out.println(question);
				List<VariableNode> inputVarList = ((InputNode) currentNode).getVarList();
				Scanner scan = new Scanner(System.in);
				//For each variable we will store it in the appropriate hashmap with the value given by the **USER**
				for (int i = 0; i < inputVarList.size(); i++) {
					
					String variableString = inputVarList.get(i).getVarName();
					if (variableString.lastIndexOf('$') != -1) {
						//Expect a string 
						String answer = scan.nextLine();
						stringMap.put(variableString, answer);
					} else if (variableString.lastIndexOf('%') != -1){
						//Expect a float
						float answer = scan.nextFloat();
						floatMap.put(variableString, answer);
					} else {
						//Expect an integer
						int answer = scan.nextInt();
						intMap.put(variableString, answer);
					}
					
				}
				currentNode = currentNode.next;
			}
			if (currentNode instanceof PrintNode) {
				//PrintNode is found ! 
				List<Node> printList = ((PrintNode) currentNode).getList();
				System.out.println("PRINTING . . .");
				//Loop the printList for every node that we have to print
				//Exception will be thrown for PRINT "" because printList is null, so we can't loop 
				for (int i = 0; i < printList.size(); i++) {
					Node node = printList.get(i);
					if (node instanceof StringNode) {
						String onlyString = ((StringNode) node).getString();
						System.out.print(onlyString);
						
					} else if (node instanceof MathOpNode || node instanceof IntegerNode || 
							node instanceof FloatNode || node instanceof VariableNode) {
						if (node instanceof MathOpNode) {
							//evaluate the expression/integer/float/variable using this method because it works for all 3 cases
							int intValue = evaluateIntMathOp(node);
							float floatValue = evaluateFloatMathOp(node);
							//999999999 and 123456789 represent invalid values
							if (intValue != 999999999) {
								//value is int
								System.out.println(intValue);
							} else if (floatValue != 123456789) {
								//value is float
								System.out.print(floatValue);
							}
						} else if (node instanceof FloatNode) {
							//value is float
							float floatValue = evaluateFloatMathOp(node);
							System.out.print(floatValue);
						} else if (node instanceof VariableNode) {
							String varString = ((VariableNode) node).getVarName();
							//value is variable; inside variable can be string, int, or float
							if (varString.lastIndexOf("$") != -1) {
								//this is a string
								System.out.print(stringMap.get(varString));
							} else if (varString.lastIndexOf("%") != -1) {
								//this is a float
								System.out.print(floatMap.get(varString));
							} else {
								//has to be an int
								System.out.print(intMap.get(varString));
							}
						} else {
							throw new Exception ("NODE IS NOT VALID IN MATHOPNODE");
						}
					} else if (node instanceof FunctionNode) {
						String base = ((FunctionNode) node).getFunctionName();
						//Get the first and second elements in the list since there can only be two params and we don't want to loop a list with 2 elements
						List<Node> parameters = ((FunctionNode) node).getNodeList();
						switch (base) { //Create a function for this switch statement 
							case "RANDOM" : 
								System.out.print("RANDOM Number between 0 and 1000: " + random());
								break;
							case "LEFT$" :
								StringNode value = (StringNode)parameters.get(0);
								IntegerNode amount = (IntegerNode)parameters.get(1);
								System.out.print("LEFT$(" + value.getString() + ", " + amount.getValue() + ") : " + left(value.getString(), amount.getValue()));
								break;
							case "RIGHT$" :
								StringNode value2 = (StringNode)parameters.get(0);
								IntegerNode amount2 = (IntegerNode)parameters.get(1);
								System.out.print("RIGHT$(" + value2.getString() + ", " + amount2.getValue() + ") : " +right(value2.getString(), amount2.getValue()));
								break;
							case "MID$" :
								//This method has 3 parameters, so we need to increment our gets by 1 from the original
								StringNode value3 = (StringNode)parameters.get(0);
								IntegerNode start = (IntegerNode) parameters.get(1);
								IntegerNode stringAmount = (IntegerNode)parameters.get(2);
								System.out.print("MID$(" + value3.getString() + ", " + start.getValue() + ", " + stringAmount.getValue() + ") : " + mid(value3.getString(), start.getValue(), stringAmount.getValue()));
								break;
							case "NUM$" : 
								if (parameters.get(0) instanceof IntegerNode) {
									IntegerNode numToString = (IntegerNode) parameters.get(0);
									System.out.print("NUM$(" + numToString.getValue() + ") : " + num(numToString.getValue()));

								} else if (parameters.get(0) instanceof FloatNode) {
									FloatNode numToString = (FloatNode) parameters.get(0);
									System.out.print("NUM$(" + numToString.getValue() + ") : " + num(numToString.getValue()));
								}
								break;
							case "VAL" :
								StringNode value4 = (StringNode)parameters.get(0);
								System.out.print("VAL(" + value4.getString() + ") : " + val(value4.getString()));
								break;
							case "VAL%" :
								StringNode value5 = (StringNode)parameters.get(0);
								System.out.print("VAL%(" + value5.getString() + ") : " +valPercentSign(value5.getString()));
								break;
							default:
								break;
						}
						
					} else {
						
					}
					System.out.print(" ");
				}
				System.out.println("\n");
				currentNode = currentNode.next;
			}
			if (currentNode instanceof IfNode) {
				boolean good = evaluateBooleanOp(((IfNode) currentNode).getOperation());
				if (good) {
					//boolean operation returned true !
					currentNode = (StatementNode) labelNodeMap.get(((IfNode) currentNode).getLabel().getVarName());
				} else {
					currentNode = currentNode.next;
				}
			}
			if (currentNode instanceof ForNode) {
				
				VariableNode varName = ((ForNode) currentNode).getVarName();
				// -------------- INITIALIZATION ---------------------
				IntegerNode step = ((ForNode) currentNode).getStep();
				int increment = 1;
				if (intMap.containsKey(varName.getVarName())) {
					//The variable is already initialized !
					//If step is not found, then the default (1) will be added to the value but if it is found, then take that value and add it to the value of the variable
					if (step != null ) {
						//step is found !
						increment = ((ForNode) currentNode).getStep().getValue();
					}
					//Add the current value and the increment to get the new value
					int newValue = intMap.get(varName.getVarName()) + increment;
					//hashmap will overwrite the key with this new value
					intMap.put(varName.getVarName(), newValue);
				} else {
					//the variable is not in the hashmap, so we need to initialize it !
					int initializeValue = ((ForNode) currentNode).getInitialValue().getValue();
					intMap.put(varName.getVarName(), initializeValue);
					//If step is not found, then the default (1) will be added to the value but if it is found, then take that value and add it to the value of the variable
					if (step != null ) {
						//step is found !
						increment = ((ForNode) currentNode).getStep().getValue();
					}
				}
				
				// ----------------- CHECKING CONDITION -----------------------
				
				//check if the current value is over the limit because that determines what the next node will be
				int currentValue = intMap.get(varName.getVarName());
				int limit = ((ForNode) currentNode).getLimit().getValue();
				//Assume that if the increment is positive, the currentValue should be less than or equal to the limit
				if (increment >= 0 && currentValue <= limit) {
					//body of the for loop
					currentNode = currentNode.next;
				//Assume that if the increment is negative, the current value is greater than the limit
				} else if (increment <= 0 && currentValue >= limit) {
					//body of the for loop
					currentNode = currentNode.next;
				} else {
					//this is the statement after the next statement
					currentNode = (StatementNode) ((ForNode) currentNode).nextReference;
					//Remove the variable so we can use it again without mixing up values
					intMap.remove(varName.getVarName());
				}
			}
			if (currentNode instanceof NextNode) {
				//This will reference the ForNode to check if condition is true/false
				currentNode = ((NextNode) currentNode).forReference;
			}
			if (currentNode instanceof GoSubNode) {
				//
				stack.push(currentNode.next);
				String name = ((GoSubNode) currentNode).getGoSub().getVarName();
				if (labelNodeMap.containsKey(name)) { 
					Node lblStatement = labelNodeMap.get(name);
					currentNode = (StatementNode) lblStatement;
				}
				
			}
			if (currentNode instanceof ReturnNode) {
				if (!stack.isEmpty()) {
					currentNode = (StatementNode) stack.pop();
				} else {
					currentNode = currentNode.next;
				}
				
			}
		} while (currentNode != null);
	}
	public int evaluateIntMathOp(Node node) throws Exception {
		if (node instanceof IntegerNode) {
			//This is just an integer -- easy !
			return ((IntegerNode) node).getValue();
		} else if (node instanceof VariableNode) {
		  /*VariableNode(x) has to be an integer
			VariableNode(x$) has to be a string
			VariableNode(x%) has to be a float */

			//We assume that the variable has to be of an integer value so we will search the integer hashmap 
			for (int i = 0; i < intMap.size(); i++) {
				if (intMap.containsKey(((VariableNode) node).getVarName())) {
					return intMap.get(((VariableNode) node).getVarName());
				} 
			}
			//The variable is not found in the map !
			//throw new Exception("Variable is undefined in Integer Hashmap");
		} else if (node instanceof MathOpNode) {
			//MathOpNode is found ! 
			//float values can be taken so we need to return an invalid int so that it doesn't process the float
			if (((MathOpNode) node).getLeft() instanceof FloatNode || ((MathOpNode) node ).getRight() instanceof FloatNode) {
				return 999999999;
			}
			int left = evaluateIntMathOp(((MathOpNode) node).getLeft());
			int right = evaluateIntMathOp(((MathOpNode) node).getRight());
			if (((MathOpNode) node).getOperation() == MathOpNode.Operation.Add) {
				return left + right;
			} else if (((MathOpNode) node).getOperation() == MathOpNode.Operation.Subtract) {
				return left - right;
			} else if (((MathOpNode) node).getOperation() == MathOpNode.Operation.Multiply) {
				return left * right;
			} else if (((MathOpNode) node).getOperation() == MathOpNode.Operation.Divide) {
				return left / right;
			} else {
				throw new Exception("Math Operation is not valid");
			}
			
		}  
		//System.out.println("-------------------\nThis Node is not valid\n-------------------");
		return 999999999;
	}
	public float evaluateFloatMathOp(Node node) throws Exception {
		if (node instanceof FloatNode) {
			//This is just an float -- easy !
			return ((FloatNode) node).getValue();
		} else if (node instanceof VariableNode) {
		  /*VariableNode(x) has to be an integer
			VariableNode(x$) has to be a string
			VariableNode(x%) has to be a float */
			
			//We assume that the variable has to be of an integer value so we will search the integer hashmap 
			for (int i = 0; i < floatMap.size(); i++) {
				if (floatMap.containsKey(((VariableNode) node).getVarName())) {
					return floatMap.get(((VariableNode) node).getVarName());
				}
			}
			//The variable is not found in the map !
			//throw new Exception("Variable is undefined in Float Hashmap");
		} else if (node instanceof MathOpNode) {
			//MathOpNode is found ! 
			//float values can be taken so we need to return an invalid int so that it doesn't process the float
			if (((MathOpNode) node).getLeft() instanceof IntegerNode || ((MathOpNode) node ).getRight() instanceof IntegerNode) {
				return 123456789;
			}
			float left = evaluateFloatMathOp(((MathOpNode) node).getLeft());
			float right = evaluateFloatMathOp(((MathOpNode) node).getRight());
			if (((MathOpNode) node).getOperation() == MathOpNode.Operation.Add) {
				return left + right;
			} else if (((MathOpNode) node).getOperation() == MathOpNode.Operation.Subtract) {
				return left - right;
			} else if (((MathOpNode) node).getOperation() == MathOpNode.Operation.Multiply) {
				return left * right;
			} else if (((MathOpNode) node).getOperation() == MathOpNode.Operation.Divide) {
				return left / right;
			} else {
				throw new Exception("Math Operation is not valid");
			}
			
		} 
		//System.out.println("-------------------\nThis Node is not valid\n-------------------");
		return 123456789;
	}
	public boolean evaluateBooleanOp(Node booleanOpNode) throws Exception {
		Node exp1 = ((BooleanOperationNode)booleanOpNode).getExpression1();
		Node exp2 = ((BooleanOperationNode)booleanOpNode).getExpression2();
		String operation = ((BooleanOperationNode)booleanOpNode).getOperation();
		int intExp1 = evaluateIntMathOp(exp1);
		if (intExp1 != 999999999) {
			//first expression is an IntegerNode !
			//Now, we know that the next expression has to be IntegerNode as well since we only evaluate same-type expressions
			int intExp2 = evaluateIntMathOp(exp2);
			
			//
			if (operation.equals("LESSTHAN")) {
				return intExp1 < intExp2;
			} else if (operation.equals("LESSTHANEQUALS")) {
				return intExp1 <= intExp2;
			} else if (operation.equals("GREATERTHAN")) {
				return intExp1 > intExp2;
			} else if (operation.equals("GREATERTHANEQUALS")) {
				return intExp1 >= intExp2;
			} else if (operation.equals("NOTEQUALS")) {
				return intExp1 != intExp2;
			} else if (operation.equals("EQUALS")) {
				return intExp1 == intExp2;
			} 
		} 
		//first expression is a FloatNode !
		float floatExp1 = evaluateFloatMathOp(exp1);
		float floatExp2 = evaluateFloatMathOp(exp2);
		if (operation.equals("LESSTHAN")) {
			return floatExp1 < floatExp2;
		} else if (operation.equals("LESSTHANEQUALS")) {
			return floatExp1 <= floatExp2;
		} else if (operation.equals("GREATERTHAN")) {
			return floatExp1 > floatExp2;
		} else if (operation.equals("GREATERTHANEQUALS")) {
			return floatExp1 >= floatExp2;
		} else if (operation.equals("NOTEQUALS")) {
			return floatExp1 != floatExp2;
		} else if (operation.equals("EQUALS")) {
			return floatExp1 == floatExp2;
		} 
		
		System.out.println("If you reached here, then there is an error : Going to default FALSE");
		return false;
	}
	
	public int nodeToInteger(Node n) {
		if (n instanceof IntegerNode) {
			return ((IntegerNode) n).getValue();
		} 
		return -1;
	}
	public float nodeToFloat(Node n) {
		if (n instanceof FloatNode) {
			return ((FloatNode) n).getValue();
		} 
		return -1;
	}
	public String nodeToString(Node n) {
		//Get the value inside the stringNode
		if (n instanceof StringNode) {
			return ((StringNode) n).getString();
		} 
		return null;
	}
	
	//Below are the Built-in Functions 
	//------------------------------------
	
	public int random() {
		//Return a random integer between 0 - 1000
		return (int)(Math.random() * 1000);
	}
	public String left(String str, int amount) {
		//Take the string and return the leftmost chars depending on the amount specificed in the param
		return str.substring(0, amount);
	}
	public String right(String str, int amount) {
		//Take the string and return the rightmost chars depending on the amount specificed in the param
		int start = str.length() - amount;
		int end = str.length();
		return str.substring(start, end);
	}
	public String mid(String str, int startingPoint, int amount) {
		//Take the string and return the chars starting from the second param with the amount specified in the 3rd param
		//Add 1 to the startingPoint because the built-in method subtracts second param by 1 and we want it to include the last char in string
		int endingPoint = startingPoint + amount;
		return str.substring(startingPoint, endingPoint);
	}
	public String num(int number) {
		//converts an integer to a string
		return String.valueOf(number);
	}
	public String num(float number) {
		//converts a float to a string
		return String.valueOf(number);
	}
	public int val(String str) {
		//String to integer
		return Integer.parseInt(str);
	}
	public float valPercentSign(String str) {
		//String to integer
		return Float.parseFloat(str);
	}
}
