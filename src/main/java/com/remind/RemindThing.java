package com.remind;

import love.forte.simboot.autoconfigure.EnableSimbot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author huangshen
 */
@SpringBootApplication
@EnableSimbot
public class RemindThing {
    public static void main(String[] args) {
        SpringApplication.run(RemindThing.class,args);
    }
}
