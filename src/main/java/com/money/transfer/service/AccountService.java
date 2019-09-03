package com.money.transfer.service;

import java.math.BigDecimal;
import java.util.Collection;

import com.money.transfer.model.Account;
import com.money.transfer.service.exceptions.AccountServiceException;

public interface AccountService {
    public Account credit(Account account, BigDecimal amount) throws AccountServiceException;
    public Account debit(Account account, BigDecimal amount) throws AccountServiceException;
    public Account get(String id);
    public Collection<Account> all();
	Account create(BigDecimal balance) throws AccountServiceException;
}
