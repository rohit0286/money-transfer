package com.money.transfer.repository.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.money.transfer.model.MoneyTransferTransaction;
import com.money.transfer.repository.MoneyTransferTransactionRepository;

public class MoneyTransferTransactionRepositoryImpl implements MoneyTransferTransactionRepository {

	private Map<String, MoneyTransferTransaction> transactions;

	public MoneyTransferTransactionRepositoryImpl() {
		this.transactions = new HashMap<>();
	}

	@Override
	public MoneyTransferTransaction get(String id) {
		return transactions.get(id);
	}

	@Override
	public MoneyTransferTransaction create(MoneyTransferTransaction transaction) {
		String txnId = UUID.randomUUID().toString();
    	transaction.setId(txnId);
		transactions.put(transaction.getId(), transaction);
		return get(transaction.getId());
	}
	
	@Override
	public Collection<MoneyTransferTransaction> all() {
		return transactions.values();
	}

}
