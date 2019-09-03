package com.money.transfer.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.money.transfer.model.MoneyTransferTransaction;
import com.money.transfer.repository.exceptions.DuplicateAccountException;
import com.money.transfer.repository.impl.MoneyTransferTransactionRepositoryImpl;

@RunWith(JUnitPlatform.class)
public class MoneyTransferTransactionRepositoryTest {

	@Test
    void testCreateTxnValid() throws DuplicateAccountException {
		MoneyTransferTransaction txn = new MoneyTransferTransaction(
				"a", "b", BigDecimal.valueOf(1000));
		MoneyTransferTransactionRepository repo = new MoneyTransferTransactionRepositoryImpl();
		MoneyTransferTransaction actual = repo.create(txn);
		assertNotNull(actual.getId());
		assertEquals(txn.getSource(), actual.getSource());
		assertEquals(txn.getTarget(), actual.getTarget());
    }
	
	@Test
    void testGetTxn() throws DuplicateAccountException {
		MoneyTransferTransaction txn = new MoneyTransferTransaction(
				"a", "b", BigDecimal.valueOf(1000));
		MoneyTransferTransactionRepository repo = new MoneyTransferTransactionRepositoryImpl();
		MoneyTransferTransaction created = repo.create(txn);
		MoneyTransferTransaction actual = repo.get(created.getId());
		assertNotNull(created.getId(), actual.getId());
		assertEquals(txn.getSource(), actual.getSource());
		assertEquals(txn.getTarget(), actual.getTarget());
    }
	
	@Test
    void testGetTxnNull() throws DuplicateAccountException {
		MoneyTransferTransactionRepository repo = new MoneyTransferTransactionRepositoryImpl();
		MoneyTransferTransaction actual = repo.get("abc");
		assertNull(actual);
    }
	
	@Test
    void testGetAllTxnEmpty() {
		MoneyTransferTransactionRepository repo = new MoneyTransferTransactionRepositoryImpl();
		Collection<MoneyTransferTransaction> actual = repo.all();
		assertEquals(0, actual.size());;
    }
	
	@Test
    void testGetAllTxn() {
		MoneyTransferTransaction txn = new MoneyTransferTransaction(
				"a", "b", BigDecimal.valueOf(1000));
		MoneyTransferTransactionRepository repo = new MoneyTransferTransactionRepositoryImpl();
		repo.create(txn);
		Collection<MoneyTransferTransaction> actual = repo.all();
		assertEquals(1, actual.size());;
    }
}
