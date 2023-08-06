package com.example.chatsx.Model;

public class ChatModel {

    String uId, textMessage, textMessageId;
    Long timestamp;

    public ChatModel(String uId, String textMessage, Long timestamp) {
        this.uId = uId;
        this.textMessage = textMessage;
        this.timestamp = timestamp;
    }

    public ChatModel(String uId, String textMessage) {
        this.uId = uId;
        this.textMessage = textMessage;
    }

    public ChatModel() {
    }

    public String getTextMessageId() {
        return textMessageId;
    }

    public void setTextMessageId(String textMessageId) {
        this.textMessageId = textMessageId;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
