package edu.louisiana.cacs.csce561.assignment1.vo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author rsunkara
 *
 */
public class Term {

	private String name;
	
	private List<String> documentList;
	
	private int[] incidenceMatrixValues = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getDocumentList() {
		if(documentList==null)
			documentList = new ArrayList<String>();
		return documentList;
	}

	public void setDocumentList(List<String> documentList) {
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

	@Override
	public String toString() {
		return "Term [name=" + name + ", documentList=" + documentList
				+ ", incidenceMatrixValues="
				+ Arrays.toString(incidenceMatrixValues) + "]";
	}
	
	
}
