//test file A1
//Brittany Ally

/* 
 Purpose of this program is to test the output dic and postings file from
 InvertIdx.java -- this test program will use those files by having a user
 query:
  		The query involves typing a single term 
  		 If the term is in one of the documents in the collection:
  		 OUTPUT = the document frequency and all the documents which contain this term, 
  		 		  for each document it should display: the document ID, the title, 
  		 the term frequency, all the positions the term occurs in that document, 
  		 and a summary of the document highlighting the first occurrence of this 
  		 term with 10 terms in its context. 
  		 
  		 When user types in the term ZZEND, the program will stop (this requirement 
  		 is valid only when your program doesn't have a graphical interface). Each time, 
  		 when user types in a valid term, the program should also output the time from 
  		 getting the user input to outputting the results. Finally, when the program stops, 
  		 the average value for above-mentioned time should also be displayed. 
 */

import java.util.*;
import java.io.*;

public class TestInvert 
{
	public static ArrayList<String> stopwords = new ArrayList<String>();
	
	//read and store stop words
	public static void stopwords() throws FileNotFoundException
	{
		File stop = new File("C:\\Users\\britt\\OneDrive\\Desktop\\CPS842\\ASSIGNMENT 1\\stopwords.txt");
		Scanner scan = new Scanner(stop);
		while(scan.hasNext())
		{
			String word = scan.next();
			stopwords.add(word);
		}
		scan.close();
	}
	
	public static String preprocess(String str)
	{
		String s = str.replaceAll("[^a-zA-Z0-9 ]", "");
		if(!s.equals(s.toUpperCase()))
		{
			s = s.toLowerCase();
		}
		
		/*Stemmer pstem = new Stemmer();
		char[] ch = new char[s.length()];
		for (int i = 0; i < s.length(); i++) 
		{ 
            ch[i] = s.charAt(i); 
        } 
		pstem.add(ch,s.length());
		pstem.stem();
		s = pstem.toString(); */
		
		return s;
	}
	
	public static boolean finddict(String str, File dictf) throws FileNotFoundException
	{
		String df = "The document frequency is: ";
		boolean found = false;		
		Scanner sc = new Scanner(dictf);
		while(sc.hasNextLine())
		{
			String line = sc.nextLine();
			String[] arr = line.split(" ");
			String t = arr[0];
			if(t.equals(str))
			{
				String docn = arr[1];
				df = df + docn;
				found = true;
				System.out.println(df);
				System.out.println("===================================================");
				break;
			}
		}
		sc.close();
		if(found == false)
		{
			System.out.println("Term not found.");
		}
		return found;
	}
	
	public static void findpost(String str, File postf, File file) throws FileNotFoundException
	{
		Scanner scp = new Scanner(postf);
		while(scp.hasNextLine())
		{
			String line = scp.nextLine();
			String[] arr = line.split(" ");
			String t = arr[0];
			if(t.equals(str))
			{
				filesearch(str, arr, file);
			}
		}
		scp.close();
	}
	
	public static void filesearch(String str, String[] docids, File cacmf) throws FileNotFoundException
	{
		Scanner sc = new Scanner(cacmf);
		for(int i = 1; i<docids.length; i++)
		{
			String docID = "[DocID]: ";
			String title = "[Title]: ";
			String termf = "[Term Frequency]: ";
			//for later use
			//String sum10 = "[Context Highlight]: ";
			
			int count = 1;
			
			while(sc.hasNext())
			{
				String s = sc.next();
				if(s.equals(".I"))
				{
					s = sc.next();
					if(s.equals(docids[i]))
					{
						System.out.println(docID + docids[i]);
						s = sc.next();
						if(s.equals(".T"))
						{
							s = sc.next();
							while(!s.equals(".W"))
							{
								title = title + s + " ";
								s = preprocess(s);
								if(s.equals(str))
								{
									count++;
								}
								s = sc.next();
							}
							System.out.println(title);
						}
						
						if(!s.equals(".B") || !s.equals(".A"))
						{
							String temp = preprocess(s);
							if(temp.equals(str))
							{
								count++;
								/*if(first == false)
								{
									
								}*/
							}
						}
						System.out.println(termf + count);
						//System.out.println(sum10);
						System.out.println("---------------------------------------------------");
						break;
					}
				}
			}
		}
		sc.close();
	}
	
	public static void main(String[]args) throws FileNotFoundException
	{
		System.out.print("Enter a keyword to search (Type 'ZZEND' to exit): ");
		
		File dict = new File("C:\\Users\\britt\\OneDrive\\Desktop\\CPS842\\ASSIGNMENT 1\\dictionary.txt");
		File post = new File("C:\\Users\\britt\\OneDrive\\Desktop\\CPS842\\ASSIGNMENT 1\\posting.txt");
		File cacm = new File("C:\\Users\\britt\\OneDrive\\Desktop\\CPS842\\ASSIGNMENT 1\\cacm.all");
				
		Scanner in = new Scanner(System.in); // user input scanner
		
		ArrayList<Long> times = new ArrayList<Long>();
		double avgtime = 0;
		
		String time = "Search time was: ";
		
		while(in.hasNext())
		{			
			String str = in.next();
			if(str.equals("ZZEND") || str.equals("zzend"))
			{
				in.close();
				break;
			}
			System.out.println(" Search Query: "+str);
			
			str = preprocess(str);
			if(stopwords.contains(str) || str.equals(""))
			{
				System.out.print("Invalid Search, please try again. [Search time was: 0s]");
			}
			
			else 
			{
				long startTime = System.currentTimeMillis();
				boolean valid = finddict(str, dict);
				if(valid == true)
				{
					findpost(str, post, cacm);
				}
				long endTime = System.currentTimeMillis();
				long duration = (endTime - startTime);
				times.add(duration);
				System.out.println(time + duration + " milliseconds");
				System.out.println("===================================================");
				System.out.print("Enter another term: ");
			}
		}
		
		if(times.size() == 0)
		{
			System.out.println("Program Terminated. Average Time per Search was: 0s");
		}
		
		if(times.size() == 1)
		{
			System.out.println("Program Terminated. Average Time per Search was: " + times.get(0));
		}
		
		else
		{
			for(int i = 0; i<times.size(); i++)
			{
				long n = times.get(i);
				avgtime = avgtime+n;
			}
			avgtime = avgtime/(times.size());
			System.out.println("Program Terminated. Average Time per Search was: " + avgtime + " milliseconds");
		}
	}

}
