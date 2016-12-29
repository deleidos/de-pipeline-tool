package com.deleidos.framework.service.data;

import org.junit.Before;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Base unit test for data access functionality.
 * 
 * @author vernona
 */
public class DataTestBase {

	protected SystemDataManager manager;
	protected Gson gson = (new GsonBuilder()).setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();

	@Before
	public void setUpBase() throws Exception {
		manager = SystemDataManager.getInstance();
	}
}
