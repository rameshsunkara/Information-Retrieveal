package edu.louisiana.cacs.csce561.assignment2.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Model to represents Document
 * @author rsunkara
 *
 */
public class Document {

	private String name;
	
	//Stores the terms present in map
	private Map<String,Term> termsMap = new HashMap<String,Term>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Map<String, Term> getTermsMap() {
		return termsMap;
	}

	public void setTermsMap(Map<String, Term> termsMap) {
		this.termsMap = termsMap;
	}
	
	@Override
	public String toString() {
		return "Document [name=" + name + ", termList=" + termsMap.keySet() + "]";
	}
}
