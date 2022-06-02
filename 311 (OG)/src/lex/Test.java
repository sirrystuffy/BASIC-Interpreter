package lex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {
	
	public static void main(String[] args) {
		String filename = "input.txt";
		try {
			for(String line : Files.readAllLines(Paths.get(filename))) {
				System.out.println(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int[] test = {1, 2, 3, 4};
		int[] test2 = {1, 2, 3, 4};
		System.out.println(Arrays.equals(test, test2));
		
		String x = "IDENTIFIER(YELLOW)";
		char[] wocky = x.toCharArray();
		System.out.println(x.indexOf('('));
		for (int q = x.indexOf('('); q < wocky.length; q++) {
			if (wocky[q] == 'W') {
				System.out.println(q);
			}
		}
		/* PRINT "hello world.9^3hs",8+888 ,"?" DATA 9 , 4, 0
DATA 10.89,900, "mphipps"
READ a, a$, podjfiu%
INPUT "What is your name and age ?", name, age$, jamal%, "sdf"
beginning: PRINT "Hello!"
GOSUB dfjgaff
FOR variable = 0 TO 11
NEXT A
IF x + 7 <= 5*100
MID$("ALBANY", 2, 3) */
	}
}

//using the bit .and(bit other) method to compare each bit in one longword to each bit in another longword. 
//Then use setBit() to set the bits of a resultant longword to bit 0 or bit 1; return the resultant longword. 
//The problem is that I initialize the resultant longword with a new bit(0), and new bit(0) is not equal to new bit(0).