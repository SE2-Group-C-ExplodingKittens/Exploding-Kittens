package com.example.se2_exploding_kittens;

import com.example.se2_exploding_kittens.Network.ClientConnectedCallback;
import com.example.se2_exploding_kittens.Network.DisconnectedCallback;
import com.example.se2_exploding_kittens.Network.GameManager;
import com.example.se2_exploding_kittens.Network.Message;
import com.example.se2_exploding_kittens.Network.MessageCallback;
import com.example.se2_exploding_kittens.Network.MessageType;
import com.example.se2_exploding_kittens.Network.TCP.ClientTCP;
import com.example.se2_exploding_kittens.Network.TCP.MessageCallbackPair;
import com.example.se2_exploding_kittens.Network.TCP.ServerTCPSocket;
import com.example.se2_exploding_kittens.Network.TCP.TCP;
import com.example.se2_exploding_kittens.Network.TCP.TCPServer;
import com.example.se2_exploding_kittens.Network.TypeOfConnectionRole;
import com.example.se2_exploding_kittens.game_logic.cards.Card;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class NetworkManager implements MessageCallback, ClientConnectedCallback, DisconnectedCallback, Serializable {

    public static final int TEST_MESSAGE_ID = 9999;

    private static NetworkManager networkManager = null;

    private TCP connection = null;
    private TCPServer server = null;

    private TypeOfConnectionRole connectionRole;

    //Contains client connections
    private CopyOnWriteArrayList <ServerTCPSocket> serverToClientConnections = new CopyOnWriteArrayList<ServerTCPSocket>();
    private CopyOnWriteArrayList <MessageCallbackPair> subscribedCallbacks = new CopyOnWriteArrayList<MessageCallbackPair>();

    private CopyOnWriteArrayList <ClientConnectedCallback> connectedCallbacks = new CopyOnWriteArrayList<ClientConnectedCallback>();

    private CopyOnWriteArrayList <DisconnectedCallback> disconnectedCallback = new CopyOnWriteArrayList<DisconnectedCallback>();

    public static synchronized NetworkManager getInstance()
    {
        if (networkManager == null)
            networkManager = new NetworkManager();

        return networkManager;
    }

    public static boolean isServer(NetworkManager networkManager) {
        if(networkManager == null){
            return false;
        }
        return networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER;
    }

    public static boolean isClient(NetworkManager networkManager) {
        if(networkManager == null){
            return false;
        }
        return networkManager.getConnectionRole() == TypeOfConnectionRole.CLIENT;
    }

    public static boolean isNotIdle(NetworkManager networkManager) {
        if(networkManager == null){
            return false;
        }
        return networkManager.getConnectionRole() != TypeOfConnectionRole.IDLE;
    }

    public void sendMessageFromTheClient(Message message) throws IllegalAccessException{
        if(connection instanceof ClientTCP){
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

    private NetworkManager() {
        connectionRole = TypeOfConnectionRole.IDLE;
    }

    private void clearAllCallbacks(){
        for(int i = 0; i < connectedCallbacks.size(); i++){
            connectedCallbacks.remove(0);
        }
        for(int i = 0; i < disconnectedCallback.size(); i++){
            disconnectedCallback.remove(0);
        }
        for(int i = 0; i < subscribedCallbacks.size(); i++){
            subscribedCallbacks.remove(0);
        }
    }

    public void runAsServer(int port){
        clearAllCallbacks();
        connectionRole = TypeOfConnectionRole.SERVER;
        server = new TCPServer(port,this);
        Thread thread = new Thread(server);
        thread.start();
    }

    public void runAsClient(String serverAddress, int port){
        clearAllCallbacks();
        connectionRole = TypeOfConnectionRole.CLIENT;
        connection = new ClientTCP(serverAddress, port,this);
        connection.setDisconnectCallback(this);
        Thread thread = new Thread((ClientTCP) connection);
        thread.start();
    }

    public void terminateConnection(){
        if(connectionRole == TypeOfConnectionRole.SERVER){
            for ( ServerTCPSocket con: serverToClientConnections) {
                con.endConnection();
            }
            server.terminateServer();
            connectionRole = TypeOfConnectionRole.IDLE;
            clearAllCallbacks();
        }
        if(connectionRole == TypeOfConnectionRole.CLIENT){
            connection.endConnection();
            connectionRole = TypeOfConnectionRole.IDLE;
            clearAllCallbacks();
        }
    }


    public void addServerToClientConnection(ServerTCPSocket connection){
        serverToClientConnections.add(connection);
    }

    public CopyOnWriteArrayList <ServerTCPSocket> getServerConnections(){
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
        if(!callbackAdded){
            subscribedCallbacks.add(new MessageCallbackPair(callback, messageID));
        }
    }

    public void subscribeToClientConnectedCallback(ClientConnectedCallback callback){
        if(callback != null)
            connectedCallbacks.add(callback);
    }

    public void unsubscribeToClientConnectedCallback(ClientConnectedCallback callback){
        if(callback != null)
            connectedCallbacks.remove(callback);
    }

    public void subscribeToDisconnectedCallback(DisconnectedCallback callback){
        if(callback != null)
            disconnectedCallback.add(callback);
    }

    public void unsubscribeToDisconnectedCallback(DisconnectedCallback callback){
        if(callback != null)
            disconnectedCallback.remove(callback);
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
            int messageID = Message.parseAndExtractMessageID(text);
            for (MessageCallbackPair mcb:subscribedCallbacks) {
                if(messageID == mcb.getMessageID()){
                    for (MessageCallback cb:mcb.getCallbacks()) {
                        cb.responseReceived(text,sender);
                    }
                }
            }
    }

    @Override
    public void clientConnected(ServerTCPSocket connection) {
        connection.setDefaultCallback(this);
        connection.setDisconnectCallback(this);
        serverToClientConnections.add(connection);
        for(ClientConnectedCallback cb:connectedCallbacks){
            cb.clientConnected(connection);
        }
    }

    @Override
    public void connectionDisconnected(Object connection) {
        if(connection instanceof ServerTCPSocket){
            serverToClientConnections.remove(connection);
        }else if(connection instanceof ClientTCP){
            connection = null;
        }
        for(DisconnectedCallback cb:disconnectedCallback){
            cb.connectionDisconnected(connection);
        }
    }

    public TypeOfConnectionRole getConnectionRole() {
        return connectionRole;
    }

    public void setConnectionRole(TypeOfConnectionRole connectionRole) {
        this.connectionRole = connectionRole;
    }

    public void sendCheckeCard(Card card) {
        String cardId = Integer.toString(card.getCardID());
        Message m = new Message(MessageType.CHECKED_DETAILS, GameManager.GAME_MANAGER_MESSAGE_CHECKED_CARD, cardId);

        try {
            networkManager.sendMessageBroadcast(m);
        } catch (IllegalAccessException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
