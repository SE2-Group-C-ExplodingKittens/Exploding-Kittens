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

public class NetworkManager implements MessageCallback, ClientConnectedCallback, DisconnectedCallback, Serializable {

    public static final int TEST_MESSAGE_ID = 9999;

    private static NetworkManager networkManager = null;

    private TCP connection = null;
    private TCPServer server = null;

    private TypeOfConnectionRole connectionRole;

    //Contains client connections
    private final ArrayList<ServerTCPSocket> serverToClientConnections = new ArrayList<>();
    private final ArrayList<MessageCallbackPair> subscribedCallbacks = new ArrayList<>();

    private final ArrayList<ClientConnectedCallback> connectedCallbacks = new ArrayList<>();

    private final ArrayList<DisconnectedCallback> disconnectedCallback = new ArrayList<>();

    public static synchronized NetworkManager getInstance() {
        if (networkManager == null)
            networkManager = new NetworkManager();

        return networkManager;
    }

    public void sendMessageFromTheClient(Message message) throws IllegalAccessException {
        if (connection instanceof ClientTCP) {
            connection.addMessage(message);
        } else {
            throw new IllegalAccessException("This is a server connection");
        }
    }

    public void sendMessageFromTheSever(Message message, ServerTCPSocket connection) throws IllegalAccessException {
        if (serverToClientConnections.contains(connection)) {
            connection.addMessage(message);
        } else {
            throw new IllegalAccessException("This is a client or connection does not exist");
        }
    }

    public void sendMessageBroadcast(Message message) throws IllegalAccessException {
        if (connection instanceof ClientTCP) {
            connection.addMessage(message);
        } else if (serverToClientConnections.size() > 0) {
            for (ServerTCPSocket conn : serverToClientConnections) {
                conn.addMessage(message);
            }
        } else {
            throw new IllegalAccessException("No connection found.");
        }
    }

    private NetworkManager() {
        connectionRole = TypeOfConnectionRole.IDLE;
    }

    private void clearAllCallbacks() {
        for (ClientConnectedCallback cb : connectedCallbacks) {
            connectedCallbacks.remove(cb);
        }
        for (DisconnectedCallback dc : disconnectedCallback) {
            disconnectedCallback.remove(dc);
        }
        for (MessageCallbackPair mc : subscribedCallbacks) {
            subscribedCallbacks.remove(mc);
        }
    }

    public void runAsServer(int port) {
        clearAllCallbacks();
        connectionRole = TypeOfConnectionRole.SERVER;
        server = new TCPServer(port, this);
        Thread thread = new Thread(server);
        thread.start();
    }

    public void runAsClient(String serverAddress, int port) {
        clearAllCallbacks();
        connectionRole = TypeOfConnectionRole.CLIENT;
        connection = new ClientTCP(serverAddress, port, this);
        Thread thread = new Thread((ClientTCP) connection);
        thread.start();
    }

    public void terminateConnection() {
        if (connectionRole == TypeOfConnectionRole.SERVER) {
            for (ServerTCPSocket con : serverToClientConnections) {
                con.endConnection();
            }
            server.terminateServer();
            connectionRole = TypeOfConnectionRole.IDLE;
        }
        if (connectionRole == TypeOfConnectionRole.CLIENT) {
            connection.endConnection();
        }
    }

    public ArrayList<ServerTCPSocket> getServerConnections() {
        return serverToClientConnections;
    }

    public void subscribeCallbackToMessageID(MessageCallback callback, int messageID) {
        boolean callbackAdded = false;
        for (MessageCallbackPair mcp : subscribedCallbacks) {
            if (mcp.getMessageID() == messageID) {
                mcp.addCallback(callback);
                callbackAdded = true;
            }
        }
        if (!callbackAdded) {
            subscribedCallbacks.add(new MessageCallbackPair(callback, messageID));
        }
    }

    public void unsubscribeToClientConnectedCallback(ClientConnectedCallback callback) {
        if (callback != null)
            connectedCallbacks.remove(callback);
    }

    public void subscribeToDisconnectedCallback(DisconnectedCallback callback) {
        if (callback != null)
            disconnectedCallback.add(callback);
    }

    public void unsubscribeToDisconnectedCallback(DisconnectedCallback callback) {
        if (callback != null)
            disconnectedCallback.remove(callback);
    }

    public void unsubscribeCallbackFromMessageID(MessageCallback callback, int messageID) {
        for (MessageCallbackPair mcp : subscribedCallbacks) {
            if (mcp.getMessageID() == messageID) {
                mcp.removeCallback(callback);
            }
        }
    }

    @Override
    public void responseReceived(String text, Object sender) {
        int messageID = Message.parseAndExtractMessageID(text);
        for (MessageCallbackPair mcb : subscribedCallbacks) {
            if (messageID == mcb.getMessageID()) {
                for (MessageCallback cb : mcb.getCallbacks()) {
                    cb.responseReceived(text, sender);
                }
            }
        }
    }

    @Override
    public void clientConnected(ServerTCPSocket connection) {
        connection.setDefaultCallback(this);
        serverToClientConnections.add(connection);
        for (ClientConnectedCallback cb : connectedCallbacks) {
            cb.clientConnected(connection);
        }
    }

    @Override
    public void connectionDisconnected(Object connection) {
        if (connection instanceof ServerTCPSocket) {
            serverToClientConnections.remove(connection);
        } else if (connection instanceof ClientTCP) {
            connection = null;
        }
        for (DisconnectedCallback cb : disconnectedCallback) {
            cb.connectionDisconnected(connection);
        }
    }

    public TypeOfConnectionRole getConnectionRole() {
        return connectionRole;
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
