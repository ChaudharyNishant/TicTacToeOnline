package com.nishant.tictactoeonline;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Random;

public class CreateGame extends AppCompatActivity {

    Button btn[][] = new Button[3][3];
    long k;
    long joined;
    DatabaseReference databaseReference;
    TextView gameNumber;
    TextView gameStatus;
    Random rand;
    boolean created;
    int n;
    DatabaseHelper databaseHelper;
    boolean won;
    String cameFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        databaseHelper = new DatabaseHelper(this);
        databaseHelper.newPlayer();
        rand = new Random();
        gameNumber = findViewById(R.id.game_number_tv);
        gameStatus = findViewById(R.id.game_status);
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
        n = getIntent().getExtras().getInt("NUMBER");
        cameFrom = getIntent().getExtras().getString("CAME_FROM");
        FirebaseApp.initializeApp(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        if(n != -1)
        {
//            joined = 1;
//            databaseHelper.updatePlayer(n, 2);
            if(cameFrom.equals("createGame"))
            {
                databaseReference.child("test" + n).child("joined").setValue(0);
                databaseReference.child("test" + n).child("k").setValue(1);
                for (int i = 0; i < 3; i++)
                    for (int j = 0; j < 3; j++)
                        databaseReference.child("test" + n).child(String.valueOf(i)).child("a" + j).setValue(".");
                n = -1;
            }
            else if(cameFrom.equals("joinGame"))
            {
//                databaseReference.child("test" + databaseHelper.getGameNumber()).child("joined").setValue(0);
//                databaseReference.child("test" + databaseHelper.getGameNumber()).child("k").setValue(1);
//                for (int i = 0; i < 3; i++)
//                    for (int j = 0; j < 3; j++)
//                        databaseReference.child("test" + databaseHelper.getGameNumber()).child(String.valueOf(i)).child("a" + j).setValue(".");
                joined = 1;
                databaseHelper.updatePlayer(n, 2);
            }
        }
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++)
                btn[i][j].setText(".");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(!created)
                {
                    if(n == -1)
                    {
                        do
                        {
                            Log.d("Create Game", "while" + n);
                            n = rand.nextInt() % 10;
                            if (n < 0)
                                n *= -1;
                        }while((long)dataSnapshot.child("test" + n).child("joined").getValue() != 0);
                        databaseHelper.updatePlayer(n, 1);
                    }
                    gameNumber.setText("Game #" + n);
                    databaseReference.child("test" + n).child("joined").setValue(joined + 1);
                    created = true;
//                    Toast.makeText(CreateGame.this, "Game Number: " + databaseHelper.getGameNumber() +
//                                    "Player Number: " + databaseHelper.getPlayerNumber(), Toast.LENGTH_LONG).show();
                }
//                Toast.makeText(CreateGame.this, dataSnapshot.toString() + "" +
//                        dataSnapshot.child("joined").getValue(), Toast.LENGTH_LONG).show();
                for(int i = 0; i < 3; i++)
                {
                    DataSnapshot data = dataSnapshot.child("test" + n).child(String.valueOf(i));
                    for(int j = 0; j < 3; j++)
                    {
                        String c = (String)data.child("a" + j).getValue();
                        btn[i][j].setText(c);
                        if(c.equals("O"))
                            btn[i][j].setBackgroundResource(R.drawable.ball);
                        else if(c.equals("X"))
                            btn[i][j].setBackgroundResource(R.drawable.cross);
                        updateStatus();
                    }
                }
                k = (long)dataSnapshot.child("test" + n).child("k").getValue();
                joined = (long)dataSnapshot.child("test" + n).child("joined").getValue();
                if(!won)
                {
                    if ((k % 2 == 1 && databaseHelper.getPlayerNumber() == 1) || (k % 2 == 0 && databaseHelper.getPlayerNumber() == 2))
                        gameStatus.setText("Your turn");
                    else if ((k % 2 == 1 && databaseHelper.getPlayerNumber() == 2) || (k % 2 == 0 && databaseHelper.getPlayerNumber() == 1))
                        gameStatus.setText("Opponent's turn");
                }
//                if(joined != (long)dataSnapshot.child("test" + n).child("joined").getValue())
//                    Toast.makeText(CreateGame.this, "Opponent left. You should go back.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Main Activity", "error: " + databaseError);
            }
        });

        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++)
            {
                final int finalI = i;
                final int finalJ = j;
                btn[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(k <= 9 && btn[finalI][finalJ].getText().equals(".") &&
                                k % 2 != databaseHelper.getPlayerNumber() - 1 && !won)
                        {
                            String c = (databaseHelper.getPlayerNumber() == 1) ? "X" : "O";
                            databaseReference.child("test" + n).child(String.valueOf(finalI)).child("a" + finalJ).setValue(c);
                            databaseReference.child("test" + n).child("k").setValue(k + 1);
                            if(c.equals("O"))
                                btn[finalI][finalJ].setBackgroundResource(R.drawable.ball);
                            else if(c.equals("X"))
                                btn[finalI][finalJ].setBackgroundResource(R.drawable.cross);
                        }
                    }
                });
            }
    }

    @Override
    public void onBackPressed()
    {
        databaseHelper.newPlayer();
        databaseReference.child("test" + n).child("joined").setValue(joined - 1);
        databaseReference.child("test" + n).child("k").setValue(1);
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++)
                databaseReference.child("test" + n).child(String.valueOf(i)).child("a" + j).setValue(".");
        Intent i = new Intent(CreateGame.this, MainActivity.class);
        startActivity(i);
    }

    public void updateStatus()
    {
        for(int i = 0; i < 3; i++)
            if(btn[i][0].getText().equals(btn[i][1].getText()) && btn[i][1].getText().equals(btn[i][2].getText()) &&
                    !btn[i][1].getText().equals("."))
                update(btn[i][1].getText().toString());
        for(int j = 0; j < 3; j++)
            if(btn[0][j].getText().equals(btn[1][j].getText()) && btn[1][j].getText().equals(btn[2][j].getText()) &&
                    !btn[1][j].getText().equals("."))
                update(btn[1][j].getText().toString());
        if(btn[0][0].getText().equals(btn[1][1].getText()) && btn[1][1].getText().equals(btn[2][2].getText()) &&
                !btn[1][1].getText().equals("."))
            update(btn[1][1].getText().toString());
        if(btn[0][2].getText().equals(btn[1][1].getText()) && btn[1][1].getText().equals(btn[2][0].getText()) &&
                !btn[1][1].getText().equals("."))
            update(btn[1][1].getText().toString());
    }

    public void update(String winner)
    {
        gameStatus.setText(winner + " won");
        if(databaseHelper.getPlayerNumber() == 1)
        {
            if(winner.equals("X"))
                gameStatus.setText("You won");
            else
                gameStatus.setText("You lost");
        }
        else
        {
            if(winner.equals("O"))
                gameStatus.setText("You won");
            else
                gameStatus.setText("You lost");
        }
        won = true;
    }
}