//A2
//Brittany Ally

import java.util.*;
import java.io.*;

public class Search 
{
	public TreeMap<String, Double> queryvect;
	public HashMap<Integer, Double> cosscore;
	public ArrayList<String> qwords;
	
	public double qv;
	public double cosnum;
	public double cosden;
	
	public ArrayList<Integer> retdocs; //retrieved docs for query
	
	public ArrayList<String> stopwords;
	public ArrayList<String> titles;
	
	public TreeMap<String,  ArrayList<Double>> tf;
	public TreeMap<String, ArrayList<Integer>> post;
	public TreeMap<String, Double> idf;
	
	//key doc id, each term in the doc is paired with its 
	public TreeMap<Integer, TreeMap<String,Double>> weightvect;
	public TreeMap<Integer, Double> docnorms;
	
	
	public Search()
	{
		queryvect = new TreeMap<String, Double>();
		cosscore = new HashMap<Integer, Double>();
		qwords = new ArrayList<String>();
		
		qv = 0.0;
		cosnum = 0.0;
		cosden = 0.0;
		
		retdocs = new ArrayList<Integer>(); //retrieved docs for query
		
		stopwords = new ArrayList<String>();
		titles = new ArrayList<String>();
		
		tf = new TreeMap<String, ArrayList<Double>>();
		post = new TreeMap<String, ArrayList<Integer>>();
		idf = new TreeMap<String, Double>();
		
		weightvect = new TreeMap<Integer, TreeMap<String,Double>>();
		docnorms = new TreeMap<Integer, Double>();
		
	}
	
	//store stop words
	public void stopwords() throws FileNotFoundException
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
	
	//store titles
	public void titles() throws FileNotFoundException
	{
		File title = new File("list.txt");
		Scanner scan = new Scanner(title);
		while(scan.hasNextLine())
		{
			String t = scan.nextLine();
			titles.add(t);
		}
		scan.close();
	}
	
	//store term frequency
	public void freq() throws FileNotFoundException
	{
		File fq = new File("tf.txt");
		Scanner scan = new Scanner(fq);
		while(scan.hasNextLine())
		{
			String line = scan.nextLine();
			String[] arr = line.split(" ");
			String term = arr[0];
			ArrayList<Double> f = new ArrayList<Double>();
			for(int i = 1; i<arr.length; i++)
			{
				double x = Double.parseDouble(arr[i]);
				f.add(x);
			}
			tf.put(term, f);			
		}
		scan.close();
	}
	
	//read process and store created posting list
	public void posts() throws FileNotFoundException
	{
		File fq = new File("posting.txt");
		Scanner scan = new Scanner(fq);
		while(scan.hasNextLine())
		{
			String line = scan.nextLine();
			String[] arr = line.split(" ");
			String term = arr[0];
			ArrayList<Integer> f = new ArrayList<Integer>();
			for(int i = 1; i<arr.length; i++)
			{
				Integer x = Integer.parseInt(arr[i]);
				f.add(x);
			}
			post.put(term, f);			
		}
		scan.close();
	}
	
	public void normalizedoc()
	{
		Iterator<NavigableMap.Entry<Integer, TreeMap<String, Double>>> itr = weightvect.entrySet().iterator();
		while(itr.hasNext())
		{
			NavigableMap.Entry<Integer, TreeMap<String, Double>> entry = itr.next();
			 int doc = entry.getKey(); 
	         TreeMap<String,Double> tfs = entry.getValue();
	         
	         Iterator<NavigableMap.Entry<String,Double>> itr1 = tfs.entrySet().iterator();
	         double n = 0.0;
	         while(itr1.hasNext())
	         {
	        	   NavigableMap.Entry<String, Double> entry1 = itr1.next();
	        	   double freq = entry1.getValue();	
	        	   n = n + (freq*freq);
	        }
	        n = Math.sqrt(n);
	        docnorms.put(doc, n);
		}
	}
	
	//calc weight vectors for each doc
	public void docweightvect() throws FileNotFoundException
	{
		File dict = new File("dictionary.txt");
		Scanner scan = new Scanner(dict);
		
		while(scan.hasNextLine())
		{
			String line = scan.nextLine();
			String[] arr = line.split(" ");
			String term = arr[0];
			double idf = Double.parseDouble(arr[1]);
			
			ArrayList<Double> tfreq = tf.get(term);
			ArrayList<Integer> p = post.get(term);
			for(int i = 0; i<tfreq.size(); i++)
			{
				double freq = tfreq.get(i);
				int doc = p.get(i);
				double w = freq*idf;
				
				if(weightvect.containsKey(doc))
				{
					TreeMap<String,Double> temp = weightvect.get(doc);
					temp.put(term, w);
					weightvect.replace(doc, temp);
				}
				else
				{
					TreeMap<String, Double> x = new TreeMap<String,Double>();
					x.put(term,w);
					weightvect.put(doc, x);
				}
			}
			
		}
		scan.close();
	}
	
	
	//normalise the query
	public void normalizeq()
	{
		Iterator<NavigableMap.Entry<String, Double>> itr = queryvect.entrySet().iterator();
		while(itr.hasNext())
		{
			NavigableMap.Entry<String, Double> entry = itr.next();
			double val = entry.getValue();
			qv = qv + (val*val);
		}
		double res = Math.sqrt(qv);
		qv = res;
	}
	
	public void invertquery(String query)
	{
		String[] qarr = query.split(" ");
		for(int i = 0; i<qarr.length; i++)
		{
			String temp = qarr[i];
			String s = temp.replaceAll("[^a-zA-Z0-9]", "");
			if(!s.equals(s.toUpperCase()))
			{
				s = s.toLowerCase();
			}
			if(!stopwords.contains(s))
			{			
				//optional stemming option
				/*Stemmer pstem = new Stemmer();
				char[] ch = new char[s.length()];
				for (int i = 0; i < s.length(); i++) 
				{ 
		            ch[i] = s.charAt(i); 
		        } 
				pstem.add(ch,s.length());
				pstem.stem();
				s = pstem.toString();*/
				
				if(queryvect.containsKey(s))
				{
					double f = queryvect.get(s);
					f++;
					queryvect.replace(s,f);
				}
				else
				{
					queryvect.put(s, 1.0);
				}
			}
		}
	}
	
	public static HashMap<Integer, Double> sortvals(HashMap<Integer, Double> hm) 
    { 
        // Create a list from elements of HashMap 
        List<Map.Entry<Integer, Double> > list = 
               new LinkedList<Map.Entry<Integer, Double> >(hm.entrySet()); 
  
        // Sort the list 
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() { 
            public int compare(Map.Entry<Integer, Double> o1,  
                               Map.Entry<Integer, Double> o2) 
            { 
                return (o1.getValue()).compareTo(o2.getValue()); 
            } 
        }); 
          
        // put data from sorted list to hashmap  
        HashMap<Integer, Double> temp = new LinkedHashMap<Integer, Double>(); 
        for (Map.Entry<Integer, Double> aa : list) { 
            temp.put(aa.getKey(), aa.getValue()); 
        } 
        return temp; 
    } 
	
	//find cosine similarity
	public void cosinesim() 
	{	
		Iterator<NavigableMap.Entry<String, Double>> itr = queryvect.entrySet().iterator();
		while(itr.hasNext())
		{
			NavigableMap.Entry<String, Double> entry = itr.next();
			String t = entry.getKey();
			//double val = entry.getValue();
			if(post.containsKey(t))
			{
				ArrayList<Integer> temp = post.get(t);
				for(int i = 0; i<temp.size(); i++)
				{
					int x = temp.get(i);
					if(!retdocs.contains(x))
					{
						retdocs.add(x);
					}
				}
			}
			qwords.add(t);
		}
		//make case for if term is not in dictionary 
		//go through reldocs list and for each one get the weight 
		for(int i = 0; i < retdocs.size(); i++)
		{
			int docid = retdocs.get(i);
			double dn = docnorms.get(docid);
			double score = 0;
			double den = dn * qv;
			for(int j = 0; j < qwords.size(); j++)
			{
				String word = qwords.get(j);
				if(weightvect.containsKey(docid))
				{
					TreeMap<String,Double> x = weightvect.get(docid); //weights for all terms in current doc
					if(x.containsKey(word))
					{
						double dval = x.get(word);
						double qval = queryvect.get(word);
						score = score + (dval*qval);
					}
				}
			}
			score = score/den;
			cosscore.put(docid, score);
		}
	}
	
	public void find(String query)
	{
		invertquery(query);
		normalizeq();
		cosinesim();
	}
	
	public void reset()
	{
	    queryvect = new TreeMap<String, Double>();
	    cosscore = new HashMap<Integer, Double>();
	    retdocs = new ArrayList<Integer>();
	    qwords = new ArrayList<String>();
		
		qv = 0.0;
		cosnum = 0.0;
		cosden = 0.0;
	}
	
	public ArrayList<Integer> result() throws FileNotFoundException
	{
		ArrayList<Integer> sorted = new ArrayList<Integer>();
		cosscore = sortvals(cosscore);
		for(Map.Entry<Integer, Double> en : cosscore.entrySet()) 
		{ 
			sorted.add(en.getKey());
        } 
		return retdocs;
	}
	
	public void printresult() throws FileNotFoundException
	{
		System.out.println("======================================================");
		System.out.println("The following documents are relevant to your query:");
		System.out.println("======================================================");
		System.out.println(retdocs.size());
		if(retdocs.size() > 0)
		{
			cosscore = sortvals(cosscore);
			Iterator<NavigableMap.Entry<Integer, Double>> itr = cosscore.entrySet().iterator();
			while(itr.hasNext())
			{
				NavigableMap.Entry<Integer, Double> entry = itr.next();
				int di = entry.getKey();
				Double relscore = entry.getValue();
				
				String docID = "[DocID]: ";
				String title = "[Title]: ";
				String rscore = "[Relevance Score]: ";
				
				docID = docID + di;
				title = title + titles.get(di-1);
				//rscore = rscore + relscore; 
				
				System.out.println(docID);
				System.out.println(title);
				System.out.printf(rscore + "%.3f %n", relscore);
				System.out.println("");
				System.out.println("======================================================");
				
			}
		}
		
		else
		{
			System.out.println("No relevant documents found.");
			System.out.println("======================================================");
		}
	}
	
	public void setup() throws FileNotFoundException
	{
		stopwords();
		titles();
		freq();
		posts();
		docweightvect();
		normalizedoc();	
	}
	
	public static void main (String[]args) throws FileNotFoundException
	{
		//below is a quick test
		Search search = new Search();
		search.setup();
		search.find("circles");
		search.printresult();
		System.out.print("+++++++++++++ NEW ++++++++++++++");
		search.reset();
		search.find("explain digits");
		search.printresult();
		
	}
} 
