package com.deleidos.framework.operators.mapping;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.deleidos.framework.operators.datamodel.BugCheckException;
import com.deleidos.framework.operators.datamodel.CanonicalFormat;
import com.deleidos.framework.operators.datamodel.CanonicalFormat.DataTypes;
import com.deleidos.framework.operators.datamodel.DataModelBasedNames;
import com.deleidos.framework.operators.datamodel.DataModelZipFile;
import com.deleidos.framework.operators.datamodel.Initializable;
import com.deleidos.framework.operators.datamodel.InitializationException;
import com.deleidos.framework.operators.datamodel.StandardHeader;
import com.deleidos.framework.operators.datamodel.ValidationException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import net.sf.json.JSONSerializer;
import net.sf.json.JSONException;
/**
 * Accepts a JSON configuration file, called a translation directive file, that
 * specifies how parsed input fields are mapped from an input set of fields to
 * the canonical format.
 */
public abstract class AbstractMappingDMA implements Initializable {

	private static final Logger log = Logger.getLogger(AbstractMappingDMA.class);
	private static final String headerSeperator = "\\s*\\+\\s*";

	private JsonObject translationDirectiveJson;
	//private CanonicalFormat canonicalFormat;
	private FieldHandlingErrorPolicy fieldHandlingErrorPolicy;
	private DataModelBasedNames dataModelNaming;
	// private AbstractFileSetResource dataModelResource;

	private JSONObject json;

	private String modelName;
	private String inputFormatName;
	private String modelVersionString;
	private StandardHeader header;
	private List<String> expectedHeaders;
	private List<String> missingHeaders;

	private Matcher nestedArrayMatcher;
	private String nestedArrayRegex = ".*\\[.*\\[";
	private JsonParser parse = new JsonParser();


	/**
	 * Defines policy for handling errors when processing data fields
	 */
	public enum FieldHandlingErrorPolicy {
		DISCARD_AND_LOG, // Discard the field and log the error (default)
		DISCARD_AND_IGNORE // Discard the field silently
	};

	/**
	 * Defines the translation operations that are performed to create a JSON
	 * object.
	 */
	private enum Operations {
		Error, NoOperation, NestObject, NestArray, Unnest, CopyLiteral, GetField, GetArray, Convert, Custom, Script
	};

	/**
	 * Class defines a translation action, which when processed in sequence
	 * creates a translated JSON output object. Built from the translation
	 * directives.
	 */
	class TranslationAction {

		static final String LITERAL_INDICATOR = "=";
		static final String GET_FIELD_KEYWORD = "get";
		static final String CONVERT_FIELD_KEYWORD = "convert";
		static final String CUSTOM_KEYWORD = "custom";
		static final String SCRIPT_FIELD_KEYWORD = "script";
		static final String NO_OPERATION = "none";
		static final String GET_ARRAY_KEYWORD = "arrget";

		String key;
		String path;
		Operations operation;
		Object parameters[];
		DataTypes dataType;
		String format;
		String errorMsg;
		String scriptOperation;

		/**
		 * Returns the argument part of a directive string
		 * 
		 * @param directive
		 *            a string of the form <keyword>(<argument>)
		 * @param keyword
		 *            string that starts the directive
		 * @return the argument string between the parens - white spaces before
		 *         and after are removed
		 */
		String getArgumentString(String directive, String keyword) {
			return directive.replaceAll(keyword + "\\s*\\(\\s*", "").replaceAll("\\s*\\)\\s*$", "");
		}

		String[] getConvertArgumentString(String directive, String keyword) {
			String argumentString = getArgumentString(directive, keyword);
			return argumentString.split("\\s*(?<!\\\\),\\s*");
			// negative lookbehind - does not match "\,", does not capture
			// preceeding characters in split.
		}

		/**
		 * Check if the given string is a valid JSON number.
		 */
		void checkNumber(String value) {
			if (!CanonicalFormat.isNumberString(value)) {
				this.operation = Operations.Error;
				this.errorMsg = "Not a valid number: " + value;
			} else {
				this.operation = Operations.CopyLiteral;
			}
		}

		/**
		 * Check if the given string is a valid date format string.
		 */
		void checkDateTime(String path, String format, String value) {
			if (format == null) {
				this.operation = Operations.Error;
				this.errorMsg = "Cannot find datetime format string for path:" + path;
			} else if (!CanonicalFormat.isDateTimeString(format, value)) {
				this.operation = Operations.Error;
				this.errorMsg = "Date/time string '" + value + "' does not conform to format '" + format + "'";
			} else {
				this.operation = Operations.CopyLiteral;
			}
		}

		/**
		 * Create a translation action for a key using a directive and data type
		 * 
		 * @param key
		 *            the simple unqualified name of the field
		 * @param path
		 *            the full path of the field
		 * @param directive
		 *            specifier in the translator directive file
		 */
		TranslationAction(String key, String path, String directive) {

			this.key = key;
			this.path = path;
			this.operation = null;
			this.parameters = null;
			this.errorMsg = null;
			this.format = null;
			this.scriptOperation = null;

			directive = directive.trim();
			directive = directive.replace("\"", "");
			/*this.dataType = getDataType(path);
			log.info(path);
			log.info(dataType.toString());
			if (this.dataType == DataTypes.datestring) {
				this.format = getParameterString(path);
			}

			if (this.dataType == null) {
				
				this.operation = Operations.Error;
				this.errorMsg = "Lookup of json path failed: " + path;
			} else */if (directive.startsWith(LITERAL_INDICATOR)) {

				String value = directive.substring(LITERAL_INDICATOR.length());

	/*			switch (dataType) {
				case number:
					checkNumber(value);
					break;
				case datestring:
					checkDateTime(path, format, value);
					break;
				default:*/
					this.operation = Operations.CopyLiteral;
	//				break;
	//			}

				this.parameters = new Object[1];
				this.parameters[0] = value; //AbstractMapping.convertString(dataType, format, value);
			} else if (directive.startsWith(GET_FIELD_KEYWORD)) {
				// Extract comma separated list of parameters
				parameters = new String[2];
				String parsed[] = getConvertArgumentString(directive, GET_FIELD_KEYWORD);
				if (parsed.length > 2) {
					this.operation = Operations.Error;
					errorMsg = "No more than two parameters supported with 'get' directive";
				} else {
					for (int i = 0; i < parsed.length; i++) {
						parameters[i] = parsed[i];
					}
					this.operation = Operations.GetField;
				}
			} else if (directive.startsWith(GET_ARRAY_KEYWORD)) {
				//
				parameters = new String[2];
				String parsed[] = getConvertArgumentString(directive, GET_ARRAY_KEYWORD);
				if (parsed.length > 1) {
					this.operation = Operations.Error;
					errorMsg = "No more than one parameter supported with '" + GET_ARRAY_KEYWORD + "' directive";
				} else {
					parameters[0] = parsed[0];
					if (!parsed[0].endsWith("[*]")) {
						this.operation = Operations.Error;
						errorMsg = "Directive does not contain valid array indexing expression.  Should end with [*]";
					} else {
						this.operation = Operations.GetArray;
					}
				}
			} else if (directive.startsWith(CONVERT_FIELD_KEYWORD)) {
				parameters = getConvertArgumentString(directive, CONVERT_FIELD_KEYWORD);
				this.operation = Operations.Convert;
			} else if (directive.equals("custom")) {
				this.operation = Operations.Custom;
			} else if (directive.startsWith("script")) {
				String[] scriptCall = getConvertArgumentString(directive, SCRIPT_FIELD_KEYWORD);
				int scriptCallLength = scriptCall.length;

				if (scriptCallLength > 0) {
					this.scriptOperation = scriptCall[0];
				}

				parameters = Arrays.copyOfRange(scriptCall, 1, scriptCallLength);
				this.operation = Operations.Script;
			} else if (directive.equals(NO_OPERATION)) {
				this.operation = Operations.NoOperation;
			} else {
				this.operation = Operations.Error;
				errorMsg = "Invalid directive: " + directive;
			}
	
		}

		TranslationAction(String key, String path, Operations operation) {
			this.key = key;
			this.path = path;
			this.operation = operation;
			this.parameters = null;
			this.dataType = null;
			this.errorMsg = null;
			this.format = null;
		}
	}

	/**
	 * List of translation actions created from the translation directives
	 */
	private ArrayList<TranslationAction> translationActionList;

	/**
	 * Recursively process a json object to add translation actions to the
	 * translationActionList.
	 * 
	 * @param key
	 *            the simple unqualified name of the current field
	 * @param path
	 *            string path syntax for the json value/object/array
	 * @param value
	 *            the value/object/array to examine
	 * @param errorCount
	 *            current error count
	 * @return current error count
	 */
	private int recursiveAddTranslationActions(String key, String path, Object value, int errorCount) {
		
		if (value instanceof JsonArray) {
			// Process an array by iterating through all declared values
			translationActionList.add(new TranslationAction(key, path, Operations.NestArray));
			JsonArray jsonArray = (JsonArray) value;
			int index = 0;
			for (Object obj : jsonArray) {
				String childPath = path + '[' + index + ']';
				errorCount = recursiveAddTranslationActions(String.valueOf(index), childPath, obj, errorCount);
				index++;
			}
			translationActionList.add(new TranslationAction(key, path, Operations.Unnest));
		} else if (value instanceof JsonObject) {

			// Process an object by iterating through each key in the object
			translationActionList.add(new TranslationAction(key, path, Operations.NestObject));
			JsonObject jsonObject = (JsonObject) value;
			for (Entry<String,JsonElement> e : jsonObject.entrySet()) {
				
				String childKey = e.getKey();
				String childPath = (path.length() == 0) ? childKey : path + '.' + childKey;
				
				errorCount = recursiveAddTranslationActions(childKey, childPath, jsonObject.get(childKey), errorCount);
			}
			translationActionList.add(new TranslationAction(key, path, Operations.Unnest));
		} else {
			// All other value types - get the target data type and add the
			// appropriate action
			TranslationAction action = new TranslationAction(key, path, value.toString());
			translationActionList.add(action);
			if (action.operation == Operations.Error) {
				errorCount++;
			}
		}

		return errorCount;
	}

	public void setFieldHandlingErrorPolicy(FieldHandlingErrorPolicy policyCode) {
		this.fieldHandlingErrorPolicy = policyCode;
	}

	public void setFieldHandlingErrorPolicy(String policy) {
		try {
			this.fieldHandlingErrorPolicy = FieldHandlingErrorPolicy.valueOf(policy);
		} catch (IllegalArgumentException e) {
			this.fieldHandlingErrorPolicy = FieldHandlingErrorPolicy.DISCARD_AND_LOG;
			log.error("Invalid type conversion policy: " + policy + ", using default of "
					+ this.fieldHandlingErrorPolicy.toString());
		}
	}



	/**
	 * Set and get the data model name.
	 */
	public void setModelName(String value) {
		this.modelName = value;
	}

	public String getModelName() {
		return this.modelName;
	}

	/**
	 * Set and get a string representation of the data model version. Format is
	 * <major>.<minor>
	 */
	public void setModelVersion(String value) {
		this.modelVersionString = value;
	}

	public String getModelVersion() {
		return this.modelVersionString;
	}

	/**
	 * Set and get the input format name.
	 */
	public void setInputFormatName(String value) {
		this.inputFormatName = value;
	}

	public String getInputFormat() {
		return this.inputFormatName;
	}
	public static String inputStream2String(final InputStream in) throws IOException {
		final int bufferSize = 4096;
	    StringBuffer out = new StringBuffer();
	    byte[] b = new byte[bufferSize];
	    for (int n; (n = in.read(b)) != -1; ) {
	        out.append(new String(b, 0, n));
	    }
	    in.close();
	    return out.toString();
	}
	/**
	 * Set and get the data model resource where data model configuration can be
	 * obtained. This is primarily intended to be used for testing, under normal
	 * system operation the data model is pulled from the repository.
	 * 
	 * @param modelPath
	 *            source folder where the data model should reside
	 */
	public void loadDataModel(String modelPath) throws IOException {
		dataModelNaming = new DataModelBasedNames(modelName, modelVersionString);
		//File modelFile = new File(modelPath, dataModelNaming.getFullModeZipFilename());
		//DataModelZipFile zipFile = new DataModelZipFile(new FileInputStream(modelFile));
		
		//canonicalFormat = new CanonicalFormat(zipFile.getCanonicalModel());
		//InputStream is = this.getClass().getResourceAsStream(modelPath);
		 
		


		InputStream inputStream = this.getClass().getResourceAsStream(modelPath);
		 StringWriter writer = new StringWriter();
		 IOUtils.copy(inputStream, writer, "UTF-8");
		 String theString = writer.toString();
		
		//InputStream inputStream = AbstractMapping.class.getClassLoader().getResourceAsStream(modelPath);
		//File initialFile = new File(modelPath);
		//InputStream targetStream = new FileInputStream(initialFile);
		String translationDirectives = theString;
		//System.out.println("wheras: " +inputStream2String(inputStream));//targetStream);//zipFile.getTranslationMapping(inputFormatName));
		Gson gson = new Gson();
		
		JsonElement element = gson.fromJson(translationDirectives, JsonElement.class);
		translationDirectiveJson = element.getAsJsonObject();


	}

	public List<String> getExpectedHeaderNames() {
		return expectedHeaders;
	}

	public List<String> getMissingHeaderNames() {
		return missingHeaders;
	}

	/**
	 * Constructor - use this class by creating an instance of it, calling the
	 * set methods to set the translation directive file and canonical format
	 * file (at a minimum), and then call initialize().
	 */
	public AbstractMappingDMA() {
		fieldHandlingErrorPolicy = FieldHandlingErrorPolicy.DISCARD_AND_LOG;
	}

	private void checkRequiredProperty(String value, String name) {
		if (value == null) {
			throw new InitializationException("Missing required property:" + name);
		}
	}

	public static DataModelZipFile getDataModel(String dataModelName) throws IOException{
		InputStream is = new FileInputStream(dataModelName);
		return new DataModelZipFile(is);
	}
	
	
	/**
	 * Initialize the object. Must be called before the object can be used.
	 */
	public void initialize() throws InitializationException {

		int errorCount = 0;

		checkRequiredProperty(modelName, "modelName");
		checkRequiredProperty(modelVersionString, "modelVersionString");
		checkRequiredProperty(inputFormatName, "inputName");

		Pattern nestedArrayPattern = Pattern.compile(nestedArrayRegex);
		nestedArrayMatcher = nestedArrayPattern.matcher("");

		try {
			/* Create the script engine if one is configured. */

			dataModelNaming = new DataModelBasedNames(modelName, modelVersionString);

			// Get the canonical and translation files from the configured data
			// model resource
			/*
			 * dataModelResource.initialize(); FileSetId fileSetId = new
			 * FileSetId(modelName, modelVersionString); canonicalFormat = new
			 * CanonicalFormat(dataModelResource.retrieve(fileSetId,
			 * DataModelBasedNames.CANONICAL )); String translationDirectives =
			 * dataModelResource.inputStream2String(
			 * dataModelResource.retrieve(fileSetId,
			 * DataModelBasedNames.TRANSLATE, inputFormatName));
			 * translationDirectiveJson = (JsonObject) JSONSerializer.toJSON(
			 * translationDirectives );
			 */
			// Load the canonical format and translation directives if it hasn't
			// already been done
		
			if (/*(canonicalFormat == null) &&*/ (translationDirectiveJson == null)) {
				DataModelZipFile zipFile = getDataModel(dataModelNaming.getFullModeZipFilename());
				//canonicalFormat = new CanonicalFormat(zipFile.getCanonicalModel());
				String translationDirectives = inputStream2String(zipFile.getTranslationMapping(inputFormatName));
				translationDirectives = translationDirectives.replace("\"", "");
		
				Gson gson = new Gson();
				JsonElement element = gson.fromJson(translationDirectives, JsonElement.class);
				translationDirectiveJson = element.getAsJsonObject();
				
			}

			// Create translation list and process translation directive JSON
			// object
			// to populate directives
			translationActionList = new ArrayList<TranslationAction>();
			errorCount = recursiveAddTranslationActions("", "", translationDirectiveJson, 0);

			// Verify first action is to nest an object
			TranslationAction firstAction = translationActionList.get(0);
			if (firstAction.operation != Operations.NestObject) {
				log.error("Root object of parser directive JSON is not a JSON object");
				errorCount++;
			}

			header = new StandardHeader();
			header.setModelName(dataModelNaming.getModelName());
			header.setModelVersion(dataModelNaming.getMajorVersion(), dataModelNaming.getMinorVersion());

			expectedHeaders = new ArrayList<String>();
			// Iterate and log all errors and add the expected fields
			for (TranslationAction action : translationActionList) {
				if (action.operation == Operations.Error) {
					log.error(action.errorMsg);
				} else if (action.operation == Operations.GetField) {
					addExpectedGetField((String) action.parameters[0]);
				} else if (action.operation == Operations.Convert) {
					addExpectedConvertFields((String) action.parameters[0]);
				}
			}
		} catch (Exception e) {
			throw new InitializationException("Initialization failed due to exception: " + e.getMessage(), e);
		}

		if (errorCount > 0) {
			throw new InitializationException(errorCount + " errors detected during translaction directive processing");
		}
	}

	public void dispose() {
		translationDirectiveJson = null;
		//canonicalFormat = null;
		translationActionList = null;
	}

	/**
	 * Verify a string value conforms to the given target type, and return a
	 * value object appropriate for the string value, performing conversions if
	 * necessary.
	 * 
	 * @param dataType
	 *            base data type
	 * @param format
	 *            format specifier (only needed for certain types)
	 * @param value
	 *            value string
	 * @return an object appropriate for the string value, or null if the value
	 *         does not conform
	 */
	private static Object convertString(DataTypes dataType, String format, String value) {

		Object result = null;

		try {
			switch (dataType) {
			case string:
				result = value;
				break;
			case number:
				value = value.replaceAll(",", "");
				char chars[] = value.toCharArray();
				boolean onlyDigits = true;
				for (Character c : chars) {
					if (!Character.isDigit(c)) {
						onlyDigits = false;
						break;
					}
				}
				if (onlyDigits) {
					Long longObj = new Long(value);
					result = longObj;
				} else {
					Double doubleObj = new Double(value);
					result = doubleObj;
				}
				break;
			case datestring:
				if ((format != null) && (value != null)) {
					SimpleDateFormat dateFormat = new SimpleDateFormat(format);
					if (dateFormat.parse(value) != null) {
						result = value;
					}
				}
				break;
			default:
				throw new BugCheckException("dataType is invalid");
			}
		} catch (IllegalArgumentException e) {
			result = null;
		} catch (ParseException e) {
			result = null;
		}

		return result;
	}

	/**
	 * Adds a value object to a JsonObject or JsonArray.
	 * 
	 * @param json
	 *            JsonObject or JsonArray
	 * @param action
	 *            translation action
	 * @param value
	 *            value object
	 */
	private void addValue(Object json, TranslationAction action, Object value) {

		if (json instanceof JsonObject) {
			JsonObject jsonObj = (JsonObject) json;
			jsonObj.addProperty(action.key, value.toString());
		} else if (json instanceof JsonArray) {
			JsonArray jsonArray = (JsonArray) json;
			JsonElement elem = parse.parse(value.toString());
			jsonArray.add(elem);
		} else {
			throw new BugCheckException("json parameter is not a JsonObject or JsonArray");
		}
	}

	private void addArrayValue(Object json, TranslationAction action, List<Object> values) {
		JsonArray outArr = new JsonArray();
		JsonElement elem = null;
		for (Object o : values)
			elem = parse.parse(o.toString());
			outArr.add(elem);
		if (json instanceof JsonObject) {
			JsonObject jsonObj = (JsonObject) json;
			jsonObj.add(action.key, outArr);
		} else if (json instanceof JsonArray) {
			JsonArray jsonArray = (JsonArray) json;
			jsonArray.add(outArr);
		} else {
			throw new BugCheckException("json parameter is not a JsonObject or JsonArray");
		}
	}

	/**
	 * Log an error based on the field error handling policy.
	 */
	private void fieldError(String message, Throwable e) {
		if (fieldHandlingErrorPolicy == FieldHandlingErrorPolicy.DISCARD_AND_LOG) {
			if (e == null)
				log.error(message);
			else
				log.error(message, e);
		}
	}

	/**
	 * Add a field to the json object, converting it from a string if needed.
	 */
	private void addField(Object json, TranslationAction action, Object value) {

		Object valueObj = value;//convertString(DataTypes.string, action.format, value);
		if (valueObj == null) {
			// Cannot convert value to the target type
			fieldError("Value '" + value + "' for canonical field " + action.path + " does not conform to the type "
					+ action.dataType.toString(), null);
		} else {
			addValue(json, action, valueObj);
		}
	}

	private void addArrayField(Object json, TranslationAction action, List<Object> values) {
		List<Object> valueObjs = new ArrayList<Object>();
		for (Object s : values) {
			valueObjs.add(s);//convertString(action.dataType, action.format, s));
		}
		addArrayValue(json, action, valueObjs);
	}

	/**
	 * Perform a translation of the name/value pairs in the provided recordMap
	 * to the JSON output format specified in the translationDirectiveJson
	 * object.
	 * 
	 * The script command uses the following format in the translation JSOM
	 * file: script(<scriptName>, <scriptParameter>, ...). The script name is
	 * the name of a function in the script file that has been loaded. The
	 * script parameters are generally values which are stored in the recordMap.
	 * They can be accessed by using the key as a script parameter. Script
	 * parameters can also be a literal if it is prepended by an "="
	 * character.(e.g. =true). It is also possible to pass a null as a script
	 * parameter.
	 * 
	 * 
	 * @param recordMap
	 *            input fields mapped as string key/value pairs
	 * @param source
	 *            string describing the source of the record
	 * @param defaultAccessLabel
	 *            default string describing the access label for the record
	 * @return A JSON object ready for further processing
	 */
	public JsonObject recordTranslation(Map<String, Object> recordMap, String source, String defaultAccessLabel)
			throws ParseException {
		return recordTranslation(recordMap, source, defaultAccessLabel, null);
	}

	/**
	 * Overloaded method to include UUID
	 */
	public JsonObject recordTranslation(Map<String, Object> recordMap, String source, String defaultAccessLabel, String uuid)
			throws ParseException {

		Stack<Object> objectStack = new Stack<Object>();
		JsonObject rootObj = null;
		Object curObj = null;
		Object value;
		String inputFormat;
		String timeZone;

		if (missingHeaders == null) { // Missing Headers is not being used,
										// replace with an empty list.
			missingHeaders = new ArrayList<String>();
		}

		for (String field : recordMap.keySet()) {
			nestedArrayMatcher.reset(field);
			if (nestedArrayMatcher.find()) {
				throw new ParseException("Arrays are not allowed to be nested.", 0);
			}

		}

		for (TranslationAction action : translationActionList) {

			// if (curObj != null) {
			// System.out.println(action.operation.toString() + "(" +
			// action.path + ")" + " : " + curObj.toString());
			// }

			switch (action.operation) {
			case Error:
				throw new BugCheckException("Translation list contains an error - translation is invalid");

			case NoOperation:
				break;

			case NestObject:
				objectStack.push(curObj);
				JsonObject nextObj = new JsonObject();
				if (curObj == null) {
					rootObj = (JsonObject) nextObj;
					String accessLabel = this.getAccessLabel(recordMap);
					header.setAccessLabel((accessLabel == null) ? defaultAccessLabel : accessLabel);
					header.setSource(source);
					// changed line
					if (uuid != null) {
						header.setUUID(uuid);
					} else {
						header.updateUUID();
					}
					rootObj.add(StandardHeader.HEADER_KEY, header.getJson());
				}
				curObj = nextObj;
				break;

			case NestArray:
				objectStack.push(curObj);
				JsonArray nextArray = new JsonArray();
				curObj = nextArray;
				break;

			case Unnest:
				Object prevObj = objectStack.pop();
				if (prevObj != null) {
					/*
					 * TJC - Moved the below to the NestObject operation so an
					 * record that has no subobjects works.
					 */
					/*
					 * if ((rootObj == null) && (prevObj.getClass() ==
					 * JsonObject.class) && (objectStack.size() == 1)) { rootObj
					 * = (JsonObject)prevObj; header.updateUUID(); String
					 * accessLabel = this.getAccessLabel(recordMap);
					 * header.setAccessLabel( (accessLabel == null) ?
					 * defaultAccessLabel : accessLabel );
					 * header.setSource(source);
					 * rootObj.put(StandardHeader.HEADER_KEY, header.getJson());
					 * }
					 */
					addValue(prevObj, action, curObj);
				}
				curObj = prevObj;
				break;

			case CopyLiteral:
				addValue(curObj, action, action.parameters[0]);
				break;

			case GetField:
				value = recordMap.get(action.parameters[0]);
				if (value == null) {
					value = (String) action.parameters[1]; // Can't find the
															// field - try using
															// default
					if ((value == null) && (!(missingHeaders.contains(action.parameters[0])))) {
						// Value could not be found and there is no default and
						// it is not part of the missing fields.
						//fieldError("Input data did not contain the input field " + (String) action.parameters[0], null);
					}
				} /*else if (value.length() == 0) {
					value = (String) action.parameters[1]; // Null string - try
															// default
				}*/

				if (value != null) {
					addField(curObj, action, value);
				}
				break;
			case GetArray:
				String arrVal = (String) action.parameters[0];
				List<Object> values = new ArrayList<Object>();
				int index = 0;
				boolean foundVal = true;
				while (foundVal) {
					String thisIndex = arrVal.replace("[*]", "[" + index + "]");
					if (recordMap.containsKey(thisIndex)) {
						values.add(recordMap.get(thisIndex));
					} else {
						foundVal = false;
					}
					index++;
				}
				// If there is no array in the recordMap, then look for a single
				// value with a non-array key
				if (index == 1) {
					String thisIndex = arrVal.replace("[*]", "");
					if (recordMap.containsKey(thisIndex)) {
						values.add(recordMap.get(thisIndex));
					}
				}
				addArrayField(curObj, action, values);

				break;
			case Convert:

				String fields = (String) action.parameters[0];
				value = combineFields(recordMap, fields);

				if (action.parameters.length > 1) {
					inputFormat = (String) action.parameters[1];
				} else {
					inputFormat = null;
				}

				if (action.parameters.length > 2) {
					timeZone = (String) action.parameters[2];
				} else {
					timeZone = null;
				}
				String val = value.toString();
				if (val.isEmpty()) {
					// Cannot find the field in the input
					fieldError(
							"Input Date String'" + value + "' for canonical field " + action.path + " is not specified",
							null);
				} else if (inputFormat != null) {
					if (action.format == null) {
						fieldError("Canonical Date format for canonical field " + action.path + " is not specified",
								null);
					} else {
						
						value = convertDate(action, val, inputFormat, timeZone);
					}
					addField(curObj, action, value);
				}
				break;

			case Custom:
				try {
					value = customFieldTranslator(action.path, action.key, recordMap);

					if (value == null) {
						// Cannot find the field in the input
						fieldError(
								"Null string returned from custom field translator for canonical field " + action.path,
								null);
					} else {
						addField(curObj, action, value);
					}
				} catch (Exception e) {
					value = null;
					fieldError("Exception raised during custom field translator for canonical field " + action.path, e);
				}

				break;



			}
		}

		return rootObj;
	}


	private void addExpectedGetField(String headerName) {
		expectedHeaders.add(headerName);
	}

	private void addExpectedConvertFields(String headerNamesString) {
		String[] headerNames = headerNamesString.split(headerSeperator);
		for (String headerName : headerNames) {
			expectedHeaders.add(headerName);
		}
	}

	public void validateHeaders(String[] headerKeys, boolean stopOnMissingField) throws ValidationException {
		missingHeaders = new ArrayList<String>();
		List<String> actualHeaders = Arrays.asList(headerKeys);

		for (String expectedHeader : expectedHeaders) {
			if (!(actualHeaders.contains(expectedHeader))) {
				log.error("Header " + expectedHeader + " is missing");
				missingHeaders.add(expectedHeader);
			}
		}
		if ((missingHeaders.size() > 0) && (stopOnMissingField)) {
			throw new ValidationException(
					"Parsing stopped because of missing field(s) and StopOnMissingField Parameter is TRUE");
		}
	}

	private String convertDate(TranslationAction action, String inputDateString, String inputFormat, String timeZone) {
		String convertedDate = null;

		try {
			SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputFormat);
			SimpleDateFormat canonicalDateFormat = new SimpleDateFormat(action.format);
			if (timeZone != null) {
				canonicalDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
			}
			Date inputDate = inputDateFormat.parse(inputDateString);
			convertedDate = canonicalDateFormat.format(inputDate);
		} catch (Exception ex) {
			log.warn("Error converting date to specified format.  Returning original string.", ex);
			// Do not record, just return original string, since it cannot be
			// formatted.
			return inputDateString;
		}

		return convertedDate;
	}

	private String combineFields(Map<String, Object> recordMap, String fields) {
		StringBuffer returnValue = new StringBuffer();
		String[] fieldsToCombine = fields.split(headerSeperator);

		for (String field : fieldsToCombine) {
			returnValue.append(recordMap.get(field));
			returnValue.append(" ");
		}

		return returnValue.toString().trim();
	}

	/**
	 * Returns the access label string from the input data. Must be overridden
	 * if needed.
	 * 
	 * @param recordMap
	 *            Record Map input data
	 * @return the access label string
	 */
	public String getAccessLabel(Map<String, Object> recordMap) {
		return null;
	}

	/**
	 * Method called to perform a custom field translation.
	 * 
	 * @param outputFieldPath
	 *            path to the canonical output field being produced
	 * @param outputFieldKey
	 *            key name to the canonical output field being produced
	 * @param recordMap
	 *            map of input name/value pairs
	 * @return the custom string value to be added to the output
	 * @throws UnsupportedOperationException
	 */
	public abstract String customFieldTranslator(String outputFieldPath, String outputFieldKey,
			Map<String, Object> recordMap);
	
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
			if (!(curObj instanceof JSONObject)) {
				// Detects if a non-array, non-object reference was specified
				// but there are still more elements in the path
				curObj = null;
				break;
			}			
			Object obj = ((JSONObject)curObj).get(name);
			if (obj == null) {
				// Likely invalid name
				curObj = null;
				inArray = false;
				break;
			}
			else if (obj instanceof JSONArray) {
				// Only the first element of the array should exist
				curObj = ((JSONArray)obj).get(0);
				inArray = true;
			}
			else {
				curObj = obj;
				inArray = false;
			}
		}
		
		if (curObj != null) {
			if (curObj instanceof String) {
				result = (String)curObj;
			}
			else if (inArray) {
				// For arrget the path will be an array
				// So in this case just return string
				result = "string";
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
}
