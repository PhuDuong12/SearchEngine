package searcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;

public class WordDistribution {
	
	public static int NUMWORD_TITLE = 2972;
	public static int NUMWORD_ABSTRACT = 7493;
	public static int NUMWORD_COMBINED = 7755;
	
	
	public static void main(String[] args) throws IOException {
		WordDistribution wDist = new WordDistribution();
		wDist.normalizeFile(
				"data/title/titleVector.txt", 
				"data/title/titleVectorNormalize.txt", 
				NUMWORD_TITLE
				);
		
		wDist.normalizeFile(
				"data/abstract/abstractVector.txt", 
				"data/abstract/abstractVectorNormalize.txt", 
				NUMWORD_ABSTRACT
				);
		
		wDist.normalizeFile(
				"data/titleabstract/titleabstractVector.txt", 
				"data/titleabstract/titleabstractVectorNormalize.txt", 
				NUMWORD_COMBINED
				);
	}

	
	public void normalizeFile(String fInVector, String fOutVector, int numWord) throws IOException {
		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutVector)));
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fInVector)));
		String line = null;
		while ((line = br.readLine())!= null ) {
			
			if (!line.endsWith("::")) {
				double[] vector = new double[numWord];
				
				//2::227 1 182,299 1 1262,342 1 1022,369 2 1313,371 3 1353
				String[] element = line.substring(line.indexOf("::")+2).split(",");
				HashMap<Integer, Double> wordFreq = new HashMap<Integer, Double>();
				double sum = 0.0;
				for (int i = 0; i <element.length; i++) {
					//System.out.println(element[i]);
					String[] ele = element[i].split(" ");
					int wordIndex = Integer.parseInt(ele[0]);
					double freq = Double.parseDouble(ele[1]);
					wordFreq.put(wordIndex, freq);
					sum += freq;
				}
				
				StringBuffer vec = new StringBuffer(line.substring(0,line.indexOf(":")));
				vec.append("::");
				for(int i = 0; i < numWord; i++) {
					
					if (wordFreq.containsKey(i+1)) {
						if (!vec.toString().endsWith("::")) vec.append(",");
						vec.append(i+1).append(" ");
						vec.append( (int)((wordFreq.get(i+1)/sum)*1000000)/1000000.0);
					}
					
				}
				wr.append(vec.toString());
				wr.append("\n");
				
			}
		}
		wr.close();
		br.close();		
	}
}
