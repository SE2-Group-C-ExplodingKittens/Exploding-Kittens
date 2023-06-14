package com.example.se2_exploding_kittens;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ChoosePlayerViewHolder extends RecyclerView.ViewHolder {

    private final Button buttonPlayerOne;
    private final Button buttonPlayerTwo;
    private final Button buttonPlayerThree;
    private final Button buttonPlayerFour;
    private final TextView timerTextView;

    private PopupWindow popupWindow;

    public ChoosePlayerViewHolder(View itemView) {
        super(itemView);
        buttonPlayerOne = itemView.findViewById(R.id.buttonFirstPlayer);
        buttonPlayerTwo = itemView.findViewById(R.id.buttonSecondPlayer);
        buttonPlayerThree = itemView.findViewById(R.id.buttonThirdPlayer);
        buttonPlayerFour = itemView.findViewById(R.id.buttonFourthPlayer);
        timerTextView = itemView.findViewById(R.id.textViewCounter);
    }

    public void bindData(String firstPlayerID, String secondPlayerID, String thirdPlayerID, String fourthPlayerID, PopupWindow popupWindow, OnPlayerSelectedListener listener) {
        updateButton(buttonPlayerOne, firstPlayerID);
        updateButton(buttonPlayerTwo, secondPlayerID);
        updateButton(buttonPlayerThree, thirdPlayerID);
        updateButton(buttonPlayerFour, fourthPlayerID);

        this.popupWindow = popupWindow;

        buttonPlayerOne.setOnClickListener(v -> {
            //set playerID to tag
            String playerID = (String) v.getTag();
            //listener if button gets clicked
            listener.onPlayerSelected(playerID);
            //if clicked dismiss popup
            dismissPopup();
        });

        buttonPlayerTwo.setOnClickListener(v -> {
            String playerID = (String) v.getTag();
            listener.onPlayerSelected(playerID);
            dismissPopup();
        });

        buttonPlayerThree.setOnClickListener(v -> {
            String playerID = (String) v.getTag();
            listener.onPlayerSelected(playerID);
            dismissPopup();
        });

        buttonPlayerFour.setOnClickListener(v -> {
            String playerID = (String) v.getTag();
            listener.onPlayerSelected(playerID);
            dismissPopup();
        });
    }

    private void updateButton(Button button, String playerID) {
        if (playerID != null) {
            String text = "Player " + playerID;
            button.setText(text);
            //tag is only the playerID, so we can pass it on to the listener when clicked
            button.setTag(playerID);
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.INVISIBLE);
        }
    }

    private void dismissPopup() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    public void run(Handler handler, PopupWindow popupWindow, OnNoPlayerSelectedListener noPlayerSelectedListener) {
        timerTextView.setText("5");

        // Delay between each update in milliseconds
        final int delay = 1000;
        handler.postDelayed(new Runnable() {
            // Total time in milliseconds
            int remainingTime = 5000;

            @Override
            public void run() {
                if (popupWindow.isShowing()) {
                    // Subtract the delay from the remaining time
                    remainingTime -= delay;
                    // Calculate seconds
                    int seconds = remainingTime / 1000;

                    if (seconds > 0) {
                        timerTextView.setText(String.valueOf(seconds));
                        // Schedule the next update
                        handler.postDelayed(this, delay);
                    } else {
                        // Dismiss the PopupWindow after 5000ms
                        dismissPopup();
                        //
                        noPlayerSelectedListener.onNoPlayerSelected();
                    }
                }
            }
        }, delay);
    }

    public interface OnPlayerSelectedListener {
        //listener if button gets clicked
        void onPlayerSelected(String playerID);
    }

    public interface OnNoPlayerSelectedListener {
        //listener if timer runs out
        void onNoPlayerSelected();
    }
}