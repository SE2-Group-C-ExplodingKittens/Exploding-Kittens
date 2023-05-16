package com.example.se2_exploding_kittens;

import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Build;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.se2_exploding_kittens.cards.Cards;

import java.util.ArrayList;


public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

        private ArrayList<Cards> cards; // List of cards to display

        public CardAdapter(ArrayList<Cards> cards) {
            this.cards = cards;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Inflate the card view layout
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_layout, parent, false);
            return new ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // Get the card at the specified position
            Cards card = cards.get(position);

            // Set the card image and title
            holder.cardImage.setImageResource(card.getImageResource());

            holder.cardImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String imageResource = String.valueOf(card.getImageResource());
                    int myPosition = holder.getAdapterPosition();
                    ClipData data = ClipData.newPlainText("card", imageResource);
                    ClipData.Item item = new ClipData.Item(String.valueOf(myPosition));
                    data.addItem(item);

                    // Start the drag operation
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        v.startDragAndDrop(data, shadowBuilder, myPosition, 0);
                    }

                    return true;
                }
            });


        }

        // Return the size of the dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return cards.size();
        }

        // Provide a reference to the views for each card item
        public static class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView cardImage;

            public ViewHolder(View itemView) {
                super(itemView);
                cardImage = itemView.findViewById(R.id.playingCard);
            }
        }

        public void removeItem(int position) {
            cards.remove(position);
            notifyItemRemoved(position);
        }




    }


