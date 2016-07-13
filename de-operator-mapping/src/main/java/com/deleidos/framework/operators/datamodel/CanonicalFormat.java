package com.deleidos.framework.operators.datamodel;




import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.deleidos.framework.operators.datamodel.DataModelBasedNames;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * The Canonical Format is what is produced when a parser parses the data as well as what is stored.
 * It is a JSON string that contains the data parsed into fields(and possibly enriched with more data fields).
 * Canonical Format is comprised of 3 parts.  The first two parts are mandatory and the third is optional: 
 * 
 * 1. RTWS Header - Inserted by all parsers, this adds information that all records will have.
 * 2. Common Model Fields: These are the normalized fields shared by all inputs, i.e., part of a common data model.
 * 3. Extrinsic Fields: These are fields that are unique to each input in their raw form.
 * 
 * Example Canonical Format:
 * {
 *    // Header included for all RTWS data
 *    "rtwsHeader" : {
 *         "id"         : "<uuid>"
 *         "accessLabel": "<label>",
 *         "inputName"  : "<name that represents this data input>",
 *         "modelName"  : "<name of the data model the data is parsed into>",
 *     },
 * 
 *     // Fields that are part of the common data model
 *     "eventid"             : "string",
 *     "eventType"           : "string",
 *     "objectType"          : "string",
 *     "objectIdName"        : "string",
 *     "objectIdValue"       : "string",
 *     "NumContent"  : number,
 *     "Content"     :
 *         [ { "contentName"   : "string",
 *             "mimeType"      : "string",
 *             "contentThumb"  : "string",
 *             "isURI"         : "string",
 *             "contentValue"  : "string"
 * 	  		 }
 *         ],
 * 
 *     // Extrinsic Data fields specific to each input, each listed separately
 *     "extrinsic_TaxiRoute" : {
 * 			"Route_Label"           : "string",
 *     },
 * 
 *     "extrinsic_GeoTaggedRouteImagery" : {
 *  	   "ImageWidth"          : "string",
 *         "ImageHeight"         : "string",
 *         "Filename"            : "string"
 *     }
 * }
 * 
 */
public class CanonicalFormat {
	

	
	/**
	 * Supported data types of CanonicalFormat - these enumeral strings should always be in lowercase 
	 */
	public enum DataTypes { string, number, datestring, enrichment };
	
	private JsonObject json;

	/**
	 * Constructor that expects the Canonical Format to be in a file
	 * 
	 * @param file
	 * 			the file that contains the Canonical Format
	 * 
	 * @throws IOException
	 * @throws JSONException
	 */
	public CanonicalFormat(File file) throws IOException {		
		this(FileUtils.readFileToString(file));
	}
	
	/**
	 * Constructor that expects the Canonical Format to come from an InputStream
	 * 
	 * @param stream
	 * 			the stream that will send the Canonical Format
	 * 
	 * @throws IOException
	 * @throws JSONException
	 */	
	public CanonicalFormat(InputStream stream) throws IOException {
		this(IOUtils.toString(stream));
	}
	
	/**
	 * Constructor that expects the Canonical Format to come from a string
	 * 
	 * @param canonicalFormatJSONText
	 * 			the string that is the Canonical Format
	 * @throws JSONException
	 */
	public CanonicalFormat(String canonicalFormatJSONText){
		// Can throw JSONException
		//System.out.println("Dump canonical format text:");
		//System.out.println(canonicalFormatJSONText);
		//System.out.println("***************************");
		Gson gson = new Gson();
		JsonElement element = gson.fromJson(canonicalFormatJSONText, JsonElement.class);
		json = element.getAsJsonObject();
	}
	
	/**
	 * Recursively processes the json for validation.
	 * @param key current simple key name
	 * @param parentPath full JSON path to this object
	 * @param value value for the current object
	 * @param errorMessages array to collect error messages
	 */
	private void recursiveValidator(String key, String parentPath, Object value, ArrayList<String> errorMessages) {
		
		if (value instanceof JsonObject) {
			JsonObject jsonObject = (JsonObject)value;
			for (Iterator it = jsonObject.entrySet().iterator(); it.hasNext(); ) {
				String nextKey = (String)it.next();
				Object nextVal = jsonObject.get(nextKey);
			    String path = (parentPath.length() == 0) ? nextKey : parentPath + '.' + nextKey;
				recursiveValidator(nextKey, path, nextVal, errorMessages);
			}
		}
		else if (value instanceof JsonArray) {
			JsonArray jsonArray = (JsonArray)value;
			int index = 0;
			for (Object obj : jsonArray) {
				String path = parentPath + '[' + index + ']';
				recursiveValidator(index+"", path, obj, errorMessages);
				index++;
			}			
		}
		else if (value instanceof String) {
			
			String raw = (String)value;
			String typeString = null;
			String paramString = null;
			DataTypes dataType = null;
			
			try {
				// Parse type and optional parameter
				int index = Math.max(raw.indexOf(' '), raw.indexOf('\t'));
				if (index < 0) {
					typeString = raw;
				}
				else {
					typeString = raw.substring(0,index).trim();
					paramString = raw.substring(index).trim();
				}

				// Try to convert data type
				dataType = DataTypes.valueOf(typeString.toLowerCase());				
				if (dataType == DataTypes.datestring) {
					// Check date format string
					if (paramString == null) {
						errorMessages.add(parentPath + ": Date/time format string is missing");
					}
					else {
						SimpleDateFormat dateFormat = new SimpleDateFormat(paramString);											
					}
				}
			}
			catch (IllegalArgumentException e) {
				errorMessages.add(parentPath + ": invalid data type or parameter string" );
			}			
		}
		else {
			errorMessages.add(parentPath + ": must be a JSON string but its not" );
		}
	}

	/**
	 * Validates the canonical json data, checking for invalid data types and parameters. 
	 * @param errors empty arraylist that will contain any error messages
	 * @return true if no errors, false otherwise
	 */
	public boolean validate(ArrayList<String> errors) {
		
		recursiveValidator("", "", json, errors);
		
		return (errors.size() == 0);
	}

	/**
	 * Returns true if the value string represents a JSON number.
	 * @param value
	 * 			the string to check is a JSON number
	 * @return true, if the value string represents a JSON number
	 */
	public static boolean isNumberString(String value) {
		// return (value.matches("[\\-]{0,1}[0-9]*(\\.[0-9]*([eE]{1,1}[+-]{0,1}[0-9]*){0,1}){0,1}"));
		return (value.matches("^([\\-]{0,1}[0-9]*(\\.[0-9]*){0,1}([eE]{1}[+-]{0,1}[0-9]*){0,1})$"));
		
	}

	/**
	 * Returns true if the value string represents a date/time string that conforms to the format.
	 * @param format
	 * 			the format string. see SimpleDateFormat class
	 * @param value
	 * 			the string to check is a date/time of the given format
	 * @return true if the string conforms to the given format
	 */
	public static boolean isDateTimeString(String format, String value) {
		boolean result = false;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			if (dateFormat.parse(value) != null) {
				result = true;
			}
		}
		catch (IllegalArgumentException e) {
			// should not happen on validated canonical json
		}
		catch (ParseException e) {
			// do nothing
		}
		return result;
	}
	
	/**
	 * Returns the raw type string for the given JSON path.
	 * @param jsonPath simple dot notation with [] for array references
	 * @return the string value or null if the path cannot be resolved to a string value
	 */
	public String getRawValue(String jsonPath) {
		String result = null;
		
		// Remove indexing sequences
		jsonPath = jsonPath.replaceAll("\\[[0-9*]*\\]","");
		
		// Split on the dot
		String names[] = jsonPath.split("\\.");
		
		// Go through each name, stepping into objects and arrays as needed
		Object curObj = json;
		Boolean inArray = false;
		for (String name : names) {
			if (!(curObj instanceof JsonObject)) {
				// Detects if a non-array, non-object reference was specified
				// but there are still more elements in the path
				curObj = null;
				break;
			}			
			Object obj = ((JsonObject)curObj).get(name);
			if (obj == null) {
				// Likely invalid name
				curObj = null;
				inArray = false;
				break;
			}
			else if (obj instanceof JsonArray) {
				// Only the first element of the array should exist
				curObj = ((JsonArray)obj).get(0);
				inArray = true;
			}
			else {
				curObj = obj;
				inArray = false;
			}
		}
		
		if (curObj != null) {
			
			if (curObj.toString().equals("\"string\"")) {
				
				result = curObj.toString();
			}
			else if (inArray) {
				// For arrget the path will be an array
				// So in this case just return string
				result = "string";
			}
		}
		
		return result.replace("\"", "");
	}
	
	/**
	 * Returns the data type for the given json path, or null if it cannot be computed.
	 * @param jsonPath
	 * 			the json path to get the data type for
	 * @return the data type
	 */
	public DataTypes getDataType(String jsonPath) {
		
		DataTypes result = null;
		String raw = getRawValue(jsonPath);
		if (raw != null) {
			int index = Math.max(raw.indexOf(' '), raw.indexOf('\t'));
			index = (index < 0) ? raw.length() : index;
			raw = raw.substring(0,index).trim();
			try {
				
				result = DataTypes.valueOf(raw.toLowerCase());				
			}
			catch (IllegalArgumentException e) {
				result = null;
			}
		}
		
		return result;
	}

	/**
	 * Returns the parameter string for the given json path, or null if it cannot be computed.
	 * @param jsonPath
	 * 			the json path to get the parameter for
	 * @return the parameter string
	 */
	public String getParameterString(String jsonPath) {
		String result = null;
		String raw = getRawValue(jsonPath);
		if (raw != null) {
			int index = Math.max(raw.indexOf(' '), raw.indexOf('\t'));
			if (index > 0) {
				result = raw.substring(index).trim();
			}
		}
		return result;
	}
	
	/**
	 * Returns the list json path for all available primitive fields.
	 * @return a list of json paths for all available primitive fields 
	 */
	public Collection<String> getFields() {
		LinkedList<String> fields = new LinkedList<String>();
		computeFields(json, "", fields);
		return fields;
	}
	
	private void computeFields(JsonObject obj, String path, Collection<String> fields) {
		path = (path.isEmpty()) ? path : path + ".";
		for (Object field : obj.entrySet()) {
			Object value = obj.get(field.toString());
			if (value instanceof String) {
				fields.add(path + field);
			} else if (value instanceof JsonObject) {
				computeFields((JsonObject)value, path + field, fields);
			} else if (value instanceof JsonArray) {
				value = ((JsonArray)value).get(0);
				if(value instanceof JsonObject) {
					computeFields((JsonObject)value, path + field + "[*]", fields);
				}
			}
		}
	}
	
	/**
	 * Extracts the model name and version from the given record's header.
	 * 
	 * @param record
	 * 			the JsonObject to get the model name and version from
	 * 
	 * @return the model name and version of the given record
	 */
	public static DataModelBasedNames getModel(JsonObject record) {
		JsonObject header = (JsonObject) record.get("standardHeader");
		if(header != null) {
			String name = header.get("modelName").getAsString();
			String version = header.get("modelVersion").getAsString();
			if(name != null && version != null) {
				return new DataModelBasedNames(name, version);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Extracts the simple model name from the given record's header.
	 * 
	 * @param record
	 * 			the JsonObject to get the simple model name from
	 * 
	 * @return the simple model name of the given record
	 */
	public static String getSimpleModelName(JsonObject record) {
		DataModelBasedNames model = getModel(record);
		return (model == null) ? null : model.getModelName();
	}
	
	/**
	 * Extracts the full model name from the given record's header.
	 * 
	 * @param record
	 * 			the JsonObject to get the full model name from
	 * 
	 * @return the full model name of the given record
	 */
	public static String getFullModelName(JsonObject record) {
		DataModelBasedNames model = getModel(record);
		return (model == null) ? null : model.getFullModelName();
	}
	
	/**
	 * Extracts the input source name from the given record's header.
	 * 
	 * @param record
	 * 			the JsonObject to get the input source name from
	 * 
	 * @return the input source name of the given record
	 */
	public static String getInputName(JsonObject record) {
		if(record.has("standardHeader")) {
			return ((JsonObject)record.get("standardHeader")).get("inputName").getAsString();
		} else {
			return null;
		}
	}

	/**
	 * Gets the json.
	 *
	 * @return the json
	 */
	public JsonObject getJson() {		
		return json;
	}
	
}
