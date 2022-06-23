package com.remind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import love.forte.simbot.spring.autoconfigure.EnableSimbot;

/**
 * @author huangshen
 */
@EnableSimbot
@EnableScheduling
@SpringBootApplication
public class RemindThingApplication {
    public static void main(String[] args) {
        SpringApplication.run(RemindThingApplication.class,args);
    }
}
