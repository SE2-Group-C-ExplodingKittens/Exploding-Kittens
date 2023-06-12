package com.example.se2_exploding_kittens;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

public class ChoosePlayerViewHolder extends RecyclerView.ViewHolder {

    private Button buttonPlayerOne;
    private Button buttonPlayerTwo;
    private Button buttonPlayerThree;
    private Button buttonPlayerFour;

    private Handler handler;

    public ChoosePlayerViewHolder(View itemView) {
        super(itemView);
        buttonPlayerOne = itemView.findViewById(R.id.buttonFirstPlayer);
        buttonPlayerTwo = itemView.findViewById(R.id.buttonSecondPlayer);
        buttonPlayerThree = itemView.findViewById(R.id.buttonThirdPlayer);
        buttonPlayerFour = itemView.findViewById(R.id.buttonFourthPlayer);
        handler = new Handler(Looper.getMainLooper());

    }

    public void bindData(String firstPlayerID, String secondPlayerID, String thirdPlayerID, String fourthPlayerID) {
        updateButton(buttonPlayerOne, firstPlayerID);
        updateButton(buttonPlayerTwo, secondPlayerID);
        updateButton(buttonPlayerThree, thirdPlayerID);
        updateButton(buttonPlayerFour, fourthPlayerID);
    }

    private void updateButton(Button button, String playerID) {
        if (playerID != null) {
            String text = "Player " + playerID;
            button.setText(text);
            button.setTag(text);
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.INVISIBLE);
        }
    }
}
