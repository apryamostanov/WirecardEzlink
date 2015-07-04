package com.wirecard.ezlink.model;

import java.math.BigDecimal;

public class ReceiptRequest {
	private int id;
	private String merchantNo;
	private String orderNo;
	private String merchantRefNo;
	private String can;
	private BigDecimal amount;
	private String receiptData;
	private String errorCode;
	private String errorDescript;
	

	public ReceiptRequest(String merchantNo, String orderNo,
			String merchantRefNo, String can, BigDecimal amount,
			String receiptData, String errorCode, String errorDescript) {
		this.merchantNo = merchantNo;
		this.orderNo = orderNo;
		this.merchantRefNo = merchantRefNo;
		this.can = can;
		this.amount = amount;
		this.receiptData = receiptData;
		this.errorCode = errorCode;
		this.errorDescript = errorDescript;
	}

	public ReceiptRequest() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMerchantNo() {
		return merchantNo;
	}

	public void setMerchantNo(String merchantNo) {
		this.merchantNo = merchantNo;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getMerchantRefNo() {
		return merchantRefNo;
	}

	public void setMerchantRefNo(String merchantRefNo) {
		this.merchantRefNo = merchantRefNo;
	}

	public String getCan() {
		return can;
	}

	public void setCan(String can) {
		this.can = can;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getReceiptData() {
		return receiptData;
	}

	public void setReceiptData(String receiptData) {
		this.receiptData = receiptData;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDescript() {
		return errorDescript;
	}

	public void setErrorDescript(String errorDescript) {
		this.errorDescript = errorDescript;
	}

	@Override
	public String toString() {
		return "ReceiptRequest [id=" + id + ", merchantNo=" + merchantNo
				+ ", orderNo=" + orderNo + ", merchantRefNo=" + merchantRefNo
				+ ", can=" + can + ", amount=" + amount + ", receiptData="
				+ receiptData + ", errorCode=" + errorCode + ", errorDescript=" + errorDescript + "]";
	}
	
}
