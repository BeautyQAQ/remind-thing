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

    @Test
    public void should_return() {
        LocalDate now = LocalDate.now();
        DayOfWeek week = now.getDayOfWeek();
        int weekValue = week.getValue();
        Calendar calendar = Calendar.getInstance();
        // 获取当前小时
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        System.out.println(hour);
        if (weekValue == 6 || weekValue == 7){
            // 如果是周末，只在早上11点到晚上10点发送消息提醒
            if (hour <= 11 || hour >= 22) {
                return;
            }
        } else {
            // 不是周末，只在早上9点到晚上10点发送消息提醒
            if (hour <= 9 || hour >= 22) {
                return;
            }
        }
    }
}
