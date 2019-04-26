package com.nishant.tictactoeonline;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/*
    TODO: Add background music and turn off auto rotation
 */


public class MainActivity extends AppCompatActivity {

    Button createGame;
    Button joinGame;
    DatabaseHelper databaseHelper;
    MediaPlayer mediaPlayer;
    MediaPlayer btnPressed;
    Music music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bg_music);
        mediaPlayer.setLooping(true);
        btnPressed = MediaPlayer.create(getApplicationContext(), R.raw.btn_press);
        btnPressed.setLooping(false);
        music = new Music(mediaPlayer, btnPressed);

        createGame = findViewById(R.id.create_game_btn);
        joinGame = findViewById(R.id.join_game_btn);
        databaseHelper = new DatabaseHelper(this);

        createGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnPressed.seekTo(0);
                btnPressed.start();
                Intent i = new Intent(MainActivity.this, CreateGame.class);
                i.putExtra("NUMBER", databaseHelper.getGameNumber());
                i.putExtra("CAME_FROM", "createGame");
                startActivity(i);
            }
        });

        joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnPressed.seekTo(0);
                btnPressed.start();
                Intent i = new Intent(MainActivity.this, JoinGame.class);
                i.putExtra("NUMBER", databaseHelper.getGameNumber());
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    public void onBackPressed()
    {
        finishAffinity();
        System.exit(0);
    }
}