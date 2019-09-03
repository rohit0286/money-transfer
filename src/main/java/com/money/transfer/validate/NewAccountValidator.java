package com.money.transfer.validate;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.money.transfer.model.Account;

public class NewAccountValidator implements Validator<Account> {

	@Override
	public void validate(Account input) throws ValidationException {
		if (null == input) {
			throw new IllegalArgumentException("Account is required to validate");
		} else if (StringUtils.isBlank(input.getId())) {
			throw new ValidationException("Account id is not provided.");
		} else if (null == input.getBalance() || input.getBalance().compareTo(BigDecimal.ZERO) < 0) {
			throw new ValidationException("Invalid balance in account.");
		}
		
	}

}
