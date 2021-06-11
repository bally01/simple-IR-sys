//A2
//Brittany Ally

import java.util.*;
import java.io.*;

public class TestSearchInterface 
{
	public static void main(String[]args) throws FileNotFoundException
	{
		System.out.print("Enter a query to search (Type 'ZZEND' to exit) and hit enter: ");
				
		Scanner in = new Scanner(System.in); // user input scanner
		Search search = new Search(); //new search object
		search.setup();
		while(in.hasNextLine())
		{
			String query = in.nextLine();
			// "ZZEND" to exit
			if(query.equals("ZZEND") || query.equals("zzend"))
			{
				in.close();
				System.out.println("Program Terminated.");
				break;				
			}
			else
			{
				search.find(query);
				search.printresult();
				search.reset();
				System.out.println("======================================================");
				System.out.println("Please enter another query (or type 'ZZEND' to exit): ");
			}
		}
	}
}
