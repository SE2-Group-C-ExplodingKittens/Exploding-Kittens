package com.example.se2_exploding_kittens;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class FavorCardFragment extends Fragment {
    private TextView textViewCounter;
    private int counter = 10;
    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.favor_card_choose_card_fragment, container, false);
        textViewCounter = rootView.findViewById(R.id.textViewCounter);

        //Start at 10
        textViewCounter.setText(String.valueOf(counter));

        // Start the countdown timer
        handler = new Handler(Looper.getMainLooper());
        startTimer();

        return rootView;
    }

    private void startTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                counter--;

                // Update the TextView with the new counter value

                if (counter > 0) {
                    textViewCounter.setText(String.valueOf(counter));
                    // Schedule the next update after 1 second
                    handler.postDelayed(this, 1000);
                } else {
                    // Counter has reached 0, remove the fragment
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.remove(FavorCardFragment.this).commit();
                }
            }
        }, 1000); // Delay in milliseconds (1 second)
    }
}