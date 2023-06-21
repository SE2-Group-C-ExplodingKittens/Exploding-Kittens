package com.example.se2_exploding_kittens.Network.LobbyLogic;


import com.example.se2_exploding_kittens.Network.IPUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class LobbyBroadcaster implements Runnable{

    private final String lobbyName;
    private final int hostingPort;
    private boolean terminateBroadcasting = false;

    public LobbyBroadcaster(String lobbyName, int port){
        lobbyName=lobbyName.replaceAll("#","");
        this.lobbyName = lobbyName;
        this.hostingPort = port;
    }

    public void terminateBroadcasting(){
        terminateBroadcasting = true;
    }

    public void broadcastLobbyInfo(String lobbyName) {
        DatagramSocket socket = null;
        try {
            if (lobbyName.length() > 15) {
                lobbyName = lobbyName.substring(0, 15);
            }
            // Create a new DatagramSocket for sending UDP packets
            socket = new DatagramSocket();

            // Define the broadcast address and port number for the UDP packets
            InetAddress broadcastAddress = IPUtil.getLocalBroadcastAddress();
            int port = 51600;

            // Create a byte array to hold the lobby information data
            byte[] lobbyData = (lobbyName+"#"+this.hostingPort).getBytes();

            // Create a new DatagramPacket with the lobby information data, broadcast address, and port number
            DatagramPacket packet = new DatagramPacket(lobbyData, lobbyData.length, broadcastAddress, port);

            // Send the UDP packet
            do{
                socket.send(packet);
                Thread.sleep(200);
            }while (!terminateBroadcasting);




        } catch (IOException e) {
            if(socket != null)
                socket.close();
        } catch (InterruptedException e) {
            //Sonar Cloud: Either re-interrupt this method or rethrow the "InterruptedException" that can be caught here.
            Thread.currentThread().interrupt();
        }finally {
            // Close the socket when we're done sending packets
            if(socket != null)
                socket.close();
        }
    }

    @Override
    public void run() {
        broadcastLobbyInfo(lobbyName);
    }
}
