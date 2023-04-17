package com.example.se2_exploding_kittens;

import com.example.se2_exploding_kittens.Network.TCP.ClientTCP;
import com.example.se2_exploding_kittens.Network.TCP.ServerTCPSocket;
import com.example.se2_exploding_kittens.Network.TCP.TCP;

import java.util.ArrayList;

public class NetworkManager implements MessageCallback{

    private TCP connection = null;
    private ArrayList <ServerTCPSocket> serverToClientConnections = null;
    private ArrayList <MessageCallbackPair> subscribedCallbacks = new ArrayList<MessageCallbackPair>();

    private void sendMessageFromTheClient(Message message) throws IllegalAccessException{
        if(connection instanceof ClientTCP && connection != null){
            connection.addMessage(message);
        }else {
            throw new IllegalAccessException("This is a server connection");
        }
    }

    private void sendMessageFromTheSever(Message message, ServerTCPSocket connection) throws IllegalAccessException{
        if(serverToClientConnections != null && serverToClientConnections.contains(connection)){
            connection.addMessage(message);
        } else {
            throw new IllegalAccessException("This is a client or connection does not exist");
        }
    }

    private void sendMessageBroadcast(Message message) throws IllegalAccessException{
        if(connection instanceof ClientTCP){
            connection.addMessage(message);
        }else if(serverToClientConnections != null && serverToClientConnections.size() > 0){
            for(ServerTCPSocket conn: serverToClientConnections){
                conn.addMessage(message);
            }
        }else{
            throw new IllegalAccessException("No connection found.");
        }
    }

    public NetworkManager(TCP connection){
        connection.setDefaultCallback(this);
        if(connection instanceof ClientTCP){
            this.connection = connection;
        }else if(connection instanceof ServerTCPSocket){

        }

    }

    public void addServerToClientConnection(ServerTCPSocket connection){
        serverToClientConnections.add(connection);
    }

    public void subscribeCallbackToMessageID(MessageCallback callback, int messageID){
        boolean callbackAdded = false;
        for (MessageCallbackPair mcp : subscribedCallbacks){
            if (mcp.getMessageID() == messageID){
                mcp.addCallback(callback);
                callbackAdded = true;
            }
        }
        if(callbackAdded == false){
            subscribedCallbacks.add(new MessageCallbackPair(callback, messageID));
        }
    }

    @Override
    public void responseReceived(String text) {
        int messageID = Message.parseAndExtractMessageID(text);

    }
}
