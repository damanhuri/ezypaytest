package com.ezypay.test.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ezypay.test.domain.SubsPayload;
import com.ezypay.test.domain.Subscription;
import com.ezypay.test.service.SubscriptionService;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {
		
	@Autowired
	private SubscriptionService subService;
	
	@PostMapping
	public ResponseEntity<Subscription> create(@RequestBody SubsPayload payload) throws Exception {
		List<LocalDate> invoiceLocalDates = subService.getInvoices(payload);
		
		List<Date> invoiceDates = invoiceLocalDates.stream().map(d -> Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant())).collect(Collectors.toList());
		
		Subscription sub = new Subscription();
		sub.setAmount(payload.getAmount());
		sub.setType(payload.getType());
		sub.setInvoiceDates(invoiceDates);
		
        return new ResponseEntity<>(sub, HttpStatus.CREATED);
	}
	
}
