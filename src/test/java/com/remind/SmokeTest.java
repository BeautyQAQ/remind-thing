package com.remind;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;
import java.time.LocalDate;

@SpringBootTest
public class SmokeTest {

    @Test
    public static void main(String[] args) {
        LocalDate now = LocalDate.now();
        DayOfWeek week = now.getDayOfWeek();
        System.out.println(week);
        System.out.println(week.getValue());
    }
}
