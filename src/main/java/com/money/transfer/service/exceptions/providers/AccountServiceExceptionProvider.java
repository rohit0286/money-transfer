package com.money.transfer.service.exceptions.providers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.money.transfer.service.exceptions.AccountServiceException;

@Provider
public class AccountServiceExceptionProvider implements ExceptionMapper<AccountServiceException>{

	@Override
	public Response toResponse(AccountServiceException exception) {
		return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).type("text/plain").build();
	}
}
