package com.remind.scheduled;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class RemindTest {

    @Resource
    private Remind remind;

    /**
     * 买水果提醒测试
     */
    @Test
    public void should_send_buy_fruits_message_to_configurations_qq() {
        remind.fruitsRemind();
    }

    /**
     * 提醒站起来活动活动测试
     */
    @Test
    public void should_send_sport_message_to_configurations_qq() {
        remind.sportRemind();
    }

}