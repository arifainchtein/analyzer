package com.teleonome.analyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONException;
import org.json.JSONObject;

import com.teleonome.framework.denome.DenomeUtils;
import com.teleonome.framework.exception.MissingDenomeException;
import com.teleonome.framework.exception.TeleonomeValidationException;
import com.teleonome.framework.utils.Utils;

public class Analyzer {
	
	Logger logger = Logger.getLogger(getClass());
	private static String teleonomeFileName="";
	
	public Analyzer() {
		
		String fileName =  Utils.getLocalDirectory() + "lib/Log4J.properties";
		PropertyConfigurator.configure(fileName);
		
		
		JSONObject pulseJSONObject = null;
		File parentDirectory=null;
		try {
			File theFile = new File(teleonomeFileName);
			parentDirectory = theFile.getParentFile();		
			String fileInString = FileUtils.readFileToString(theFile);
			pulseJSONObject = new JSONObject(fileInString);
		}catch(JSONException e) {
			System.out.println(teleonomeFileName  + " is not a valid Denome file");
			System.exit(-1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(teleonomeFileName  + " is not a valid Denome file");
			System.exit(-1);
		}
		
		JSONObject denomeObject =  pulseJSONObject.getJSONObject("Denome");
		String teleonomeName = denomeObject.getString("Name");
		
		
		ArrayList<String> htmlArrayList;
		File analysisFile = new File(parentDirectory.getAbsolutePath() + "/" + teleonomeName +"_Analisis.html");
		
		try {
			htmlArrayList = DenomeUtils.generateDenomePhysiologyReportHTMLTable(pulseJSONObject);
			String htmlText = String.join( System.getProperty("line.separator"), htmlArrayList);
			analysisFile.delete();
			FileUtils.write(analysisFile, htmlText);
			
		} catch (MissingDenomeException | TeleonomeValidationException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.warn("Analysis Process Complete.");
		logger.warn(" ");
		logger.warn("look for:");
		logger.warn(analysisFile.getAbsolutePath());
	
	}
	
	
    public static void main( String[] args )
    {
    	if(args.length!=1){
			//System.out.println("Usage: Analyzer  TeleonomeFileName ");
			//System.exit(-1);
    		Scanner scanner = new Scanner(System.in);
			System.out.println("Input the complete path of the denom file");
			teleonomeFileName = scanner.nextLine();
			scanner.close();
		}else {
			teleonomeFileName=args[0];
		}
    	
    	new Analyzer();
    }
}
