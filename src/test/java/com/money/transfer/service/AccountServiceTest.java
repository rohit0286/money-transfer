package com.money.transfer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.money.transfer.model.Account;
import com.money.transfer.repository.AccountRepository;
import com.money.transfer.repository.exceptions.DuplicateAccountException;
import com.money.transfer.service.exceptions.AccountServiceException;
import com.money.transfer.service.impl.AccountServiceImpl;
import com.money.transfer.validate.ValidationException;
import com.money.transfer.validate.Validator;

@RunWith(JUnitPlatform.class)
@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
	@Mock
	AccountRepository accountRepository;
	@Mock
	Validator<Account> accountValidator;
	@Mock
	Validator<BigDecimal> amountValidator;
	
	AccountService accountService;
	
	@BeforeEach
	public void before() {
		accountService = new AccountServiceImpl(accountRepository, accountValidator, amountValidator);
	}

	@Test
    void testDebitInvalidAmount() throws AccountServiceException, ValidationException {
		Account account = new Account("1", BigDecimal.valueOf(100));
		Mockito.doThrow(new ValidationException()).when(amountValidator).validate(BigDecimal.valueOf(0));
		Assertions.assertThrows(AccountServiceException.class,
				() -> accountService.debit(account, BigDecimal.valueOf(0)));
		
    }
	
	@Test
    void testDebitLowBalance() throws DuplicateAccountException, AccountServiceException {
		Account account = new Account("1", BigDecimal.valueOf(9));
		Assertions.assertThrows(AccountServiceException.class,
				() -> accountService.debit(account, BigDecimal.valueOf(10)));
		
    }
	
	@Test
    void testDebitNoLock() throws AccountServiceException {
		Account account = new Account("1", BigDecimal.valueOf(100));
		Runnable accountLocker = new Runnable() {
			
			@Override
			public void run() {
				account.lock();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(accountLocker).start();
		Assertions.assertThrows(AccountServiceException.class,
				() -> accountService.debit(account, BigDecimal.valueOf(10)));
		
    }
	
	@Test
    void testDebit() throws AccountServiceException {
		Account account = new Account("1", BigDecimal.valueOf(100));
		account.lock();
	    accountService.debit(account, BigDecimal.valueOf(10));
	    Assertions.assertEquals(BigDecimal.valueOf(90), account.getBalance());
		
    }
	
	@Test
    void testCredit() throws AccountServiceException {
		Account account = new Account("1", BigDecimal.valueOf(100));
		account.lock();
	    accountService.credit(account, BigDecimal.valueOf(10));
	    Assertions.assertEquals(BigDecimal.valueOf(110), account.getBalance());
		
    }
	
	@Test
    void testCreditNoLock() throws AccountServiceException {
		Account account = new Account("1", BigDecimal.valueOf(100));
		Runnable accountLocker = new Runnable() {
			
			@Override
			public void run() {
				account.lock();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(accountLocker).start();
		Assertions.assertThrows(AccountServiceException.class,
				() -> accountService.credit(account, BigDecimal.valueOf(10)));
		
    }
	
	@Test
    void testCreditInvalidAmount() throws AccountServiceException, ValidationException {
		Account account = new Account("1", BigDecimal.valueOf(100));
		Mockito.doThrow(new ValidationException()).when(amountValidator).validate(BigDecimal.valueOf(0));
		Assertions.assertThrows(AccountServiceException.class,
				() -> accountService.credit(account, BigDecimal.valueOf(0)));
		
    }
	
	@Test
    void testGetById() {
		Mockito.when(accountRepository.get("test")).thenReturn(new Account("test", BigDecimal.valueOf(100)));
        assertEquals(BigDecimal.valueOf(100), accountService.get("test").getBalance());
    }
	
	@Test
    void testAll() {
		List<Account> accounts = new ArrayList<>();
		accounts.add(new Account("test", BigDecimal.valueOf(100)));
		Mockito.when(accountRepository.all()).thenReturn(accounts);
        assertEquals(1, accountService.all().size());
    }
	
	@Test
    void testCreateInvalidAccount() throws AccountServiceException, ValidationException {
		Mockito.doThrow(new ValidationException()).when(accountValidator).validate(Mockito.any());
		Assertions.assertThrows(AccountServiceException.class,
				() -> accountService.create(BigDecimal.valueOf(10)));
		
    }
	
	@Test
    void testCreateDuplicateAccount() throws AccountServiceException, DuplicateAccountException {
		Mockito.doThrow(new DuplicateAccountException()).when(accountRepository).create(Mockito.any());
		Assertions.assertThrows(AccountServiceException.class,
				() -> accountService.create(BigDecimal.valueOf(10)));
    }
	
	@Test
    void testCreateSucess() throws AccountServiceException, DuplicateAccountException {
		Mockito.when(accountRepository.create(Mockito.any())).thenReturn(new Account("test", BigDecimal.valueOf(10)));
		Account actual = accountService.create(BigDecimal.valueOf(10));
		Assertions.assertEquals("test", actual.getId());
    }
}
