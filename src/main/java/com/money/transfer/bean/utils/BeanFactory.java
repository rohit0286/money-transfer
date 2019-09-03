package com.money.transfer.bean.utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.money.transfer.executor.MoneyTransferTransactionExecutor;
import com.money.transfer.executor.MoneyTransferTransactionExecutorImpl;
import com.money.transfer.model.Account;
import com.money.transfer.model.MoneyTransferTransaction;
import com.money.transfer.repository.AccountRepository;
import com.money.transfer.repository.MoneyTransferTransactionRepository;
import com.money.transfer.repository.impl.AccountRepositoryImpl;
import com.money.transfer.repository.impl.MoneyTransferTransactionRepositoryImpl;
import com.money.transfer.service.AccountService;
import com.money.transfer.service.MoneyTransferTransactionService;
import com.money.transfer.service.impl.AccountServiceImpl;
import com.money.transfer.service.impl.MoneyTransferTransactionServiceImpl;
import com.money.transfer.validate.DebitOrCreditAmountValidator;
import com.money.transfer.validate.NewAccountValidator;
import com.money.transfer.validate.PendingMoneyTransferTransactionValidator;
import com.money.transfer.validate.Validator;

public class BeanFactory {
	private  Map<Class<?>, Object> beans;
	private static final BeanFactory FACTORY = new BeanFactory();
	
	private BeanFactory() {
		beans = new HashMap<>();
		initialize();
	}
	
	public static final BeanFactory get() {
		return FACTORY;
	}
	
	public Object getBean(Class<?> clazz) {
		return beans.get(clazz);
	}
	
	public void initialize() {
		initializeRepositories();
		initializeValidators();
		initializePreServices();
		initializeExecutors();
		initializeOtherServices();
	}
	
	private void initializeRepositories() {
		beans.put(AccountRepository.class, new AccountRepositoryImpl());
		beans.put(MoneyTransferTransactionRepository.class, new MoneyTransferTransactionRepositoryImpl());
	}
	
	private void initializeValidators() {
		beans.put(NewAccountValidator.class, new NewAccountValidator());
		beans.put(PendingMoneyTransferTransactionValidator.class,
				new PendingMoneyTransferTransactionValidator((AccountRepository) beans.get(AccountRepository.class)));
		beans.put(DebitOrCreditAmountValidator.class, new DebitOrCreditAmountValidator());
	}
	
	private void initializePreServices() {
		beans.put(AccountService.class, new AccountServiceImpl((AccountRepository) beans.get(AccountRepository.class),
				(Validator<Account>) beans.get(NewAccountValidator.class),
				(Validator<BigDecimal>) beans.get(DebitOrCreditAmountValidator.class)));
	}
	
	private void initializeExecutors() {
		ExecutorService transferThreadPoolExecutor = new ThreadPoolExecutor(5, 10, 5000, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());
		beans.put(MoneyTransferTransactionExecutor.class, new MoneyTransferTransactionExecutorImpl(
				(AccountService) beans.get(AccountService.class), transferThreadPoolExecutor));
	}
	
	
	private void initializeOtherServices() {
		beans.put(MoneyTransferTransactionService.class, new MoneyTransferTransactionServiceImpl(
				(MoneyTransferTransactionRepository) beans.get(MoneyTransferTransactionRepository.class),
				(Validator<MoneyTransferTransaction>) beans.get(PendingMoneyTransferTransactionValidator.class), 
				(MoneyTransferTransactionExecutor) beans.get(MoneyTransferTransactionExecutor.class)));
	}
}
