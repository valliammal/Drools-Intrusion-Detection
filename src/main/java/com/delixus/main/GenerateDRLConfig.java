package com.delixus.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.delixus.model.RecipientsThresholdsAndInterval;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GenerateDRLConfig {
	final Gson gsonBuilder = new GsonBuilder().create();
	final JsonParser jsonParser = new JsonParser();
	final String SERIALIZED_DB_DRL_CONFIG_DATA_URL = "http://18.224.129.80/elky/api/log/read.php";
	final String drlConfigFormat =  "rule %s\r\n" + 
			"	when \r\n" + 
			"		indicesAndKeywordsObject: IndicesAndKeywords(option==%s)\r\n" + 
			"	then\r\n" + 
			"		String[] sourceIndices = new String[]{%s};\r\n" + 
			"		String[] keywords = new String[]{\"%s\"};\r\n" + 
			"		\r\n" + 
			"		indicesAndKeywordsObject.setSourceIndices(sourceIndices);\r\n" + 
			"		indicesAndKeywordsObject.setKeywords(keywords);\r\n" + 
			"		indicesAndKeywordsObject.setTargetIndex(\"thinkpad1\");\r\n" + 
			"		\r\n" + 
			"		indicesAndKeywordsObject.setHostOSFamily(%s);\r\n" + 
			"		indicesAndKeywordsObject.setUserName(%s);\r\n" + 
			"		indicesAndKeywordsObject.setEventType(%s);\r\n" + 
			"		indicesAndKeywordsObject.setHostOSName(%s);\r\n" + 
			"		indicesAndKeywordsObject.setAgentType(%s);\r\n" + 
			"		indicesAndKeywordsObject.setHostOSPlatform(%s);\r\n" + 
			"	end\n";
	
	
	public void writeDRLConfig(String drlConfigString) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Delixus\\CyberSecurity\\java-drools-master\\src\\main\\resources\\com\\rule\\backup\\Rules1.drl", false));
        writer.write(drlConfigString);
        writer.close();
	}
	
	public HashMap<String, RecipientsThresholdsAndInterval> generateDRLConfigAndRetrieveRuleNames()
	{
		HashMap<String, RecipientsThresholdsAndInterval> RuleNames = new HashMap<String, RecipientsThresholdsAndInterval>();
		RecipientsThresholdsAndInterval recipientsThresholdsAndInterval;
		HashMap<String, Integer> RecipientAndThreshold = new HashMap<String, Integer>();
		try 
		{
			String dbJSON = "{\"rules\":[{\"id\":\"4\",\"agent_hostname\":\"\",\"agent_version\":\"\",\"agent_type\":\"winlogbeat\",\"event_type\":\"\",\"host_arch\":\"\",\"host_hostname\":\"EC2AMAZ-4PJ0E5T\",\"host_name\":\"EC2AMAZ-4PJ0E5T\",\"host_os_family\":\"windows\",\"host_os_name\":\"\",\"host_os_plat\":\"windows\",\"host_os_ver\":\"\",\"message\":\"SQL Server is terminating because of a system shutdown.\",\"uname\":\"\",\"rule_name\":\"SQLServerTerminating\",\"source_index\":\"winlogbeat-*\",\"rule_interval\":\"0\",\"description\":\"\"},{\"id\":\"5\",\"agent_hostname\":\"\",\"agent_version\":\"Test\",\"agent_type\":\"\",\"event_type\":\"\",\"host_arch\":\"\",\"host_hostname\":\"\",\"host_name\":\"\",\"host_os_family\":\"Test\",\"host_os_name\":\"\",\"host_os_plat\":\"\",\"host_os_ver\":\"\",\"message\":\"\",\"uname\":\"\",\"rule_name\":\"Test\",\"source_index\":\"Test\",\"rule_interval\":\"0\",\"description\":\"\"},{\"id\":\"6\",\"agent_hostname\":\"\",\"agent_version\":\"\",\"agent_type\":\"\",\"event_type\":\"\",\"host_arch\":\"\",\"host_hostname\":\"\",\"host_name\":\"\",\"host_os_family\":\"\",\"host_os_name\":\"\",\"host_os_plat\":\"\",\"host_os_ver\":\"\",\"message\":\"\",\"uname\":\"\",\"rule_name\":\"Rule5\",\"source_index\":\"drools_rule_engine\",\"rule_interval\":\"0\",\"description\":\"\"},{\"id\":\"7\",\"agent_hostname\":\"\",\"agent_version\":\"\",\"agent_type\":\"\",\"event_type\":\"\",\"host_arch\":\"\",\"host_hostname\":\"\",\"host_name\":\"\",\"host_os_family\":\"\",\"host_os_name\":\"\",\"host_os_plat\":\"\",\"host_os_ver\":\"\",\"message\":\"\",\"uname\":\"\",\"rule_name\":\"Rule7\",\"source_index\":\"kie workbench\",\"rule_interval\":\"0\",\"description\":\"\"},{\"id\":\"8\",\"agent_hostname\":\"\",\"agent_version\":\"\",\"agent_type\":\"\",\"event_type\":\"\",\"host_arch\":\"\",\"host_hostname\":\"\",\"host_name\":\"\",\"host_os_family\":\"\",\"host_os_name\":\"\",\"host_os_plat\":\"\",\"host_os_ver\":\"\",\"message\":\"\",\"uname\":\"\",\"rule_name\":\"Rule8\",\"source_index\":\"Filebeat rule\",\"rule_interval\":\"0\",\"description\":\"\"}]}";

			//String dbJSON = requestDRLConfigDefinitionFromDB(dbJSON);
			JsonArray dbRuleObjs = new JsonArray();
			dbRuleObjs = (JsonArray)jsonParser.parse(dbJSON.toString()).getAsJsonObject().get("rules");
			//System.out.println("Created: " + Integer.parseInt(responseJSON.get("created").toString()));

			String drlConfig = "import com.delixus.model.IndicesAndKeywords;\r\n";
			for(Object dbro : dbRuleObjs)
			{
				JsonObject dbRuleObj = (JsonObject)dbro;
				String ruleKeyword = "";
				if(!dbRuleObj.get("message").toString().equals("\"\""))
					ruleKeyword = "**" + dbRuleObj.get("message").toString().substring(1,dbRuleObj.get("message").toString().length()-1) + "**";
				
				/*JsonArray emailAndThresholdArray = dbRuleObj.getAsJsonArray("email");
	            for (JsonElement emailAndThresholdElement : emailAndThresholdArray) {
	                JsonObject emailAndThresholdObj = emailAndThresholdElement.getAsJsonObject();
	                
	                Map<String, Object> attributes = new HashMap<String, Object>();
	                Set<Entry<String, JsonElement>> entrySet = emailAndThresholdObj.entrySet();
	                for(Map.Entry<String,JsonElement> entry : entrySet){
	                   //System.out.println("DEBUG: " + entry.getKey() + " : " + emailAndThresholdObj.get(entry.getKey()).toString().replace("\"",""));
	                   String emailAddress = entry.getKey();
	                   Integer emailThreshold = Integer.parseInt(emailAndThresholdObj.get(emailAddress).getAsString());
	                   RecipientAndThreshold.put(emailAddress, emailThreshold);
	                }
	            }*/
				Integer emailThreshold = 1;
				RecipientAndThreshold.put("vallir63@gmail.com", emailThreshold);
	            recipientsThresholdsAndInterval = new RecipientsThresholdsAndInterval(RecipientAndThreshold, 
						Long.parseLong( dbRuleObj.get("rule_interval").toString().substring(1,dbRuleObj.get("rule_interval").toString().length()-1)));
				
				RuleNames.put(dbRuleObj.get("rule_name").toString().substring(1,dbRuleObj.get("rule_name").toString().length()-1),
						recipientsThresholdsAndInterval);
				drlConfig = drlConfig + String.format(drlConfigFormat, dbRuleObj.get("rule_name"), dbRuleObj.get("rule_name"), 
        			dbRuleObj.get("source_index"), ruleKeyword, dbRuleObj.get("host_os_family"),
        			dbRuleObj.get("uname"), dbRuleObj.get("event_type"), dbRuleObj.get("host_os_name"),
        			dbRuleObj.get("agent_type"), dbRuleObj.get("host_os_plat")
					);
			}
        
        	writeDRLConfig(drlConfig);
        }catch(IOException ioe)
        {
        	System.out.print(ioe.getMessage());
        }
    	return RuleNames;
	}
	
	public String requestDRLConfigDefinitionFromDB(String url) {		
        BufferedReader httpResponseReader = null;
        try {
            URL serverUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);
            
            httpResponseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String lineRead = "";
            StringBuilder drlResponse = new StringBuilder();
            while((lineRead = httpResponseReader.readLine()) != null) {
            	drlResponse = drlResponse.append(lineRead);
            }
            System.out.println("DEBUG - DRL DefinitionResponse:\r\n" + drlResponse.toString());
            return drlResponse.toString();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            return "";
        }
    }
}
