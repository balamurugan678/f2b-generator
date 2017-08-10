package com.gcp.poc.f2b.generator.helpers;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateHelper {
    public LocalDate AddWorkingDays(LocalDate date, int days){
        int workingDays = 0;
        return date.plusDays(workingDays);
    }

    public String printDate(LocalDateTime dateTime) {
        return dateTime.getYear()+"-"+String.format("%02d",dateTime.getMonthValue())+"-"+String.format("%02d",dateTime.getDayOfMonth());
    }

    public LocalDateTime addDays(LocalDateTime dateTime, int days) {
        return dateTime.plusDays(days);
    }

    public LocalDateTime addMonths(LocalDateTime dateTime, int months) {
        return dateTime.plusMonths(months);
    }

    public LocalDateTime addYears(LocalDateTime dateTime, int years) {
        return dateTime.plusYears(years);
    }
}
