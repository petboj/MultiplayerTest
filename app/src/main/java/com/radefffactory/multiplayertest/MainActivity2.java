package com.radefffactory.multiplayertest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    ListView listView;
    Button button;

    List<String> roomsList;
    List<String> playersList;

    String playerName = "";
    String roomName = "";

    FirebaseDatabase database;
    DatabaseReference roomRef;
    DatabaseReference roomsRef;
    DatabaseReference playerRef;
    DatabaseReference playersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        database = FirebaseDatabase.getInstance();

        // get the player name and assign his room name to the player name
        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        playerName = preferences.getString("playerName", "");
        roomName = playerName;

        listView = findViewById(R.id.listView);
        button = findViewById(R.id.button);

        // all existing available rooms
        roomsList = new ArrayList<>();
        playersList = new ArrayList<>();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create room and add yourself as a player1
                button.setText("CREATING ROOM");
                button.setEnabled(false);
                roomName = playerName;
                roomRef = database.getReference("rooms/" + roomName + "/player1");
              //  addRoomEventListener();
                roomRef.setValue(playerName);
                playerRef = database.getReference("players/" + playerName);
                playerRef.setValue(PlayerState.WAITINGFOROPPONENT);
                listView.setEnabled(false);
                addPlayerWasChosenByAnotherPlayerListener();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // join an existing room and add yourself as player2
                String selectedPlayerName = playersList.get(position);
                if (selectedPlayerName.endsWith("  ...čeka na protivnika")) {
                    Log.d("DebugTag", "Kliknulo se na igraca koji ceka protivnika.");
                    selectedPlayerName = selectedPlayerName.replace("  ...čeka na protivnika", "");
                    roomName = selectedPlayerName;
                    roomRef = database.getReference("rooms/" + roomName + "/player2");
                    addRoomEventListener();
                    roomRef.setValue(playerName);
                    playerRef = database.getReference("players/" + selectedPlayerName);
                    playerRef.setValue(PlayerState.PLAYINGGAME);
                    playerRef = database.getReference("players/" + playerName);
                    playerRef.setValue(PlayerState.PLAYINGGAME);
                }
              //  roomName = roomsList.get(position);
              //  roomRef = database.getReference("rooms/" + roomName + "/player2");
             //   addRoomEventListener();
                // roomRef.setValue(playerName);
            }
        });

        // show if new room is available
    //    addRoomsEventListener();
        addPlayersEventListener();
    }


    private void addRoomEventListener() {
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // join the room
                button.setText("CREATE ROOM");
                button.setEnabled(true);
                Intent intent = new Intent(getApplicationContext(), Main3Activity.class);
                intent.putExtra("roomName", roomName);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // error
                button.setText("CREATE ROOM");
                button.setEnabled(true);
                Toast.makeText(MainActivity2.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addRoomsEventListener() {
        roomsRef = database.getReference("rooms");
        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // show list of rooms
                roomsList.clear();
                Iterable<DataSnapshot> rooms = snapshot.getChildren();
                for (DataSnapshot room : rooms) {
                    roomsList.add(room.getKey());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity2.this, android.R.layout.simple_list_item_1, roomsList);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // error - nothing
            }
        });
    }

    private void addPlayersEventListener() {
        playersRef = database.getReference("players");
        playersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // show list of players
                playersList.clear();
                Iterable<DataSnapshot> players = snapshot.getChildren();
                ArrayList<Integer> highlighted = new ArrayList<>();
                int position = 0;
                for (DataSnapshot player : players) {
                    if (!player.getKey().equals(playerName)) {
                        if (((String) player.getValue()).equals(PlayerState.WAITINGFOROPPONENT)) {
                            playersList.add(player.getKey() + "  ...čeka na protivnika");
                            highlighted.add(new Integer(position));
                            position++;
                        } else if (!((String) player.getValue()).equals(PlayerState.PLAYINGGAME)){
                            playersList.add(player.getKey());
                            position++;
                        }

                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity2.this, android.R.layout.simple_list_item_1, playersList) {
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);
                            boolean condition = false;
                            for (Integer pos : highlighted) {
                                if (pos.intValue() == position) {
                                    condition = true; break;
                                }
                            }
                            if (condition) view.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                            else view.setBackgroundColor(getResources().getColor(android.R.color.white));
                            return view;
                        }
                    };
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // error - nothing
            }
        });
    }

    private void addPlayerWasChosenByAnotherPlayerListener() {
        playerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = (String) snapshot.getValue();
                if (value.equals(PlayerState.PLAYINGGAME)) {
                    button.setText("CREATE ROOM");
                    button.setEnabled(true);
                    Intent intent = new Intent(getApplicationContext(), Main3Activity.class);
                    intent.putExtra("roomName", roomName);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}