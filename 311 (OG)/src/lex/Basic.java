package lex;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
//Throw the exception and continue to parse the NEXT LINE
public class Basic {
	public static void main(String[] args) throws IOException {
		if (args.length > 1 || args.length == 0) {
			throw new IllegalArgumentException ("More than 1 argument OR No argument");
			
		} 
		//filename will be passed as argument
		String filename = args[0];
		//create lexer object
		Lexer lexing = new Lexer();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		//if file empty, bye bye 
		if (br.readLine() == null) {
			throw new IllegalArgumentException ("File Is Empty");
		}
		//Create a list to store the collection of bits from lex
		List<List<String>> list = new ArrayList<List<String>>();
		List<StatementNode> completeList = new ArrayList<StatementNode>();
		
		
		//loop for every line in the file
		for(String line : Files.readAllLines(Paths.get(filename))) {
			try {
				//Store lexing.lex(line) into a temp list to make this neater. 
				new Parser(lexing.lex(line));
				StatementsNode parsedStatements = Parser.parse();
				completeList.addAll(parsedStatements.getList());
				//System.out.println("PARSER OUTPUT: " + parsedStatements + "\n---------------------------");
				
				//Temporary step to prove lex works
				//lex every line
				list.add(lexing.lex(line));
				
				//Temporary step to prove lex works
				//System.out.println("LEXER OUTPUT: " + lexing.lex(line) + "\n---------------------------");
				//listToString(lx.lex(line));
			
			//Catch the exception and continue to parse the NEXT LINE
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		Interpreter interpret = new Interpreter(completeList);
		try {
			interpret.initialize();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	//Not really necessary, but just in case
	public static String listToString(List<String> listname) {
		String str = "";
		for (int i = 0; i < listname.size(); i++) {
			str += listname.get(i).toString();
			str += " ";
			//System.out.println(listname.get(i).toString()); //this will return each token line by line
		}
		return str;
		
	}
	
}
