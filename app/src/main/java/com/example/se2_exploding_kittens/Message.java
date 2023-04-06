package com.example.se2_exploding_kittens;

public class Message {
    private boolean replyExpected;
    private MessageType messageType;
    private String payload;
    private MessageCallback callback;
    private int messageID;

    public static String parseAndExtractPayload(String res){
        String [] sp = res.split("###");
        if(sp.length == 3){
            return sp[2];
        }
        return "";
    }

    public static int parseAndExtractMessageID(String res){
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

    public static MessageType parseAndExtractMessageType(String res){
        String [] sp = res.split("###");
        if(sp.length == 3){
            if(sp[0].equals(MessageType.MESSAGE.value))
                return MessageType.MESSAGE;
            if(sp[0].equals(MessageType.REPLY.value))
                return MessageType.REPLY;
            if(sp[0].equals(MessageType.ERROR.value))
                return MessageType.ERROR;
        }
        return MessageType.UNKNOWN;
    }

    public Message(boolean replyExpected, MessageType messageType, int messageID, String payload, MessageCallback callback) {
        this.replyExpected = replyExpected;
        this.payload = payload;
        this.callback = callback;
        this.messageType = messageType;
        this.messageID = messageID;
    }

    public String getTransmitMessage() {
        switch (messageType){
            case MESSAGE:
                return MessageType.MESSAGE.value + "###"+ messageID +"###"+payload;
            case ERROR:
                return MessageType.ERROR.value + "ERR###"+ messageID +"###"+payload;
            case REPLY:
                return MessageType.REPLY.value + "R###"+ messageID +"###"+payload;
            default:
                return MessageType.UNKNOWN.value + "###"+ messageID +"###"+payload;
        }

    }

    public boolean isReplyExpected() {
        return replyExpected;
    }

    public MessageCallback getCallback() {
        return callback;
    }

    public int getMessageID() {
        return messageID;
    }
    public MessageType getMessageType() {
        return messageType;
    }
}
