package searcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.Version;

import searcher.Stopwords;

public class Indexer {
	
	//public static int FREQUENCE = 10; //for title
	public static int FREQUENCE = 20; //for abstract
	//public static int FREQUENCE = 30; //for title and abstract 
	
	public static void main(String[] args) throws IOException {
		Indexer indexer = new Indexer();
//		indexer.doIndex(
//				"data/title/title.txt",
//				"data/title/titleindex/");
//		
//		indexer.doDump(
//				"data/title/titleindex/",
//				"data/title/titleDic.txt",
//				"data/title/titleVector.txt"
//				);
		
//		indexer.doIndex(
//		"data/abstract/abstract.txt",
//		"data/abstract/abstractindex/");
//
//		indexer.doDump(
//		"data/abstract/abstractindex/",
//		"data/abstract/abstractDic.txt",
//		"data/abstract/abstractVector.txt"
//		);		
	

//		indexer.doIndex(
//		"data/titleabstract/titleabstract.txt",
//		"data/titleabstract/titleabstractindex/");
//
//		indexer.doDump(
//		"data/titleabstract/titleabstractindex/",
//		"data/titleabstract/titleabstractDic.txt",
//		"data/titleabstract/titleabstractVector.txt"
//		);
		


	}
	
	/** Indexing the data**/
	public void doIndex(String fIn, String indexFolder) throws IOException {
		File path = new File(indexFolder);
		//Deleting the old index
        String[] children = path.list();
        if (children != null) {
	        for (int i=0; i<children.length; i++) {
	        	File delFile = new File(path,children[i]);
	        	delFile.delete();
	        }
        }
        Directory indexDir = new MMapDirectory(path);
	    //EnglishAnalyzer luceneAnalyzer = new EnglishAnalyzer(Version.LUCENE_34, Stopwords.stopwords);
        Analyzer luceneAnalyzer = new StandardAnalyzer(Version.LUCENE_34, Stopwords.stopwords);
        //for words transform from path
        //MyAnalyzer luceneAnalyzer = new MyAnalyzer(Version.LUCENE_34, Stopwords.stopwords);
	    IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_34,luceneAnalyzer);
	    IndexWriter indexWriter = new IndexWriter(indexDir,config);
	    long startTime = new Date().getTime();
	    int count  = 0;
	    String line = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fIn),"UTF-8"));
		while ( (line = br.readLine()) != null) {
		
			if (line.contains(":")) {
				String documentId = line.substring(0,line.indexOf(":"));
				String content = line.substring(line.indexOf(": ")+2); 
				Document document = new Document();
				//document.add(new Field("documentId",documentId,Field.Store.YES,Field.Index.NOT_ANALYZED,Field.TermVector.NO));
				document.add(new Field("documentId",documentId,Field.Store.YES,Field.Index.NOT_ANALYZED,Field.TermVector.NO));
				document.add(new Field("content",content, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES));
				indexWriter.addDocument(document);
				System.out.println(count + " -- Indexing ::: "+line);
				count++;
			}
		}
	    indexWriter.optimize();
	    indexWriter.close();
	    long endTime = new Date().getTime();
        System.out.println("It took " + (endTime - startTime)
        		+ " milliseconds to create an index for the files in the directory " + path.getName());
        br.close();
	}
	
	
	/** Dumping  index **/
	public void doDump(String indexPath, String fOutDict, String fOutTFDF) throws IOException {
		BufferedWriter wrDic = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutDict),"UTF-8"));
		BufferedWriter wrVector = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutTFDF),"UTF-8"));
		int count  = 0;
		HashMap<String,Integer> wordSet = new HashMap<String,Integer>();
		File path = new File(indexPath);
	    Directory indexDir = new MMapDirectory(path);
		IndexReader reader = IndexReader.open(indexDir);
		//Writing to the dictionary file	
		count = 1;
		TermEnum tenum = reader.terms();
		while (tenum.next()) {
			if (tenum.term().field().equals("content")) {
				if (reader.docFreq(tenum.term()) > FREQUENCE) {
					wordSet.put(tenum.term().text(), count);
					wrDic.write(tenum.term().text()+ "\n");
					count++;
				}
			}
		}
		wrDic.close();
		for (int numDoc = 0; numDoc < reader.numDocs(); numDoc++) {
			TermFreqVector vector = reader.getTermFreqVector(numDoc, "content");
			if (vector != null) {
				Document document = reader.document(numDoc);
				System.out.println(numDoc + " :: "+document.get("documentId"));
				StringBuffer data = new StringBuffer("");
				data.append(document.get("documentId")).append("::");
				for (int numTerm = 0; numTerm < vector.getTerms().length; numTerm++) {
					if (wordSet.containsKey(vector.getTerms()[numTerm])) {
						if (!data.toString().endsWith("::"))
							data.append(",");
						data.append(wordSet.get(vector.getTerms()[numTerm])).append(" ").
						append(vector.getTermFrequencies()[numTerm]).append(" ").
						append(reader.docFreq(new Term("content",vector.getTerms()[numTerm])));
					}
				}
				data.append("\n");				
				wrVector.write(data.toString());
				data = new StringBuffer("");
			}
		}
		reader.close();
		wrVector.close();
	}
	
	
	/** Filtering the infrequent words **/
	//List low document frequency term
	public void listLowFreqTerms(String indexPath,String fOutHighFreqDic) throws IOException {
		BufferedWriter wrDic = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutHighFreqDic),"UTF-8"));
		File path = new File(indexPath);
	    Directory indexDir = new MMapDirectory(path);
		IndexReader reader = IndexReader.open(indexDir);
		//Writing to the dictionary file
		int count = 0;
		int averFre = 0;
		int countHigh = 0;
		TermEnum tenum = reader.terms();
		while (tenum.next()) {
			if (tenum.term().field().equals("content")) {
				if (reader.docFreq(tenum.term()) < 10   ) {
					count++;
					System.out.println(count+ " ::: "+tenum.term().text());
				} else {
					wrDic.write(tenum.term().text() + " : "+ reader.docFreq(tenum.term())+"\n");
					countHigh++;
					averFre += reader.docFreq(tenum.term());
				}
			}
		}
		System.out.println("Avearage document term freqency : "+ averFre/countHigh);
		reader.close();
		wrDic.close();
	}

}
