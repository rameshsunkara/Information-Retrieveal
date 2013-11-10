package edu.louisiana.cacs.csce561.assignment2.model;

import java.util.List;

/**
 * Model to represents Document
 * @author rsunkara
 *
 */
public class Document {

	private String name;
	
	private List<String> termList;
	
	private List<String> rawTermList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getTermList() {
		return termList;
	}

	public void setTermList(List<String> termList) {
		this.termList = termList;
	}

	public List<String> getRawTermList() {
		return rawTermList;
	}

	public void setRawTermList(List<String> rawTermList) {
		this.rawTermList = rawTermList;
	}

	@Override
	public String toString() {
		return "Document [name=" + name + ", termList=" + termList
				+ ", rawTermList=" + rawTermList + "]";
	}
	
	
	
}
