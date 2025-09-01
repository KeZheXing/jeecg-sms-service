package org.jeecg.modules.airag.app.utils;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecg.modules.airag.app.entity.SmsDevice;
import org.jeecg.modules.airag.app.entity.TgMessage;
import org.jeecg.modules.airag.app.mapper.SmsDeviceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;
import java.util.UUID;
@Component
@Slf4j
public class TelegramBot {


    // 替换为你从BotFather获取的Token
    private static final String BOT_TOKEN = "7505362943:AAFWTM8jNntYIovjqy4wm1dfx402UBHtX3E";
    // 替换为你的机器人用户名
    private static final String BOT_USERNAME = "nexon_bot";

    public TelegramBot(){
        try {
            // 初始化API
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            // 注册机器人
            botsApi.registerBot(new MyTelegramBot());
            System.out.println("机器人已启动，等待消息...");

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



    @Component
    // 自定义机器人类，继承TelegramLongPollingBot
    public static class MyTelegramBot extends TelegramLongPollingBot {


        // 处理接收到的消息
        @Override
        @Transactional
        public void onUpdateReceived(Update update) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                // 消息中艾特了机器人
                TgMessage tgMessage = new TgMessage();
                tgMessage.setMessage(update.getMessage().getText());
                tgMessage.setChatId(update.getMessage().getChatId());
                tgMessage.setIsGroup(update.getMessage().isSuperGroupMessage()||update.getMessage().isGroupMessage());
                tgMessage.setMsgId(update.getMessage().getMessageId());
                tgMessage.setFromBot(update.getMessage().getFrom().getIsBot());
                tgMessage.setFromUser(update.getMessage().getFrom().getId());
                tgMessage.setGroupName(tgMessage.getIsGroup()?update.getMessage().getChat().getTitle():null);
                // 检查消息是否艾特了机器人
                List<MessageEntity> entities = update.getMessage().getEntities();
                if (entities != null) {
                    for (MessageEntity entity : entities) {
                        if (entity.getType().equalsIgnoreCase("MENTION") && entity.getText().equals("@ppzd_online_sms_bot")) {
                            // 消息中艾特了机器人
                            log.info("AT 机器人");
                            tgMessage.setAtBot(true);
                            // 执行相应的逻辑
                        }
                    }
                }
                handleMsg(tgMessage);
                // 执行相应的逻辑
                log.info(update.getMessage().getChatId().toString());
            }
        }

        private void handleMsg(TgMessage tgMessage) {
            if (!tgMessage.getIsGroup()&&tgMessage.getMessage().equals("/id")){
                sendMsg(String.valueOf(tgMessage.getFromUser()),tgMessage.getFromUser().toString());
                return;
            }
            if (tgMessage.getIsGroup()){
                if (tgMessage.getMessage().endsWith("") && tgMessage.getAtBot()){
                    SmsDeviceMapper deviceMapper = SpringContextUtils.getBean(SmsDeviceMapper.class);
                    RedisTemplate redisTemplate = SpringContextUtils.getBean(RedisTemplate.class);
                    Integer effect = deviceMapper.ok(tgMessage.getMessage().split(" ")[1]);
                    if (effect==1){
                        SmsDevice smsdevice = deviceMapper.getByDeviceCode(tgMessage.getMessage().split(" ")[1]);
                        redisTemplate.delete(smsdevice.getDeviceCode());
                        sendToChats(String.format("设备编号%s 恢复", tgMessage.getMessage().split(" ")[1]));;
                        sendMsgOne("-1002868299484",tgMessage.getMessage().split(" ")[1]+"—设备已恢复");
                    }
                }
            }

        }

        public void sendToChats(String text) {
            sendMsgOne("-1002508407739", text);
        }

        public void sendMsgOne(String uid, String text) {
            try {
                SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
                message.setChatId(uid);
                message.setText(text);
                execute(message); // Call method to send the message
            } catch (Exception e) {
                log.info("send to {} , content {}", uid, text);
                log.error("send error ", e);
            }

        }

        public void sendMsg(String str, String chatId){
            SendMessage response = new SendMessage();
//            response.setChatId("6853840116");
//            response.setText(str);
//            try {
//                execute(response);
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
//
//            response.setChatId("6148006020");
//            response.setText(str);
//            try {
//                execute(response);
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }

            response.setChatId(chatId);
            response.setText(str);
            try {
                execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }

        // 获取机器人Token
        @Override
        public String getBotToken() {
            return BOT_TOKEN;
        }

        // 获取机器人用户名
        @Override
        public String getBotUsername() {
            return BOT_USERNAME;
        }
    }
}
