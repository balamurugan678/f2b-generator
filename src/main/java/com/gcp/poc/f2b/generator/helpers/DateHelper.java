package com.gcp.poc.f2b.generator.helpers;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateHelper {
    public LocalDate AddWorkingDays(LocalDate date, int days){
        int workingDays = 0;
        return date.plusDays(workingDays);
    }

    public String PrintDate(LocalDateTime dateTime) {
        return dateTime.getYear()+"-"+dateTime.getMonthValue()+"-"+dateTime.getDayOfMonth();
    }

    public LocalDateTime AddDays(LocalDateTime dateTime, int days) {
        return dateTime.plusDays(days);
    }

    public LocalDateTime AddMonths(LocalDateTime dateTime, int months) {
        return dateTime.plusMonths(months);
    }
}
