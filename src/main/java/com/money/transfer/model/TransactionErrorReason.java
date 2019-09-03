package com.money.transfer.model;

public enum TransactionErrorReason {
  INSUFFICIENT_BALANCE("E100",
		  "Money Transfer can't be performed due to lack of funds on the account."),
  PENDING_TRANSACTION_ACCOUNT("E101",
		  "Another transaction pending on either Account"),
  INVALID_AMOUNT_FOR_TXN("E102", "Invalid amount provided for transaction"),
  UNKNOWN("E103", "Unknown error has occured.");
  private String code;
  private String reason;
  TransactionErrorReason(String code, String reason) {
	  this.code = code;
	  this.reason = reason;
  }
  public String getCode() {
	return code;
  }
  public String getReason() {
	return reason;
  }
  
  
}
