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
import java.util.LinkedHashMap;
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

	private Map<Query, QueryResult> m_ourSystemResults = null;

	private Map<Query, QueryResult> m_benchMarked_Results = null;

	private Map<String, Term> m_termMap = null;

	public EngineEvaluator(Configurator p_configurator,
			Map<String, Term> termMap) {
		m_configurator = p_configurator;
		m_termMap = termMap;
	}

	public void evaluate() {
		// Read stemmed queries
		List<String> queries = readQueries();
		// Generate RSV values using our system
		generateQueryResults(queries);

		PrintWriter xEvaluationResultWriter = null;
		try {
			xEvaluationResultWriter = new PrintWriter(new File(
					m_configurator.get_evaluation_result_file_path()));
			m_benchMarked_Results = readQueryResult(m_configurator
					.get_bench_marked_query_results_dir());
			m_ourSystemResults = readQueryResult(m_configurator
					.get_eval_gen_query_output_dir());

			findEvaluationMeasures(m_ourSystemResults,
					"Values for the system we developed",
					xEvaluationResultWriter);
			xEvaluationResultWriter.flush();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (xEvaluationResultWriter != null) {
				xEvaluationResultWriter.close();
			}
		}
	}

	public void findEvaluationMeasures(Map<Query, QueryResult> queryResultMap,
			String strToBePrinted, PrintWriter xEvaluationResultWriter) {
		Set<Entry<Query, QueryResult>> queryResultEntrySet = queryResultMap
				.entrySet();
		Iterator<Entry<Query, QueryResult>> queryResultItr = queryResultEntrySet
				.iterator();
		double recall = 0;
		double precision = 0;
		double r = 0;
		double p = 0;
		Entry<Query, QueryResult> tempRes = null;
		for (int i = 0; i < RANK_LEVELS.length; i++) {
			while (queryResultItr.hasNext()) {
				tempRes = queryResultItr.next();
				r = calculateRecall(RANK_LEVELS[i], tempRes.getValue());
				xEvaluationResultWriter.println("Recall at "+RANK_LEVELS[i] + " for Query:"+tempRes.getKey().getQueryName() +" is:"+r);
				recall += r;
				p = calculatePrecision(RANK_LEVELS[i],
						tempRes.getValue());
				xEvaluationResultWriter.println("Precision at "+RANK_LEVELS[i] + " for Query:"+tempRes.getKey().getQueryName()+" is:"+p);
				xEvaluationResultWriter.println("");
				precision += p;
			}
			xEvaluationResultWriter.println("");
			xEvaluationResultWriter.println("Average Recall value at " +RANK_LEVELS[i]+" :"
					+ (recall / (double) queryResultMap.size()));
			xEvaluationResultWriter.println("Average Precision value at " +RANK_LEVELS[i]+" :"
					+ (precision / (double) queryResultMap.size()));
			xEvaluationResultWriter.println("***********************************************");
			xEvaluationResultWriter.flush();
			recall = 0;
			precision = 0;
			queryResultItr = queryResultEntrySet
					.iterator();
		}
	}

	public void calcualteAverageValues(double[][] recallValues,
			double[][] precisionValues, PrintWriter xEvaluationResultWriter,
			int querySetSize) {
		for (int i = 0; i < querySetSize; i++) {
			for (int j = 0; j < RANK_LEVELS.length; j++) {
				xEvaluationResultWriter.format(
						"Average Precision at RANK %d is %.4f\n",
						RANK_LEVELS[i],
						findAverage(precisionValues[i], RANK_LEVELS.length));
				xEvaluationResultWriter.format(
						"Average Recall at RANK %d is %.4f\n", RANK_LEVELS[i],
						findAverage(recallValues[i], RANK_LEVELS.length));
			}
		}
	}

	public double calculateRecall(int rankLevel, QueryResult qResult) {
		Map<String, Double> fileRSVMap = qResult.getFileRSVMap();
		Set<Entry<String, Double>> entrySet = fileRSVMap.entrySet();
		Iterator<Entry<String, Double>> entrySetItr = entrySet.iterator();
		double recall = 0;
		int count = 1;
		List<String> ourSystemsRelvantDocuments = new ArrayList<String>();
		while (entrySetItr.hasNext()) {
			Entry<String, Double> entry = entrySetItr.next();
			if (entry.getValue() != 0) {
				ourSystemsRelvantDocuments.add(entry.getKey());
				if (count > rankLevel)
					break;
			}
			count++;
		}
		System.out.println("Our system relvant document:"
				+ ourSystemsRelvantDocuments);
		QueryResult benchMarkedResult = m_benchMarked_Results.get(qResult
				.getQuery());
		List<String> benchMarkedRelvantDocuments = getRelevantDocuements(
				benchMarkedResult, rankLevel);
		int relvanceMatchCount = 0;
		Iterator<String> itr = ourSystemsRelvantDocuments.iterator();
		while (itr.hasNext()) {
			if (benchMarkedRelvantDocuments.contains(itr.next()))
				relvanceMatchCount++;
		}
		System.out.println("Bench marked count:"
				+ benchMarkedResult.getTotalRelevantCount());
		recall = (relvanceMatchCount / (double) benchMarkedResult
				.getTotalRelevantCount());
		System.out.println("Recall for :" + qResult.getQuery().getQueryName()
				+ " at level" + rankLevel + " is:" + recall);
		return recall;
	}

	private List<String> getRelevantDocuements(QueryResult benchMarkedResult,
			int rankLevel) {
		List<String> relvantDocuments = new ArrayList<String>();

		Iterator<String> itr = benchMarkedResult.getRelevantDocsList()
				.iterator();
		int count = 0;
		while (count < rankLevel && itr.hasNext()) {
			relvantDocuments.add(itr.next());
		}
		return relvantDocuments;
	}

	public double calculatePrecision(int rankLevel, QueryResult qResult) {
		Map<String, Double> fileRSVMap = qResult.getFileRSVMap();
		Set<Entry<String, Double>> entrySet = fileRSVMap.entrySet();
		Iterator<Entry<String, Double>> entrySetItr = entrySet.iterator();
		double precision = 0;
		int count = 1;
		List<String> ourSystemsRelvantDocuments = new ArrayList<String>();
		while (entrySetItr.hasNext()) {
			Entry<String, Double> entry = entrySetItr.next();
			if (entry.getValue() != 0) {
				ourSystemsRelvantDocuments.add(entry.getKey());
				if (count >= rankLevel)
					break;
			}
			count++;
		}

		QueryResult benchMarkedResult = m_benchMarked_Results.get(qResult
				.getQuery());
		List<String> benchMarkedRelvantDocuments = getRelevantDocuements(
				benchMarkedResult, rankLevel);
		int relvanceMatchCount = 0;
		Iterator<String> itr = ourSystemsRelvantDocuments.iterator();
		while (itr.hasNext()) {
			if (benchMarkedRelvantDocuments.contains(itr.next()))
				relvanceMatchCount++;
		}
		precision = (relvanceMatchCount / (double) rankLevel);
		System.out.println("Precision for :"
				+ qResult.getQuery().getQueryName() + " at level" + rankLevel
				+ " is:" + precision);
		return precision;
	}

	public List<String> readQueries() {
		BufferedReader xCurrentDocReader = null;
		String xCurrLineInDoc = null;
		List<String> queryList = new ArrayList<String>();
		File f = new File(m_configurator.get_given_queries_file_path());
		try {
			xCurrentDocReader = new BufferedReader(new FileReader(f));
			while ((xCurrLineInDoc = xCurrentDocReader.readLine()) != null) {
				queryList.add(xCurrLineInDoc);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (xCurrentDocReader != null)
				try {
					xCurrentDocReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

		return queryList;
	}

	private void generateQueryResults(List<String> queries) {
		SearchFiles searchFiles = new SearchFiles(m_configurator, m_termMap,
				true);
		Iterator<String> queryItr = queries.iterator();
		while (queryItr.hasNext()) {
			searchFiles.findRSV(queryItr.next());
		}
	}

	private double findAverage(double[] ds, int querySetSize) {
		double sum = 0;
		for (double d : ds) {
			sum = sum + d;
		}
		return (sum / (double) RANK_LEVELS.length);
	}

	public Map<Query, QueryResult> readQueryResult(String queryResultDirPath) {
		File[] qResultFiles = MyUtilities.getDocumentList(queryResultDirPath);
		BufferedReader xCurrentDocReader = null;
		String xCurrLineInDoc = null;
		Map<Query, QueryResult> queryResultMap = new LinkedHashMap<Query, QueryResult>();
		int relvantCount = 0;
		for (File xCurrentQueryResultFile : qResultFiles) {
			try {
				relvantCount = 0;
				xCurrentDocReader = new BufferedReader(new FileReader(
						xCurrentQueryResultFile));
				xCurrLineInDoc = xCurrentDocReader.readLine();
				String[] queryTerms = xCurrLineInDoc.split("[\\s]");
				Query q = new Query();
				q.setQueryName(MyUtilities.getQueryName(queryTerms));
				q.setQueryStringList(Arrays.asList(queryTerms));
				QueryResult queryReslult = new QueryResult();
				queryReslult.setQuery(q);
				while ((xCurrLineInDoc = xCurrentDocReader.readLine()) != null) {
					String[] parsedStrings = xCurrLineInDoc.split("[\\s]");
					String docName = parsedStrings[0];
					double rsvValue = Double.parseDouble(parsedStrings[1]);
					queryReslult.getFileRSVMap().put(docName, rsvValue);
					if (rsvValue != 0) {
						relvantCount++;
						queryReslult.getRelevantDocsList().add(docName);
					}
				}
				queryReslult.setTotalRelevantCount(relvantCount);
				queryResultMap.put(q, queryReslult);
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
		return queryResultMap;
	}
}
