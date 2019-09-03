package com.money.transfer.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.money.transfer.model.Account;
import com.money.transfer.repository.exceptions.DuplicateAccountException;
import com.money.transfer.repository.impl.AccountRepositoryImpl;

@RunWith(JUnitPlatform.class)
class AccountRepositoryTest {

    @Test
    void testCreateAccountValid() throws DuplicateAccountException {
        Account expected = new Account("test", BigDecimal.valueOf(1000));
        AccountRepository repository = new AccountRepositoryImpl();
        Account actual = repository.create(expected);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBalance(), actual.getBalance());
    }

    @Test
    void testCreateDuplicateAccount() throws DuplicateAccountException {
    	Account original = new Account("test", BigDecimal.valueOf(1000));
        AccountRepository repository = new AccountRepositoryImpl();
        repository.create(original);

        Account duplicate = new Account("test", BigDecimal.valueOf(1002));

        assertThrows(DuplicateAccountException.class,
        		() -> repository.create(duplicate));
    }

    @Test
    void testGetById() throws DuplicateAccountException {
    	Account original = new Account("test", BigDecimal.valueOf(1000));
        AccountRepository repository = new AccountRepositoryImpl();
        Account originalActual = repository.create(original);
        Account retreived = repository.get("test");
        assertEquals(originalActual.getId(), retreived.getId());
    }
    
    @Test
    void testGetByIdNull() throws DuplicateAccountException {
    	Account original = new Account("test", BigDecimal.valueOf(1000));
        AccountRepository repository = new AccountRepositoryImpl();
        repository.create(original);
        Account retreived = repository.get(null);
        assertEquals(null, retreived);
    }
    
    @Test
    void testGetByIdNotExists() throws DuplicateAccountException {
    	Account original = new Account("test", BigDecimal.valueOf(1000));
        AccountRepository repository = new AccountRepositoryImpl();
        repository.create(original);
        Account retreived = repository.get("abc");
        assertEquals(null, retreived);
    }
    
    @Test
    void testGetAll() throws DuplicateAccountException {
    	Account one = new Account("test", BigDecimal.valueOf(1000));
    	Account two = new Account("test1", BigDecimal.valueOf(1002));
        AccountRepository repository = new AccountRepositoryImpl();
        repository.create(one);
        repository.create(two);
        Collection<Account> retreived = repository.all();
        assertEquals(2, retreived.size());
        List<String> ids = retreived.stream().map(Account::getId)
        		.collect(Collectors.toList());
        assertTrue(ids.contains("test"));
        assertTrue(ids.contains("test1"));
    }

    @Test
    void testGetAllNoAccount() throws DuplicateAccountException {
        AccountRepository repository = new AccountRepositoryImpl();
        Collection<Account> retreived = repository.all();
        assertEquals(0, retreived.size());
    }
}