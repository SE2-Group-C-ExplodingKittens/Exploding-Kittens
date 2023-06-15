package com.example.se2_exploding_kittens;

import android.content.ClipData;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se2_exploding_kittens.game_logic.cards.Card;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;


public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> implements PropertyChangeListener {

    private final ArrayList<Card> cards; // List of cards to display

    public CardAdapter(ArrayList<Card> cards) {
        this.cards = cards;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
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
        Card card = cards.get(position);

        // Set the card image and title
        holder.cardImage.setImageResource(card.getImageResource());

//            TODO Trying to implement the drag and drop function
        holder.cardImage.setOnLongClickListener(v -> {
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
        });


    }

    // Return the size of the dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return cards.size();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("hand")) {
            notifyDataSetChanged();
        }
    }

    // Provide a reference to the views for each card item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView cardImage;

        public ViewHolder(View itemView) {
            super(itemView);
            cardImage = itemView.findViewById(R.id.playingCard);
        }
    }

    public Card getSelectedCard(int position) {
        return cards.get(position);
    }

    public void removeCard(int position) {
        cards.remove(position);
        notifyItemRemoved(position);
    }


}


