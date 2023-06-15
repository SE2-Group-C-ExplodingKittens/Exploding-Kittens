package com.example.se2_exploding_kittens;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ChoosePlayerViewHolder extends RecyclerView.ViewHolder {

    private final LinearLayout playerButtonContainer;
    private final TextView timerTextView;

    private PopupWindow popupWindow;

    public ChoosePlayerViewHolder(View itemView) {
        super(itemView);
        TextView textViewChoosePlayer = itemView.findViewById(R.id.textViewChoosePlayer);
        playerButtonContainer = itemView.findViewById(R.id.playerButtonContainer);
        timerTextView = itemView.findViewById(R.id.textViewCounter);
    }

    public void bindData(String firstPlayerID, String secondPlayerID, String thirdPlayerID, String fourthPlayerID, PopupWindow popupWindow, OnPlayerSelectedListener listener) {
        this.popupWindow = popupWindow;

        playerButtonContainer.removeAllViews(); // Clear existing buttons

        if (firstPlayerID != null) {
            addButtonToContainer(firstPlayerID, listener);
        }

        if (secondPlayerID != null) {
            addButtonToContainer(secondPlayerID, listener);
        }

        if (thirdPlayerID != null) {
            addButtonToContainer(thirdPlayerID, listener);
        }

        if (fourthPlayerID != null) {
            addButtonToContainer(fourthPlayerID, listener);
        }
    }

    private void addButtonToContainer(String playerID, OnPlayerSelectedListener listener) {
        Button button = new Button(itemView.getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.width = (int) itemView.getContext().getResources().getDimension(R.dimen.button_width);
        params.height = (int) itemView.getContext().getResources().getDimension(R.dimen.button_height);

        // set spacing = 12dp
        int margin = (int) itemView.getContext().getResources().getDimension(R.dimen.spacing);
        params.setMargins(margin, margin, margin, margin);

        button.setLayoutParams(params);
        String text = "Player " + playerID;
        button.setText(text);
        button.setTag(playerID);
        button.setOnClickListener(v -> {
            String clickedPlayerID = (String) v.getTag();
            listener.onPlayerSelected(clickedPlayerID);
            dismissPopup();
        });
        playerButtonContainer.addView(button);
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
