package com.money.transfer.service;

import java.util.Collection;

import com.money.transfer.model.MoneyTransferTransaction;
import com.money.transfer.service.exceptions.MoneyTransferTransactionServiceException;

public interface MoneyTransferTransactionService {

	MoneyTransferTransaction create(MoneyTransferTransaction transaction) throws MoneyTransferTransactionServiceException;

	MoneyTransferTransaction process(String transactionId) throws MoneyTransferTransactionServiceException;
	
	MoneyTransferTransaction get(String transactionId);

	Collection<MoneyTransferTransaction> all();

}
