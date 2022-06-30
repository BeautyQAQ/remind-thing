package com.remind.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.remind.simple.Send;

@RestController
@RequestMapping("/test")
public class TestController {
    
    private final Send send;

    public TestController(Send send) {
        this.send = send;
    }

    @Value("${remind.qq}")
    private String qq;

    @GetMapping("/sendMessage")
    public String test(){
        // Bot bot = botManager.getDefaultBot();
        // bot.getSender().SENDER.sendPrivateMsg("1426887150", "测试消息");
        // Send send = new Send(get, new MessageExceptionHandling());
        send.privates(qq, "测试消息");
        return "发送成功";
    }
}
