package com.example.se2_exploding_kittens.Network.TCP;

import com.example.se2_exploding_kittens.Network.ClientConnectedCallback;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Runnable{

    ServerSocket serverSocket = null;
    ClientConnectedCallback clientConnectedCallback = null;
    int port = -1;

    public TCPServer(int port){
        this.port = port;
    }

    public TCPServer(int port, ClientConnectedCallback clientConnectedCallback){
        this.port = port;
        this.clientConnectedCallback = clientConnectedCallback;
    }

    private void connectionEstablished(ServerTCPSocket conn){
        if(clientConnectedCallback != null)
            clientConnectedCallback.clientConnected(conn);
    }

    public void setClientConnectedCallback(ClientConnectedCallback clientConnectedCallback){
        this.clientConnectedCallback = clientConnectedCallback;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                ServerTCPSocket serverTCPSocket = new ServerTCPSocket(clientSocket);
                connectionEstablished(serverTCPSocket);
                Thread thread = new Thread(serverTCPSocket);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
