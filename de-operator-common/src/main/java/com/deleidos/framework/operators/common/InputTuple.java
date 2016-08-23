package com.deleidos.framework.operators.common;

import java.util.List;

public class InputTuple {
	private List<String> header;
	private String data;

	public InputTuple() {
	}

	public InputTuple(List<String> header, String data) {
		this.header = header;
		this.data = data;
	}

	public void setHeader(List<String> header) {
		this.header = header;
	}

	public void setData(String data) {
		this.data = data;
	}

	public List<String> getHeader() {
		return this.header;
	}

	public String getData() {
		return this.data;
	}
}
