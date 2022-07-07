package com.remind.scheduled;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Set;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.remind.simple.Send;

/**
 * 定时任务
 * 每一分钟提醒一次: 0 0/1 * * * ?
 * 每一小时提醒一次: 0 0 0/1 * * ?
 */
@Component
public class Remind {

    final static Logger log = org.slf4j.LoggerFactory.getLogger(Remind.class);

    private final Send send;

    public Remind(Send send) {
        this.send = send;
    }

    @Value("${remind.qq}")
    private Set<String> qqSet;

    /**
     * 每周五晚7点30提醒买水果
     */
    @Scheduled(cron = "0 30 19 ? * 5")
    public void fruitsRemind() {
        qqSet.forEach(qq -> {
            try {
                log.info("开始加载提醒内容~~~");
                // 喝水语录
                LocalDate now = LocalDate.now();
                DayOfWeek week = now.getDayOfWeek();
                String msg = "今天是 " + now + "，星期" + week.getValue() + "，该买水果了！";
                // 发送买水果提醒
                send.privates(qq, msg);
                log.info("正在发送买水果提醒，当前qq={}, 语录={}", qq, msg);
            } catch (Exception e) {
                log.error("发送买水果提醒异常, qq={}", qq, e);
            }
        });
    }

    /**
     * 每隔一小时提醒站起来活动活动
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void sportRemind() {
        LocalDate now = LocalDate.now();
        DayOfWeek week = now.getDayOfWeek();
        int weekValue = week.getValue();
        Calendar calendar = Calendar.getInstance();
        // 获取当前小时
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (weekValue == 6 || weekValue == 7){
            // 如果是周末，只在早上11点到晚上10点发送消息提醒
            if (hour > 11 || hour < 22) {
                return;
            }
        } else {
            // 不是周末，只在早上9点到晚上10点发送消息提醒
            if (hour > 9 || hour < 22) {
                return;
            }
        }

        qqSet.forEach(qq -> {
            try {
                log.info("开始加载提醒内容~~~");
                String msg;
                if (weekValue == 6 || weekValue == 7){
                    msg = "你已经玩了1小时啦！起来活动一下吧！";
                } else {
                    msg = "你已经工作1小时啦！起来活动一下吧！";
                }
                // 发送果提醒
                send.privates(qq, msg);
                log.info("正在发送提醒，当前qq={}, 语录={}", qq, msg);
            } catch (Exception e) {
                log.error("发送提醒异常, qq={}", qq, e);
            }
        });
    }

}