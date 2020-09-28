package com.demo.transaction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ReadCSVFile {

	HashMap<String, String> prodRefData = new HashMap<String, String>();
	HashMap<String, Double> transactionByAttr = new HashMap<String, Double>();
	HashMap<String, Double> transactionsByCity = new HashMap<String, Double>();
	
	public void loadProductReferenceData() {

    	readCSVData("Product Reference Folder/ProductReference.csv","","ProdReference");

	}
	public String readCSVData(String fileName,String param,String identifier) {
		
		boolean firstLine = true;
		String line = null;
		String splitBy = ",";
		String apiOutput = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		File f;
		
		if(identifier.equalsIgnoreCase("ProdReference"))
			 f = new File (getClass().getClassLoader().getResource(fileName).getFile());
		else
			f = new File (fileName);			

        BufferedReader lineReader;
		try 
		{
			lineReader = new BufferedReader(new FileReader(f.getAbsolutePath().replaceAll("%20", " ")));
			while ((line = lineReader.readLine()) != null) 
			{
                String[] singleRow = line.split(splitBy);

                if(firstLine)
    			{
    				firstLine=false;
    				continue;
    			}
                
                if(identifier.equalsIgnoreCase("ProdReference"))
                {
                	prodRefData.put(singleRow[0], singleRow[1]+","+singleRow[2]);
                }
                else if(identifier.equalsIgnoreCase("searchTransaction"))
                {
                	if(param.equalsIgnoreCase(singleRow[0]))
                	{
                		String[] prodName = prodRefData.get(singleRow[1]).split(",");
                		
                		apiOutput = apiOutput + "{ “transactionId”: "+singleRow[0]+", “productName”:\""+prodName[0]+"\",\r\n" + 
                				"“transactionAmount”: "+singleRow[2]+", “transactionDatetime”: \""+singleRow[3]+"\"}\n";
                	}
                }
                else if(identifier.equalsIgnoreCase("searchTransactionByAttribute"))
                {					
        		    Date transDt = formatter.parse(singleRow[3]);
        		    Calendar last_n_days = Calendar.getInstance();
        		    Calendar transDate = Calendar.getInstance();
        		    
        		    last_n_days.add(Calendar.DATE, - Integer.parseInt(param));
        		    
        		    transDate.setTime(transDt);
        			
                	if(last_n_days.before(transDate))
                	{
	                	if(transactionByAttr.containsKey(singleRow[1])) {
	                		Double value = transactionByAttr.get(singleRow[1]);
	                		transactionByAttr.put(singleRow[1], value + new Double(singleRow[2]));
	                	}
	                	else {
	                		transactionByAttr.put(singleRow[1], new Double(singleRow[2]));
	                	}
                	}
                }            
            }
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return apiOutput;
	}

	public String searchTransactions(String tranID) {
		
		//File f = new File (getClass().getClassLoader().getResource("Transaction Folder").getPath().replaceAll("%20", " "));
		File f = new File("C:\\Transaction Folder");
		File[] listOfFiles = f.listFiles();
		String apiOut = "";
		
		for (int i = 0; i < listOfFiles.length; i++) 
		{
			if (listOfFiles[i].isFile()) 
			{
		        if(listOfFiles[i].getName().contains(".csv")) 
		        {
		        	//apiOut = apiOut + readCSVData("Transaction Folder/"+listOfFiles[i].getName(),tranID,"searchTransaction");
		        	apiOut = apiOut + readCSVData("C:\\Transaction Folder\\"+listOfFiles[i].getName(),tranID,"searchTransaction");
		        }
			}
		}
		if("".equalsIgnoreCase(apiOut))
			apiOut = "No Transactions Found !!";
		
		return apiOut;
	}
	public String searchTransactionsByAttribute(String noOfDays,String attr) {
		
		//File f = new File (getClass().getClassLoader().getResource("Transaction Folder").getPath().replaceAll("%20", " "));
		File f = new File("C:\\Transaction Folder");
		File[] listOfFiles = f.listFiles();
		String apiOut = "";
		String prodName = "";
		Double currValue;
		String outputAPI="";
		String setAttrName="";
		
		for (int i = 0; i < listOfFiles.length; i++) 
		{
			if (listOfFiles[i].isFile()) 
			{
		        if(listOfFiles[i].getName().contains(".csv")) 
		        {
		        	//apiOut = apiOut + readCSVData("Transaction Folder/"+listOfFiles[i].getName(),noOfDays,"searchTransactionByAttribute");
		        	apiOut = apiOut + readCSVData("C:\\Transaction Folder\\"+listOfFiles[i].getName(),noOfDays,"searchTransactionByAttribute");		        
		        }
			}
		}
		
		outputAPI = "{ “summary”: [ ";
		
		for(Map.Entry<String, Double> mapData : transactionByAttr.entrySet())
		{
			if(attr.equalsIgnoreCase("PRODUCT"))
			{
				prodName = prodRefData.get(mapData.getKey()).split(",")[0];
				setAttrName = "\"productName\"";
			}
			else if(attr.equalsIgnoreCase("CITY"))
			{
				prodName = prodRefData.get(mapData.getKey()).split(",")[1];
				setAttrName = "\"cityName\"";
			}
			
			currValue = mapData.getValue();			
			outputAPI = outputAPI + "{"+setAttrName+": \""+prodName+"\", {“totalAmount”:" +currValue+"}}";
		}
		
		
		outputAPI = outputAPI + "]}";
		
		if(!outputAPI.contains("totalAmount"))
			outputAPI = "No Records Found !!";
		
		return outputAPI;
	}
}
