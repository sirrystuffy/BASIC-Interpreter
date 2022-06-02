package lex;

public class ForNode extends StatementNode{
	//
	public Node nextReference;
	private VariableNode varName;
	private IntegerNode initialValue;
	private IntegerNode limit;
	private IntegerNode step = null;
	//Constructors to handle step/no step
	public ForNode(VariableNode variable, IntegerNode value, IntegerNode limitAmount) {
		this.varName = variable;
		this.initialValue = value;
		this.limit = limitAmount;
	}
	public ForNode(VariableNode variable, IntegerNode value, IntegerNode limitAmount, IntegerNode increment) {
		this.varName = variable;
		this.initialValue = value;
		this.limit = limitAmount;
		this.step = increment;
	}
	
	//Accessors
	public VariableNode getVarName() {
		return varName;
	}
	public IntegerNode getInitialValue() {
		return initialValue;
	}
	public IntegerNode getLimit() {
		return limit;
	}
	public IntegerNode getStep() {
		return step;
	}
	
	@Override
	public String toString() {
		//with step 
		return "For(" + getVarName() + ", " + getInitialValue() + ", " + getLimit() + ", " + getStep() + ")";

		/*if (getStep() != null) {
			return "For(" + getVarName() + ", " + getInitialValue() + ", " + getLimit() + ", " + getStep() + ")";
		}
		//without step; print the default step value
		return "For(" + getVarName() + ", " + getInitialValue() + ", " + getLimit() + " STEP 1 (DEFAULT) "+ ")"; */
	}
}
