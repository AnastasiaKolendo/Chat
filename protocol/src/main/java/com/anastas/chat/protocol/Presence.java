package com.anastas.chat.protocol;

import java.util.ArrayList;
import java.util.List;

public class Presence {
    private final List<SingleUserPresence> presence;
    public Presence(){
        presence = new ArrayList<>();
    }

    public  List<SingleUserPresence> getPresence(){
        return presence;
    }

    public void add(String name, boolean isOffline){
        presence.add(new SingleUserPresence(name, isOffline));
    }
}
