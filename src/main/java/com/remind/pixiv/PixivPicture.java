package com.remind.pixiv;

import com.remind.simple.Send;
import love.forte.simbot.annotation.*;
import love.forte.simbot.api.message.MessageContentBuilder;
import love.forte.simbot.api.message.MessageContentBuilderFactory;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.MsgGet;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.component.mirai.message.MiraiMessageContentBuilder;
import love.forte.simbot.component.mirai.message.MiraiMessageContentBuilderFactory;
import love.forte.simbot.filter.MatchType;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

@Component
public class PixivPicture {

    final static Logger log = org.slf4j.LoggerFactory.getLogger(PixivPicture.class);

    //注入一个消息工厂
    private final MessageContentBuilderFactory messageContentBuilderFactory;
    //注入一个可以构建合并消息的消息工厂
    private final MiraiMessageContentBuilderFactory factory;
    //图片请求地址
    private final static String URL = "https://api.lolicon.app/setu/v2?&%s";

    //图片请求地址，r18=2
    private final static String URL2 = "https://api.lolicon.app/setu/v2?&r18=2&%s";

    //每张图片的简介模板
    private final static String INTRODUCE = "画师id:%s\n画师名称:%s\n标题:%s\n标签:%s\n%s\n";

    private final Send send;//发送消息

    public PixivPicture(MessageContentBuilderFactory messageContentBuilderFactory, MiraiMessageContentBuilderFactory factory, Send send) {
        this.messageContentBuilderFactory = messageContentBuilderFactory;
        this.factory = factory;
        this.send = send;
    }

    /**
     * 随机获取一张R18图片
     * 消息内容：色图
     */
    @OnGroup
    @OnPrivate
    @Filter(value = "色图", matchType = MatchType.EQUALS, trim = true)
    public void pixivOnlyPictureR18(@NotNull MsgGet msgGet) {
        try {
            Connection connection = Jsoup.connect(String.format(URL, "r18=1"));
            packageRequestOnlyPicture(connection, msgGet);
        } catch (Exception e) {
            exceptionPrompt(msgGet);
            log.error("pixivOnlyPictureR18接口异常", e);
        }
    }

    /**
     * 随机获取一张R18图片, 带画师信息
     * 消息内容：色图
     */
    @OnGroup
    @OnPrivate
    @Filter(value = "色图1", matchType = MatchType.EQUALS, trim = true)
    public void pixivPictureR18(@NotNull MsgGet msgGet) {
        try {
            Connection connection = Jsoup.connect(String.format(URL, "r18=1"));
            packageRequest(connection, msgGet);
        } catch (Exception e) {
            exceptionPrompt(msgGet);
            log.error("pixivPictureR18接口异常", e);
        }
    }

    /**
     * 随机获取一张混合（R18与非R18）图片
     * 消息内容：涩图
     */
    @OnGroup
    @OnPrivate
    @Filter(value = "涩图", matchType = MatchType.EQUALS, trim = true)
    public void pixivOnlyPictureMixture(@NotNull MsgGet msgGet) {
        try {
            Connection connection = Jsoup.connect(String.format(URL, "r18=2"));
            packageRequestOnlyPicture(connection, msgGet);
        } catch (Exception e) {
            exceptionPrompt(msgGet);
            log.error("pixivOnlyPictureMixture接口异常", e);
        }
    }

    /**
     * 随机获取一张混合（R18与非R18）图片, 带画师信息
     * 消息内容：涩图
     */
    @OnGroup
    @OnPrivate
    @Filter(value = "涩图1", matchType = MatchType.EQUALS, trim = true)
    public void pixivPictureMixture(@NotNull MsgGet msgGet) {
        try {
            Connection connection = Jsoup.connect(String.format(URL, "r18=2"));
            packageRequest(connection, msgGet);
        } catch (Exception e) {
            exceptionPrompt(msgGet);
            log.error("pixivPictureMixture接口异常", e);
        }
    }

    /**
     * 根据标签类型获取图片
     * 涩图#JK 3
     * 涩图#为前缀
     * 参数一：标签类型
     * 参数二：返回图片的数量
     * 一次性返回图片数量最多100张
     *
     * @param msgGet    消息父类
     * @param parameter 正则判断
     */
    @OnGroup
    @OnPrivate
    @Filters(value = {@Filter(value = "涩图#：*{{parameter,\\D+\\s\\d+}}", matchType = MatchType.REGEX_MATCHES, trim = true), @Filter(value = "涩图#：*{{parameter,\\D+}}", matchType = MatchType.REGEX_MATCHES, trim = true), @Filter(value = "涩图#：*{{parameter,\\w+\\s\\d+}}", matchType = MatchType.REGEX_MATCHES, trim = true), @Filter(value = "涩图#：*{{parameter,\\w+}}", matchType = MatchType.REGEX_MATCHES, trim = true)})
    public void pixivPictureMain(@NotNull MsgGet msgGet, @FilterValue("parameter") String parameter) {
        try {
            Connection connection;
            if (parameter.contains(" ")) {//标签/返回数量
                String tag = parameter.split(" ")[0];
                String num = parameter.split(" ")[1];
                connection = Jsoup.connect(String.format(URL2, String.format("tag=%s&num=%s", tag, num)));
            } else {
                connection = Jsoup.connect(String.format(URL2, String.format("tag=%s", parameter)));
            }
            packageRequest(connection, msgGet);
        } catch (Exception e) {
            exceptionPrompt(msgGet);
            log.error("pixivPictureMain接口异常", e);
        }
    }

    @OnGroup
    @OnPrivate
    @Filters(value = {@Filter(value = "涩图：*{{parameter,\\D+#\\D+}}", matchType = MatchType.REGEX_MATCHES, trim = true), @Filter(value = "涩图：*{{parameter,\\D+#\\D+\\s\\d+}}", matchType = MatchType.REGEX_MATCHES, trim = true),})
    public void pixivPicture(@NotNull MsgGet msgGet, @FilterValue("parameter") String parameter) {
        try {
            String[] tag = parameter.split("#");
            Connection connection;
            if (Objects.requireNonNull(msgGet.getText()).contains(" ")) {
                String num = Objects.requireNonNull(msgGet.getText()).split(" ")[1];
                connection = Jsoup.connect(String.format(URL2, "tag=" + tag[0] + "%7C" + tag[1].split(" ")[0] + "&num=" + num));
            } else {
                connection = Jsoup.connect(String.format(URL2, "tag=" + tag[0] + "%7C" + tag[1]));
            }
            packageRequest(connection, msgGet);
        } catch (Exception e) {
            exceptionPrompt(msgGet);
            log.error("pixivPicture接口异常", e);
        }
    }

    /**
     * 根据id查找指定作者的P站作品
     * 例：pid#3036679 20
     * pid#为前缀
     * 参数一：作者id
     * 参数二：返回图片的数量
     * 指定id一次性返回图片数量最多20张
     *
     * @param msgGet    消息父类
     * @param parameter 正则判断
     */
    @OnGroup
    @OnPrivate
    @Filter(value = "pid#：*{{parameter,\\d+\\s\\d+}}", matchType = MatchType.REGEX_MATCHES, trim = true)
    public void findAuthorById(@NotNull MsgGet msgGet, @FilterValue("parameter") String parameter) {
        try {
            String uid = parameter.split(" ")[0];
            String num = parameter.split(" ")[1];
            //使用Jsoup爬取URL链接的Json数据并封装成Bean对象
            Connection connection = Jsoup.connect(String.format(URL2, String.format("uid=%s&num=%s", uid, num)));
            packageRequest(connection, msgGet);
        } catch (Exception e) {
            exceptionPrompt(msgGet);
            log.error("findAuthorById接口异常", e);
        }
    }

    /**
     * 请求封装方法
     * 拿到url后执行请求操作
     * 获取json数据封装成Pixiv对象
     * 遍历Pixiv对象获取图片链接
     * 根据图片链接获取到图片
     * 如果是群聊则执行构建合并消息内容
     * 如为私聊消息则不构建合并消息内容
     *
     * @param connection Connection
     * @param msgGet     消息父类
     * @throws IOException 读取失败异常
     */
    private void packageRequest(@NotNull Connection connection, MsgGet msgGet) throws IOException {
        //获取网页的Document对象并设置超时时间和忽略内容类型get请求后使用标签选择器来获取body标签体的内容
        ArrayList<Pixiv> arrayPixiv = send.getPixivs(connection);
        if (msgGet instanceof GroupMsg) {
            log.info("群聊消息");
            //获取一个Mirai消息内容生成器
            MiraiMessageContentBuilder builder = factory.getMessageContentBuilder();
            //构建合并消息内容
            builder.forwardMessage(forwardBuilder -> {
                //获取Pixiv对象里面的数据
                for (Pixiv pixiv : arrayPixiv) {
                    //先清除一次再构建
                    builder.clear();
                    //新url链接
                    String newUrl = newUrl(pixiv.getUrls().get("original"));
                    //获取秒为单位的时间戳
                    String timestamp = String.valueOf(new Date().getTime());
                    int length = timestamp.length();
                    int integer = Integer.parseInt(timestamp.substring(0, length - 3));
                    forwardBuilder.add(msgGet, integer, builder.text(introduce(pixiv)).image(newUrl).build());
                }
            });
            send.groupMsgAsync((GroupMsg) msgGet, builder.build());
        } else if (msgGet instanceof PrivateMsg) {
            for (Pixiv pixiv : arrayPixiv) {
                //获取消息工厂
                MessageContentBuilder message = messageContentBuilderFactory.getMessageContentBuilder();
                //新url链接
                String newUrl = newUrl(pixiv.getUrls().get("original"));
                send.privateMsgAsync((PrivateMsg) msgGet, message.text(introduce(pixiv)).image(newUrl).build());
            }
        }
    }

    /**
     * 请求封装方法
     * 拿到url后执行请求操作
     * 获取json数据封装成Pixiv对象
     * 遍历Pixiv对象获取图片链接
     * 根据图片链接获取到图片
     * 如果是群聊则执行构建合并消息内容
     * 如为私聊消息则不构建合并消息内容
     *
     * @param connection Connection
     * @param msgGet     消息父类
     * @throws IOException 读取失败异常
     */
    private void packageRequestOnlyPicture(@NotNull Connection connection, MsgGet msgGet) throws IOException {
        //获取网页的Document对象并设置超时时间和忽略内容类型get请求后使用标签选择器来获取body标签体的内容
        ArrayList<Pixiv> arrayPixiv = send.getPixivs(connection);
        if (msgGet instanceof GroupMsg) {
            log.info("群聊消息");
            log.info(msgGet.getId());
            log.info(msgGet.getText());
            //获取一个Mirai消息内容生成器
            MiraiMessageContentBuilder builder = factory.getMessageContentBuilder();
            //构建合并消息内容
            builder.forwardMessage(forwardBuilder -> {
                //获取Pixiv对象里面的数据
                for (Pixiv pixiv : arrayPixiv) {
                    //先清除一次再构建
                    builder.clear();
                    //新url链接
                    String newUrl = newUrl(pixiv.getUrls().get("original"));
                    //获取秒为单位的时间戳
                    String timestamp = String.valueOf(new Date().getTime());
                    int length = timestamp.length();
                    int integer = Integer.parseInt(timestamp.substring(0, length - 3));
                    forwardBuilder.add(msgGet, integer, builder.image(newUrl).build());
                }
            });
            send.groupMsgAsync((GroupMsg) msgGet, builder.build());
        } else if (msgGet instanceof PrivateMsg) {
            for (Pixiv pixiv : arrayPixiv) {
                //获取消息工厂
                MessageContentBuilder message = messageContentBuilderFactory.getMessageContentBuilder();
                //新url链接
                String newUrl = newUrl(pixiv.getUrls().get("original"));
                send.privateMsgAsync((PrivateMsg) msgGet, message.image(newUrl).build());
            }
        }
    }

    /**
     * 请求封装方法
     * 发送私聊消息
     *
     * @param connection Connection
     * @throws IOException 读取失败异常
     */
    private void packageRequestPrivate(@NotNull Connection connection) throws IOException {
        // 获取网页的Document对象并设置超时时间和忽略内容类型get请求后使用标签选择器来获取body标签体的内容
        ArrayList<Pixiv> arrayPixiv = send.getPixivs(connection);
        for (Pixiv pixiv : arrayPixiv) {
            // 获取消息工厂
            MessageContentBuilder message = messageContentBuilderFactory.getMessageContentBuilder();
            // 新url链接
            String newUrl = newUrl(pixiv.getUrls().get("original"));
            send.privates("1426887150", message.text(introduce(pixiv)).image(newUrl).build());

        }
    }

    /**
     * 异常提示信息
     *
     * @param msgGet 消息父类
     */
    private void exceptionPrompt(MsgGet msgGet) {
        if (msgGet instanceof GroupMsg) {
            send.groups(((GroupMsg) msgGet), "网络波动异常请重试...");
        } else if (msgGet instanceof PrivateMsg) {
            send.privates((PrivateMsg) msgGet, "网络波动异常请重试...");
        }
    }

    /**
     * 封装图片介绍信息
     *
     * @param pixiv Pixiv
     * @return String
     */
    private String introduce(Pixiv pixiv) {
        return String.format(INTRODUCE, pixiv.getUid(), pixiv.getAuthor(), pixiv.getTitle(), Arrays.toString(pixiv.getTags()), newUrl(pixiv.getUrls().get("original")));
    }

    /**
     * 替换图片链接头部
     *
     * @param urls 链接
     * @return String
     */
    private String newUrl(String urls) {
        return String.format("https://i.pixiv.re%s", urls.split("https://i.pixiv.re")[1]);
    }
}
