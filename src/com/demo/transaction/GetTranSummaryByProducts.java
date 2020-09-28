package com.demo.transaction;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/transactionSummaryByProducts")

public class GetTranSummaryByProducts {
		
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		@Path("{last_n_days}")
		
		public String getOutputJSON(@PathParam("last_n_days") String noOfDays)
		{
			ReadCSVFile csvFile = new ReadCSVFile();
			csvFile.loadProductReferenceData();
			String output = csvFile.searchTransactionsByAttribute(noOfDays,"PRODUCT");
			
			return output;
		}
}
