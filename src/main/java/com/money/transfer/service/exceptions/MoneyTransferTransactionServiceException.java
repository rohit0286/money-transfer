package com.money.transfer.service.exceptions;

public class MoneyTransferTransactionServiceException extends Exception {

	private FailureType type;

	public MoneyTransferTransactionServiceException() {
	}

	public MoneyTransferTransactionServiceException(FailureType type, Throwable cause) {
		super( cause.getMessage(), cause);
		if (type == null) {
			throw new IllegalArgumentException("Failure Type is required.");
		}
		this.type = type;
	}
	
	public MoneyTransferTransactionServiceException(FailureType type, String message) {
		super(message);
		if (type == null) {
			throw new IllegalArgumentException("Failure Type is required.");
		}
		this.type = type;

	}

	public FailureType getType() {
		return type;
	}
	

}
