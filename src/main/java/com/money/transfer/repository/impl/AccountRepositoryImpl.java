package com.money.transfer.repository.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.money.transfer.model.Account;
import com.money.transfer.repository.AccountRepository;
import com.money.transfer.repository.exceptions.DuplicateAccountException;

public class AccountRepositoryImpl implements AccountRepository {

    private final Map<String, Account> accounts;

    public AccountRepositoryImpl() {
        this.accounts = new HashMap<String, Account>();
    }

    @Override
    public Account get(String id) {
        return accounts.get(id);
    }

    @Override
    public Collection<Account> all() {
        return accounts.values();
    }

    @Override
    public Account create(Account account) throws DuplicateAccountException {
        Account accountExists = accounts.putIfAbsent(account.getId(), account);
        if (accountExists != null) {
            throw new DuplicateAccountException(accountExists.getId());
        }

        return get(account.getId());
    }

}
