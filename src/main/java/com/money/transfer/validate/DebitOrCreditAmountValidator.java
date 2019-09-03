package com.money.transfer.validate;

import java.math.BigDecimal;
import java.util.Objects;

public class DebitOrCreditAmountValidator implements Validator<BigDecimal> {
	@Override
	public void validate(BigDecimal amount) throws ValidationException {
		if (Objects.isNull(amount) || BigDecimal.ZERO.compareTo(amount) >= 0) {
            throw new ValidationException("You can only issue positive amount.");
        }
	}

}
