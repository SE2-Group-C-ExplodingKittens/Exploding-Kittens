package com.example.se2_exploding_kittens.Network.TCP;

import com.example.se2_exploding_kittens.Network.ClientConnectedCallback;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Runnable{

    ServerSocket serverSocket = null;
    ClientConnectedCallback clientConnectedCallback = null;
    int port = -1;
    boolean running = true;

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

    public void terminateServer(){
        running = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            while (running) {
                Socket clientSocket = serverSocket.accept();
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
