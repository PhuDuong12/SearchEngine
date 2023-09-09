package searcher;

import java.io.IOException;
import java.util.ArrayList;

public class TestQuery {
	
	
	public static void main(String[] args) throws IOException {	
		
		int K = 10;
		
		Searcher searcher = new Searcher();
		
		if (args.length != 3) {
			System.out.println("Usage: java searcher.TestQuery <number of result> <query string> <search-based string>");
			System.out.println("search-based can be \"title\" or \"abstract\" or \"titleabstract\" ");
			return;
		}
		
		K = Integer.parseInt(args[0]);
		searcher.setK(K);
		if (args[2].equals("title")) {
			searcher.loadData(
					"data/title/titleDic.txt",
					"data/title/titleVectorNormalize.txt",
					"data/authordate.txt", 
					"data/title/title.txt",
					"data/abstract/abstract.txt"
					);
		} else if( args[2].equals("abstract")) {
			searcher.loadData(
					"data/abstract/abstractDic.txt",
					"data/title/abstractVectorNormalize.txt",
					"data/authordate.txt", 
					"data/title/title.txt",
					"data/abstract/abstract.txt"
					);
			
		} else if (args[2].equals("titleabstract")) {
			searcher.loadData(
					"data/titleabstract/titleabstractDic.txt",
					"data/titleabstract/titleabstractVectorNormalize.txt",
					"data/authordate.txt", 
					"data/title/title.txt",
					"data/abstract/abstract.txt"
					);
		} else {
			System.out.println("Invalid search-based option");
			return;
		}
		//String query = "HIERARCHICAL MIXTURES OF EXPERTS";
		//String query = "Reinforcement Learning Algorithms for Average-Payoff Markovian Decision Processes";
		ArrayList<Document> result = searcher.doQuery(args[1]);
		for (int i = 0; i < result.size(); i++) 
			result.get(i).printDocument();
		
	}

}
