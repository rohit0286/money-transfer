package com.money.transfer.service.exceptions;

public class MoneyTransferTransactionExecutorException extends RuntimeException {

	public FailureType type;
	public MoneyTransferTransactionExecutorException(String message, FailureType type) {
		super(message);
		this.type = type;
	}
	
	public FailureType getType() {
		return type;
	}
}
