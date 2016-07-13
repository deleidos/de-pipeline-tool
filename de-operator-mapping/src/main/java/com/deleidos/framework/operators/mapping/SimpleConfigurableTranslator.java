package com.deleidos.framework.operators.mapping;

import java.util.Map;


/**
 * Simple version of the configurable translator that provides no custom field translation and
 * no access label extraction (access label must be defined elsewhere).
 */
public class SimpleConfigurableTranslator extends AbstractMapping {
	
	public SimpleConfigurableTranslator() {
		super();
	}
	
	public String getAccessLabel( Map<String, String>recordMap ) {
		// Null means that the label will be provided either from the stream properties or in the
		// configuration parameters of the parser - otherwise the label will be equal to
		// StandardHeader.NO_ACCESS_LABEL
		return null;
	}

	public String customFieldTranslator(String outputFieldPath, String outputFieldKey, Map<String, String> recordMap) {
		// Null means here that there should not be any custom field translation needed
		return null;
	}
}
