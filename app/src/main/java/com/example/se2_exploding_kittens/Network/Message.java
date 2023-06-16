package com.example.se2_exploding_kittens.Network;

public class Message {
    private final MessageType messageType;
    private final String payload;
    private final int messageID;

    public static String parseAndExtractPayload(String res) {
        String[] sp = res.split("###");
        if (sp.length == 3) {
            return sp[2];
        }
        return "";
    }

    public static int parseAndExtractMessageID(String res) {
        String[] sp = res.split("###");
        if (sp.length == 3) {
            try {
                return Integer.parseInt(sp[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return -1;
    }

    public Message(MessageType messageType, int messageID, String payload) {
        this.payload = payload;

        this.messageType = messageType;
        this.messageID = messageID;
    }

    public String getTransmitMessage() {
        switch (messageType) {
            case MESSAGE:
                return MessageType.MESSAGE.value + "###" + messageID + "###" + payload;
            case ERROR:
                return MessageType.ERROR.value + "ERR###" + messageID + "###" + payload;
            case REPLY:
                return MessageType.REPLY.value + "R###" + messageID + "###" + payload;
            case CHECKED_DETAILS:
                return MessageType.CHECKED_DETAILS.value + "D###" + messageID + "###" + payload;
            default:
                return MessageType.UNKNOWN.value + "###" + messageID + "###" + payload;
        }
    }
}
