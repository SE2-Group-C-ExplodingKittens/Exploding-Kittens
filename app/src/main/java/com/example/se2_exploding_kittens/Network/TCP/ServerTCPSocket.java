package com.example.se2_exploding_kittens.Network.TCP;

import com.example.se2_exploding_kittens.Message;
import com.example.se2_exploding_kittens.MessageCallback;

public class ServerTCPSocket implements Runnable, TCP{

    //The server just listens to the socket for a connection request (Socket clientSocketConnection = serverSocket.accept();)
    //On the accept method, it blocks until a connection request from a client.
    //this Class is supposed to be created from the clientSocketConnection

    @Override
    public boolean addMessage(Message message) {
        return false;
    }

    @Override
    public void setDefaultCallback(MessageCallback defaultCallback) {

    }

    @Override
    public void endConnection() {

    }

    @Override
    public void run() {

    }
}
