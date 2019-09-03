package com.money.transfer.repository;

import java.util.Collection;

import com.money.transfer.model.Account;
import com.money.transfer.repository.exceptions.DuplicateAccountException;

public interface AccountRepository {

	Account get(String id);

	Collection<Account> all();

	Account create(Account account) throws DuplicateAccountException;

}
