package com.money.transfer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import com.money.transfer.executor.MoneyTransferTransactionExecutor;
import com.money.transfer.model.MoneyTransferTransaction;
import com.money.transfer.model.TransferStatus;
import com.money.transfer.repository.MoneyTransferTransactionRepository;
import com.money.transfer.service.exceptions.FailureType;
import com.money.transfer.service.exceptions.MoneyTransferTransactionExecutorException;
import com.money.transfer.service.exceptions.MoneyTransferTransactionServiceException;
import com.money.transfer.service.impl.MoneyTransferTransactionServiceImpl;
import com.money.transfer.validate.ValidationException;
import com.money.transfer.validate.Validator;


@RunWith(JUnitPlatform.class)
@ExtendWith(MockitoExtension.class)
public class MoneyTransferTransactionServiceTest {
	@Mock MoneyTransferTransactionRepository transactionRepository;
	@Mock Validator<MoneyTransferTransaction> transactionValidator;
	@Mock MoneyTransferTransactionExecutor transactionExecutor;
	
	MoneyTransferTransactionService service;
	@BeforeEach
	public void before() {
		service = new MoneyTransferTransactionServiceImpl(transactionRepository, 
				transactionValidator, transactionExecutor);
	}

	@Test
	public void testCreateValidationException() throws ValidationException {
		Mockito.doThrow(new ValidationException()).when(transactionValidator).validate(Mockito.any());
		Assertions.assertThrows(MoneyTransferTransactionServiceException.class,
				() -> service.create(new MoneyTransferTransaction()));
	}
	
	@Test
	public void testCreate() throws ValidationException, MoneyTransferTransactionServiceException {
		Mockito.when(transactionRepository.create(Mockito.any())).thenReturn(new MoneyTransferTransaction("1", "2",
				BigDecimal.valueOf(1)));
		MoneyTransferTransaction actual = service.create(new MoneyTransferTransaction("1", "2"
				,BigDecimal.valueOf(1)));
		Assertions.assertEquals("1", actual.getSource());
	}
	
	@Test
	public void testProcessValidationError() {
		Mockito.when(transactionRepository.get(Mockito.any())).thenReturn(null);
		Assertions.assertThrows(MoneyTransferTransactionServiceException.class,
				() -> service.process("1"));
	}
	
	@Test
	public void testProcess() throws MoneyTransferTransactionServiceException {
		MoneyTransferTransaction sampleTxn = new MoneyTransferTransaction("1", "2",
				BigDecimal.valueOf(100));
		Mockito.when(transactionRepository.get(Mockito.any())).thenReturn(sampleTxn);
		Mockito.when(transactionExecutor.execute(sampleTxn)).thenAnswer(
				new Answer<CompletableFuture<MoneyTransferTransaction>>() {

			@Override
			public CompletableFuture<MoneyTransferTransaction> answer(InvocationOnMock invocation) throws Throwable {
				return CompletableFuture.supplyAsync(() -> {
					sampleTxn.setStatus(TransferStatus.COMPLETED);
					return sampleTxn;
				});
			}
		});
		Assertions.assertEquals(TransferStatus.PENDING, sampleTxn.getStatus());
		MoneyTransferTransaction actual = service.process("1");
		Assertions.assertEquals(TransferStatus.COMPLETED, actual.getStatus());
	}
	
	@Test
	public void testProcessExecutorException() throws MoneyTransferTransactionServiceException {
		MoneyTransferTransaction sampleTxn = new MoneyTransferTransaction("1", "2",
				BigDecimal.valueOf(100));
		Mockito.when(transactionRepository.get(Mockito.any())).thenReturn(sampleTxn);
		Mockito.when(transactionExecutor.execute(sampleTxn)).thenReturn(
				CompletableFuture.supplyAsync(() -> {
					throw new MoneyTransferTransactionExecutorException("test",
							FailureType.VALIDATTION);
				}));
		Mockito.when(transactionExecutor.execute(sampleTxn)).thenAnswer(
				new Answer<CompletableFuture<MoneyTransferTransaction>>() {

			@Override
			public CompletableFuture<MoneyTransferTransaction> answer(InvocationOnMock invocation) throws Throwable {
				return CompletableFuture.supplyAsync(() -> {
					throw new MoneyTransferTransactionExecutorException("test",
							FailureType.VALIDATTION);
				});
			}
		});
		Assertions.assertThrows(MoneyTransferTransactionServiceException.class,
				() -> service.process("1"));
		Assertions.assertEquals(TransferStatus.ERROR, sampleTxn.getStatus());
	}

	@Test
	public void testGet() {
		MoneyTransferTransaction sampleTxn = new MoneyTransferTransaction("1", "2",
				BigDecimal.valueOf(100));
		Mockito.when(transactionRepository.get(Mockito.any())).thenReturn(sampleTxn);
		MoneyTransferTransaction actual = service.get("X");
		assertEquals("1", actual.getSource());
	}
	
	@Test
	public void testGetNull() {
		Mockito.when(transactionRepository.get(Mockito.any())).thenReturn(null);
		MoneyTransferTransaction actual = service.get("X");
		assertNull(actual);;
	}
	
	@Test
	public void testAll() {
		MoneyTransferTransaction sampleTxn = new MoneyTransferTransaction("1", "2",
				BigDecimal.valueOf(100));
		List<MoneyTransferTransaction> txns = new ArrayList<>();
		txns.add(sampleTxn);
		Mockito.when(transactionRepository.all()).thenReturn(txns);
		Collection<MoneyTransferTransaction> actual = service.all();
		assertEquals(1, actual.size());
	}
}


