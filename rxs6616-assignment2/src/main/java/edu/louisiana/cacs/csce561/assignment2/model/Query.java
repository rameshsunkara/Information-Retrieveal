package edu.louisiana.cacs.csce561.assignment2.model;

import java.util.ArrayList;
import java.util.List;

public class Query {
	
	private String queryName;

	private List<String> queryStringList = new ArrayList<String>();
	
	private double[] qeuryVector = null;

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public List<String> getQueryStringList() {
		return queryStringList;
	}

	public void setQueryStringList(List<String> queryStringList) {
		this.queryStringList = queryStringList;
	}

	public double[] getQeuryVector() {
		return qeuryVector;
	}

	public void setQeuryVector(double[] qeuryVector) {
		this.qeuryVector = qeuryVector;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((queryName == null) ? 0 : queryName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Query other = (Query) obj;
		if (queryName == null) {
			if (other.queryName != null)
				return false;
		} else if (!queryName.equals(other.queryName))
			return false;
		return true;
	}
	
	
}
