package com.money.transfer.controller;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.money.transfer.dto.AccountDto;
import com.money.transfer.model.MoneyTransferTransaction;
import com.money.transfer.service.exceptions.providers.AccountServiceExceptionProvider;
import com.money.transfer.service.exceptions.providers.MoneyTransferTransactionServiceExceptionProvider;

import io.restassured.RestAssured;

@RunWith(JUnitPlatform.class)
public class TransactionControllerTest extends JerseyTest {


	@BeforeEach
	public void configureRestAssured() throws Exception {
		super.setUp();
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 9998;
	}
	
	@AfterEach
	public void after() throws Exception {
		super.tearDown();
	}

    @Test
    public void testCreateValidTransaction() {
    	AccountDto accountOne = new AccountDto(1000);
		Response responseOne = target("/accounts").request(MediaType.APPLICATION_JSON).post(Entity.json(accountOne));
		String sourceId = (String)responseOne.readEntity(Map.class).get("id");
		Assertions.assertTrue(StringUtils.isNotBlank(sourceId));

		AccountDto accountTwo = new AccountDto(1000);
		Response responseTwo = target("/accounts").request(MediaType.APPLICATION_JSON).post(Entity.json(accountTwo));
		String targetId = (String)responseTwo.readEntity(Map.class).get("id");
		Assertions.assertTrue(StringUtils.isNotBlank(targetId));

		MoneyTransferTransaction txn = new MoneyTransferTransaction(sourceId, targetId, BigDecimal.valueOf(100));
		Response txnResponse = target("/transactions").request(MediaType.APPLICATION_JSON).post(Entity.json(txn));
		Map<String, String> actual = txnResponse.readEntity(Map.class);
		Assertions.assertEquals(sourceId, actual.get("source"));
		Assertions.assertEquals(targetId, actual.get("target"));
		Assertions.assertNotNull(actual.get("id"));
		Assertions.assertEquals("PENDING", actual.get("status"));
    }
    
    @Test
    public void testCreateTransactionNoSource() {
		String sourceId = "testing";

		AccountDto accountTwo = new AccountDto(1000);
		Response responseTwo = target("/accounts").request(MediaType.APPLICATION_JSON).post(Entity.json(accountTwo));
		String targetId = (String)responseTwo.readEntity(Map.class).get("id");
		Assertions.assertTrue(StringUtils.isNotBlank(targetId));

		MoneyTransferTransaction txn = new MoneyTransferTransaction(sourceId, targetId, BigDecimal.valueOf(100));
		Response txnResponse = target("/transactions").request(MediaType.APPLICATION_JSON).post(Entity.json(txn));
		
		Assertions.assertEquals(400, txnResponse.getStatus());
    }
    
    @Test
    public void testCreateTransactionNoTarget() {
		String targetId = "testing";

		AccountDto accountTwo = new AccountDto(1000);
		Response responseTwo = target("/accounts").request(MediaType.APPLICATION_JSON).post(Entity.json(accountTwo));
		String sourceId = (String)responseTwo.readEntity(Map.class).get("id");
		Assertions.assertTrue(StringUtils.isNotBlank(sourceId));

		MoneyTransferTransaction txn = new MoneyTransferTransaction(sourceId, targetId, BigDecimal.valueOf(100));
		Response txnResponse = target("/transactions").request(MediaType.APPLICATION_JSON).post(Entity.json(txn));
		Assertions.assertEquals(400, txnResponse.getStatus());
    }

    @Test
    public void testCreateTxnZeroAmount() {
    	AccountDto accountOne = new AccountDto(1000);
		Response responseOne = target("/accounts").request(MediaType.APPLICATION_JSON).post(Entity.json(accountOne));
		String sourceId = (String)responseOne.readEntity(Map.class).get("id");
		Assertions.assertTrue(StringUtils.isNotBlank(sourceId));

		AccountDto accountTwo = new AccountDto(1000);
		Response responseTwo = target("/accounts").request(MediaType.APPLICATION_JSON).post(Entity.json(accountTwo));
		String targetId = (String)responseTwo.readEntity(Map.class).get("id");
		Assertions.assertTrue(StringUtils.isNotBlank(targetId));

		MoneyTransferTransaction txn = new MoneyTransferTransaction(sourceId, targetId, BigDecimal.ZERO);
		Response txnResponse = target("/transactions").request(MediaType.APPLICATION_JSON).post(Entity.json(txn));
		Assertions.assertEquals(400, txnResponse.getStatus());
    }
    
    @Test
    public void testCreateTxnNegativeAmount() {
    	AccountDto accountOne = new AccountDto(1000);
		Response responseOne = target("/accounts").request(MediaType.APPLICATION_JSON).post(Entity.json(accountOne));
		String sourceId = (String)responseOne.readEntity(Map.class).get("id");
		Assertions.assertTrue(StringUtils.isNotBlank(sourceId));

		AccountDto accountTwo = new AccountDto(1000);
		Response responseTwo = target("/accounts").request(MediaType.APPLICATION_JSON).post(Entity.json(accountTwo));
		String targetId = (String)responseTwo.readEntity(Map.class).get("id");
		Assertions.assertTrue(StringUtils.isNotBlank(targetId));

		MoneyTransferTransaction txn = new MoneyTransferTransaction(sourceId, targetId, BigDecimal.valueOf(-1));
		Response txnResponse = target("/transactions").request(MediaType.APPLICATION_JSON).post(Entity.json(txn));
		Assertions.assertEquals(400, txnResponse.getStatus());
    }
    
    @Test
    public void testProcessValidTransaction() {
    	AccountDto accountOne = new AccountDto(10);
		Response responseOne = target("/accounts").request(MediaType.APPLICATION_JSON).post(Entity.json(accountOne));
		String sourceId = (String)responseOne.readEntity(Map.class).get("id");
		Assertions.assertTrue(StringUtils.isNotBlank(sourceId));

		AccountDto accountTwo = new AccountDto(20);
		Response responseTwo = target("/accounts").request(MediaType.APPLICATION_JSON).post(Entity.json(accountTwo));
		String targetId = (String)responseTwo.readEntity(Map.class).get("id");
		Assertions.assertTrue(StringUtils.isNotBlank(targetId));

		MoneyTransferTransaction txn = new MoneyTransferTransaction(sourceId, targetId, BigDecimal.valueOf(2));
		Response txnResponse = target("/transactions").request(MediaType.APPLICATION_JSON).post(Entity.json(txn));
		String txnId = (String) txnResponse.readEntity(Map.class).get("id");
		Assertions.assertTrue(StringUtils.isNotBlank(txnId));

		Response processResponse = target("/transactions/" + txnId).request(
				MediaType.APPLICATION_JSON).put(Entity.json("{}"));
		Assertions.assertEquals(200, processResponse.getStatus());
		Map<String, String> actual = processResponse.readEntity(Map.class);
		Assertions.assertEquals(sourceId, actual.get("source"));
		Assertions.assertEquals(targetId, actual.get("target"));
		Assertions.assertEquals(txnId, actual.get("id"));
		Assertions.assertEquals("COMPLETED", actual.get("status"));

    }
    
    @Test
    public void testProcessInvalidTransaction() {
		Response processResponse = target("/transactions/" + "noneTxn").request(
				MediaType.APPLICATION_JSON).put(Entity.json("{}"));
		Assertions.assertEquals(400, processResponse.getStatus());
    }
    
	@Test
	public void testGetAllNoTxns() {
		AccountDto accountOne = new AccountDto(10);
		Response responseOne = target("/accounts").request(MediaType.APPLICATION_JSON).post(Entity.json(accountOne));
		String sourceId = (String)responseOne.readEntity(Map.class).get("id");
		Assertions.assertTrue(StringUtils.isNotBlank(sourceId));

		AccountDto accountTwo = new AccountDto(20);
		Response responseTwo = target("/accounts").request(MediaType.APPLICATION_JSON).post(Entity.json(accountTwo));
		String targetId = (String)responseTwo.readEntity(Map.class).get("id");
		Assertions.assertTrue(StringUtils.isNotBlank(targetId));

		MoneyTransferTransaction txn = new MoneyTransferTransaction(sourceId, targetId, BigDecimal.valueOf(2));
		Response txnResponse = target("/transactions").request(MediaType.APPLICATION_JSON).post(Entity.json(txn));
		String txnId = (String) txnResponse.readEntity(Map.class).get("id");
		Assertions.assertTrue(StringUtils.isNotBlank(txnId));

		Response response = target("/transactions").request(MediaType.APPLICATION_JSON).get();
		Object[] actual = response.readEntity(Object[].class);
		Assertions.assertEquals(200, response.getStatus());
		Assertions.assertTrue(actual.length > 0);
	}

    @Override
    public Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        Set<Class<?>> resources = new HashSet<>();
        resources.add(TransactionController.class);
        resources.add(AccountController.class);
        resources.add(AccountServiceExceptionProvider.class);
        resources.add(MoneyTransferTransactionServiceExceptionProvider.class);
        return new ResourceConfig(resources);
    }
}