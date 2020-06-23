package com.anastas.chat.protocol;

public class SingleUserPresence {
    private final String name;
    private final boolean isOffline;

    public SingleUserPresence(String name, boolean isOffline) {
        this.name = name;
        this.isOffline = isOffline;
    }

    public String getName() {
        return name;
    }

    public boolean isOffline() {
        return isOffline;
    }
}
