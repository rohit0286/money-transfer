package com.money.transfer.validate;

import java.math.BigDecimal;

import com.money.transfer.model.Account;

public class AccountBalanceForDebitValidator implements Validator<BigDecimal> {

	Account account;
	public AccountBalanceForDebitValidator(Account account) {
		if (null == account) {
			throw new IllegalArgumentException("Amount cannot null.");
		}
		this.account = account;
	}

	@Override
	public void validate(BigDecimal amount) throws ValidationException {
		if (account.getBalance().compareTo(amount) < 0) {
			throw new ValidationException("Insufficient Balance");
		}
		
	}

}
