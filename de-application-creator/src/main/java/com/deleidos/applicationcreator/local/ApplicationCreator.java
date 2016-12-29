package com.deleidos.applicationcreator.local;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;

import com.deleidos.analytics.common.util.FileUtil;
import com.deleidos.applicationcreator.APAClientNode;
import com.deleidos.applicationcreator.APAStaging;
import com.deleidos.applicationcreator.AppCreationInterface;
import com.deleidos.applicationcreator.utility.JSONOperations;
import com.deleidos.applicationcreator.utility.fileUtility;
import com.deleidos.framework.model.system.OperatorDescriptor;
import com.deleidos.framework.model.system.OperatorFile;
import com.deleidos.framework.model.system.SystemDescriptor;

import org.apache.log4j.Logger;
import org.mortbay.log.Log;

public class ApplicationCreator implements AppCreationInterface {

	private SystemDescriptor sys;
	private String appBundleName;
	private Map<String, Map<String, OperatorFile>> files;
	private Logger logger = Logger.getLogger(ApplicationCreator.class);
	private APAClientNode apaClientNode;

	public ApplicationCreator(String managerServiceHostname, SystemDescriptor sys, String appBundleName) {
		this.sys = sys;
		this.appBundleName = appBundleName;
		this.files = sys.getOperatorFiles();
		apaClientNode = new APAClientNode(managerServiceHostname);
	}

	public String run() throws Exception {
		String message = "";
		APAStaging.createApaStage(this.appBundleName);// create app structure
														// (folders)
		List<OperatorDescriptor> operators = this.sys.getApplication().getOperators();
		String className = "";
		String jarName = "";
		File destDir = new File("/opt/apex-staging/" + appBundleName + "/lib");

		for (OperatorDescriptor op : operators) {
			// copy the jars of the operators used into the lib folder
			className = op.getClassName();
			jarName = className + ".jar";
			File jar = new File("/opt/apex-deployment/operators/" + jarName);
			if (!(op.getClassName().contains("ConsoleOutputOperator"))) {
				FileUtils.copyFileToDirectory(jar, destDir, false);
			}

			String opName = op.getName();
			if (files.containsKey(opName)) {
				for (OperatorFile opFile : files.get(opName).values()) {
					String filename = opFile.getFilename();

					String opFilePath = "/opt/apex-deployment/files/" + opName + "/";
					File file = new File(opFilePath + filename);
					FileUtil.writeByteArray(file, opFile.getBytes());

					op.getProperties().put(opFile.getFilenameField(), filename);

					String jarFile = "/opt/apex-staging/" + appBundleName + "/lib/" + jarName;
				
					Process p = Runtime.getRuntime().exec("jar uf " + jarFile + " -C " + opFilePath + " " + filename);
					p.waitFor();
					
				}
			}
		}
		// BEGIN OLD FILE STUFF
		// if (op.getClassName().contains("JSONMappingOperator")) {
		// If json operator then copy mapping file into jar
		// File map = new File("/opt/apex-deployment/files/" + op.getName() +
		// ".json");
		// FileWriter mapWrite = new FileWriter(map);
		//
		// mapWrite.write(JsonUtil.toJsonString(files.get(op.getName())));
		// mapWrite.close();
		//
		// String jarFile = "/opt/apex-staging/" + appBundleName +
		// "/lib/de-operator-mapping-0.0.1-SNAPSHOT.jar";
		// String mappingFile = op.getName() + ".json";
		// Runtime.getRuntime().exec("jar uf " + jarFile + " -C
		// /opt/apex-deployment/mapping " + mappingFile);
		// // Add mapping path of file
		//
		// JsonArray jArray = jObject.get("operators").getAsJsonArray();
		// for (int i = 0; i < jArray.size(); i++) {
		// JsonElement temp = jArray.get(i);
		// JsonObject tempObj = temp.getAsJsonObject();
		// if (tempObj.get("class").toString().contains("JSONMappingOperator"))
		// {
		// JsonObject tempProps = tempObj.get("properties").getAsJsonObject();
		// tempProps.addProperty("modelPath", "/" + op.getName() + ".json");
		// }
		// }
		// }
		// }
		// END OLD FILE STUFF

		FileWriter jsonFile = new FileWriter("/opt/apex-staging/" + appBundleName + "/app/" + appBundleName + ".json");
		jsonFile.write(sys.getApplication().getApexAppJson());
		jsonFile.close();

		// json file copy to app folder
		// zip directory and now have an apa
		File appDirectory = new File("/opt/apex-staging/" + appBundleName);
		List<File> fileList = new ArrayList<File>();

		fileUtility.getInstance().getFiles(appDirectory, fileList);
		fileUtility.getInstance().writeFile(appDirectory, fileList);
		// Delete apa Staging grounds
		fileUtility.getInstance().delete(appDirectory);

		// Delete operator files
		if (!files.isEmpty()) {
			File mappingDir = new File("/opt/apex-deployment/files");
			FileUtils.cleanDirectory(mappingDir);
		}
		// Push the apa file/bundle to the hadoop client node
		String SysDescriptor = JSONOperations.getInstance().sysDescriptorToString(sys);
		String response;
		logger.info(SysDescriptor);
		try {
			response = apaClientNode.postServer("/opt/apex-deployment/" + appBundleName + ".apa", SysDescriptor,
					"/tmp/" + appBundleName + ".apa");
			message = "launching " + appBundleName + ".apa";
		} catch (Throwable t) {
			message = "Error " + message;
			logger.error(message);
			
			//If theres an error, this run will not process. therefore delete APA file
			File appFile = new File("/opt/apex-deployment/" + appBundleName + ".apa");
			appFile.delete();
			return message;
		}
		// Remove file locally now that its on the client node
		File appFile = new File("/opt/apex-deployment/" + appBundleName + ".apa");
		appFile.delete();

		return message;
	}

	@Override
	public String appID() {
		return null;
	}

	@Override
	public String appDescription() {
		return "APA Creation via Rest";
	}
}
