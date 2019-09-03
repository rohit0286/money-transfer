package com.money.transfer.service.exceptions.providers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.money.transfer.service.exceptions.MoneyTransferTransactionServiceException;

@Provider
public class MoneyTransferTransactionServiceExceptionProvider
		implements ExceptionMapper<MoneyTransferTransactionServiceException> {
	@Override
	public Response toResponse(MoneyTransferTransactionServiceException exception) {
		switch (exception.getType()) {
		case EXECUTION:
			return Response.status(500).entity(exception.getMessage()).type("text/plain").build();

		case VALIDATTION:
			return Response.status(400).entity(exception.getMessage()).type("text/plain").build();
		default:
			return Response.status(500).entity(exception.getMessage()).type("text/plain").build();

		}
	}
}
