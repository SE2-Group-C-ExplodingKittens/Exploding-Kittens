package com.example.se2_exploding_kittens;

import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class TopThreeCardsViewHolder extends RecyclerView.ViewHolder {

    private final LinearLayout linearCards;
    private final TextView timerTextView;
    private int cardCount;

    public TopThreeCardsViewHolder(View itemView) {
        super(itemView);
        linearCards = itemView.findViewById(R.id.linearCards);
        timerTextView = itemView.findViewById(R.id.textViewCounter);
        cardCount = 0;
    }

    public void bindData(int firstCard, int secondCard, int thirdCard) {
        LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
        linearCards.removeAllViews();

        addCardView(inflater, firstCard);
        addCardView(inflater, secondCard);
        addCardView(inflater, thirdCard);
    }

    private void addCardView(LayoutInflater inflater, int cardImage) {
        if (cardImage != 0) {
            cardCount++;
            View cardView = inflater.inflate(R.layout.three_cards_single_card, linearCards, false);
            ImageView imageView = cardView.findViewById(R.id.cardImageView);
            TextView textView = cardView.findViewById(R.id.cardTextView);

            imageView.setImageResource(cardImage);
            String text = "Card " + cardCount;
            textView.setText(text);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

            // set spacing = 12dp
            int spacing = (int) itemView.getContext().getResources().getDimension(R.dimen.spacing);
            layoutParams.setMargins(spacing, spacing, spacing, spacing);

            layoutParams.gravity = Gravity.CENTER;
            cardView.setLayoutParams(layoutParams);

            linearCards.addView(cardView);
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
