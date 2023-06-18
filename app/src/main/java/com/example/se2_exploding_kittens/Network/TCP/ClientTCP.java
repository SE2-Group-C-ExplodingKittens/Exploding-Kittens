package com.example.se2_exploding_kittens.Network.TCP;

import com.example.se2_exploding_kittens.Network.ConnectionState;
import com.example.se2_exploding_kittens.Network.DisconnectedCallback;
import com.example.se2_exploding_kittens.Network.Message;
import com.example.se2_exploding_kittens.Network.MessageCallback;
import com.example.se2_exploding_kittens.Network.MessageType;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ClientTCP implements Runnable, TCP{

    private String serverAddress;
    private int serverPort;
    private ArrayList <Message> messages = new ArrayList<Message>();

    private MessageCallback defaultCallback = null;
    private DisconnectedCallback disconnectedCallback = null;
    private Socket clientSocket = null;
    private ConnectionState connState = ConnectionState.IDLE;
    private DataOutputStream out = null;
    private BufferedReader in = null;

    public ClientTCP(String serverAddress, int serverPort, MessageCallback defaultCallback) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.defaultCallback = defaultCallback;
    }

    public ClientTCP(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void setDefaultCallback(MessageCallback defaultCallback){
        this.defaultCallback = defaultCallback;
    }

    public void setDisconnectCallback(DisconnectedCallback disconnectedCallback){
        this.disconnectedCallback = disconnectedCallback;
    }


    public boolean addMessage(Message message) {
        if(connState == ConnectionState.CONNECTED){
            messages.add(message);
            return true;
        }else {
            return false;
        }
    }


    private boolean connect(){
        try {
            clientSocket = new Socket(serverAddress, serverPort);
            connState = ConnectionState.CONNECTED;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkConnectionDisconnected(Socket connection){
        //tcp sends EOF on disconnect
        if (connection.isClosed() || connState != ConnectionState.CONNECTED) {
            connState = ConnectionState.DISCONNECTING;
            return true;
        }
        return false;
    }

    private void listenForMessages(BufferedReader in) throws IOException {
        String response = null;
        response = in.readLine();
        if(response == null){
            //EOF sent
            connState = ConnectionState.DISCONNECTING;
        }else {
            if (defaultCallback != null) {
                defaultCallback.responseReceived(response, this);
            }
        }
    }

    public void endConnection(){
        connState = ConnectionState.DISCONNECTING;
        try {
            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startSenderThread(){
        //separate thread to write
        new Thread(() -> {
            try {
                while (connState == ConnectionState.CONNECTED){
                    //drop empty messages
                    if(messages.size() == 0){
                        continue;
                    }
                    if(messages.get(0) == null){
                        messages.remove(0);
                        continue;
                    }else{
                        out.writeBytes(messages.get(0).getTransmitMessage() + "\n");
                        messages.remove(0);
                    }
                    Thread.sleep(20);
                }
            } catch (IOException e) {
                connState = ConnectionState.DISCONNECTING;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @Override
    public void run() {
        if(connect()){
            try {
                out = new DataOutputStream (clientSocket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.US_ASCII));

                startSenderThread();

                while (connState == ConnectionState.CONNECTED){
                    //wait for messages
                    if(checkConnectionDisconnected(clientSocket)) {
                        break;
                    }
                    listenForMessages(in);
                }

                if(connState == ConnectionState.DISCONNECTING){
                    connState = ConnectionState.DISCONNECTED;
                    out.close();
                    in.close();
                    clientSocket.close();
                    if(disconnectedCallback != null) {
                        disconnectedCallback.connectionDisconnected(this);
                    }
                }

            } catch (IOException e) {
                connState = ConnectionState.DISCONNECTED;
                try {
                    out.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try {
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try {
                    clientSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if(disconnectedCallback != null)
                    disconnectedCallback.connectionDisconnected(this);
            }
        }
    }

}
