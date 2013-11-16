package edu.louisiana.cacs.csce561.assignment2.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QueryResult {
	
	private Query query;
	
	private int totalRelevantCount = 0;
	
	private Map<String,Double> fileRSVMap = new LinkedHashMap<String, Double>();

	private List<String> relevantDocsList = new ArrayList<String>();
	
	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public int getTotalRelevantCount() {
		return totalRelevantCount;
	}

	public void setTotalRelevantCount(int totalRelevantCount) {
		this.totalRelevantCount = totalRelevantCount;
	}

	public Map<String, Double> getFileRSVMap() {
		return fileRSVMap;
	}

	public void setFileRSVMap(Map<String, Double> fileRSVMap) {
		this.fileRSVMap = fileRSVMap;
	}

	public List<String> getRelevantDocsList() {
		return relevantDocsList;
	}

	public void setRelevantDocsList(List<String> relevantDocsList) {
		this.relevantDocsList = relevantDocsList;
	}
	
	
}
