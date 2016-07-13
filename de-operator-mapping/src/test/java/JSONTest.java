import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

import com.deleidos.framework.operators.mapping.SimpleConfigurableTranslator;
import com.google.gson.JsonObject;

public class JSONTest {
	public static void main(String[] args) throws IOException, ParseException{
		HashMap<String,String> tuple = new HashMap<String,String>();
		tuple.put("clock", "yeah");
		tuple.put("lat", "55");
		tuple.put("lon", "77");
		tuple.put("alt", "100");
		  SimpleConfigurableTranslator translator = new SimpleConfigurableTranslator();
		  translator.setModelName("position_vehicle");
		  translator.setInputFormatName("flightData");
		  translator.setModelVersion("1.0");

		  translator.loadDataModel("C:\\Users\\doyleao\\Documents\\Workspace_Git\\de-operator-mapping\\position_vehicle_v1.0");
		  translator.initialize();
		  JsonObject parsedData = null;
		  parsedData = translator.recordTranslation(tuple, null, null);
		  System.out.println(parsedData);
	}
}
