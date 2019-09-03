package com.money.transfer.model;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
class AccountTest {

    @Test
    void testAccountSetBalanceNegative() {
        Account testAccount = new Account("test", BigDecimal.valueOf(100));
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> testAccount.setBalance(BigDecimal.valueOf(-100)));

    }
    
    @Test
    void testAccountSetBalanceNoLock() {
        Account testAccount = new Account("test", BigDecimal.valueOf(100));
        Runnable accountLocker = new Runnable() {

			@Override
			public void run() {
				testAccount.lock();
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(accountLocker).start();
		Assertions.assertThrows(IllegalMonitorStateException.class,
				() -> testAccount.setBalance(BigDecimal.valueOf(100)));

    }

    @Test
    void testAccountSetBalanceLock() {
        Account testAccount = new Account("test", BigDecimal.valueOf(100));
        testAccount.lock();
        testAccount.setBalance(BigDecimal.valueOf(90));
        Assertions.assertEquals(BigDecimal.valueOf(90), testAccount.getBalance());
    }
    
    @Test
    void testAccountRelease() {
        Account testAccount = new Account("test", BigDecimal.valueOf(100));
        Runnable accountLocker = new Runnable() {

			@Override
			public void run() {
				testAccount.lock();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(accountLocker).start();
		testAccount.release();
		Assertions.assertFalse(testAccount.lock());
    }
    @Test
    void testAccountLock() {
        Account testAccount = new Account("test", BigDecimal.valueOf(100));
		Assertions.assertTrue(testAccount.lock());
    }
    
    @Test
    void testAccountEquals() {
        Account testAccount = new Account("test", BigDecimal.valueOf(100));
        Account testTwo = new Account("test", BigDecimal.valueOf(102));
		Assertions.assertTrue(testAccount.equals(testTwo));
    }
    
    @Test
    void testAccountEqualsSameObject() {
        Account testAccount = new Account("test", BigDecimal.valueOf(100));
		Assertions.assertTrue(testAccount.equals(testAccount));
    }
    
    @Test
    void testAccountEqualsDifferentType() {
        Account testAccount = new Account("test", BigDecimal.valueOf(100));
		Assertions.assertFalse(testAccount.equals(new Object()));
    }
}