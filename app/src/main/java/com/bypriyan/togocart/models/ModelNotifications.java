package com.bypriyan.togocart.models;

public class ModelNotifications {
    private String MessageReplyId, isSeen, message, messageId, messageSenderId;

    public ModelNotifications() {
    }

    public ModelNotifications(String messageReplyId, String isSeen, String message, String messageId, String messageSenderId) {
        MessageReplyId = messageReplyId;
        this.isSeen = isSeen;
        this.message = message;
        this.messageId = messageId;
        this.messageSenderId = messageSenderId;
    }

    public String getMessageReplyId() {
        return MessageReplyId;
    }

    public void setMessageReplyId(String messageReplyId) {
        MessageReplyId = messageReplyId;
    }

    public String getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(String isSeen) {
        this.isSeen = isSeen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageSenderId() {
        return messageSenderId;
    }

    public void setMessageSenderId(String messageSenderId) {
        this.messageSenderId = messageSenderId;
    }
}
