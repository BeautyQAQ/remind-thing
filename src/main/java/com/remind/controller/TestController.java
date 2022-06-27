package com.remind.controller;

import love.forte.simbot.bot.Bot;
import love.forte.simbot.bot.BotManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private BotManager botManager;

    @GetMapping("/sendMessage")
    public String test(){
        Bot bot = botManager.getDefaultBot();
        bot.getSender().SENDER.sendPrivateMsg("1426887150", "测试消息");
        return "发送成功";
    }
}
