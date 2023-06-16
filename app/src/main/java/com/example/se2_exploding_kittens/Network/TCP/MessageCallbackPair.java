package com.example.se2_exploding_kittens.Network.TCP;

import com.example.se2_exploding_kittens.Network.MessageCallback;

import java.util.ArrayList;

public class MessageCallbackPair {
    private final ArrayList<MessageCallback> callbacks = new ArrayList<>();
    private final int messageID;

    public MessageCallbackPair(MessageCallback callback, int messageID) {
        callbacks.add(callback);
        this.messageID = messageID;
    }

    public int getMessageID() {
        return messageID;
    }

    public ArrayList<MessageCallback> getCallbacks() {
        return callbacks;
    }

    public void addCallback(MessageCallback callback) {
        callbacks.add(callback);
    }

    public void removeCallback(MessageCallback callback) {
        callbacks.remove(callback);
    }
}
