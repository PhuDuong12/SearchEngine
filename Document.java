package searcher;

import java.util.HashMap;
import java.util.StringTokenizer;

/** This class demonstrate a paper **/
public class Document {
	
	public int id;
	public String title;
	public String abs;
	public String date;
	public String author;
	public double distance;
	
	public HashMap<Integer, Double> wordDist;
	
	
	public Document(int  id, String title, String abs, String date, String author, String wordDistribution) {
		this.id = id;
		this.title = title;
		this.abs = abs;
		this.date = date;
		this.author = author;
		this.distance = -1.0;
		wordDist = new HashMap<Integer, Double>();
		String[] element = wordDistribution.split(",");
		for(int i = 0; i < element.length; i++) {
			String[] ele = element[i].split(" ");
			int index = Integer.parseInt(ele[0]);
			double value = Double.parseDouble(ele[1]);
			wordDist.put(index, value);
		}
	}
	
	/** **/
	public static HashMap<Integer, Double> convertQuery(String query, HashMap<String, Integer> dicMap) {
		HashMap<Integer, Double> vector = new HashMap<Integer, Double>();
		query = query.toLowerCase();
		
	     StringTokenizer st = new StringTokenizer(query);
	     while (st.hasMoreTokens()) {
	         String word = st.nextToken();
	         if (dicMap.containsKey(word)) {
	        	 int index = dicMap.get(word);
	        	 if (vector.containsKey(index)) {
	        		 vector.put(index, vector.get(index)+1.0);
	        	 } else {
	        		 vector.put(index, 1.0);
	        	 }
	         }
	     }
	     //Normalize vector
	     double sum = 0;
	     for(Integer key:vector.keySet()) {
	    	 sum += vector.get(key);
	     }
	     
	     for(Integer key:vector.keySet()) {
	    	 vector.put(key, vector.get(key)/sum);
	     }
		
		return vector;
		
		
	}
	
	
	public void printDocument() {
		System.out.print("Title: ");
		System.out.println(this.title);
		System.out.print("Author: ");
		System.out.println(this.author);
		System.out.print("Date: ");
		System.out.println(this.date);
		System.out.print("Abstraction: ");
		System.out.println(this.abs);
		System.out.println("-------------------------------------------------------------------------");
	}

}
