package com.ezypay.test.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Subscription {
	
	private BigDecimal amount;
	private int type;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	private List<Date> invoiceDates;
	
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public List<Date> getInvoiceDates() {
		return invoiceDates;
	}
	public void setInvoiceDates(List<Date> invoiceDates) {
		this.invoiceDates = invoiceDates;
	}

}
