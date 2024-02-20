package com.digicoworker.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/statement")
public class StatementService {
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response generateStatement(String body) {
		System.out.println("received request :" + body);
		return Response.ok().build();
	}
}
