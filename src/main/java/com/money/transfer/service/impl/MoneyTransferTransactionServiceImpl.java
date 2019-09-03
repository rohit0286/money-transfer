package com.money.transfer.service.impl;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.money.transfer.executor.MoneyTransferTransactionExecutor;
import com.money.transfer.model.MoneyTransferTransaction;
import com.money.transfer.model.TransferStatus;
import com.money.transfer.repository.MoneyTransferTransactionRepository;
import com.money.transfer.service.MoneyTransferTransactionService;
import com.money.transfer.service.exceptions.FailureType;
import com.money.transfer.service.exceptions.MoneyTransferTransactionExecutorException;
import com.money.transfer.service.exceptions.MoneyTransferTransactionServiceException;
import com.money.transfer.validate.ValidationException;
import com.money.transfer.validate.Validator;

public class MoneyTransferTransactionServiceImpl implements MoneyTransferTransactionService {

    private MoneyTransferTransactionRepository transactionRepository;
    private Validator<MoneyTransferTransaction> transactionValidator;
    private MoneyTransferTransactionExecutor transactionExecutor;

    public MoneyTransferTransactionServiceImpl(MoneyTransferTransactionRepository transactionRepository,
    		Validator<MoneyTransferTransaction> transactionValidator,
    		MoneyTransferTransactionExecutor transactionExecutor) {
        this.transactionRepository = transactionRepository;
        this.transactionValidator = transactionValidator;
        this.transactionExecutor = transactionExecutor;
    }

    @Override
    public MoneyTransferTransaction create(MoneyTransferTransaction transaction) throws MoneyTransferTransactionServiceException {
    	try {
			transactionValidator.validate(transaction);
		} catch (ValidationException e) {
			throw new MoneyTransferTransactionServiceException(FailureType.VALIDATTION, e);
		}
    	return transactionRepository.create(transaction);
    }

    @Override
    public MoneyTransferTransaction process(String transactionId) throws MoneyTransferTransactionServiceException {
    	MoneyTransferTransaction txn =transactionRepository.get(transactionId);
    	if (null == txn) {
    		throw new MoneyTransferTransactionServiceException(FailureType.VALIDATTION, "Transaction does not exists.");
    	}
    	CompletableFuture<MoneyTransferTransaction> future = transactionExecutor.execute(txn);
    	try {
			return future.get();
    	} catch(MoneyTransferTransactionExecutorException e) {
    		throw new MoneyTransferTransactionServiceException(FailureType.EXECUTION, e.getMessage());
		} catch (InterruptedException | ExecutionException e) {
			txn.setStatus(TransferStatus.ERROR);
			if (e.getCause() instanceof MoneyTransferTransactionExecutorException) {
				throw new MoneyTransferTransactionServiceException(((MoneyTransferTransactionExecutorException)e.getCause()).getType(),
						e.getCause().getMessage());
			}
			throw new MoneyTransferTransactionServiceException(FailureType.EXECUTION, "An error occurred while executing transfer transaction.");
		}
    }

	@Override
	public MoneyTransferTransaction get(String transactionId) {
		return transactionRepository.get(transactionId);
	}
	
	@Override
	public Collection<MoneyTransferTransaction> all() {
		return transactionRepository.all();
	}

}
