package com.remind.scheduled;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import love.forte.simbot.bot.Bot;
import love.forte.simbot.bot.BotManager;

/**
 * 定时任务，每周三晚上6.30执行
 */
@Component
public class Remind {

    final static Logger logger = org.slf4j.LoggerFactory.getLogger(Remind.class);
    
    @Resource
    private BotManager botManager;

    @Value("${remind.qq}")
    private Set<String> qqSet;

    /**
     * 提醒内容
     */
    static List<String> content;

    static {
        content = new ArrayList<>();
        logger.info("开始加载提醒内容~~~");
        // 喝水语录
        LocalDate now = LocalDate.now();
        DayOfWeek week = now.getDayOfWeek();
        content.add("今天是 " + now + "，星期" + week.getValue() + "，改买水果了！");
    }

    /**
     * 每一分钟提醒一次: 0 0/1 * * * ?
     * 每一小时提醒一次: 0 0 0/1 * * ?
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void handler() {
        Calendar calendar = Calendar.getInstance();
        // 获取当前小时
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        // 只在早上9点到晚上8点发送消息提醒
        if (hour < 9 || hour > 20) {
            return;
        }
        qqSet.forEach(qq -> {
            try {
                final String msg = content.get(new Random().nextInt(content.size()));
                // // 发送买水果提醒
                Bot bot = botManager.getDefaultBot();
                bot.getSender().SENDER.sendPrivateMsg(qq, msg);
                // // 发送随机喝水图片
                bot.getSender().SENDER.sendPrivateMsg(qq, msg);
                logger.info("正在发送喝水提醒，当前qq={}, 语录={}", qq, msg);
                bot.close();
            } catch (Exception e) {
                logger.error("发送喝水提醒异常, qq={}", qq, e);
            }
        });
    }

}