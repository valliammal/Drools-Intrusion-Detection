package com.delixus.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Timer;
import org.apache.commons.io.IOUtils;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.core.WorkingMemory;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;

import com.delixus.model.Product;
import com.delixus.main.ElasticsearchAPI;
import com.delixus.model.IndicesAndKeywords;
import com.delixus.main.DRLServerTimerTask;
import org.apache.commons.io.IOUtils;


public class DroolsTest {

	public static final void main(String[] args) {
		try {
			KieServices ks = KieServices.Factory.get();
			KieContainer kContainer = ks.getKieClasspathContainer();
			KieSession kSession = kContainer.newKieSession("ksession-rule");

			Product product = new Product();
			product.setType("gold");

			FactHandle fact1;

			fact1 = kSession.insert(product);
			kSession.fireAllRules();

			System.out.println("The discount for the jewellery product "
					+ product.getType() + " is " + product.getDiscount());

			Timer t = new Timer();
			DRLServerTimerTask drlServerTimerTask = new DRLServerTimerTask();
			t.scheduleAtFixedRate(drlServerTimerTask, 0, 30000);
			
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void executeDrools(String option, HashMap<String, Integer> RecipientsAndThresholds) throws DroolsParserException, IOException {
		System.out.println("Execute Drools ");
		String ruleFile = "/rules/Rules.drl";
		InputStream resourceAsStream = getClass().getResourceAsStream(ruleFile);
		byte[] bytes = IOUtils.toByteArray(resourceAsStream);
		String drl = new String(bytes, StandardCharsets.UTF_8);
		System.out.println(" Drl values " + drl);
		int lengthOfTheFile = bytes.length;
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
        resourceAsStream.close();
		KieSession kSession  =  new KieHelper().addContent( drl, ResourceType.DRL ).build().newKieSession();
		com.delixus.model.IndicesAndKeywords indicesAndKeywords = new com.delixus.model.IndicesAndKeywords();
		indicesAndKeywords.setOption(option);
		kSession.insert(indicesAndKeywords);
		kSession.fireAllRules();
		kSession.dispose();
		System.out.println("Entered for executing the drools");
		System.out.println(" indicesAndKeywords " + indicesAndKeywords.toString());
		System.out.println(" indicesAndKeywords " + indicesAndKeywords.getOption());
		System.out.println(" indicesAndKeywords " + indicesAndKeywords.getTargetIndex());
		indicesAndKeywords.setTargetIndex("JHe");
		if(indicesAndKeywords.getTargetIndex() != null)
		{
			ElasticsearchAPI esAPI = new ElasticsearchAPI();
			System.out.println("Elastic Search API is called");
			//System.out.print(esAPI.generateMatchObjects(indicesAndKeywords));
			//System.out.print("DEBUG Query: "+ esAPI.generateQueryString(indicesAndKeywords.getTargetIndex(), indicesAndKeywords.getSourceIndices(), indicesAndKeywords.getKeywords(), indicesAndKeywords));
			//esAPI.initiateESReindexing(indicesAndKeywords, RecipientsAndThresholds, option);
			//System.out.print(
			//		esAPI.generateQueryString(
			//				indicesAndKeywords.getTargetIndex(), indicesAndKeywords.getSourceIndices(), indicesAndKeywords.getKeywords()));
		}

        // Use of close() method to Close InputStreamReader
          
	}
	
	
}
