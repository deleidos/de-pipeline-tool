package com.deleidos.framework.operators.common;

import java.util.List;

public class FileInputTuple {
	private List<String> header;
	private byte[] data;

	public FileInputTuple() {
	}

	public FileInputTuple(List<String> header, byte[] data) {
		this.header = header;
		this.data = data;
	}

	public void setHeader(List<String> header) {
		this.header = header;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public List<String> getHeader() {
		return this.header;
	}

	public byte[] getData() {
		return this.data;
	}
}
