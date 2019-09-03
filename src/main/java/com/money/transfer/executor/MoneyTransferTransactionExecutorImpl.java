package com.money.transfer.executor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.money.transfer.model.Account;
import com.money.transfer.model.MoneyTransferTransaction;
import com.money.transfer.model.TransactionErrorReason;
import com.money.transfer.model.TransferStatus;
import com.money.transfer.service.AccountService;
import com.money.transfer.service.exceptions.AccountServiceException;
import com.money.transfer.service.exceptions.FailureType;
import com.money.transfer.service.exceptions.MoneyTransferTransactionExecutorException;

public class MoneyTransferTransactionExecutorImpl implements MoneyTransferTransactionExecutor {
    private static Logger log = LogManager.getLogger(MoneyTransferTransactionExecutor.class.getName());

	private AccountService accountService;
	private ExecutorService executor;

	public MoneyTransferTransactionExecutorImpl(AccountService accountService, ExecutorService executor) {
		this.accountService = accountService;
		this.executor = executor;
	}

	@Override
	public CompletableFuture<MoneyTransferTransaction> execute(MoneyTransferTransaction transaction) {
		return CompletableFuture.supplyAsync(() -> this.process(transaction),
				executor);

	}

	private MoneyTransferTransaction process(MoneyTransferTransaction transaction) {
		if (null == transaction) {
			throw new MoneyTransferTransactionExecutorException("MoneyTransferTransaction is required.", FailureType.VALIDATTION);
		} else if (transaction.getStatus().equals(TransferStatus.COMPLETED)) {
			throw new MoneyTransferTransactionExecutorException("Invalid state of of MoneyTransferTransaction", FailureType.VALIDATTION);
		}
		Account source = accountService.get(transaction.getSource());
		Account target = accountService.get(transaction.getTarget());
		if (source == null || target == null) {
			throw new MoneyTransferTransactionExecutorException("Invalid source or target account.", FailureType.VALIDATTION);
		}
		Account low = (target.hashCode() < source.hashCode() ? target : source);
		Account high = (target.hashCode() < source.hashCode() ? source : target);
		boolean lowLocked = low.lock();
		try {
			if (lowLocked) {
				boolean highLocked = high.lock();
				if (highLocked) {
					this.debit(source, transaction);
					log.info("Debited for transaction {}", transaction );
					if (transaction.getStatus().equals(TransferStatus.ERROR)) {
						return transaction;
					}
					this.credit(target, transaction);
					log.info("Credited for transaction {}", transaction );
					if (transaction.getStatus().equals(TransferStatus.ERROR)) {
						try {
							accountService.credit(source, transaction.getAmount());
							log.info("Rolledback succss for transaction {}", transaction );
							transaction.setRollbackSucess(true);
						} catch (AccountServiceException e) {
							transaction.setRollbackSucess(false);
						}
						return transaction;
					} else {
						transaction.setStatus(TransferStatus.COMPLETED);
					}

				} else {
					transaction.setReason(TransactionErrorReason.PENDING_TRANSACTION_ACCOUNT);
					transaction.setStatus(TransferStatus.ERROR);
					return transaction;
				}

			} else {
				transaction.setReason(TransactionErrorReason.PENDING_TRANSACTION_ACCOUNT);
				transaction.setStatus(TransferStatus.ERROR);
				return transaction;
			}
		} finally {
			high.release();
			low.release();
		}
		return transaction;
	}

	private void debit(Account account, MoneyTransferTransaction transaction) {
		try {
			accountService.debit(account, transaction.getAmount());
		} catch (AccountServiceException e) {
			switch (e.getReason()) {
			case INVALIDDEBITAMOUNT:
				transaction.setReason(TransactionErrorReason.INVALID_AMOUNT_FOR_TXN);
				transaction.setStatus(TransferStatus.ERROR);
				break;
			case INSUFFICIENTAMOUNT:
				transaction.setReason(TransactionErrorReason.INSUFFICIENT_BALANCE);
				transaction.setStatus(TransferStatus.ERROR);
				break;
			case ACCOUNTLOCKNOTACQUIRED:
				transaction.setReason(TransactionErrorReason.PENDING_TRANSACTION_ACCOUNT);
				transaction.setStatus(TransferStatus.ERROR);
				break;
			default:
				transaction.setReason(TransactionErrorReason.UNKNOWN);
				transaction.setStatus(TransferStatus.ERROR);
			}
		}
	}

	private void credit(Account account, MoneyTransferTransaction transaction) {
		try {
			accountService.credit(account, transaction.getAmount());
		} catch (AccountServiceException e) {
			switch (e.getReason()) {
			case INVALIDCREDITAMOUNT:
				transaction.setReason(TransactionErrorReason.INVALID_AMOUNT_FOR_TXN);
				transaction.setStatus(TransferStatus.ERROR);
				break;
			case ACCOUNTLOCKNOTACQUIRED:
				transaction.setReason(TransactionErrorReason.PENDING_TRANSACTION_ACCOUNT);
				transaction.setStatus(TransferStatus.ERROR);
				break;
			default:
				transaction.setReason(TransactionErrorReason.UNKNOWN);
				transaction.setStatus(TransferStatus.ERROR);
			}
		}
	}

}
