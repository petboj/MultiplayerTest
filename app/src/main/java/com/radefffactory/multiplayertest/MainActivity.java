package com.radefffactory.multiplayertest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    Button button;

    String playerName = "";

    FirebaseDatabase database;
    DatabaseReference playerRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button);

        database = FirebaseDatabase.getInstance();

        Log.d("DebugTag","Izvrsava se metoda onCreate.");
        // check if the player exists and get reference
        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        playerName = preferences.getString("playerName", "");
        if (!playerName.equals("")) {
            Log.d("DebugTag","Ime igraca je zapamceno od ranije i glasi " + playerName);
//            playerRef = database.getReference("players/" + playerName);
//            addEventListener();
//            playerRef.setValue("");
            startActivity(new Intent(getApplicationContext(), MainActivity2.class));
            finish();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // logging the player in
                playerName = editText.getText().toString();
                editText.setText("");
                if (!playerName.equals("")) {
                    button.setText("LOGGING IN");
                    button.setEnabled(false);
                    playerRef = database.getReference("players/" + playerName);
                    checkIfNickAlreadyExists(playerRef);                     
                 //   playerRef.setValue("Sadrzzzaj");
                    Log.d("DebugTag","Referenca ima vrednost " + playerRef.getKey());
                }
            }
        });
    }

    private void addEventListener() {
        // read from database
        playerRef.addValueEventListener(new ValueEventListener() {
            // metod onDataChange se poziva jednom kada se listener doda, i posle svaki put kada se desi promena sadrzaja
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // success - continue on the next screen after saving the player name
                if (!playerName.equals("")) {
                    Log.d("DebugTag","Uslo se u onDataChange");
                    SharedPreferences preferences = getSharedPreferences("PREFS", 0);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("playerName", playerName);
                    editor.apply();
                    // uklanjamo ovaj listener da se ova metoda ne bi pozivala pri budućim promenama na lokaciji playerRef. Ovo je bio skriveni bug
                    playerRef.removeEventListener(this);
                    startActivity(new Intent(getApplicationContext(), MainActivity2.class));
                    finish();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // error
                button.setText("LOG IN");
                button.setEnabled(true);
                Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfNickAlreadyExists(DatabaseReference playerRef) {
        playerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = (String) snapshot.getValue();
                Log.d("DebugTag", "snapshot ima vrednost : " + value);
//                if (value.equals(PlayerState.PLAYINGGAME) || value.equals(PlayerState.WAITINGFOROPPONENT)
//                    || value.equals(PlayerState.IDLE) ) {
                if (value != null) {

//                    Toast.makeText(MainActivity.this,
//                            "Igrač sa takvim imenom već postoji!" , Toast.LENGTH_SHORT).show();
                    showAlertDialog("Igrač sa takvim imenom već postoji!");
                    button.setText("LOG IN"); button.setEnabled(true);
                } else {
                    addEventListener();
                    playerRef.setValue(PlayerState.IDLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DebugTag", "Uslo se u onCacelled.");
                
            }
        });
        Log.d("DebugTag", "Sledi povratna vrednost metode checkIfNickAlreadyExists");
    }

    private void showAlertDialog(String messageToShow) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(messageToShow);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("DebugTag", "pozvalo se onPause()");
    }
}