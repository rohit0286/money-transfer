package com.money.transfer.repository;

import java.util.Collection;

import com.money.transfer.model.MoneyTransferTransaction;

public interface MoneyTransferTransactionRepository {

	MoneyTransferTransaction get(String id);

	MoneyTransferTransaction create(MoneyTransferTransaction transaction);

	Collection<MoneyTransferTransaction> all();

}
