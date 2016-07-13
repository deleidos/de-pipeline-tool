package com.deleidos.applicationcreator;

import java.util.HashMap;

public class ClassMappings {
	
	HashMap<String,String> mappings = new HashMap<String,String>();
	
	public ClassMappings(){
		mappings.put("com.deleidos.framework.operators.csv.parser.CsvParserOperator", "de-operator-csv-parser-0.0.1-SNAPSHOT.jar");
		mappings.put("com.deleidos.framework.operators.elasticsearch.ElasticSearchOutputJsonOperator", "de-operator-elasticsearch-0.0.1-SNAPSHOT.jar");
		mappings.put("com.deleidos.framework.operators.json.parser.JsonParserOperator","de-operator-json-parser-0.0.1-SNAPSHOT.jar");
		mappings.put("com.deleidos.framework.operators.mapping.JSONMappingOperator", "de-operator-mapping-0.0.1-SNAPSHOT.jar");
		mappings.put("com.deleidos.framework.operators.mongodb.MongoDbOutputOperator", "de-operator-mongodb-0.0.1-SNAPSHOT.jar");
		mappings.put("com.deleidos.framework.operators.s3.S3InputOperator", "de-operator-s3-0.0.1-SNAPSHOT.jar");
		mappings.put("com.deleidos.framework.operators.dimensional_enrichment.RedisDimensionalEnrichmentOperator", "de-operator-dimensional-enrichment-0.0.1-SNAPSHOT.jar");
	}
	
	public String getMappedVal(String input){
		return mappings.get(input);
	}

}
