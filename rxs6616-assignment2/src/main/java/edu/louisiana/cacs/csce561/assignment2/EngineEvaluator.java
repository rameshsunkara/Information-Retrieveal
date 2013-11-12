package edu.louisiana.cacs.csce561.assignment2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.louisiana.cacs.csce561.assignment2.model.Query;
import edu.louisiana.cacs.csce561.assignment2.model.QueryResult;
import edu.louisiana.cacs.csce561.assignment2.model.Term;
import edu.louisiana.cacs.csce561.assignment2.util.Configurator;
import edu.louisiana.cacs.csce561.assignment2.util.MyUtilities;

public class EngineEvaluator {

	private static Log m_logger = LogFactory.getLog(EngineEvaluator.class);

	private int[] RANK_LEVELS = { 10, 20, 30, 40, 50 };

	// Holds all the system properties
	private Configurator m_configurator = null;

	List<QueryResult> m_qResults = null;
	
	private Map<String, Term> m_termMap = null;

	public EngineEvaluator(Configurator p_configurator, Map<String, Term> termMap) {
		m_configurator = p_configurator;
		m_termMap = termMap;
	}

	public double calculateRecall(int rANK_LEVELS2, QueryResult qResult) {
		Map<String,Double> fileRSVMap = qResult.getFileRSVMap();
		Set<Entry<String,Double>> entrySet = fileRSVMap.entrySet();
		Iterator<Entry<String, Double>> entrySetItr = entrySet.iterator();
		double recall = 0;
		int count =1;
		int relavantDocCountTillRank = 0;
		int totalRelvantDocCount = 0;
		while(entrySetItr.hasNext()){
			Entry<String,Double> entry = entrySetItr.next();
			if(entry.getValue()!=0){
				++totalRelvantDocCount;
				if(count<=rANK_LEVELS2)
					++relavantDocCountTillRank;
			}
			count++;
		}
		recall =  (relavantDocCountTillRank/(double)totalRelvantDocCount);
		return recall;
	}

	public double calculatePrecision(int rANK_LEVELS2, QueryResult qResult) {
		Map<String,Double> fileRSVMap = qResult.getFileRSVMap();
		Set<Entry<String,Double>> entrySet = fileRSVMap.entrySet();
		Iterator<Entry<String, Double>> entrySetItr = entrySet.iterator();
		double precision = 0;
		int count =1;
		int relavantDocCountTillRank = 0;
		while(entrySetItr.hasNext()){
			Entry<String,Double> entry = entrySetItr.next();
			if(entry.getValue()!=0){
				++relavantDocCountTillRank;
			}
			if(count==rANK_LEVELS2)
				break;
			count++;
		}
		precision= (relavantDocCountTillRank/(double)rANK_LEVELS2);
		return precision;
	}

	public List<String> readQueries() {
		BufferedReader xCurrentDocReader = null;
		String xCurrLineInDoc = null;
		List<String> queryList = new ArrayList<String>();
		File f = new File(m_configurator.get_given_queries_file_path());
		try {
			xCurrentDocReader = new BufferedReader(new FileReader(
					f));
			while((xCurrLineInDoc = xCurrentDocReader.readLine())!=null){
				queryList.add(xCurrLineInDoc);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(xCurrentDocReader!=null)
				try {
					xCurrentDocReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		
		return queryList;
	}

	public void evaluate() {
		List<String> queries = readQueries();
		generateQueryResults(queries);
		m_qResults = readQueryResult();
		int querySetSize = m_qResults.size();
		double[][] precisionValues = new double[RANK_LEVELS.length][querySetSize];
		double[][] recallValues = new double[RANK_LEVELS.length][querySetSize];

		Iterator<QueryResult> queryResultItr = m_qResults.iterator();
		int queryCount = 0;
		for (int i = 0; i < RANK_LEVELS.length; i++) {
			while (queryResultItr.hasNext()) {
				QueryResult tempRes = queryResultItr.next();
				recallValues[i][queryCount] =
							calculateRecall(
						RANK_LEVELS[i],tempRes );
				precisionValues[i][queryCount] = 
						calculatePrecision(
						RANK_LEVELS[i], tempRes);
				queryCount++;
			}
			//Reset 
			queryCount = 0;
			queryResultItr = m_qResults.iterator();
		}

		PrintWriter xEvaluationResultWriter = null;
		try {
			xEvaluationResultWriter = new PrintWriter(new File(m_configurator.get_evaluation_result_file_path()));
			for (int i = 0; i < RANK_LEVELS.length; i++) {
				xEvaluationResultWriter.println("***********************************");
				xEvaluationResultWriter.format("Average Precision at RANK %d is %.4f\n"
								,RANK_LEVELS[i]
								,findAverage(precisionValues[i],
										querySetSize));
				xEvaluationResultWriter.format("Average Recall at RANK %d is %.4f\n"
								,RANK_LEVELS[i]
								,findAverage(recallValues[i], querySetSize));
				xEvaluationResultWriter.println("***********************************");
			}
		} catch (FileNotFoundException e) {
			m_logger.error("FileNotFoundException",e);
		}finally{
			if(xEvaluationResultWriter!=null){
				xEvaluationResultWriter.close();
			}
		}
		
	}

	private void generateQueryResults(List<String> queries) {
		SearchFiles searchFiles = new SearchFiles(m_configurator, m_termMap,true);
		Iterator<String> queryItr = queries.iterator();
		while(queryItr.hasNext()){
			searchFiles.findRSV(queryItr.next());
		}
	}

	private double findAverage(double[] ds, int querySetSize) {
		double sum = 0;
		for (double d : ds) {
			sum = sum + d;
		}
		return (sum/(double)querySetSize);
	}

	public List<QueryResult> readQueryResult() {
		File[] qResultFiles = MyUtilities.getDocumentList(m_configurator
				.get_eval_gen_query_output_dir());
		BufferedReader xCurrentDocReader = null;
		List<QueryResult> qrList = new ArrayList<QueryResult>();
		String xCurrLineInDoc = null;
		for (File xCurrentQueryResultFile : qResultFiles) {
			try {
				xCurrentDocReader = new BufferedReader(new FileReader(
						xCurrentQueryResultFile));
				xCurrLineInDoc = xCurrentDocReader.readLine();
				String[] queryTerms = xCurrLineInDoc.split("[\\s]");
				Query q = new Query();
				q.setQueryStringList(Arrays.asList(queryTerms));
				QueryResult queryReslult = new QueryResult();
				queryReslult.setQuery(q);
				while ((xCurrLineInDoc = xCurrentDocReader.readLine()) != null) {
					String[] parsedStrings = xCurrLineInDoc.split("[\\s]");
					//System.out.println(parsedStrings[0] +"--"+parsedStrings[1]);
					queryReslult.getFileRSVMap().put(parsedStrings[0],
					Double.parseDouble(parsedStrings[1]));
				}
				qrList.add(queryReslult);
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
		}
		return qrList;
	}
}
