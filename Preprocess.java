package searcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashSet;

public class Preprocess {

	public static void main(String[] args) throws IOException {
		Preprocess prep = new Preprocess();
		prep.filter(
				"data/extractions/",
				"data/title/title.txt", 
				"data/abstract/abstract.txt",
				"data/titleabstract/titleabstract.txt",
				"data/authordate.txt"
				);
	}
	
	
	public void filter(String path, String fOutTitle, String fOutAbstract, String fOutTitleAbstract, String fOutMisc) throws IOException {
		
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles(); 
		BufferedWriter wrTitle = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutTitle)));
		BufferedWriter wrAbstract = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutAbstract)));
		BufferedWriter wrTitleAbstract = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutTitleAbstract)));
		BufferedWriter wrMisc = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutMisc)));
		HashSet<String> titleSet = new HashSet<String>();
		
		
		System.out.println(listOfFiles.length);
		int paperId = 0;
		for(int i = 0; i < listOfFiles.length; i++) {
			
			String title = "";
			String abs = "";
			String author = "";
			String date = "";
			
			//System.out.println(listOfFiles[i]);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(listOfFiles[i])));
			String line = null;
			boolean exit = false;
			while ((!exit && (line = br.readLine())!= null)) {
				if (line.startsWith("Title:")) {
					title = line.substring(line.indexOf(":")+1);
				}
				if (line.startsWith("Author:")) {
					author = line.substring(line.indexOf(":")+1);
				}
				if (line.startsWith("Abstract:")) {
					abs = line.substring(line.indexOf(":")+1);
				}
				if (line.startsWith("Date:")) {
					date = line.substring(line.indexOf(":")+1);
				}
				
				if (!titleSet.contains(title) && line.startsWith("Reference:")) {
					titleSet.add(title);
					paperId++;
					System.out.println(paperId);
					//writing to files title
					if (paperId!=1)
						wrTitle.write("\n");
					wrTitle.write(paperId+":");
					wrTitle.write(title);
					
					//writing to file abstract
					if (paperId!=1)
						wrAbstract.write("\n");
					wrAbstract.write(paperId+":");
					wrAbstract.write(abs);
					
					//writing to combined file
					if (paperId!=1)
						wrTitleAbstract.write("\n");
					wrTitleAbstract.write(paperId+":");
					wrTitleAbstract.write(title);
					wrTitleAbstract.write(" ");
					wrTitleAbstract.write(abs);
					
					//write to misc file
					String authorDate = "||"+author+"||"+date;
					if (paperId!=1)
						wrMisc.write("\n");
					wrMisc.write(paperId+":");
					wrMisc.write(authorDate);
					
					
					exit = true;
					br.close();
				}
			}
			
			
		}
		
		wrTitle.close();
		wrAbstract.close();
		wrTitleAbstract.close();
		wrMisc.close();
	}
}
