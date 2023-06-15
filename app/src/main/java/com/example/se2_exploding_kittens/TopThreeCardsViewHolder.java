package com.example.se2_exploding_kittens;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class TopThreeCardsViewHolder extends RecyclerView.ViewHolder {
    private final ImageView cardOne;
    private final ImageView cardTwo;
    private final ImageView cardThree;
    private final TextView textCardOne;
    private final TextView textCardTwo;
    private final TextView textCardThree;

    public TopThreeCardsViewHolder(View itemView) {
        super(itemView);
        cardOne = itemView.findViewById(R.id.firstCard);
        cardTwo = itemView.findViewById(R.id.secondCard);
        cardThree = itemView.findViewById(R.id.thirdCard);
        textCardOne = itemView.findViewById(R.id.textViewFirstCard);
        textCardTwo = itemView.findViewById(R.id.textViewSecondCard);
        textCardThree = itemView.findViewById(R.id.textViewThirdCard);
    }

    public void bindData(int firstCard, int secondCard, int thirdCard) {
        if (firstCard != 0) {
            cardOne.setImageResource(firstCard);
            cardOne.setVisibility(View.VISIBLE);
        } else {
            cardOne.setVisibility(View.INVISIBLE);
            textCardOne.setVisibility(View.INVISIBLE);
        }

        if (secondCard != 0) {
            cardTwo.setImageResource(secondCard);
            cardTwo.setVisibility(View.VISIBLE);
        } else {
            cardTwo.setVisibility(View.INVISIBLE);
            textCardTwo.setVisibility(View.INVISIBLE);
        }

        if (thirdCard != 0) {
            cardThree.setImageResource(thirdCard);
            cardThree.setVisibility(View.VISIBLE);
        } else {
            cardThree.setVisibility(View.INVISIBLE);
            textCardThree.setVisibility(View.INVISIBLE);
        }
    }

    public void run(Handler handler, PopupWindow popupWindow, TextView timerTextView) {
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
                        popupWindow.dismiss();
                    }
                }
            }
        }, delay);
    }
}
