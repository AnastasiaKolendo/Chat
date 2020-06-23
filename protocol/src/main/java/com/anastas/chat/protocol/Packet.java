package com.anastas.chat.protocol;

public class Packet {
    private final PacketType type;
    private final String payloadJson;

    public Packet(PacketType type, String payloadJson) {
        this.type = type;
        this.payloadJson = payloadJson;
    }

    public PacketType getType() {
        return type;
    }

    public String getPayloadJson() {
        return payloadJson;
    }
}
