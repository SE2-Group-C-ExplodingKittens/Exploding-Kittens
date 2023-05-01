package com.example.se2_exploding_kittens;

import com.example.se2_exploding_kittens.Network.ClientConnectedCallback;
import com.example.se2_exploding_kittens.Network.DisconnectedCallback;
import com.example.se2_exploding_kittens.Network.Message;
import com.example.se2_exploding_kittens.Network.MessageCallback;
import com.example.se2_exploding_kittens.Network.TCP.ClientTCP;
import com.example.se2_exploding_kittens.Network.TCP.MessageCallbackPair;
import com.example.se2_exploding_kittens.Network.TCP.ServerTCPSocket;
import com.example.se2_exploding_kittens.Network.TCP.TCP;
import com.example.se2_exploding_kittens.Network.TCP.TCPServer;

import java.util.ArrayList;

public class NetworkManager implements MessageCallback, ClientConnectedCallback, DisconnectedCallback {

    private TCP connection = null;
    private TCPServer server = null;
    //Contains client connections
    private ArrayList <ServerTCPSocket> serverToClientConnections = new ArrayList<ServerTCPSocket>();
    private ArrayList <MessageCallbackPair> subscribedCallbacks = new ArrayList<MessageCallbackPair>();

    public void sendMessageFromTheClient(Message message) throws IllegalAccessException{
        if(connection instanceof ClientTCP && connection != null){
            connection.addMessage(message);
        }else {
            throw new IllegalAccessException("This is a server connection");
        }
    }

    public void sendMessageFromTheSever(Message message, ServerTCPSocket connection) throws IllegalAccessException{
        if(serverToClientConnections != null && serverToClientConnections.contains(connection)){
            connection.addMessage(message);
        } else {
            throw new IllegalAccessException("This is a client or connection does not exist");
        }
    }

    public void sendMessageBroadcast(Message message) throws IllegalAccessException{
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
            serverToClientConnections.add((ServerTCPSocket) connection);
        }

    }

    public NetworkManager() {

    }

    void runAsServer(int port){
        server = new TCPServer(port,this);
        Thread thread = new Thread(server);
        thread.start();
    }

    void runAsClient(String serverAddress, int port){
        connection = new ClientTCP(serverAddress, port,this);
        Thread thread = new Thread((ClientTCP) connection);
        thread.start();
    }


    public void addServerToClientConnection(ServerTCPSocket connection){
        serverToClientConnections.add(connection);
    }

    public ArrayList <ServerTCPSocket> getServerConnections(){
        return serverToClientConnections;
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

    public void unsubscribeCallbackFromMessageID(MessageCallback callback, int messageID){
        for (MessageCallbackPair mcp : subscribedCallbacks){
            if (mcp.getMessageID() == messageID){
                mcp.removeCallback(callback);
            }
        }
    }

    @Override
    public void responseReceived(String text, Object sender) {
        if(sender instanceof ClientTCP){
            int messageID = Message.parseAndExtractMessageID(text);
            for (MessageCallbackPair mcb:subscribedCallbacks) {
                if(messageID == mcb.getMessageID()){
                    for (MessageCallback cb:mcb.getCallbacks()) {
                        cb.responseReceived(text,sender);
                    }
                }
            }
        }
    }

    @Override
    public void clientConnected(ServerTCPSocket connection) {
        connection.setDefaultCallback(this);
        serverToClientConnections.add(connection);
    }

    @Override
    public void connectionDisconnected(Object connection) {
        if(connection instanceof ServerTCPSocket){
            serverToClientConnections.remove(connection);
        }else if(connection instanceof ClientTCP){
            connection = null;
        }
    }
}
