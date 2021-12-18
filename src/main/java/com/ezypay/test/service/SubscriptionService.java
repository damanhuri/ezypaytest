package com.ezypay.test.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ezypay.test.domain.SubsPayload;

@Service
public class SubscriptionService {

    Logger logger = LoggerFactory.getLogger(SubscriptionService.class);
	
	private static final int DAILY = 1;
	private static final int WEEKLY = 2;
	private static final int MONTHLY = 3;
	
	private static final int MAX_SUBS_MONTHS = 3;
	
	public List<LocalDate> getInvoices(SubsPayload sub) throws Exception {
		
		LocalDate paramFrom = sub.getFromDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate paramTo = sub.getToDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate maxEndDate = paramFrom.plusMonths(MAX_SUBS_MONTHS).plusDays(1); // set the limit date of the subscription
		
		if (sub.getType() == DAILY) {
		
			return getDailyInvoiceDates(paramFrom, paramTo);

		}
		else if (sub.getType() == WEEKLY) {
			
			return getWeeklyInvoiceDates(sub.getFromDate(), sub.getToDate(), sub.getDayOfWeek(), maxEndDate);
			
		}
		else if (sub.getType() == MONTHLY) {
			
			return getMonthlyInvoiceDates(paramFrom, paramTo, sub.getDayOfMonth(), maxEndDate);
		}
		else {
			throw new Exception("Invalid Subscription Type!");
		}
		
	}
	
	private List<LocalDate> getDailyInvoiceDates(LocalDate from, LocalDate to) {
		long numOfDaysBetween = ChronoUnit.DAYS.between(from, to);
		return IntStream.iterate(1, i -> i + 1)
			      .limit(numOfDaysBetween+1) // to include last day of subscription
			      .mapToObj(i -> from.plusDays(i))
			      .filter(isBefore -> from.isBefore(to))
			      .collect(Collectors.toList()); 
	}
	
	private List<LocalDate> getWeeklyInvoiceDates(Date from, Date to, int dayOfWeek, LocalDate maxEndDate) {
		
		LocalDate startDate = calculateDayOfWeek(from, dayOfWeek, maxEndDate).plusDays(1); // calculate invoice start date of the week
		LocalDate endDate = calculateDayOfWeek(to, 7, maxEndDate).plusDays(1); // calculate invoice end date of the week, limit by max month

		long numOfWeeksBetween = ChronoUnit.WEEKS.between(startDate, endDate);

		return IntStream.iterate(0, i -> i + 1)
			      .limit(numOfWeeksBetween+1) // to include last week of subscription
			      .mapToObj(i -> startDate.plusWeeks(i))
			      .filter(isBefore -> startDate.isBefore(endDate))
			      .collect(Collectors.toList()); 
	}
	
	private List<LocalDate> getMonthlyInvoiceDates(LocalDate from, LocalDate to, int dayOfMonth, LocalDate maxEndDate) {
		
		LocalDate startDate = calculateDayOfMonth(from, dayOfMonth).plusDays(1); // calculate invoice start date of the month
		LocalDate endDate = to.isAfter(maxEndDate) ? maxEndDate : to.plusDays(1); // set limit to max months or requested end date
		
		return IntStream.iterate(0, i -> i + 1)
			      .limit(MAX_SUBS_MONTHS)
			      .mapToObj(i -> startDate.plusMonths(i))
			      .filter(isBefore -> startDate.isBefore(endDate))
			      .collect(Collectors.toList()); 
	}
	
	private LocalDate calculateDayOfWeek(Date date, int invDayOfWeek, LocalDate maxEndDate) {

		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate calcDate = localDate.isAfter(maxEndDate) ? maxEndDate : localDate;
		
		invDayOfWeek = invDayOfWeek == 0 ? localDate.getDayOfWeek().getValue() : invDayOfWeek;
		
		int dayOfWeekDiff = invDayOfWeek - calcDate.getDayOfWeek().getValue();
		if (dayOfWeekDiff < 0) {
			return calcDate.plusWeeks(1).minusDays(Math.abs(dayOfWeekDiff)); // to include last day of subscription
		}
		else {
			return calcDate.plusDays(dayOfWeekDiff); // to include last day of subscription
		}
		
	}
	
	private LocalDate calculateDayOfMonth(LocalDate localDate, int invDayOfMonth) {

		invDayOfMonth = invDayOfMonth == 0 ? localDate.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth() : invDayOfMonth;
		
		int dayOfMonthDiff = invDayOfMonth - localDate.getDayOfMonth();
		
		if (dayOfMonthDiff < 0) {
			return localDate.plusMonths(1).withDayOfMonth(invDayOfMonth);
		}
		else {
			return localDate.withDayOfMonth(invDayOfMonth);
		}
	}

}
