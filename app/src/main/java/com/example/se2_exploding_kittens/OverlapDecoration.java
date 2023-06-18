package com.example.se2_exploding_kittens;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OverlapDecoration extends RecyclerView.ItemDecoration {
    private final int overlap; // The amount of horizontal overlap between cards in pixels

    public OverlapDecoration(int overlap) {
        this.overlap = overlap;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) != 0) {
            outRect.left = outRect.left - overlap;
        }
    }
}

