package com.remind;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Calendar;

public class SmokeTest {

    @Test
    public void should_print_week() {
        LocalDate now = LocalDate.now();
        DayOfWeek week = now.getDayOfWeek();
        System.out.println(week);
        System.out.println(week.getValue());
    }

    @Test
    public void should_print_hour() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        System.out.println(hour);
    }
}
