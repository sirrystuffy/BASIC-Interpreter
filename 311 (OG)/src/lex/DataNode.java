package lex;

import java.util.List;

public class DataNode extends StatementNode{
	private List<Node> nodeList;
	//constructor takes a list of Node
	public DataNode(List<Node> listOfNodes) {
		this.nodeList = listOfNodes;
	}
	public DataNode() {
	}
	
	//Accessor
	public List<Node> getNodeList() {
		return nodeList;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "DataNode(" + getNodeList() + ")";
	}
}
