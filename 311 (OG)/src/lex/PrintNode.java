package lex;

import java.util.List;

public class PrintNode extends StatementNode{
	//holds a list of Node (things to print)
	private List<Node> nodeList;
	//constructor takes a list of Node
	public PrintNode(List<Node> listOfNode) {
		this.nodeList = listOfNode;
	}
	//Accessor
	public List<Node> getList() {
		return nodeList;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "PrintNode" + getList();
	}
}
