package com.riverisland.app.automation.helpers.qubit;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class QubitData {
	
	private List<QubitItem> data;

	public List<QubitItem> getData() {
		return data;
	}

	public void setData(List<QubitItem> data) {
		this.data = data;
	}

}
