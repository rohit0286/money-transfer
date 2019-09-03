package com.money.transfer.model;

import java.math.BigDecimal;
import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class MoneyTransferTransaction {

	private String id;
	private String source;
	private String target;
	private BigDecimal amount;
	private TransferStatus status;
	private TransactionErrorReason reason;
	private Boolean rollbackSucess;

	public MoneyTransferTransaction(String source, String target, BigDecimal amount) {
		if (StringUtils.isEmpty(source) || StringUtils.isEmpty(target)
				|| null == amount) {
			throw new IllegalArgumentException("Invalid parameters for transaction");
		}
		this.source = source;
		this.target = target;
		this.amount = amount;
		this.status = TransferStatus.PENDING;
	}

	public MoneyTransferTransaction() {
		this.status = TransferStatus.PENDING;
	}
	
	public MoneyTransferTransaction(String source, String target, double amount) {
		this.source = source;
		this.target = target;
		this.amount = BigDecimal.valueOf(amount);
		this.status = TransferStatus.PENDING;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getSource() {
		return source;
	}

	public String getTarget() {
		return target;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public TransferStatus getStatus() {
		return status;
	}
	
	public void setStatus(TransferStatus status) {
		if (status == null) {
			throw new IllegalArgumentException("status cannot be null");
		} else {
			this.status = status;
		}
	}

	@Override
    public String toString() {
    	return String.format("MoneyTransferTransaction [%s] from %s to %s is %s ",
    			id, source, target, status);
    }

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
        if (null == other || getClass() != other.getClass()) {
        	return false;
        }
        MoneyTransferTransaction txn = (MoneyTransferTransaction) other;
        return id.equals(txn.id);
	}
	
	public void setReason(TransactionErrorReason reason) {
		this.reason = reason;
	}
	public TransactionErrorReason getReason() {
		return reason;
	}
	
	public void setRollbackSucess(boolean rollbackSucess) {
		this.rollbackSucess = rollbackSucess;
	}
	
	public boolean	getRollbackSucess(){
		return BooleanUtils.toBoolean(rollbackSucess);
	}
}
