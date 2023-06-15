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

    private final TextView timerTextView;

    public TopThreeCardsViewHolder(View itemView) {
        super(itemView);
        cardOne = itemView.findViewById(R.id.firstCard);
        cardTwo = itemView.findViewById(R.id.secondCard);
        cardThree = itemView.findViewById(R.id.thirdCard);
        textCardOne = itemView.findViewById(R.id.textViewFirstCard);
        textCardTwo = itemView.findViewById(R.id.textViewSecondCard);
        textCardThree = itemView.findViewById(R.id.textViewThirdCard);
        timerTextView = itemView.findViewById(R.id.textViewCounter);
    }

    public void bindData(int firstCard, int secondCard, int thirdCard) {
        updateImage(firstCard, cardOne, textCardOne);
        updateImage(secondCard, cardTwo, textCardTwo);
        updateImage(thirdCard, cardThree, textCardThree);
    }

    private void updateImage(int cardImage, ImageView card, TextView cardText) {
        if (cardImage != 0) {
            card.setImageResource(cardImage);
            card.setVisibility(View.VISIBLE);
        } else {
            card.setVisibility(View.INVISIBLE);
            cardText.setVisibility(View.INVISIBLE);
        }
    }

    public void run(Handler handler, PopupWindow popupWindow) {
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
                        popupWindow.dismiss();
                    }
                }
            }
        }, delay);
    }

    public void dismiss(PopupWindow popupWindow) {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }
}
