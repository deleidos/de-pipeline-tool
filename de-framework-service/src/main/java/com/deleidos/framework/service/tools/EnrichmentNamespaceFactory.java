package com.deleidos.framework.service.tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Manage enrichment namespaces.
 *  
 * @author vernona
 */
public class EnrichmentNamespaceFactory {
	// TODO Manage in a configuration file.
	private static final String faaNamespace = "faa_data";
	private static final String salesCustomerNamespace = "sales_customer ";
	private static final String salesStateAbbreviationNamespace = "state_abbreviation";

	/** The singleton instance. */
	private static final EnrichmentNamespaceFactory instance = new EnrichmentNamespaceFactory();

	/**
	 * Private constructor enforces the singleton pattern.
	 */
	private EnrichmentNamespaceFactory() {
	}

	/**
	 * Get the singleton instance.
	 * 
	 * @return
	 */
	public static EnrichmentNamespaceFactory getInstance() {
		return instance;
	}

	/**
	 * Get the list of currently available enrichment namespaces.
	 * 
	 * @return
	 */
	public List<String> getNamespaces() {
		ArrayList<String> namespaces = new ArrayList<String>();
		namespaces.add(faaNamespace);
		namespaces.add(salesCustomerNamespace);
		namespaces.add(salesStateAbbreviationNamespace);
		return namespaces;
	}
}
