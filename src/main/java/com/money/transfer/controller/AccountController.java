package com.money.transfer.controller;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.logging.Level;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;

import com.money.transfer.bean.utils.BeanFactory;
import com.money.transfer.dto.AccountDto;
import com.money.transfer.model.Account;
import com.money.transfer.service.AccountService;
import com.money.transfer.service.exceptions.AccountServiceException;


@Path("/accounts")
public class AccountController {

	private AccountService accountService;
    public AccountController() {
    	this.accountService = (AccountService) BeanFactory.get().getBean(AccountService.class);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAccounts() {
        return Response.ok(Collections.unmodifiableCollection(accountService.all())).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewAccount(AccountDto accountDto) throws AccountServiceException {
    	Account account = accountService.create(
    			BigDecimal.valueOf(accountDto.getBalance()));
        return Response.ok(account).build();
    }
}
