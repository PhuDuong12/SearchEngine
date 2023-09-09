package searcher;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Database {
	
	public ArrayList<Document> m_DocumentList;
	public HashMap<String, Integer> m_DicMap;
	
	public Database(String fInDic, String fInVectorNorm, String fInMisc, String fInTitle, String fInAbs) throws IOException {
		BufferedReader brDic = new BufferedReader(new InputStreamReader(new FileInputStream(fInDic)));
		BufferedReader brVec = new BufferedReader(new InputStreamReader(new FileInputStream(fInVectorNorm)));
		BufferedReader brMisc = new BufferedReader(new InputStreamReader(new FileInputStream(fInMisc)));
		BufferedReader brTitle = new BufferedReader(new InputStreamReader(new FileInputStream(fInTitle)));
		BufferedReader brAbs = new BufferedReader(new InputStreamReader(new FileInputStream(fInAbs)));
		
		m_DocumentList = new ArrayList<Document>();
		m_DicMap = new HashMap<String, Integer>();
		
		HashMap<Integer, String> paperTitleMap = new HashMap<Integer, String>();
		HashMap<Integer, String> paperAbstractMap = new HashMap<Integer, String>();
		HashMap<Integer, String> paperMiscMap = new HashMap<Integer, String>();
		
		String line = null;
		int index = 1;
		while((line = brDic.readLine())!= null) {
			m_DicMap.put(line, index);
			index++;
		}
		
		while ((line = brTitle.readLine())!= null) {
			if (line.contains(":") && !line.endsWith(":")) {
				//System.out.println(line);
				int pIndex = Integer.parseInt(line.substring(0, line.indexOf(":")));
				paperTitleMap.put(pIndex, line.substring(line.indexOf(":")+2));
			}
		}
		
		while ((line = brAbs.readLine())!= null) {
			if (line.contains(":") && !line.endsWith(":")) {
				int pIndex = Integer.parseInt(line.substring(0, line.indexOf(":")));
				paperAbstractMap.put(pIndex, line.substring(line.indexOf(":")+2));
			}
		}
		
		while ((line = brMisc.readLine())!= null) {
			if (line.contains(":||") && !line.contains("||||")) {
				int pIndex = Integer.parseInt(line.substring(0, line.indexOf(":")));
				paperMiscMap.put(pIndex, line.substring(line.indexOf(":||")+4).trim());
			}
		}
		
		
		while ((line = brVec.readLine())!= null) {
			if (line.contains(":")) {
				int pIndex = Integer.parseInt(line.substring(0, line.indexOf(":")));
				String vector = line.substring(line.indexOf("::")+2);
				String title = "";
				String abs = "";
				String date = "";
				String author ="";
				title = (paperTitleMap.containsKey(pIndex)?paperTitleMap.get(pIndex): title);
				abs = (paperAbstractMap.containsKey(pIndex)?paperAbstractMap.get(pIndex): abs);
				String misc = "";
				misc = (paperMiscMap.containsKey(pIndex)?paperMiscMap.get(pIndex): misc);
				ArrayList<String> authorDate = getAuthorDate(misc);
				author = authorDate.get(0);
				date = authorDate.get(1);
				Document document = new Document(pIndex, title, abs, date, author, vector);
				m_DocumentList.add(document);
			}
		}
			
		brDic.close();
		brVec.close();
		brMisc.close();
		brTitle.close();
		brAbs.close();
	}
	
	/** **/
	private ArrayList<String> getAuthorDate(String authorDate) {
		ArrayList<String> result = new ArrayList<String>();
		result.add("");
		result.add("");
		if (authorDate.endsWith("||")) {
			result.add(0, authorDate.substring(0,authorDate.length()-2));
		} else  {
			String[] element = authorDate.split("\\|\\|");
			//System.out.println(authorDate);
			if (element.length == 2) {
				result.add(0,element[0]);
				result.add(1, element[1]);
			}
		}
		return result;
	}

}
