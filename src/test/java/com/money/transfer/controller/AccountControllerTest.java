package com.money.transfer.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

import io.restassured.RestAssured;

@RunWith(JUnitPlatform.class)
public class AccountControllerTest extends JerseyTest {

	private static final String BALANCE = "balance";

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
	public void shouldReturnAllAccounts() {
		AccountDto accountOne = new AccountDto(1000);
		target("/accounts").request(MediaType.APPLICATION_JSON)
				.post(Entity.json(accountOne));
		Response response = target("/accounts").request(MediaType.APPLICATION_JSON).get();
		Object[] actual = response.readEntity(Object[].class);
		Assertions.assertEquals(200, response.getStatus());
		Assertions.assertTrue(actual.length > 0);

	}

	@Test
	public void shouldBeAbleToAddNewAccount() {
		AccountDto newAccount = new AccountDto(100);
		given().contentType("application/json").body(newAccount).when().post("/accounts").then().body(BALANCE,
				equalTo(100));
	}

	@Override
	public Application configure() {
		enable(TestProperties.LOG_TRAFFIC);
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(AccountController.class);
	}
}