package com.deleidos.framework.operators.datamodel;

public class ValidationException extends Exception {

	/** Serial Version UID. */
	private static final long serialVersionUID = 1576784449627392981L;
	
	/** 
	 * Constructor. 
	 * @param message Message to be returned
	 */
	public ValidationException(String message) {
		super(message);
	}
	
	/** 
	 * Constructor. 
	 * @param message Message to be returned
	 * @param cause Exception object
	 */
	public ValidationException(String message, Exception cause) {
		super(message, cause);
	}
}
