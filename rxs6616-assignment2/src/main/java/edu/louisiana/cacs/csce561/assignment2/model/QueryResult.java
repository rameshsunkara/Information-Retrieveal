package edu.louisiana.cacs.csce561.assignment2.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class QueryResult {
	
	private Query query;
	
	private Map<String,Double> fileRSVMap = new LinkedHashMap<String, Double>();

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public Map<String, Double> getFileRSVMap() {
		return fileRSVMap;
	}

	public void setFileRSVMap(Map<String, Double> fileRSVMap) {
		this.fileRSVMap = fileRSVMap;
	}
}
