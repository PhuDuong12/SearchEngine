package searcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;

public class Searcher {
	
	public  int K = 10;
	Database m_Database = null;
	
	public void loadData(String fInDic, String fInVectorNorm, String fInMisc, String fInTitle, String fInAbs) throws IOException {
		m_Database = new Database(fInDic, fInVectorNorm, fInMisc, fInTitle, fInAbs);
	}

	public void setK(int k){
		this.K = k;
	}
	public ArrayList<Document> doQuery(String query) {
		ArrayList<Document> returnList = new ArrayList<Document>();
		Queue<Document> pQueue = new PriorityQueue<Document>(60000,idComparator);
		HashMap<Integer, Double> queryVector = Document.convertQuery(query, m_Database.m_DicMap);
		for(int i = 0; i < m_Database.m_DocumentList.size(); i++) {
			double distance = Metrics.jenShanD(queryVector, m_Database.m_DocumentList.get(i).wordDist, m_Database.m_DicMap.size());
			m_Database.m_DocumentList.get(i).distance = distance;
			pQueue.add(m_Database.m_DocumentList.get(i));		
		}
		
		int k = 0;
		while (k < K) {
			returnList.add(pQueue.poll());
			k++;
		}
		return returnList;
	}
	
	
	 //Comparator anonymous class implementation

    public static Comparator<Document> idComparator = new Comparator<Document>(){
		@Override
		public int compare(Document o1, Document o2) {
			// TODO Auto-generated method stub
			return ((Double)o1.distance).compareTo(o2.distance);
		}
    };
}
