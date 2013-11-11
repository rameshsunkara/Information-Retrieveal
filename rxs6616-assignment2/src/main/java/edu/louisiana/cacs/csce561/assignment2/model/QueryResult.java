package edu.louisiana.cacs.csce561.assignment2.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class QueryResult {
	
	private Query query;
	
	private Map<String,Integer> fileRSVMap = new LinkedHashMap<String, Integer>();

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public Map<String, Integer> getFileRSVMap() {
		return fileRSVMap;
	}

	public void setFileRSVMap(Map<String, Integer> fileRSVMap) {
		this.fileRSVMap = fileRSVMap;
	}
}
