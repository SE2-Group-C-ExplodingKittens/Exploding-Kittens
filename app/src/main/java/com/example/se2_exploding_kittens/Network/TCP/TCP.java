package com.example.se2_exploding_kittens.Network.TCP;

import com.example.se2_exploding_kittens.Network.Message;
import com.example.se2_exploding_kittens.Network.MessageCallback;

public interface TCP {

    boolean addMessage(Message message);

    void setDefaultCallback(MessageCallback defaultCallback);

    void endConnection();

}
