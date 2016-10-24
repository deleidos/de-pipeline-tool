package com.deleidos.framework.operators.xml.parser;

import java.util.Iterator;

public class ByteArrayIterator implements Iterator<Integer> {
	
	private byte[] a;
	private int i = 0;
	
	public ByteArrayIterator(byte[] arr) {
		this.a = arr;
	}

	@Override
	public boolean hasNext() {
		return i < a.length;
	}

	@Override
	public Integer next() {
		return (int) a[i++];
	}

}
