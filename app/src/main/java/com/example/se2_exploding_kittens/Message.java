package com.example.se2_exploding_kittens;

public class Message {
    private boolean replyExpected;
    private boolean messageIsReply;
    private String payload;
    private MessageCallback callback;
    private int messageType;

    public static String parseAndExtractPayload(String res){
        String [] sp = res.split("###");
        if(sp.length == 3){
            return sp[2];
        }
        return "";
    }

    public static int parseAndExtractMessageType(String res){
        String [] sp = res.split("###");
        if(sp.length == 3){
            try {
                return Integer.parseInt(sp[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return -1;
    }

    public static boolean parseAndExtractIsReply(String res){
        String [] sp = res.split("###");
        if(sp.length == 3){
            if(sp[0] == "R")
                return true;
        }
        return false;
    }

    public Message(boolean replyExpected, boolean messageIsReply, int messageType, String payload, MessageCallback callback) {
        this.replyExpected = replyExpected;
        this.payload = payload;
        this.callback = callback;
        this.messageIsReply = messageIsReply;
        this.messageType = messageType;
    }

    public String getPayload() {
        return payload;
    }

    public String getTransmitMessage() {
        if(messageIsReply){
            return "R###"+messageType+"###"+payload;
        }else {
            return "M###"+messageType+"###"+payload;
        }

    }

    public boolean isReplyExpected() {
        return replyExpected;
    }

    public MessageCallback getCallback() {
        return callback;
    }

    public int getMessageType() {
        return messageType;
    }
}
