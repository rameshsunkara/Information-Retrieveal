package edu.louisiana.cacs.csce561.assignment2.model;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Model to represent the TERM and its properties
 * @author rsunkara
 *
 */
public class Term {

	private String name;
	
	private Set<String> documentList;
	
	private int[] incidenceMatrixValues = null;
	
	private int[] termDocFrequencies = null;
	
	private double[] normTermDocFrequencies = null;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<String> getDocumentList() {
		if(documentList==null)
			documentList = new LinkedHashSet<String>();
		return documentList;
	}

	public void setDocumentList(Set<String> documentList) {
		this.documentList = documentList;
	}

	public int[] getIncidenceMatrixValues() {
		return incidenceMatrixValues;
	}

	public void setIncidenceMatrixValues(int[] incidenceMatrixValues) {
		this.incidenceMatrixValues = incidenceMatrixValues;
	}	
	
	public void setIndice(int index,int value){
		this.incidenceMatrixValues[index] = value;
	}
	
	public int[] getTermDocFrequencies() {
		return termDocFrequencies;
	}

	public void setTermDocFrequencies(int[] termDocFrequencies) {
		this.termDocFrequencies = termDocFrequencies;
	}

	public void setTermDocFrequency(int docIndex,int termDocFrequency){
		this.termDocFrequencies[docIndex] = termDocFrequency;
	}
	
	public double[] getNormTermDocFrequencies() {
		return normTermDocFrequencies;
	}

	public void setNormTermDocFrequencies(double[] normTermDocFrequencies) {
		this.normTermDocFrequencies = normTermDocFrequencies;
	}

	public void setNormTermDocFrequency(int docIndex,double xNormTermDocFrequency){
		this.normTermDocFrequencies[docIndex] = xNormTermDocFrequency;
	}
	
	@Override
	public String toString() {
		return "Term [name=" + name + ", documentList=" + documentList
				+ ", incidenceMatrixValues="
				+ Arrays.toString(incidenceMatrixValues) + "]";
	}
}
