package com.nishant.tictactoeonline;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button createGame;
    Button joinGame;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createGame = findViewById(R.id.create_game_btn);
        joinGame = findViewById(R.id.join_game_btn);
        databaseHelper = new DatabaseHelper(this);



        createGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CreateGame.class);
                i.putExtra("NUMBER", databaseHelper.getGameNumber());
                startActivity(i);
            }
        });

        joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, JoinGame.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        finishAffinity();
        System.exit(0);
    }
}