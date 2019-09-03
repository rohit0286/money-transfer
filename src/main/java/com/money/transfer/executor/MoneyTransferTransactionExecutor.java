package com.money.transfer.executor;

import java.util.concurrent.CompletableFuture;

import com.money.transfer.model.MoneyTransferTransaction;

public interface MoneyTransferTransactionExecutor {
	public CompletableFuture<MoneyTransferTransaction> execute(MoneyTransferTransaction transaction);
}
