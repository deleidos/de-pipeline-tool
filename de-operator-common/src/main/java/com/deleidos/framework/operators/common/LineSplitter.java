package com.deleidos.framework.operators.common;

import java.io.InputStream;

public class LineSplitter {

	private InputTupleHandler handler;
	
	public LineSplitter(InputTupleHandler handler) {
		this.handler = handler;
	}
	
	public void split(InputStream is) {
		// do splitting magic on is
		// build a new InputTuple object
	}
	
	public abstract static class InputTupleHandler {
		abstract void handleInputTuple(Object tuple);
	}
}
