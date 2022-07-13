package com.remind.simple;

import com.remind.pixiv.Pixiv;
import love.forte.simbot.api.message.MessageContent;
import love.forte.simbot.api.message.events.*;
import love.forte.simbot.bot.Bot;
import love.forte.simbot.bot.BotManager;
import love.forte.simbot.component.mirai.message.MiraiMessageContent;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Connection;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huangshen
 */
@Component
public class Send {

    final static Logger log = org.slf4j.LoggerFactory.getLogger(Send.class);

    @Value("${remind.botQQ}")
    private String botQQ;

    /**
     * 获取消息
     */
    private final Get get;

    /**
     * 消息异常处理
     */
    private final MessageExceptionHandling meh;
    /**
     * 通过Map储存事件ID
     * <p>
     * 当前事件ID不存在则发送消息后储存
     * <p>
     * 当前事件ID存在则发送过且消息不再发送
     * <p>
     * 储存事件ID防止多Bot状态下发送重复消息
     */
    private final Map<String, String> map = new HashMap<>();

    //指定bot发送私聊消息--------------------------------------------------------------------------------------------------

    /**
     * 指定一个Bot账号发送私聊消息
     *
     * @param qqCode QQ号
     * @param botQQ  Bot账号
     * @param str    消息体
     */
    public void privates(String qqCode, String botQQ, String str) {
        meh.sendPrivates(botQQ, qqCode, str);
        log.info("QQ:{} Msg:{}", qqCode, str);
    }

    /**
     * MessageContent类型构建消息
     * <p>
     * 内容消息可包含图片一起发送
     *
     * @param qqCode QQ号
     * @param str    完整消息体
     */
    public void privates(String qqCode, MessageContent str) {
        meh.sendPrivates(botQQ, qqCode, str);
        log.info("QQ:{} Msg:{}", qqCode, str);
    }

    /**
     * 自动获取当前QQ并查询绑定的Bot账号后发送消息
     *
     * @param msg 私聊消息
     * @param str 消息体
     */
    public void privates(PrivateMsg msg, String str) {
        //获取当前QQ账号
        String qqCode = get.prCode(msg);
        meh.sendPrivates(botQQ, qqCode, str);
        //将该事件id添加到map内
        log.info("EventID:{} QQ:{} Msg:{}", msg.getId(), qqCode, str);
    }

    /**
     * 根据当前指定的QQ来查询绑定的Bot账号后发送消息
     *
     * @param qqCode QQ账号
     * @param str    消息体
     */
    public void privates(String qqCode, String str) {
        meh.sendPrivates(botQQ, qqCode, str);
        log.info("QQ:{} Msg:{}", qqCode, str);
    }

    //------------------------------------------------------------------------------------------------------------------
    //指定bot发送群聊消息--------------------------------------------------------------------------------------------------

    /**
     * 群成员增加事件专用
     * 发送提示消息方法
     *
     * @param eventId 事件ID
     * @param grCode  群号
     * @param qqCode  QQ号
     * @param str     消息体
     */
    public synchronized void groups(String eventId, String grCode, String qqCode, String str) {
        meh.sendGroups(botQQ, grCode, str);
        //将当前事件ID添加到map集合内防止发送重复消息
        log.info("EventID:{} Gr:{} Msg:{}", eventId, grCode, str);
    }

    /**
     * 根据当前指定的QQ查询绑定的Bot账号来发送群消息
     *
     * @param msg    群消息
     * @param qqCode QQ号
     * @param str    消息体
     */
    public synchronized void groups(GroupMsg msg, String qqCode, String str) {
        if (!map.containsKey(msg.getId())) {
            String grCode = get.grCode(msg);
            meh.sendGroups(botQQ, grCode, str);
            map.put(msg.getId(), msg.getId());
            log.info("EventID:{} Gr:{} Msg:{}", msg.getId(), grCode, str);
        }
    }

    /**
     * 自动获取触发消息的QQ
     * 根据当前QQ查询绑定的Bot账号来发送群消息
     *
     * @param msg 群消息
     * @param str 消息体
     */
    public synchronized void groups(GroupMsg msg, String str) {
        //判断事件id是否存在 防止发送重复消息
        if (!map.containsKey(msg.getId())) {
            //获取当前群号
            String grCode = get.grCode(msg);
            meh.sendGroups(botQQ, grCode, str);
            //将该事件id添加到map内
            map.put(msg.getId(), msg.getId());
            log.info("EventID:{} Gr:{} Msg:{}", msg.getId(), grCode, str);
        }
    }

    /**
     * MessageContent类型构建消息
     * 内容消息可包含图片一起发送
     *
     * @param msg 群消息
     * @param str 完整消息体
     */
    public synchronized void groups(GroupMsg msg, MessageContent str) {
        //判断事件id是否存在 防止发送重复消息
        if (!map.containsKey(msg.getId())) {
            log.info("发送群聊消息");
            //获取当前群号
            String grCode = get.grCode(msg);
            meh.sendGroups(botQQ, grCode, str);
            //将该事件id添加到map内
            map.put(msg.getId(), msg.getId());
            log.info("EventID:{} Gr:{} Msg:{}", msg.getId(), grCode, str);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //指定bot发送群聊异步消息-----------------------------------------------------------------------------------------------

    /**
     * MiraiMessageContent类型用于构建合并消息
     * <p>
     * 异步发送群消息，不然构建合并消息的时候太慢
     * <p>
     * 重复触发时上次触发的效果将会作废无法发出消息
     *
     * @param msg 群聊消息
     * @param str Mirai 消息内容
     */
    public synchronized void groupMsgAsync(GroupMsg msg, MiraiMessageContent str) {
        //判断事件id是否存在 防止发送重复消息
        if (!map.containsKey(msg.getId())) {
            log.info("发送群聊消息");
            //获取当前群号
            String grCode = get.grCode(msg);
            //获取当前QQ账号
            meh.sendGroupsAsync(botQQ, grCode, str);
            //将该事件id添加到map内
            map.put(msg.getId(), msg.getId());
            log.info("EventID:{} Gr:{} Msg:{}", msg.getId(), grCode, str.getMsg());
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //指定bot发送私聊异步消息-----------------------------------------------------------------------------------------------

    /**
     * MessageContent类型用于构建文本加图片的多格式内容
     * <p>
     * 异步发送私聊消息，不然构建合并消息的时候太慢
     * <p>
     * 重复触发时上次触发的效果将会作废无法发出消息
     *
     * @param msg 私聊消息
     * @param str 完整消息体
     */
    public void privateMsgAsync(PrivateMsg msg, MessageContent str) {
        //获取当前QQ账号
        String qqCode = get.prCode(msg);
        meh.sendPrivatesAsync(botQQ, qqCode, str);
        //将该事件id添加到map内
        map.put(msg.getId(), msg.getId());
        log.info("EventID:{} QQ:{} Msg:{}", msg.getId(), qqCode, str.getMsg());
    }

    public Send(final Get get, final MessageExceptionHandling meh) {
        this.get = get;
        this.meh = meh;
    }

    public ArrayList<Pixiv> getPixivs(@NotNull Connection connection) throws IOException {
        Elements json = connection.timeout(10000 * 1000).ignoreContentType(true).get().select("body");
        // 通过fromObject将json字符串翻译成JSON对象(JSONObject)
        JSONObject jsonObject = JSONObject.fromObject(json.text());
        // 上面的jsonObject类型就是json数组如：data:[{"tags":[],"urls":{}}]
        // 返回的是非单一的json对象
        JSONArray dataJsonArray = jsonObject.getJSONArray("data");
        ArrayList<Pixiv> arrayPixiv = new ArrayList<>();
        for (Object o : dataJsonArray) {
            // 将获取的单个json字符串翻译成JSONObject
            JSONObject jsonParts = JSONObject.fromObject(o.toString());
            // 将json对象翻译成Pixiv对象
            arrayPixiv.add((Pixiv) JSONObject.toBean(jsonParts, Pixiv.class));
        }
        return arrayPixiv;
    }
}

/**
 * 处理指定Bot账号时不存在的错误信息，按照正常来讲，
 * 是不会存在错误的，但如果指定的Bot账号被冻结等，
 * 没有在群内或者其他情况下更换推送Bot时将打印，
 * 指定Bot账号不存在！
 * <p>
 * 更换一个已注册的Bot即可正常使用
 */
@Component
class MessageExceptionHandling {

    final static Logger log = org.slf4j.LoggerFactory.getLogger(MessageExceptionHandling.class);

    private final BotManager manager;//Bot管理器

    //指定bot发送消息异常处理区---------------------------------------------------------------------------------------------
    public void sendPrivates(String botQQ, String qqCode, String str) {
        try {
            //指定一个bot来发送消息
            Bot bot = manager.getBot(botQQ);
            //根据指定的bot发送私聊消息
            bot.getSender().SENDER.sendPrivateMsg(qqCode, str);
        } catch (Exception e) {
            log.error("指定Bot账号不存在！", e);
        }
    }

    public void sendPrivates(String botQQ, String qqCode, MessageContent str) {
        try {
            //指定一个bot来发送消息
            Bot bot = manager.getBot(botQQ);
            //根据指定的bot发送私聊消息
            bot.getSender().SENDER.sendPrivateMsg(qqCode, str);
        } catch (Exception e) {
            log.error("指定Bot账号不存在！", e);
        }
    }

    public void sendGroups(String botQQ, String grCode, String str) {
        try {
            //指定一个bot来发送消息
            Bot bot = manager.getBot(botQQ);
            //根据指定的bot发送群聊消息
            bot.getSender().SENDER.sendGroupMsg(grCode, str);
        } catch (Exception e) {
            log.error("指定Bot账号不存在！", e);
        }
    }

    public void sendGroupsAsync(String botQQ, String grCode, MiraiMessageContent str) {
        try {
            //指定一个bot来发送消息
            Bot bot = manager.getBot(botQQ);
            //根据指定的bot发送群组异步消息
            bot.getSender().SENDER.sendGroupMsgAsync(grCode, str);
        } catch (Exception e) {
            log.error("指定Bot账号不存在！", e);
        }
    }

    public void sendPrivatesAsync(String botQQ, String qqCode, MessageContent str) {
        try {
            //指定一个bot来发送消息
            Bot bot = manager.getBot(botQQ);
            //根据指定的bot发送私聊异步消息
            bot.getSender().SENDER.sendPrivateMsgAsync(qqCode, str);
        } catch (Exception e) {
            log.error("指定Bot账号不存在！", e);
        }
    }

    public void sendGroups(String botQQ, String grCode, MessageContent str) {
        try {
            //指定一个bot来发送消息
            Bot bot = manager.getBot(botQQ);
            //根据指定的bot发送群聊消息
            bot.getSender().SENDER.sendGroupMsg(grCode, str);
        } catch (Exception e) {
            log.error("指定Bot账号不存在！", e);
        }
    }

    public MessageExceptionHandling(BotManager manager) {
        this.manager = manager;
    }
}