package com.remind.pixiv;

import com.remind.simple.Send;
import love.forte.simbot.api.message.MessageContentBuilder;
import love.forte.simbot.api.message.MessageContentBuilderFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@SpringBootTest
public class PixivTest {

    @Resource
    private Send send;

    @Resource
    private MessageContentBuilderFactory messageContentBuilderFactory;

    @Value("${remind.qq}")
    private String qq;

    /**
     * pixivPictureR18方法测试
     */
    @Test
    public void should_send_pixiv_r18_picture_message_to_configurations_qq() throws IOException {
        Connection connection = Jsoup.connect(String.format("https://api.lolicon.app/setu/v2?%s", "r18=1"));
        ArrayList<Pixiv> arrayPixiv = send.getPixivs(connection);
        for (Pixiv pixiv : arrayPixiv) {
            //获取消息工厂
            MessageContentBuilder message = messageContentBuilderFactory.getMessageContentBuilder();
            //新url链接
            String original = pixiv.getUrls().get("original").split("https://i.pixiv.re")[1];
            String newUrl = String.format("https://i.pixiv.re%s", original);
            String introduce = String.format("画师id:%s\n画师名称:%s\n标题:%s\n标签:%s\n%s\n", pixiv.getUid(), pixiv.getAuthor(), pixiv.getTitle(), Arrays.toString(pixiv.getTags()), String.format("https://i.pixiv.re%s", original));
            send.privates(qq, message.text(introduce).image(newUrl).build());
        }
    }

    /**
     * pixivPictureMain方法测试
     */
    @Test
    public void should_send_pixiv_picture_message_to_configurations_qq() throws IOException {
        String parameter = "黑丝 1";
        Connection connection;
        String tag = parameter.split(" ")[0];
        String num = parameter.split(" ")[1];
        connection = Jsoup.connect(String.format("https://api.lolicon.app/setu/v2?r18=1&%s", String.format("tag=%s&num=%s", tag, num)));

        ArrayList<Pixiv> arrayPixiv = send.getPixivs(connection);
        for (Pixiv pixiv : arrayPixiv) {
            // 获取消息工厂
            MessageContentBuilder message = messageContentBuilderFactory.getMessageContentBuilder();
            // 新url链接
            String original = pixiv.getUrls().get("original").split("https://i.pixiv.re")[1];
            String newUrl = String.format("https://i.pixiv.re%s", original);
            String introduce = String.format("画师id:%s\n画师名称:%s\n标题:%s\n标签:%s\n%s\n", pixiv.getUid(), pixiv.getAuthor(), pixiv.getTitle(), Arrays.toString(pixiv.getTags()), String.format("https://i.pixiv.re%s", original));
            send.privates(qq, message.text(introduce).image(newUrl).build());
        }
    }
}
