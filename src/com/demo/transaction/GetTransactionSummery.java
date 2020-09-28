package com.demo.transaction;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/transaction")

public class GetTransactionSummery {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{transactionID}")
	
	public String getOutputJSON(@PathParam("transactionID") String tranID)
	{
		ReadCSVFile csvFile = new ReadCSVFile();
		csvFile.loadProductReferenceData();
		String output = csvFile.searchTransactions(tranID);
		
		return output;
	}
}
