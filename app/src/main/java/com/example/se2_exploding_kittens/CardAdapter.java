package com.example.se2_exploding_kittens;

import android.content.ClipData;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se2_exploding_kittens.game_logic.cards.Card;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> implements PropertyChangeListener {

    private final CopyOnWriteArrayList<Card> cards; // List of cards to display
    private final HelpAskListener helpAskListener;

    public CardAdapter(CopyOnWriteArrayList<Card> cards, HelpAskListener helpAskListener) {
        this.cards = cards;
        this.helpAskListener = helpAskListener;
    }

    public interface HelpAskListener {
        void askForHelp(Card card);
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the card view layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout, parent, false);
        return new ViewHolder(view, helpAskListener);
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

            holder.helpButton.setVisibility(View.VISIBLE);


            // Start the drag operation
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                v.startDragAndDrop(data, shadowBuilder, myPosition, 0);
            }

            holder.helpButton.setOnClickListener(v1 -> holder.helpAskListener.askForHelp(card));

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
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
        if (evt.getPropertyName().equals("handCardRemoved")) {
            final int removedIndex = (int) evt.getNewValue();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    notifyItemRemoved(removedIndex);
                }
            });
        }
    }

    // Provide a reference to the views for each card item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView cardImage;
        public Button helpButton;

        public final HelpAskListener helpAskListener;

        public ViewHolder(View itemView, HelpAskListener helpAskListener) {
            super(itemView);
            cardImage = itemView.findViewById(R.id.playingCard);
            helpButton = itemView.findViewById(R.id.help_button);
            this.helpAskListener = helpAskListener;
        }
    }

    public Card getSelectedCard(int position) {
        return cards.get(position);
    }

    public void removeCard(int position) {
        cards.remove(position);
        notifyDataSetChanged();
    }

}


