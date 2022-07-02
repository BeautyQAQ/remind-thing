package com.remind.controller;

import com.remind.pixiv.Pixiv;
import com.remind.simple.Send;
import love.forte.simbot.api.message.MessageContentBuilder;
import love.forte.simbot.api.message.MessageContentBuilderFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@RestController
@RequestMapping("/test")
public class TestController {
    
    private final Send send;

    private final MessageContentBuilderFactory messageContentBuilderFactory;

    public TestController(Send send, MessageContentBuilderFactory messageContentBuilderFactory) {
        this.send = send;
        this.messageContentBuilderFactory = messageContentBuilderFactory;
    }

    @Value("${remind.qq}")
    private String qq;

    @GetMapping("/sendMessage")
    public String test(){
        send.privates(qq, "测试消息");
        return "发送成功";
    }

    @GetMapping("/sendPicture")
    public String picture() throws IOException {
        Connection connection = Jsoup.connect(String.format("https://api.lolicon.app/setu/v2?%s", "r18=1"));
        ArrayList<Pixiv> arrayPixiv  = send.getPixivs(connection);
        for (Pixiv pixiv : arrayPixiv) {
            //获取消息工厂
            MessageContentBuilder message = messageContentBuilderFactory.getMessageContentBuilder();
            //新url链接
            String original = pixiv.getUrls().get("original").split("https://i.pixiv.re")[1];
            String newUrl = String.format("https://i.pixiv.re%s", original);
            String introduce = String.format("画师id:%s\n画师名称:%s\n标题:%s\n标签:%s\n%s\n", pixiv.getUid(), pixiv.getAuthor(), pixiv.getTitle(), Arrays.toString(pixiv.getTags()), String.format("https://i.pixiv.re%s", original));
            send.privates(qq, message.text(introduce).image(newUrl).build());
        }
        return "图片发送成功";
    }
}
