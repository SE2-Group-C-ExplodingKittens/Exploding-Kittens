package com.example.se2_exploding_kittens.Network.TCP;

import com.example.se2_exploding_kittens.ConnectionState;
import com.example.se2_exploding_kittens.Message;
import com.example.se2_exploding_kittens.MessageCallback;
import com.example.se2_exploding_kittens.MessageType;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ClientTCP implements Runnable, TCP{

    private String serverAddress;
    private int serverPort;
    private ArrayList <Message> messages = new ArrayList<Message>();
    //private Message message;
    private MessageCallback defaultCallback = null;
    private String response = null;
    private Socket clientSocket = null;
    private ConnectionState connState = ConnectionState.IDLE;

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



    public boolean addMessage(Message message) {
        if(connState == ConnectionState.CONNECTED){
            messages.add(message);
            return true;
        }else {
            return false;
        }

    }

    private boolean checkResponseMatch(String res, Message m){
        if(Message.parseAndExtractMessageID(res) == m.getMessageID()){
            if(Message.parseAndExtractMessageType(res) == MessageType.REPLY){
                return true;
            }
        }
        return false;
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

//    private void receiveReply(BufferedReader in) throws IOException {
//
//        while(messages.get(0).isReplyExpected()){
//            response = in.readLine();
//            if(checkResponseMatch(response,messages.get(0))){
//                if(messages.get(0).getCallback() != null) {
//                    messages.get(0).getCallback().responseReceived(response);
//                }else{
//                    defaultCallback.responseReceived(response);
//                }
//                break;
//            }else {
//                //keep listening until reply is received
//                defaultCallback.responseReceived(response);
//            }
//        }
//    }

    private void listenForMessages(BufferedReader in) throws IOException {
        if (in.ready()) {
            response = in.readLine();
            if (defaultCallback != null) {
                defaultCallback.responseReceived(response);
            }
        }
    }

    public void endConnection(){
        connState = ConnectionState.DISCONNECTING;
    }

    @Override
    public void run() {
        if(connect()){
            try {
                DataOutputStream out = new DataOutputStream (clientSocket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.US_ASCII));

                while (connState == ConnectionState.CONNECTED){
                    //wait for messages
                    while (messages.size() == 0) {
                        //poll for messages every 5ms
                        listenForMessages(in);
                        Thread.sleep(20);
                    }

                    listenForMessages(in);

                    //drop empty messages
                    if(messages.get(0) == null){
                        messages.remove(0);
                        continue;
                    }else{
                        out.writeBytes(messages.get(0).getTransmitMessage() + "\n");
//                        receiveReply(in);
                        messages.remove(0);
                    }
                }

                if(connState == ConnectionState.DISCONNECTING){
                    connState = ConnectionState.DISCONNECTED;
                    out.close();
                    in.close();
                    clientSocket.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}
