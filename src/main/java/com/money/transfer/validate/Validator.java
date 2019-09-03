package com.money.transfer.validate;

public interface Validator<T> {

	public void validate(T input) throws ValidationException;
}
