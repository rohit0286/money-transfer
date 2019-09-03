package com.money.transfer.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class Account {

	private String id;
	private BigDecimal balance;
	private ReentrantLock writeLock;

	public Account(String id, BigDecimal balance) {
		this.id = id;
		this.balance = balance;
		writeLock = new ReentrantLock();
	}

	public String getId() {
		return id;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		if (BigDecimal.ZERO.compareTo(balance) > 0) {
			throw new IllegalArgumentException("Balance cannot be negative.");
		}
		if (writeLock.isHeldByCurrentThread()) {
			this.balance = balance;
		} else {
			throw new IllegalMonitorStateException("Account is not locked by current transaction.");
		}
	}

	@Override
	public String toString() {
		return "Account{" + "id=" + id + ", balance=" + balance + '}';
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (null == other || getClass() != other.getClass())
			return false;
		Account account = (Account) other;
		return id.equals(account.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public boolean lock() {
		return writeLock.tryLock();
	}

	public void release() {
		if (writeLock.isHeldByCurrentThread()) {
			writeLock.unlock();
		}
	}
}
