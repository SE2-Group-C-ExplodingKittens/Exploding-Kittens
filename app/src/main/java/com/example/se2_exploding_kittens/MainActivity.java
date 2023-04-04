package com.example.se2_exploding_kittens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button buttonJoinGame;

    private void addEvtHandler(Button btn, View.OnClickListener listener){
        btn.setOnClickListener(listener);
    }

    public void openJoinGameActivity(){
        Intent intent = new Intent(this, JoinGameActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonJoinGame = (Button)findViewById(R.id.buttonJoinGame);
        addEvtHandler(buttonJoinGame, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openJoinGameActivity();
            }
        });
    }
}