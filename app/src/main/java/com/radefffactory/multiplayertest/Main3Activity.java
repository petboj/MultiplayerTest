package com.radefffactory.multiplayertest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Main3Activity extends AppCompatActivity {

    Button button;

    String playerName = "";
    String roomName = "";
    String role = "";
    String message = "";

    int brojPoteza = 0;

    FirebaseDatabase database;
    DatabaseReference messageRef;
    DatabaseReference playerRef;
    DatabaseReference roomRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        Log.d("DebugTag", "onCreate metoda 3. Activitya");

        database = FirebaseDatabase.getInstance();

        button = findViewById(R.id.button);
        button.setEnabled(true);

        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        playerName = preferences.getString("playerName", "");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            roomName = extras.getString("roomName");
            if (roomName.equals(playerName)) {
                role = "host";
            } else {
                role = "guest";
            }
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send message
                button.setEnabled(false);
                message = role + ":Poked!";
                messageRef.setValue(message);
            }
        });

        // listen for incoming messages
        messageRef = database.getReference("rooms/" + roomName + "/message");
//        message = role + ":Poked!";
        message = "Ceka se da igra pocne";
        messageRef.setValue(message);
        addRoomEventListener();
        playerRef = database.getReference("players/" + playerName );
        roomRef = database.getReference("rooms/" + roomName );
    }

    private void addRoomEventListener() {
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // message received
//                brojPoteza++;

                if (role.equals("host")) {
                    if (snapshot.getValue(String.class).contains("host:")) {
                        brojPoteza++;
                        button.setEnabled(false);

                    } else if (snapshot.getValue(String.class).contains("guest:")) {
                        brojPoteza++;
                        button.setEnabled(true);
//                        Toast.makeText(Main3Activity.this,
//                                "" + snapshot.getValue(String.class).replace("guest:", ""), Toast.LENGTH_SHORT).show();
                        Toast.makeText(Main3Activity.this,
                                snapshot.getValue(String.class), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (snapshot.getValue(String.class).contains("guest:")) {
                        brojPoteza++;
                        button.setEnabled(false);
//                        Toast.makeText(Main3Activity.this,
//                                "" + snapshot.getValue(String.class).replace("host:", ""), Toast.LENGTH_SHORT).show();
                    } else if (snapshot.getValue(String.class).contains("host:")) {
                        brojPoteza++;
                        button.setEnabled(true);
//                        Toast.makeText(Main3Activity.this,
//                                "" + snapshot.getValue(String.class).replace("guest:", ""), Toast.LENGTH_SHORT).show();
                        Toast.makeText(Main3Activity.this,
                                snapshot.getValue(String.class), Toast.LENGTH_SHORT).show();
                    }
                }
                Log.d("DebugTag", "Ukupan broj poteza kod igraca " + playerName + " je " + brojPoteza);
                if (isIgraZavrsena()) {
                    playerRef.setValue(PlayerState.IDLE);
                    startActivity(new Intent(getApplicationContext(), MainActivity2.class));
                    roomRef.setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // error - retry
                messageRef.setValue(message);
            }
        });
    }

    private boolean isIgraZavrsena() {
        return brojPoteza >= 5;
    }

}