package com.radefffactory.multiplayertest;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PlayActivity extends AppCompatActivity {

    private static final int HUMANCOMPMODE = 0, HUMANHUMANMODE = 1, HUMANREMOTEHUMANMODE = 2;

    /**
     * FAZA1 - bira se mod igre
     * FAZA2 - u ovu fazu dolazi se ako je mod igre HUMANCOMPMODE, i u ovoj fazi se bira težina virtuelnog protivnika, odnosno nivo
     * FAZA3 - u ovoj fazi bira se ko igra prvi, u ovu fazu se dolazi ako se pritisne dugme za novu igru, ili posle faze FAZA2
     * FAZA4 - ovo je faza u kojoj se igra partija
     */
    private static final int FAZA1 = 0, FAZA2 = 1, FAZA3 = 2, FAZA4 = 3;

    private int gameMode = 0; // može biti HUMANCOMPMODE ili HUMANHUMANMODE
    private int fazaIgre = 0; // može biti FAZA1 .. FAZA4
    private int complevel = 0;
    private boolean humanPlaysFirst = true;

    String playerName = "";
    String roomName = "";
    String role = "";
    String message = "";
    Message messageObject;

    FirebaseDatabase database;
    DatabaseReference messageRef;
    DatabaseReference playerRef;
    DatabaseReference roomRef;

    Button newGameButton;
    Button leaveRoomButton;

    String notification;

    boolean playerLeftTheRoom = false;
    boolean newGameRequested = false;

    public static PlayActivity playActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_activity);
        //    mainActivity = (Activity) getBaseContext();
        playActivity = this;

        database = FirebaseDatabase.getInstance();

        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        playerName = preferences.getString("playerName", "");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            roomName = extras.getString("roomName");
            Log.d("DebugTag", "Uslo se u if (extras != null, roomName = " + roomName + " playerName = " + playerName);
            if (roomName.equals(playerName)) {
                role = "host";
            } else {
                role = "guest";
            }
        }

        playerRef = database.getReference("players/" + playerName );
        roomRef = database.getReference("rooms/" + roomName );

        messageRef = database.getReference("rooms/" + roomName + "/message");
//        message = role + ":Poked!";

        if (role.equals("host")) {
            boolean hostIgraPrvi = hostIgraPrvi();
            zapocniNovuIgru(hostIgraPrvi);
            messageObject = new Message(Message.MessageCodes.PRVAPARTIJA, role, playerName, hostIgraPrvi ? 1 : 0, 0 , 0);
            message = Message.convertToJsonString(messageObject);
            messageRef.setValue(message);
        } else {
            messageRef.setValue("");
        }



//        message = "Ceka se da igra pocne";




        newGameButton = findViewById(R.id.newGameButton);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                zapocniNovuIgru(hostIgraPrvi());
                newGameRequested = true;
                messageObject = new Message(Message.MessageCodes.ZAHTEVZANOVOMIGROM, role, playerName, 0, 0 , 0);
                message = Message.convertToJsonString(messageObject);
                messageRef.setValue(message);
            }
        });
        addRoomEventListener();
        leaveRoomButton = findViewById(R.id.leaveRoomButton);

        leaveRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerRef.setValue(PlayerState.IDLE);
                startActivity(new Intent(getApplicationContext(), MainActivity2.class));
                playerLeftTheRoom = true;
                roomRef.setValue(null);
            }
        });

        addRoomLeftListener();
//        ((Button) findViewById(R.id.humanVsCompButton)).setOnClickListener(buttonAL);
//        ((Button) findViewById(R.id.humanVsHumanButton)).setOnClickListener(buttonAL);

      /*  ((ImageButton) findViewById(R.id.level1Button)).setOnClickListener(buttonAL);
        ((ImageButton) findViewById(R.id.level2Button)).setOnClickListener(buttonAL);
        ((ImageButton) findViewById(R.id.level3Button)).setOnClickListener(buttonAL);
        ((ImageButton) findViewById(R.id.level4Button)).setOnClickListener(buttonAL);
        ((ImageButton) findViewById(R.id.level5Button)).setOnClickListener(buttonAL);
        ((Button) findViewById(R.id.humanPlaysFirstButton)).setOnClickListener(buttonAL);
        ((Button) findViewById(R.id.computerPlaysFirstButton)).setOnClickListener(buttonAL);
        ((Button) findViewById(R.id.newGameButton)).setOnClickListener(buttonAL);
        ((Button) findViewById(R.id.mainMenuButton)).setOnClickListener(buttonAL);
        */


    }

    private void addRoomLeftListener() {
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object value = snapshot.getValue();
                if (value == null && !playerLeftTheRoom) {
                    playerRef.setValue(PlayerState.IDLE);
                    roomRef.removeEventListener(this);
                    startActivity(new Intent(getApplicationContext(), MainActivity2.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    /*
    private void changeCurrentPanel(int layout) {
        setContentView(layout);
        if (layout == R.layout.choose_level_activity) {
            ((ImageButton) findViewById(R.id.level1Button)).setOnClickListener(buttonAL);
            ((ImageButton) findViewById(R.id.level2Button)).setOnClickListener(buttonAL);
            ((ImageButton) findViewById(R.id.level3Button)).setOnClickListener(buttonAL);
            ((ImageButton) findViewById(R.id.level4Button)).setOnClickListener(buttonAL);
            ((ImageButton) findViewById(R.id.level5Button)).setOnClickListener(buttonAL);
        } else if (layout == R.layout.who_plays_first_layot) {
            ((Button) findViewById(R.id.humanPlaysFirstButton)).setOnClickListener(buttonAL);
            ((Button) findViewById(R.id.computerPlaysFirstButton)).setOnClickListener(buttonAL);
        } else if (layout == R.layout.play_activity) {
                ((Button) findViewById(R.id.newGameButton)).setOnClickListener(buttonAL);
                ((Button) findViewById(R.id.mainMenuButton)).setOnClickListener(buttonAL);

        } else if (layout == R.layout.activity_main) {
            ((Button) findViewById(R.id.humanVsCompButton)).setOnClickListener(buttonAL);
            ((Button) findViewById(R.id.humanVsHumanButton)).setOnClickListener(buttonAL);
        }
    }
    */

    /*
    private void levelButtonAction(int level) {
        if (fazaIgre != FAZA2) return;
        complevel = level;
        changeCurrentPanel(R.layout.who_plays_first_layot);
        fazaIgre = FAZA3;
    }
    */

    /*
    private View.OnClickListener buttonAL = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

        //    if (!(v instanceof Button)) return;
         //   Button src = (Button) v;
            if (v.getId() == R.id.humanVsCompButton) {
                if (fazaIgre != FAZA1) {
                    return;
                }
                gameMode = HUMANCOMPMODE;
                changeCurrentPanel(R.layout.choose_level_activity);
                fazaIgre = FAZA2;
            } else if (v.getId() == R.id.humanVsHumanButton) {
                if (fazaIgre != FAZA1) {
                    return;
                }
                gameMode = HUMANHUMANMODE;
                changeCurrentPanel(R.layout.play_activity);
                fazaIgre = FAZA4;
                zapocniNovuIgru();
            } else if (v.getId() == R.id.level1Button) {
                levelButtonAction(0);
            } else if (v.getId() == R.id.level2Button) {
                levelButtonAction(1);
            } else if (v.getId() == R.id.level3Button) {
                levelButtonAction(2);
            } else if (v.getId() == R.id.level4Button) {
                levelButtonAction(3);
            } else if (v.getId() == R.id.level5Button) {
                levelButtonAction(4);
            } else if (v.getId() == R.id.humanPlaysFirstButton) {
                if (fazaIgre != FAZA3) return;
                humanPlaysFirst = true;
                changeCurrentPanel(R.layout.play_activity);
                fazaIgre = FAZA4;
                zapocniNovuIgru();
            } else if (v.getId() == R.id.computerPlaysFirstButton) {
                if (fazaIgre != FAZA3) return;
                humanPlaysFirst = false;
                changeCurrentPanel(R.layout.play_activity);
                fazaIgre = FAZA4;
                zapocniNovuIgru();
            } else if (v.getId() == R.id.newGameButton) {
                if (fazaIgre != FAZA4) return;
                if (igra.isZavrsena()) {
                    if (gameMode == HUMANHUMANMODE) {
                        zapocniNovuIgru();
                    } else {
                        fazaIgre = FAZA3;
                        changeCurrentPanel(R.layout.who_plays_first_layot);
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                    builder.setTitle("Upozorenje");
                    builder.setMessage("Da li ste sigurni da hoćete da prekinete igru?")
                            .setCancelable(false)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // TODO: handle the OK
                                    igra.prekiniIgru();
                                    if (gameMode == HUMANHUMANMODE) {
                                        zapocniNovuIgru();
                                    } else {
                                        fazaIgre = FAZA3;
                                        changeCurrentPanel(R.layout.who_plays_first_layot);
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                /*
                    int odluka = JOptionPane.showConfirmDialog (playPanel, "Da li ste sigurni da hoćete da prekinete igru?");
                    if (odluka == JOptionPane.YES_OPTION) {
                        igra.prekiniIgru();
                        if (gameMode == HUMANHUMANMODE) {
                            zapocniNovuIgru();
                        } else {
                            fazaIgre = FAZA3;
                            changeCurrentPanel(R.layout.who_plays_first_layot);
                        }
                    }
                 */
    /*
                }
            } else if (v.getId() == R.id.mainMenuButton) {
                if (fazaIgre != FAZA4) return;
                if (! igra.isZavrsena()) return;
                fazaIgre = FAZA1;
                changeCurrentPanel(R.layout.activity_main);
            }
        }
    };

    */

    private void zapocniNovuIgru(boolean hostPlaysFirst) {
        Log.d("DebugTag", "metoda zapocniNovuIgru role = " + role + " playerName=" + playerName + "hostIgraPrvi= " + hostPlaysFirst);
        Igrac igrac1 = null, igrac2 = null;
        this.gameMode = PlayActivity.HUMANREMOTEHUMANMODE;
        if (this.gameMode == HUMANHUMANMODE) {
            igrac1 = new Covek(true); igrac2 = new Covek(false);
        } else if (this.gameMode == HUMANCOMPMODE) {
            if (humanPlaysFirst) {
                igrac1 = new Covek(true);
                igrac2 = new Kompjuter(false, complevel);
            } else {
                igrac1 = new Kompjuter(true, complevel);
                igrac2 = new Covek(false);
            }
        } else if (this.gameMode == HUMANREMOTEHUMANMODE) {
            if (role.equals("host")) {
                igrac1 = new Covek(hostPlaysFirst);
                igrac2 = new RemoteCovek(! hostPlaysFirst);
            } else if (role.equals("guest")) {
                igrac1 = new RemoteCovek(hostPlaysFirst);
                igrac2 = new Covek(! hostPlaysFirst);
            }
        }

        igra = new Igra(igrac1, igrac2);
        igrac1.setIgra(igra); igrac2.setIgra(igra);
        igra.setMessageRef(messageRef);
        Tabla tabla = new Tabla(igra);
        igra.setTabla(tabla);
        tabla.setDugmad((Button) findViewById(R.id.button00), (Button) findViewById(R.id.button01), (Button) findViewById(R.id.button02),
                (Button) findViewById(R.id.button10), (Button) findViewById(R.id.button11), (Button) findViewById(R.id.button12),
                (Button) findViewById(R.id.button20), (Button) findViewById(R.id.button21), (Button) findViewById(R.id.button22));
        PlayActivity.playActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // Stuff that updates the UI
                ((TextView) findViewById(R.id.gameResultLabel)).setText("");
            }
        });
        //((TextView) findViewById(R.id.gameResultLabel)).setText("");
        informPlayersWhoPlaysFirst(hostPlaysFirst);
        igra.start();
        new Thread(new SetGameResultLabelThread()).start();
    }
    private class SetGameResultLabelThread implements Runnable {

        @Override
        public void run() {
            while (! igra.isZavrsena()) {
                synchronized(igra) {
                    try {
                        igra.wait();
                    } catch (InterruptedException ie) {}
                }
            }
            if (igra.isIgraRegularnoZavrsena())
                PlayActivity.playActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        // Stuff that updates the UI
                        ((TextView) findViewById(R.id.gameResultLabel)).setText(igra.generisiPorukuOPobedniku());
                    }
                });
            //((TextView) findViewById(R.id.gameResultLabel)).setText(igra.generisiPorukuOPobedniku());
        }
    }

    private void informPlayersWhoPlaysFirst(boolean hostPlaysFirst) {
        if (role.equals("host") && hostPlaysFirst || role.equals("guest") && !hostPlaysFirst) {
            notification = "Igra je počela. Vi igrate prvi";
        } else {
            notification = "Igra je počela. Protivnik igra prvi";
        }
        TextView notifLabel = (TextView) findViewById(R.id.gameResultLabel);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                PlayActivity.playActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        notifLabel.setText(notification);
                    }
                });
                try {
                    Thread.sleep(6500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                PlayActivity.playActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        notifLabel.setText("");
                    }
                });

            }
        };
        new Thread(task).start();
    }

    private void addRoomEventListener() {
        messageRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // message received
                message = snapshot.getValue(String.class);
                Log.d("DebugTag", "RoomEventListener role= " + role + " playerName= " + playerName + " message=" + message);
                messageObject = Message.convertFromJsonString(message);
                if (messageObject == null) {
                    Log.d("DebugTag", "RoomEventListener role= " + role + " playerName= " + playerName + " messageObject je null.");
                    return;
                }
                if (messageObject.getMessageCode() == Message.MessageCodes.REGULARANPOTEZ) {
                    if (role.equals("host") && messageObject.getSenderRole().equals("guest") ||
                            role.equals("guest") && messageObject.getSenderRole().equals("host")) {
                        igra.getTabla().setOdigranPotezI(messageObject.getI());
                        igra.getTabla().setOdigranPotezJ(messageObject.getJ());
                        igra.setRemoteCovekOdigraoPotez(true);
                    }
                } else if (messageObject.getMessageCode() == Message.MessageCodes.PRVAPARTIJA) {
                    if (role.equals("guest")) {
                        Log.d("DebugTag", "RoomEventListener role= " + role + " playerName= " + playerName + "zapocinjemo novu igru kao " +
                                ((messageObject.getResponse() == 1) ? "true" : "false") );
                        zapocniNovuIgru(messageObject.getResponse() == 1);
                    }
                } else if (messageObject.getMessageCode() == Message.MessageCodes.ZAHTEVZANOVOMIGROM) {
                    if (!newGameRequested) { // da bi znali da je prijemna strana
                        AlertDialog.Builder builder = new AlertDialog.Builder(playActivity);
                        View view = playActivity.getLayoutInflater().inflate(R.layout.dialog_layout, null);
                        builder.setView(view);
                        final AlertDialog alertDialog = builder.create();
                        TextView dialogText = view.findViewById(R.id.dialog_text);
                        dialogText.setText("Igrač " + messageObject.getSenderPlayerName() + " želi novu partiju.");
                        Button reject = (Button) view.findViewById(R.id.reject_action);
                        reject.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                messageObject = new Message(Message.MessageCodes.ODGOVORNANOVUIGRU, role, playerName, 0, 0, 0);
                                message = Message.convertToJsonString(messageObject);
                                messageRef.setValue(message);
                                alertDialog.dismiss();
                            }
                        });
                        Button accept = (Button) view.findViewById(R.id.accept_action);
                        accept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (role.equals("host")) {
                                    boolean hostIgraPrvi = hostIgraPrvi();
                                    zapocniNovuIgru(hostIgraPrvi);
                                    // u parametar "response" ubacujemo da je prihvatio zahtev, a u parametar "i" ko igra prvi
                                    messageObject = new Message(Message.MessageCodes.ODGOVORNANOVUIGRU, role, playerName, 1, hostIgraPrvi ? 1 : 0, 0);
                                    message = Message.convertToJsonString(messageObject);
                                    messageRef.setValue(message);
                                } else if (role.equals("guest")) {
                                    messageObject = new Message(Message.MessageCodes.ODGOVORNANOVUIGRU, role, playerName, 1, 0, 0);
                                    message = Message.convertToJsonString(messageObject);
                                    messageRef.setValue(message);
                                }
                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.setCancelable(false);
                        alertDialog.show();
//                        builder.setTitle("");
//                        builder.setMessage("Igrač " + messageObject.getSenderPlayerName() + " želi novu partiju.")
//                                .setCancelable(false)
//                                .setPositiveButton("Prihvati", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        if (role.equals("host")) {
//                                            boolean hostIgraPrvi = hostIgraPrvi();
//                                            zapocniNovuIgru(hostIgraPrvi);
//                                            // u parametar "response" ubacujemo da je prihvatio zahtev, a u parametar "i" ko igra prvi
//                                            messageObject = new Message(Message.MessageCodes.ODGOVORNANOVUIGRU, role, playerName, 1, hostIgraPrvi ? 1 : 0, 0);
//                                            message = Message.convertToJsonString(messageObject);
//                                            messageRef.setValue(message);
//                                        } else if (role.equals("guest")) {
//                                            messageObject = new Message(Message.MessageCodes.ODGOVORNANOVUIGRU, role, playerName, 1, 0, 0);
//                                            message = Message.convertToJsonString(messageObject);
//                                            messageRef.setValue(message);
//                                        }
//                                        dialog.dismiss();
//                                    }
//                                })
//                                .setNegativeButton("Odbij", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        messageObject = new Message(Message.MessageCodes.ODGOVORNANOVUIGRU, role, playerName, 0, 0, 0);
//                                        message = Message.convertToJsonString(messageObject);
//                                        messageRef.setValue(message);
//                                        dialog.cancel();
//                                    }
//                                });
//                        builder.setView(R.layout.dialog_layout);
//                        AlertDialog alertDialog = builder.create();
//                        alertDialog.show();
                    }
                } else if (messageObject.getMessageCode() == Message.MessageCodes.ODGOVORNANOVUIGRU) {
                    if (newGameRequested) {
                        if (messageObject.getResponse() == 1) {

                            if (role.equals("host")) {
                                boolean hostIgraPrvi = hostIgraPrvi();
                                zapocniNovuIgru(hostIgraPrvi);
                                messageObject = new Message(Message.MessageCodes.PRVAPARTIJA, role, playerName, hostIgraPrvi ? 1 : 0, 0, 0);
                                message = Message.convertToJsonString(messageObject);
                                messageRef.setValue(message);
                            } else if (role.equals("guest")) {
                                zapocniNovuIgru(messageObject.getI() == 1);
                            }
                        } else if (messageObject.getResponse() == 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(playActivity);
                            builder.setTitle("");
                            builder.setMessage("Igrač " + messageObject.getSenderPlayerName() + " odbija zahtev.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();

                        }

                    }
                    newGameRequested = false;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // error - retry
//                messageRef.setValue(message);
                messageRef.setValue(null);
            }
        });
    }


    // ranija varijanta ove metode, kada se poruka parsirala a nije se prenosila kao JSON objekat
//    private void addRoomEventListener() {
//        messageRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                // message received
//                message = snapshot.getValue(String.class);
//                int i, j;
//                if (role.equals("host")) {
//                    if (message.contains("guest :")) {
//                        i = extractKoordI(message);
//                        j = extractKoordJ(message);
//                        Log.d("DebugTag", "Kliknulo se na koordinate i=" + i + " j=" + j);
//                        igra.getTabla().setOdigranPotezI(i);
//                        igra.getTabla().setOdigranPotezJ(j);
//                        igra.setRemoteCovekOdigraoPotez(true);
//                    }
//                } else {
//                    if (message.contains("host :")) {
//                        i = extractKoordI(message);
//                        j = extractKoordJ(message);
//                        Log.d("DebugTag", "Kliknulo se na koordinate i=" + i + " j=" + j);
//                        igra.getTabla().setOdigranPotezI(i);
//                        igra.getTabla().setOdigranPotezJ(j);
//                        igra.setRemoteCovekOdigraoPotez(true);
//                    }
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // error - retry
//                messageRef.setValue(message);
//            }
//        });
//    }
    private Igra igra;



    private static boolean hostIgraPrvi() {
        double x = Math.random();
        x = 0.4; // za potrebe testiranja da bi guest igrao prvi
        return (x >= 0.5);
    }
}