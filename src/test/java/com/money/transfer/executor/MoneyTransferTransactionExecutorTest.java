package com.money.transfer.executor;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.money.transfer.model.Account;
import com.money.transfer.model.MoneyTransferTransaction;
import com.money.transfer.model.TransactionErrorReason;
import com.money.transfer.model.TransferStatus;
import com.money.transfer.service.AccountService;
import com.money.transfer.service.exceptions.AccountErrorReason;
import com.money.transfer.service.exceptions.AccountServiceException;

@RunWith(JUnitPlatform.class)
@ExtendWith(MockitoExtension.class)
public class MoneyTransferTransactionExecutorTest {

	@Mock
	AccountService accountService;
	MoneyTransferTransactionExecutor txnExecutor;
	ExecutorService executor;

	@BeforeEach
	public void before() {
		executor = Executors.newFixedThreadPool(1);
		txnExecutor = new MoneyTransferTransactionExecutorImpl(accountService, executor);
	}

	@AfterEach
	public void after() {
		executor.shutdownNow();
	}

	@Test
	public void testExecuteNoTxn() {
		Assertions.assertThrows(ExecutionException.class, () -> txnExecutor.execute(null).get());
	}

	@Test
	public void testExecuteCompletedTxn() {
		MoneyTransferTransaction txn = new MoneyTransferTransaction("1", "2", BigDecimal.valueOf(10));
		txn.setStatus(TransferStatus.COMPLETED);
		Assertions.assertThrows(ExecutionException.class, () -> txnExecutor.execute(txn).get());
	}

	@Test
	public void testExecuteNoTargetException() throws AccountServiceException {
		MoneyTransferTransaction txn = new MoneyTransferTransaction("1", "2", BigDecimal.valueOf(10));
		Account source = new Account("1", BigDecimal.valueOf(100));
		Mockito.when(accountService.get("1")).thenReturn(source);
		Mockito.when(accountService.get("2")).thenReturn(null);
		Assertions.assertThrows(ExecutionException.class, () -> txnExecutor.execute(txn).get());
	}

	@Test
	public void testExecuteNoSourceException() throws AccountServiceException {
		MoneyTransferTransaction txn = new MoneyTransferTransaction("1", "2", BigDecimal.valueOf(10));
		Account target = new Account("2", BigDecimal.valueOf(100));
		Mockito.when(accountService.get("1")).thenReturn(null);
		Mockito.when(accountService.get("2")).thenReturn(target);
		Assertions.assertThrows(ExecutionException.class, () -> txnExecutor.execute(txn).get());
	}

	@Test
	public void testExecuteExceptionInDebit() throws AccountServiceException, InterruptedException, ExecutionException {
		MoneyTransferTransaction txn = new MoneyTransferTransaction("1", "2", BigDecimal.valueOf(10));
		Account source = new Account("1", BigDecimal.valueOf(100));
		Account target = new Account("2", BigDecimal.valueOf(100));
		Mockito.when(accountService.get("1")).thenReturn(source);
		Mockito.when(accountService.get("2")).thenReturn(target);
		Mockito.when(accountService.debit(source, BigDecimal.valueOf(10)))
				.thenThrow(new AccountServiceException(AccountErrorReason.INVALIDACCOUNTDETAILS));
		CompletableFuture<MoneyTransferTransaction> future = txnExecutor.execute(txn);
		MoneyTransferTransaction actual = future.get();
		Assertions.assertEquals(TransferStatus.ERROR, actual.getStatus());
	}

	@Test
	public void testExecuteExceptionInCredit()
			throws AccountServiceException, InterruptedException, ExecutionException {
		MoneyTransferTransaction txn = new MoneyTransferTransaction("1", "2", BigDecimal.valueOf(10));
		Account source = new Account("1", BigDecimal.valueOf(100));
		Account target = new Account("2", BigDecimal.valueOf(100));
		Mockito.when(accountService.get("1")).thenReturn(source);
		Mockito.when(accountService.get("2")).thenReturn(target);
		Mockito.when(accountService.credit(target, BigDecimal.valueOf(10)))
				.thenThrow(new AccountServiceException(AccountErrorReason.INVALIDACCOUNTDETAILS));
		CompletableFuture<MoneyTransferTransaction> future = txnExecutor.execute(txn);
		MoneyTransferTransaction actual = future.get();
		Assertions.assertEquals(TransferStatus.ERROR, actual.getStatus());
		Assertions.assertTrue(actual.getRollbackSucess());
		Mockito.verify(accountService, Mockito.times(2)).credit(Mockito.any(), Mockito.any());
	}

	@Test
	public void testExecuteExceptionInCreditRollbackFailed()
			throws AccountServiceException, InterruptedException, ExecutionException {
		MoneyTransferTransaction txn = new MoneyTransferTransaction("1", "2", BigDecimal.valueOf(10));
		Account source = new Account("1", BigDecimal.valueOf(100));
		Account target = new Account("2", BigDecimal.valueOf(100));
		Mockito.when(accountService.get("1")).thenReturn(source);
		Mockito.when(accountService.get("2")).thenReturn(target);
		Mockito.when(accountService.credit(target, BigDecimal.valueOf(10)))
				.thenThrow(new AccountServiceException(AccountErrorReason.INVALIDACCOUNTDETAILS));

		Mockito.when(accountService.credit(source, BigDecimal.valueOf(10)))
				.thenThrow(new AccountServiceException(AccountErrorReason.INVALIDACCOUNTDETAILS));
		CompletableFuture<MoneyTransferTransaction> future = txnExecutor.execute(txn);
		MoneyTransferTransaction actual = future.get();
		Assertions.assertEquals(TransferStatus.ERROR, actual.getStatus());
		Assertions.assertFalse(actual.getRollbackSucess());
		Mockito.verify(accountService, Mockito.times(2)).credit(Mockito.any(), Mockito.any());
	}

	@Test
	public void testExecuteDebitCreditSuccess()
			throws AccountServiceException, InterruptedException, ExecutionException {
		MoneyTransferTransaction txn = new MoneyTransferTransaction("1", "2", BigDecimal.valueOf(10));
		Account source = new Account("1", BigDecimal.valueOf(100));
		Account target = new Account("2", BigDecimal.valueOf(100));
		Mockito.when(accountService.get("1")).thenReturn(source);
		Mockito.when(accountService.get("2")).thenReturn(target);
		CompletableFuture<MoneyTransferTransaction> future = txnExecutor.execute(txn);
		MoneyTransferTransaction actual = future.get();
		Assertions.assertEquals(TransferStatus.COMPLETED, actual.getStatus());
	}

	@Test
	public void testExecuteSourceNoLock() throws AccountServiceException, InterruptedException, ExecutionException {
		MoneyTransferTransaction txn = new MoneyTransferTransaction("1", "2", BigDecimal.valueOf(10));
		Account source = new Account("1", BigDecimal.valueOf(100));
		Runnable accountLocker = new Runnable() {

			@Override
			public void run() {
				source.lock();
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(accountLocker).start();
		Account target = new Account("2", BigDecimal.valueOf(100));
		Mockito.when(accountService.get("1")).thenReturn(source);
		Mockito.when(accountService.get("2")).thenReturn(target);
		CompletableFuture<MoneyTransferTransaction> future = txnExecutor.execute(txn);
		MoneyTransferTransaction actual = future.get();
		Assertions.assertEquals(TransferStatus.ERROR, actual.getStatus());
		Assertions.assertEquals(TransactionErrorReason.PENDING_TRANSACTION_ACCOUNT, actual.getReason());
	}

	@Test
	public void testExecuteTargetNoLock() throws AccountServiceException, InterruptedException, ExecutionException {
		MoneyTransferTransaction txn = new MoneyTransferTransaction("1", "2", BigDecimal.valueOf(10));
		Account source = new Account("1", BigDecimal.valueOf(100));
		Account target = new Account("2", BigDecimal.valueOf(100));
		Runnable accountLocker = new Runnable() {

			@Override
			public void run() {
				target.lock();
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(accountLocker).start();
		Mockito.when(accountService.get("1")).thenReturn(source);
		Mockito.when(accountService.get("2")).thenReturn(target);
		CompletableFuture<MoneyTransferTransaction> future = txnExecutor.execute(txn);
		MoneyTransferTransaction actual = future.get();
		Assertions.assertEquals(TransferStatus.ERROR, actual.getStatus());
		Assertions.assertEquals(TransactionErrorReason.PENDING_TRANSACTION_ACCOUNT, actual.getReason());
	}
}
