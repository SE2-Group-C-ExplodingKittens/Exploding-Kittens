package com.example.se2_exploding_kittens;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class TopThreeCardsViewHolder extends RecyclerView.ViewHolder {
    private ImageView cardOne;
    private ImageView cardTwo;
    private ImageView cardThree;
    private TextView textCardOne;
    private TextView textCardTwo;
    private TextView textCardThree;

    private Handler handler;

    public TopThreeCardsViewHolder(View itemView) {
        super(itemView);
        cardOne = itemView.findViewById(R.id.firstCard);
        cardTwo = itemView.findViewById(R.id.secondCard);
        cardThree = itemView.findViewById(R.id.thirdCard);
        textCardOne = itemView.findViewById(R.id.textViewFirstCard);
        textCardTwo = itemView.findViewById(R.id.textViewSecondCard);
        textCardThree = itemView.findViewById(R.id.textViewThirdCard);
        handler = new Handler(Looper.getMainLooper());
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
}
