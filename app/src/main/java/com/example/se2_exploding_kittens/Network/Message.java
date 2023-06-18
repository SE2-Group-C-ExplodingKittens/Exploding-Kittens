package com.example.se2_exploding_kittens.Network;

import com.example.se2_exploding_kittens.game_logic.cards.Card;

public class Message {
    private MessageType messageType;
    private String payload;
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
        if(sp.length >= 2){
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
        if(sp.length >= 2){
            if(sp[0].equals(MessageType.MESSAGE.value))
                return MessageType.MESSAGE;
            if(sp[0].equals(MessageType.REPLY.value))
                return MessageType.REPLY;
            if(sp[0].equals(MessageType.ERROR.value))
                return MessageType.ERROR;
        }
        return MessageType.UNKNOWN;
    }

    public Message(MessageType messageType, int messageID, String payload) {
        this.payload = payload;

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
            case CHECKED_DETAILS:
                return MessageType.CHECKED_DETAILS.value + "D###" + messageID + "###" + payload;
            default:
                return MessageType.UNKNOWN.value + "###"+ messageID +"###"+ payload;
        }
    }

    public int getMessageID() {
        return messageID;
    }
    public MessageType getMessageType() {
        return messageType;
    }
}
