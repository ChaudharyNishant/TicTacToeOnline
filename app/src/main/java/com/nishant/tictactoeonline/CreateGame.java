package com.nishant.tictactoeonline;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.Random;

public class CreateGame extends AppCompatActivity {

    DatabaseReference databaseReference;
    DatabaseHelper databaseHelper;
    Button btn[][];
    TextView gameNumber;
    TextView gameStatus;
    Random rand;
    String cameFrom;
    long k;
    long joined;
    int n;
    boolean created;
    boolean won;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        databaseHelper = new DatabaseHelper(this);
        databaseHelper.newPlayer();
        rand = new Random();
        gameNumber = findViewById(R.id.game_number_tv);
        gameStatus = findViewById(R.id.game_status);
        btn = new Button[3][3];
        btn[0][0] = findViewById(R.id.a00);
        btn[0][1] = findViewById(R.id.a01);
        btn[0][2] = findViewById(R.id.a02);
        btn[1][0] = findViewById(R.id.a10);
        btn[1][1] = findViewById(R.id.a11);
        btn[1][2] = findViewById(R.id.a12);
        btn[2][0] = findViewById(R.id.a20);
        btn[2][1] = findViewById(R.id.a21);
        btn[2][2] = findViewById(R.id.a22);
        k = 1;
        won = false;
        created = false;
        n = Objects.requireNonNull(getIntent().getExtras()).getInt("NUMBER");
        cameFrom = getIntent().getExtras().getString("CAME_FROM");
        FirebaseApp.initializeApp(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        if(n != -1) {
            if(cameFrom.equals("createGame")) {
                databaseReference.child("test" + n).child("joined").setValue(0);
                databaseReference.child("test" + n).child("k").setValue(1);
                for (int i = 0; i < 3; i++)
                    for (int j = 0; j < 3; j++)
                        databaseReference.child("test" + n).child(String.valueOf(i)).child("a" + j).setValue(".");
                n = -1;
            }
            else if(cameFrom.equals("joinGame")) {
                joined = 1;
                databaseHelper.updatePlayer(n, 2);
            }
        }
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++)
                btn[i][j].setText(".");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!created) {
                    if(n == -1) {
                        do {
                            Log.d("Create Game", "while" + n);
                            n = rand.nextInt() % 10;
                            if (n < 0)
                                n *= -1;
                        }while((long)dataSnapshot.child("test" + n).child("joined").getValue() != 0);
                        databaseHelper.updatePlayer(n, 1);
                    }
                    gameNumber.setText(getString(R.string.game_hash, n));
                    databaseReference.child("test" + n).child("joined").setValue(joined + 1);
                    created = true;
                }
                for(int i = 0; i < 3; i++) {
                    DataSnapshot data = dataSnapshot.child("test" + n).child(String.valueOf(i));
                    for(int j = 0; j < 3; j++) {
                        String c = (String)data.child("a" + j).getValue();
                        btn[i][j].setText(c);
                        if(c != null && c.equals("O"))
                            btn[i][j].setBackgroundResource(R.drawable.ball);
                        else if(c != null && c.equals("X"))
                            btn[i][j].setBackgroundResource(R.drawable.cross);
                        updateStatus();
                    }
                }
                k = (long)dataSnapshot.child("test" + n).child("k").getValue();
                joined = (long)dataSnapshot.child("test" + n).child("joined").getValue();
                if(!won) {
                    if ((k % 2 == 1 && databaseHelper.getPlayerNumber() == 1) || (k % 2 == 0 && databaseHelper.getPlayerNumber() == 2))
                        gameStatus.setText(R.string.your_turn);
                    else if ((k % 2 == 1 && databaseHelper.getPlayerNumber() == 2) || (k % 2 == 0 && databaseHelper.getPlayerNumber() == 1))
                        gameStatus.setText(R.string.opponent_turn);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Main Activity", "error: " + databaseError);
            }
        });

        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++) {
                final int finalI = i;
                final int finalJ = j;
                btn[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(k <= 9 && btn[finalI][finalJ].getText().equals(".") && k % 2 != databaseHelper.getPlayerNumber() - 1 && !won) {
                            String c = (databaseHelper.getPlayerNumber() == 1) ? "X" : "O";
                            databaseReference.child("test" + n).child(String.valueOf(finalI)).child("a" + finalJ).setValue(c);
                            databaseReference.child("test" + n).child("k").setValue(k + 1);
                            if(c.equals("O"))
                                btn[finalI][finalJ].setBackgroundResource(R.drawable.ball);
                            else
                                btn[finalI][finalJ].setBackgroundResource(R.drawable.cross);
                            Music.btnPressed.seekTo(0);
                            Music.btnPressed.start();
                        }
                    }
                });
            }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Music.mediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Music.mediaPlayer.pause();
    }

    @Override
    public void onBackPressed() {
        databaseHelper.newPlayer();
        databaseReference.child("test" + n).child("joined").setValue(joined - 1);
        databaseReference.child("test" + n).child("k").setValue(1);
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++)
                databaseReference.child("test" + n).child(String.valueOf(i)).child("a" + j).setValue(".");
        Music.mediaPlayer.pause();
        Music.btnPressed.seekTo(0);
        Music.btnPressed.start();
        Intent i = new Intent(CreateGame.this, MainActivity.class);
        startActivity(i);
    }

    public void updateStatus() {
        for(int i = 0; i < 3; i++)
            if(btn[i][0].getText().equals(btn[i][1].getText()) && btn[i][1].getText().equals(btn[i][2].getText()) && !btn[i][1].getText().equals("."))
                update(btn[i][1].getText().toString());
        for(int j = 0; j < 3; j++)
            if(btn[0][j].getText().equals(btn[1][j].getText()) && btn[1][j].getText().equals(btn[2][j].getText()) && !btn[1][j].getText().equals("."))
                update(btn[1][j].getText().toString());
        if(btn[0][0].getText().equals(btn[1][1].getText()) && btn[1][1].getText().equals(btn[2][2].getText()) && !btn[1][1].getText().equals("."))
            update(btn[1][1].getText().toString());
        if(btn[0][2].getText().equals(btn[1][1].getText()) && btn[1][1].getText().equals(btn[2][0].getText()) && !btn[1][1].getText().equals("."))
            update(btn[1][1].getText().toString());
    }

    public void update(String winner) {
        gameStatus.setText(getString(R.string.won, winner));
        if(databaseHelper.getPlayerNumber() == 1) {
            if(winner.equals("X"))
                gameStatus.setText(R.string.you_won);
            else
                gameStatus.setText(R.string.you_lost);
        }
        else {
            if(winner.equals("O"))
                gameStatus.setText(R.string.you_won);
            else
                gameStatus.setText(R.string.you_lost);
        }
        won = true;
    }
}