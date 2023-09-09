package searcher;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Metrics {
	
	/**
	 * 
	 * @param centroid
	 * @param doc
	 * @return
	 */
	public static double Euclidean(HashMap<Integer, Double> vector1, HashMap<Integer, Double> vector2, int dim) {
		double distance = 0.0;
		for (short i = 0; i < dim; i++) {
			double val1 = (vector1.containsKey(i+1)? vector1.get(i+1): 0.0);
			double val2 = (vector2.containsKey(i+1)? vector2.get(i+1): 0.0);
			distance += (val1 - val2)*(val1 - val2);
		}
		return Math.sqrt(distance);
	}

	/**
	 * Calculate the distance between two vectors 
	 * 
	 */
	public static double KLDiv(HashMap<Integer, Double> vector1, HashMap<Integer, Double> vector2, int dim) {
		double distance = 0.0;
		double minVal = 0.00000001;		
		for (short i = 0; i < dim; i++) {
			double w_t1 = (vector1.containsKey(i+1)? vector1.get(i+1): 0.0);
			double w_t2 = (vector2.containsKey(i+1)? vector2.get(i+1): 0.0);
			if (w_t1 == 0.0) 
				w_t1 = minVal;
			if (w_t2 == 0.0)
				w_t2 = minVal;
			double pi_1 = w_t1 / (w_t1 + w_t2);
			double pi_2 = w_t2 / (w_t1 + w_t2);
			double w_t = pi_1 * w_t1 + pi_2 * w_t2;
			distance += pi_1 * w_t1 * Math.log(w_t1/w_t) + pi_2 * w_t2 * Math.log(w_t2/w_t);
		}
		return distance;
	}
	
	
//	/**
//	 * Calculate distance between two documents using cosine similarity over TFIDF
//	 * @param centroid
//	 * @param doc
//	 * @return
//	 */
//	public static double cosineSimTFIDF(double[] vector1, double[] vector2, int dim) {
//		double distance = 0.0;
//		double absV1 = 0.0;
//		double absV2 = 0.0;
//		double nom = 0.0;
//		
//		for (short i = 0; i < dim; i++) {
//			nom += vector1[i]*vector2[i];
//			absV1 += vector1[i]*vector1[i];
//			absV2 += vector2[i]*vector2[i];
//		}
//		distance = 1.0 - nom / (Math.sqrt(absV1) + Math.sqrt(absV2));
//		return distance;
//	}
	
	/**
	 * Calculate the Jensen Shannon divergence distance
	 * @param centroid
	 * @param doc
	 * @return
	 */
	public static double jenShanD(HashMap<Integer, Double> vector1, HashMap<Integer, Double> vector2, int dim) {
		double distance = 0.0;		
		for (short i = 0; i < dim; i++) {
			double p = 0.0;
			double q = 0.0;
			p = (vector1.containsKey(i+1) ? vector1.get(i+1) : p);
			q = (vector2.containsKey(i+1) ? vector2.get(i+1) : q);
			if ( !(p == 0.0 && q == 0.0 )) {
				if (p == 0.0)
					distance += (q*0.69314718056);
				else if (q == 0.0)
					distance += (p*0.69314718056);
				else 
					distance += p*Math.log(2*p/(p+q)) + q*Math.log(2*q/(p+q));
			}
		}
		distance /= 2.0;
		return distance;
	}
}
