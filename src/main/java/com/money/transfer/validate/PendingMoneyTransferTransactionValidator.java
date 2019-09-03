package com.money.transfer.validate;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.money.transfer.model.MoneyTransferTransaction;
import com.money.transfer.model.TransferStatus;
import com.money.transfer.repository.AccountRepository;

public class PendingMoneyTransferTransactionValidator implements Validator<MoneyTransferTransaction> {

	private AccountRepository accountRepository;
	    
	public PendingMoneyTransferTransactionValidator(AccountRepository repository) {
        this.accountRepository = repository;
    }

	@Override
	public void validate(MoneyTransferTransaction transaction) throws ValidationException {
		if (StringUtils.isNotBlank(transaction.getId())) {
			throw new ValidationException("New transaction shouldn't have id.");
		}
		
		if(!transaction.getStatus().equals(TransferStatus.PENDING)) {
			throw new ValidationException("New transaction should be in Pending state.");
		}
		
		if (transaction.getAmount() == null 
				|| transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new ValidationException("Invalid amount for transaction");
		}
		
		if ( StringUtils.isBlank(transaction.getSource()) 
				|| null == accountRepository.get(transaction.getSource())) {
			throw new ValidationException("Invalid source account provided.");
		}
		
		if (StringUtils.isBlank(transaction.getTarget()) 
				|| null == accountRepository.get(transaction.getTarget())) {
			throw new ValidationException("Invalid target account provided.");
		}
		
		if (transaction.getSource().equals(transaction.getTarget())) {
			throw new ValidationException("Source and target account cannot be same.");
		}
		
	}

}
