package com.gcp.poc.f2b.generator.helpers;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateHelper {

    public String printDate(LocalDateTime dateTime) {
        return dateTime.getYear()+"-"+String.format("%02d",dateTime.getMonthValue())+"-"+String.format("%02d",dateTime.getDayOfMonth());
    }

    public LocalDate addWorkingDays(LocalDate date, int days) {
        if (days < 0) {
            return minusWorkingDays(date, days * -1);
        }

        int daysToAdd = 0;
        for(int i=0; i<days; i++) {
            daysToAdd++;
            while(!isWorkingDay(date.plusDays(daysToAdd))) {
                daysToAdd++;
            }
        }
        return date.plusDays(daysToAdd);
    }

    public LocalDate minusWorkingDays(LocalDate date, int days) {
        if (days < 0) {
            return addWorkingDays(date, days * -1);
        }

        int daysToMinus = 0;
        for(int i=0; i<days; i++) {
            daysToMinus++;
            while(!isWorkingDay(date.minusDays(daysToMinus))) {
                daysToMinus++;
            }
        }
        return date.minusDays(daysToMinus);
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

    private boolean isWorkingDay(LocalDate date) {
        return date.getDayOfWeek() != DayOfWeek.SATURDAY &&
                date.getDayOfWeek() != DayOfWeek.SUNDAY;
    }
}
