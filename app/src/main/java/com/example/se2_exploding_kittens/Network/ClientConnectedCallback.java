package com.example.se2_exploding_kittens.Network;

import com.example.se2_exploding_kittens.Network.TCP.ServerTCPSocket;

public interface ClientConnectedCallback {
    public void clientConnected(ServerTCPSocket connection);
}
