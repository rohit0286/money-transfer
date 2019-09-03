package com.money.transfer.controller;

import java.util.Collections;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.money.transfer.bean.utils.BeanFactory;
import com.money.transfer.model.MoneyTransferTransaction;
import com.money.transfer.service.MoneyTransferTransactionService;
import com.money.transfer.service.exceptions.MoneyTransferTransactionServiceException;


@Path("/transactions")
public class TransactionController {

    private MoneyTransferTransactionService transactionService;
    public TransactionController() {
    	this.transactionService = (MoneyTransferTransactionService) BeanFactory.get().getBean(MoneyTransferTransactionService.class);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(MoneyTransferTransaction trx) throws MoneyTransferTransactionServiceException {
    	MoneyTransferTransaction txn = transactionService.create(trx);
		return Response.ok(txn).build();
    }
    
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response execute(@PathParam("id") String txnId) throws MoneyTransferTransactionServiceException {
    	transactionService.process(txnId);
    	MoneyTransferTransaction txn = transactionService.get(txnId);
		return Response.ok(txn).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getall() {
        return Response.ok(Collections.unmodifiableCollection(transactionService.all())).build();
    }

}
