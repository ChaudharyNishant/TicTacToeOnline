package com.nishant.tictactoeonline;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JoinGame extends AppCompatActivity {

    EditText gameNumber;
    Button join;
    boolean gameStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        FirebaseApp.initializeApp(this);
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        gameNumber = findViewById(R.id.game_number_et);
        join = findViewById(R.id.join_game_btn);
        gameStarted = false;

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String str = gameNumber.getText().toString();
                if(str.length() != 1)
                    Toast.makeText(JoinGame.this, "Game Number can be from 0 to 9", Toast.LENGTH_LONG).show();
                else
                {
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if((long)dataSnapshot.child("test" + gameNumber.getText().toString())
                                    .child("joined").getValue() != 1 && !gameStarted)
                                Toast.makeText(JoinGame.this, "Game not available", Toast.LENGTH_LONG).show();
                            else if(!gameStarted)
                            {
                                gameStarted = true;
                                Intent i = new Intent(JoinGame.this, CreateGame.class);
                                i.putExtra("NUMBER", Integer.parseInt(str));
                                startActivity(i);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("Join Game", "error: " + databaseError);
                        }
                    });
                }
            }
        });
    }
}