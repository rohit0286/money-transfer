package com.money.transfer.service.exceptions;

public class AccountServiceException extends Exception {
	AccountErrorReason reason;

	public AccountServiceException(AccountErrorReason reason, Throwable cause) {
		super(cause);
		if (null == reason) {
			throw new IllegalArgumentException("REASON is required.");
		}
		this.reason = reason;
	}

	public AccountServiceException(AccountErrorReason reason) {
		super();
		if (null == reason) {
			throw new IllegalArgumentException("REASON is required.");
		}
		this.reason = reason;
	}

	public AccountErrorReason getReason() {
		return reason;
	}

}
