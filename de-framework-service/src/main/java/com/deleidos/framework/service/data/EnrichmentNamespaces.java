package com.deleidos.framework.service.data;

import java.util.List;

/**
 * Wrapper object for namespace string list.
 * 
 * @author vernona
 */
public class EnrichmentNamespaces {

	private List<String> namespaces;

	public EnrichmentNamespaces() {
	}

	public EnrichmentNamespaces(List<String> namespaces) {
		this.namespaces = namespaces;
	}

	public List<String> getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(List<String> namespaces) {
		this.namespaces = namespaces;
	}
}
