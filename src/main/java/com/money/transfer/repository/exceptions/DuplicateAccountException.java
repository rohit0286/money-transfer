package com.money.transfer.repository.exceptions;

import javax.ws.rs.ext.Provider;

@Provider
public class DuplicateAccountException extends Exception {

    public DuplicateAccountException(String accountId) {
        super("Account with ID:" + accountId + " already exists. Duplicates are not allowed.");
    }

    public DuplicateAccountException() {
        super();
    }

}
