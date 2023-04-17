package com.example.se2_exploding_kittens.Network.TCP;

import com.example.se2_exploding_kittens.Network.DisconnectedCallback;
import com.example.se2_exploding_kittens.Network.Message;
import com.example.se2_exploding_kittens.Network.MessageCallback;

public interface TCP {

    public boolean addMessage(Message message);

    public void setDefaultCallback(MessageCallback defaultCallback);

    public void setDisconnectCallback(DisconnectedCallback disconnectedCallback);

    public void endConnection();

}
