package com.example.se2_exploding_kittens;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class OverlapDecoration extends RecyclerView.ItemDecoration {
    private int horizontalOverlapPx; // The amount of horizontal overlap between cards in pixels
    private int startMarginPx; // The margin at the start of the RecyclerView in pixels

    private int verticalOffset; // Creating more space on the top of the cards, for them not to get cut off

    public OverlapDecoration(int horizontalOverlapPx, int startMarginPx, int verticalOffset) {
        this.horizontalOverlapPx = horizontalOverlapPx;
        this.startMarginPx = startMarginPx;
        this.verticalOffset = verticalOffset;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);

        double scaleFactor =  0.50;

        if (position == 0) {
            // The first card should not overlap with anything
            outRect.set(startMarginPx, 0, 0, 0);
//            Creating separate spacing for even and odd cards
        } else if (position != 0 && position% 2 == 0){
            // For other cards, apply the horizontal overlap and a negative margin to move the card up
            outRect.set((int) ((startMarginPx + position * scaleFactor * horizontalOverlapPx)* -1), verticalOffset + (int)((position * scaleFactor) * verticalOffset/2), 0, 0);
            view.setTranslationX(horizontalOverlapPx);
            view.setTranslationY(-horizontalOverlapPx / 2);
        } else {
            outRect.set((int) ((startMarginPx + position * scaleFactor  * horizontalOverlapPx)*-1), 0, 0, 0);
            view.setTranslationX(horizontalOverlapPx);
            view.setTranslationY(horizontalOverlapPx / 2);
        }
    }
}

