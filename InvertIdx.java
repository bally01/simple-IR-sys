//A2
//Brittany Ally

import java.io.*;
import java.util.*;


public class InvertIdx
{
	/*
	 KEEP: .I (for document ID), .T (for title), .W (for abstract), .B (for publication date), 
	 and .A (for author list). The terms are extracted from the title and the abstract.
	*/ 
	
	public static TreeMap<String, ArrayList<Integer>> post = new TreeMap<String, ArrayList<Integer>>();
	public static NavigableMap<String, Double> dict = new TreeMap<String,Double>();
	public static ArrayList<String> stopwords = new ArrayList<String>();
	
	public static int docID;
	public static HashMap<Integer, String> title= new HashMap<Integer, String>();
	public static HashMap<Integer, String> author= new HashMap<Integer, String>();
	
	public static HashMap<Integer, String> pubdate= new HashMap<Integer, String>();
	public static HashMap<Integer, String> abs= new HashMap<Integer, String>();
	
	public static TreeMap<String, TreeMap<Integer, Double>> tf = new TreeMap<String, TreeMap<Integer, Double>>();
	
	public static int count = 0;	
	
	//constructor for reconstructive use in possible revamp later
	/*
	public InvertIdx()
	{
		post = new TreeMap<String, ArrayList<Integer>>();
		dict = new TreeMap<String,Double>();
		stopwords = new ArrayList<String>();
		
		docID = 0;
		title= new HashMap<Integer, String>();
		author= new HashMap<Integer, String>();
		
		pubdate= new HashMap<Integer, String>();
		abs= new HashMap<Integer, String>();
		
		tf = new TreeMap<String, TreeMap<Integer, Double>>();
		
		count = 0;
	}
	*/
	
	//creates the dictionary file
	public static void makedict() throws FileNotFoundException
	{
		PrintWriter printp = new PrintWriter(new FileOutputStream("dictionary.txt", true), true); 
		Iterator<NavigableMap.Entry<String, Double>> itr = dict.entrySet().iterator();
		
		 while(itr.hasNext()) 
		 {
			   count++;
	           NavigableMap.Entry<String, Double> entry = itr.next();
	           String term = entry.getKey(); 
	           double df = entry.getValue();
	           df = Math.log((3204.0/df));
	           printp.println(term + " " + df);	           
	     }
		
		printp.close();
	}
	
	//creates the posting list file
	public static void makepost() throws FileNotFoundException
	{
		PrintWriter printp = new PrintWriter(new FileOutputStream("posting.txt", true), true); 
		Iterator<NavigableMap.Entry<String, ArrayList<Integer>>> itr = post.entrySet().iterator();
		
		 while(itr.hasNext()) 
		 {
	           NavigableMap.Entry<String, ArrayList<Integer>> entry = itr.next();
	           String term = entry.getKey(); 
	           List<Integer> docs = entry.getValue();
	           printp.print(term);
	           
	           for(int i = 0; i< docs.size(); i++)
	           {
	        	  printp.print(" " + docs.get(i)); 
	           }
	           printp.println();
	           
	     }
		
		printp.close();
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	//writes list of doc titles
	public static void makel() throws FileNotFoundException //outputs titles
	{
		PrintWriter printp = new PrintWriter(new FileOutputStream("list.txt", true), true); 
		Iterator<NavigableMap.Entry<Integer, String>> itr = title.entrySet().iterator();
		
		 while(itr.hasNext()) 
		 {
	           NavigableMap.Entry<Integer, String> entry = itr.next();
	           Integer term = entry.getKey(); 
	           String df = entry.getValue();
	           printp.println(term + " " + df);	           
	     }
		
		printp.close();
	}
	
	//writes list of term frequency
	public static void maketf() throws FileNotFoundException
	{
		PrintWriter printp = new PrintWriter(new FileOutputStream("tf.txt", true), true); 
		Iterator<NavigableMap.Entry<String, TreeMap<Integer, Double>>> itr = tf.entrySet().iterator();

		 while(itr.hasNext()) 
		 {
	           NavigableMap.Entry<String, TreeMap<Integer, Double>> entry = itr.next();
	           String term = entry.getKey(); 
	           TreeMap<Integer,Double> tfs = entry.getValue();
	           
	           Iterator<NavigableMap.Entry<Integer,Double>> itr1 = tfs.entrySet().iterator();
	           String tofile = "";
	           while(itr1.hasNext())
	           {
	        	   NavigableMap.Entry<Integer, Double> entry1 = itr1.next();
	        	   //int doc = entry1.getKey();
	        	   double freq = entry1.getValue();
	        	   
	        	   tofile = tofile + freq + " ";
	        	   
	           }
	           printp.println(term + " " + tofile);	           
	     }
		
		printp.close();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////

	public static void stopwords() throws FileNotFoundException
	{
		File stop = new File("stopwords.txt");
		Scanner scan = new Scanner(stop);
		while(scan.hasNext())
		{
			String word = scan.next();
			stopwords.add(word);
		}
		scan.close();
	}
	
	//process and stores the term in posting and dictionary, also keeps track of term frequency(tf)
	public static void checkterm(String str)
	{
		String s = str.replaceAll("[^a-zA-Z0-9]", "");
		if(!s.equals(s.toUpperCase()))
		{
			s = s.toLowerCase();
		}
		if(stopwords.contains(s))
		{
			return;
		}
		
		//additonal stemming option
		/*Stemmer pstem = new Stemmer();
		char[] ch = new char[s.length()];
		for (int i = 0; i < s.length(); i++) 
		{ 
            ch[i] = s.charAt(i); 
        } 
		pstem.add(ch,s.length());
		pstem.stem();
		s = pstem.toString();*/
		
		if(dict.containsKey(s))
		{
			double f = dict.get(s);
			f++;
			dict.replace(s,f);
			ArrayList<Integer> po = post.get(s);
			if(!po.contains(docID))
			{
				po.add(docID);
				TreeMap<Integer,Double> fr = tf.get(s);
				fr.put(docID, 1.0);
				tf.replace(s, fr);
			}
			else
			{
				TreeMap<Integer,Double> fr = tf.get(s);
				double count = fr.get(docID);
				count++;
				fr.replace(docID, count);
				tf.replace(s, fr);
			}
		}
		else
		{
			dict.put(s, 1.0);
			
			ArrayList<Integer> p = new ArrayList<Integer>();
			p.add(docID);
			post.put(s, p);
			
			TreeMap<Integer,Double> fre = new TreeMap<Integer,Double>();
			fre.put(docID, 1.0);
			tf.put(s, fre);
			
		}
	}
	
	
	//create the Inverted index
	public static void createInvert() throws FileNotFoundException
	{
		stopwords();
		File docs = new File("cacm.all");
		Scanner scan = new Scanner(new BufferedReader(new FileReader(docs)));
		while(scan.hasNext())	
		{
			String str = scan.next();
			if(str.equals(".I"))
			{
				docID = Integer.parseInt(scan.next());
			}
			if(str.equals(".T"))
			{
				String t = "";
				str = scan.next();
				while((!str.equals(".B")) && (!str.equals(".W")))
				{
					t = t + str + " ";
					checkterm(str);
					str = scan.next();
					
				}
				title.put(docID, t);
			}
			if(str.equals(".W"))
			{
				String ab = "";
				str = scan.next();
				while((!str.equals(".B")) && (!str.equals(".A")) && (!str.equals(".N")) && (!str.equals(".X")) && (!str.equals(".K")) && (!str.equals(".C")))
				{
					ab = ab + str + " ";
					checkterm(str);
					str = scan.next();
				}
				abs.put(docID, ab);
			}
			if(str.equals(".B"))
			{
				String d = "";
				str = scan.next();
				while((!str.equals(".A")) && (!str.equals(".N")) && (!str.equals(".X")) && (!str.equals(".K")) && (!str.equals(".C")))
				{
					d = d + str + " ";
					str = scan.next();
				}
				pubdate.put(docID, d);
			}
			if(str.equals(".A"))
			{
				String a = "";
				str = scan.next();
				while((!str.equals(".N")) && (!str.equals(".X")) && (!str.equals(".K")) && (!str.equals(".C")))
				{
					a = a + str + " ";
					str = scan.next();
				}
				author.put(docID, a);
			}
		}				
		scan.close();
		makedict();
	    makepost();
	    makel();
	    maketf();
	}
		
	public static void main(String[]args) throws FileNotFoundException
	{
		createInvert();
		System.out.println("Inverted Index created. See posting list and dictionary files in directory.");
		//System.out.println("(" + count + " terms)");
	}
}
