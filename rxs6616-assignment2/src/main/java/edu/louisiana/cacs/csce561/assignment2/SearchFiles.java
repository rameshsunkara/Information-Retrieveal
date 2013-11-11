package edu.louisiana.cacs.csce561.assignment2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.louisiana.cacs.csce561.assignment2.model.Query;
import edu.louisiana.cacs.csce561.assignment2.model.QueryResult;
import edu.louisiana.cacs.csce561.assignment2.model.Term;
import edu.louisiana.cacs.csce561.assignment2.util.Configurator;
import edu.louisiana.cacs.csce561.assignment2.util.MyUtilities;

public class SearchFiles {

	private static Log m_logger = LogFactory.getLog(SearchFiles.class);

	// Holds all the system properties
	private Configurator m_configurator = null;

	private Map<String, Term> m_termMap = null;

	private List<Query> m_qrList = null;

	public SearchFiles(Configurator p_configurator, Map<String, Term> p_termMap) {
		m_configurator = p_configurator;
		m_termMap = p_termMap;
	}

	public void findRSV(String searchQuery) {
		File[] x_DocFiles = MyUtilities.getDocumentList(m_configurator
				.get_document_input_dir());
		int docIndex = 0;
		double[] docVector = null;
		double[] queryVector = null;
		SortedMap<String, Double> rsvMap = new TreeMap<String, Double>();
		docIndex = 0;
		queryVector = getQueryVector(searchQuery);
		for (File f : x_DocFiles) {
			docVector = getDocumentVector(docIndex);
			rsvMap.put(MyUtilities.getDocNameFromFileName(f.getName()),
							findRsv(docVector, queryVector));
			docIndex++;
		}
		printResults(rsvMap,searchQuery);
	}

	private void printResults(SortedMap<String, Double> rsvMap,String searchQuery){
		PrintWriter xRSVValuePrinter = null;
		try {
			xRSVValuePrinter = new PrintWriter(new File(m_configurator.get_gen_query_output_dir()+File.separator+searchQuery+"_results"));
			xRSVValuePrinter.println(searchQuery);
			Map<String, Double> rsvMap2 = new TreeMap<String, Double>();
			rsvMap2 = MyUtilities.sortByValues(rsvMap);
			Set<Entry<String, Double>> termSet = rsvMap2.entrySet();
			Iterator<Entry<String, Double>> termItr = termSet.iterator();
			while (termItr.hasNext()) {
				Entry<String, Double> tt = termItr.next();
				xRSVValuePrinter.format(tt.getKey() + "==" + tt.getValue()+"\n");
			}
		} catch (FileNotFoundException e) {
			m_logger.error("FileNotFoundException",e);
		}finally{
			if(xRSVValuePrinter!=null){
				xRSVValuePrinter.close();
			}
		}
		
		/*Map<String, Double> rsvMap2 = new TreeMap<String, Double>();
		rsvMap2 = MyUtilities.sortByValues(rsvMap);
		Set<Entry<String, Double>> termSet = rsvMap2.entrySet();
		Iterator<Entry<String, Double>> termItr = termSet.iterator();
		while (termItr.hasNext()) {
			Entry<String, Double> tt = termItr.next();
			System.out.println(tt.getKey() + "==" + tt.getValue());
		}*/
	}
	private double findRsv(double[] docVector, double[] queryVector) {
		double numerator = vectorProduct(docVector, queryVector);
		double denominator = magnitude(docVector) * magnitude(queryVector);
		if(denominator==0.0)
			return 0.0;
		return numerator / denominator;
	}

	private double[] getQueryVector(String q) {
		double[] queryVector = new double[m_termMap.size()];
		int count = 0;
		String[] queryTerms = q.split("[\\s.,]");
		List<String> queryTermList = new ArrayList<String>(Arrays.asList(queryTerms));
		Map<String,Double> queryWeightMap = new HashMap<String, Double>();
		int termWeight = 0;
		for(String eachQueryTerm:queryTerms){
			for(String s:queryTermList){
				if(eachQueryTerm.equalsIgnoreCase(s))
					termWeight++;
			}
			queryWeightMap.put(eachQueryTerm, 1/(double)termWeight);
		}
		String currTerm = null;
		Set<Entry<String, Term>> termSet = m_termMap.entrySet();
		Iterator<Entry<String, Term>> termItr = termSet.iterator();
		while (termItr.hasNext()) {
			currTerm = termItr.next().getKey();
			if(queryTermList.contains(currTerm)){
				queryVector[count] = queryWeightMap.get(currTerm);
			}
			count++;
		}
		
		for(String s:queryTerms){
			if(m_termMap.containsKey(s)){
				
			}
		}
		return queryVector;
	}

	public List<Query> readQueries() {
		BufferedReader xCurrentDocReader = null;
		m_qrList = new ArrayList<Query>();
		String xCurrLineInDoc = null;
		try {
			xCurrentDocReader = new BufferedReader(new FileReader(
					m_configurator.get_queires_file()));
			while ((xCurrLineInDoc = xCurrentDocReader.readLine()) != null) {
				String[] queryTerms = xCurrLineInDoc.split("=");
				Query q = new Query();
				q.setQueryName(queryTerms[0]);
				queryTerms = queryTerms[1].split(",");
				q.setQueryStringList(Arrays.asList(queryTerms));
				QueryResult queryReslult = new QueryResult();
				queryReslult.setQuery(q);
				m_qrList.add(q);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (xCurrentDocReader != null)
				try {
					xCurrentDocReader.close();
				} catch (IOException e) {
					e.printStackTrace();
					m_logger.error("Memory leak", e);
				}
		}
		return m_qrList;
	}

	public double vectorProduct(double[] docVector, double[] queryVector) {
		double sum = 0;
		for (int i = 0; i < docVector.length; i++) {
			sum += docVector[i] * queryVector[i];
		}
		return sum;
	}

	public double magnitude(double[] vector) {
		double sum = 0;
		for (int i = 0; i < vector.length; i++) {
			sum += vector[i] * vector[i];
		}
		return Math.sqrt(sum);
	}

	public double[] getDocumentVector(int docIndex) {
		Set<Entry<String, Term>> termSet = m_termMap.entrySet();
		Iterator<Entry<String, Term>> termItr = termSet.iterator();
		double[] docVector = new double[m_termMap.size()];
		int count = 0;
		while (termItr.hasNext()) {
			docVector[count++] = termItr.next().getValue()
					.getNormTermDocFrequencies()[docIndex];
		}
		return docVector;
	}
}
