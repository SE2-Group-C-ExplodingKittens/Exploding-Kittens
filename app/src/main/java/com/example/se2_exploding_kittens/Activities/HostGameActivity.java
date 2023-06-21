package com.example.se2_exploding_kittens.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.se2_exploding_kittens.Network.IPUtil;
import com.example.se2_exploding_kittens.Network.LobbyLogic.LobbyBroadcaster;
import com.example.se2_exploding_kittens.Network.TypeOfConnectionRole;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.R;

public class HostGameActivity extends AppCompatActivity {

    private LobbyBroadcaster lb;
    private NetworkManager connection;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(lb != null)
            lb.terminateBroadcasting();
        if(connection != null && (NetworkManager.isNotIdle(connection))) {
            connection.terminateConnection();
        }
    }

    private void hostLobby(String name){
        lb = new LobbyBroadcaster(name, 45000);
        Thread broadcast = new Thread(lb);
        broadcast.start();
        connection = NetworkManager.getInstance();
        connection.runAsServer(45000);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button buttonStartHosting;
        Button buttonStartGame;
        ImageView backButton;
        EditText editTextLobbyName;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_game);

        buttonStartHosting = findViewById(R.id.buttonStartHosting);
        buttonStartGame = findViewById(R.id.buttonStartGame);
        buttonStartGame.setEnabled(false);
        buttonStartGame.setAlpha(0.5f);
        editTextLobbyName = findViewById(R.id.editTextLobbyName);

        backButton = findViewById(R.id.backButton);

        buttonStartHosting.setOnClickListener(v -> {
            String lobbyName = editTextLobbyName.getText().toString().trim();
            if(IPUtil.getLocalBroadcastAddress() == null){
                Toast toast = Toast.makeText(this, "Connect to the local wifi!", Toast.LENGTH_LONG);
                toast.setDuration(Toast.LENGTH_SHORT); // 3 seconds
                toast.setGravity(Gravity.BOTTOM, 0, 100); // Display at the bottom with an offset
                toast.show();
            }else{
                if (!lobbyName.isEmpty()) {
                    hostLobby(lobbyName);
                }else{
                    hostLobby("Lobby");
                }
                buttonStartHosting.setEnabled(false);
                buttonStartHosting.setAlpha(0.5f);

                //  Start the pulsating animation of the button
                enableStartButton(buttonStartGame);

            }

        });

        buttonStartGame.setOnClickListener(v -> {
            lb.terminateBroadcasting();
            Intent intent = new Intent(HostGameActivity.this, GameActivity.class);
            startActivity(intent);
        });

        backButton.setOnClickListener(v -> {
            // Go back to previous screen
            onBackPressed();
        });
    }

    public void enableStartButton(Button startButton){

        startButton.setEnabled(true);
        startButton.setAlpha(1.0f);

        AnimationSet animationSet = new AnimationSet(true);

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.5f);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setRepeatCount(Animation.INFINITE);
        scaleAnimation.setRepeatMode(Animation.REVERSE);

        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);

        startButton.startAnimation(animationSet);
    }




}
