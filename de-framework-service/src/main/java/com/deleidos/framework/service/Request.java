package com.deleidos.framework.service;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Request implements Serializable {
	private String request;

	public Request() {
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}
}
