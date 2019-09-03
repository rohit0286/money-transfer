package com.money.transfer.service.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;

import com.money.transfer.model.Account;
import com.money.transfer.repository.AccountRepository;
import com.money.transfer.repository.exceptions.DuplicateAccountException;
import com.money.transfer.service.AccountService;
import com.money.transfer.service.exceptions.AccountErrorReason;
import com.money.transfer.service.exceptions.AccountServiceException;
import com.money.transfer.validate.AccountBalanceForDebitValidator;
import com.money.transfer.validate.DebitOrCreditAmountValidator;
import com.money.transfer.validate.ValidationException;
import com.money.transfer.validate.Validator;

public class AccountServiceImpl implements AccountService {
	private AccountRepository accountRepository;
	private Validator<Account> newAccountValidator;
	private Validator<BigDecimal> accountAmountValidator;

	public AccountServiceImpl(AccountRepository accountRepository,
			Validator<Account> newAccountValidator, Validator<BigDecimal> accountAmountValidator) {
		this.accountRepository = accountRepository;
		this.newAccountValidator = newAccountValidator;
		this.accountAmountValidator = accountAmountValidator;
	}

	@Override
	public Account debit(Account account, BigDecimal amount) throws AccountServiceException {
		try {
			accountAmountValidator.validate(amount);
		} catch (ValidationException e) {
			throw new AccountServiceException(AccountErrorReason.INVALIDDEBITAMOUNT, e);
		}
		Validator<BigDecimal> validator = new AccountBalanceForDebitValidator(account);
		try {
			validator.validate(amount);
		} catch (ValidationException e) {
			throw new AccountServiceException(AccountErrorReason.INSUFFICIENTAMOUNT, e);
		}
		try {
			if (account.lock()) {
				account.setBalance(account.getBalance().subtract(amount));
			} else {
				throw new AccountServiceException(AccountErrorReason.ACCOUNTLOCKNOTACQUIRED);
			}
		} finally {
			account.release();
		}
		return account;
	}

	@Override
	public Account credit(Account account, BigDecimal amount) throws AccountServiceException {
		try {
			accountAmountValidator.validate(amount);
		} catch (ValidationException e) {
			throw new AccountServiceException(AccountErrorReason.INVALIDCREDITAMOUNT, e);
		}
		try {
			if (account.lock()) {
				account.setBalance(account.getBalance().add(amount));
			} else {
				throw new AccountServiceException(AccountErrorReason.ACCOUNTLOCKNOTACQUIRED);
			}
		} finally {
			account.release();
		}

		return account;
	}

	@Override
	public Account get(String id) {
		return accountRepository.get(id);
	}

	@Override
	public Collection<Account> all() {
		return accountRepository.all();
	}

	@Override
	public Account create(BigDecimal balance) throws AccountServiceException {
		String id = UUID.randomUUID().toString();
		Account account = new Account(id, balance);
		try {
			newAccountValidator.validate(account);
		} catch (ValidationException e) {
			throw new AccountServiceException(AccountErrorReason.INVALIDACCOUNTDETAILS, e);
		}
		try {
			return accountRepository.create(account);
		} catch (DuplicateAccountException e) {
			throw new AccountServiceException(AccountErrorReason.DUPLICATEACCOUNTS, e);
		}
	}

}
