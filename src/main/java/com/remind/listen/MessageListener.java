package com.remind.listen;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.remind.utils.HttpUtil;

import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.Reply;
import love.forte.simbot.api.message.ReplyAble;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.MessageGet;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;

/**
 * @author huangshen
 */
@Component
public class MessageListener {

    final static Logger log = org.slf4j.LoggerFactory.getLogger(MessageListener.class);

    static final String URL = "http://api.qingyunke.com/api.php";

    /**
     * 监听私聊消息
     */
    @OnPrivate
    public void privateMsg(PrivateMsg privateMsg, MsgSender sender) {
        sendMsg(privateMsg, sender, false);
    }

    /**
     * 监听群消息
     */
    @OnGroup
    public ReplyAble groupMsg(GroupMsg groupMsg, MsgSender sender) {
        // 默认关闭群聊模式，需要的话把注释去掉
        // return sendMsg(groupMsg, sender, true);
        return null;
    }

    /**
     * 通过青客云封装智能聊天
     *
     * @param commonMsg commonMsg
     * @param sender    sender
     */
    private ReplyAble sendMsg(MessageGet commonMsg, MsgSender sender, boolean group) {
        log.info("智能聊天中~~~,接收消息：qq={}, msg={}", commonMsg.getAccountInfo().getAccountCode(),commonMsg.getMsgContent().getMsg());
        // 如果消息前缀为"pixiv"中定义的数据,则不进行智能聊天
        String receiveMsg = commonMsg.getMsgContent().getMsg();
        if (receiveMsg.startsWith("色图") || receiveMsg.startsWith("涩图")) {
            return null;
        }
        // 通过get请求调用聊天接口   MsgSender中存在三大送信器，以及非常多的重载方法。
        final String result = HttpUtil.sendGet(URL,
                "key=free&appid=0&msg=".concat(commonMsg.getMsgContent().getMsg()));
        if (StringUtils.hasText(result)) {
            final JSONObject json = JSONObject.parseObject(result);
            if (json.getInteger("result") == 0 && StringUtils.hasText(json.getString("content"))) {
                final String msg = json.getString("content").replace("{br}", "\n");
                log.info("智能聊天中~~~,发送消息：qq={}, msg={}", commonMsg.getAccountInfo().getAccountCode(), msg);
                //发送群消息
                if (group) {
                    // 参数1：回复的消息 参数2：是否at当事人
                    return Reply.reply(msg, true);
                }
                //发送私聊消息
                sender.SENDER.sendPrivateMsg(commonMsg, msg);
            }
        }
        return null;
    }

}
