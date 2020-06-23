package com.anastas.chat.protocol;

public class Message {
    String body;
    String sender;

    public Message(String sender, String body) {
        this.body = body;
        this.sender = sender;
    }

    public String getBody() {
        return body;
    }

    public String getSender() {
        return sender;
    }
}
