package com.radefffactory.multiplayertest;

import android.util.Log;

import com.google.gson.Gson;

public class Message {

    public interface MessageCodes {
        int REGULARANPOTEZ = 0;
        int ZAHTEVZAREMIJEM = 1;
        int ODGOVORNAREMI = 2;
        int PREDAJAPARTIJE = 3;
        int PREKIDPARTIJE = 4;
        int ZAHTEVZANOVOMIGROM = 5;
        int ODGOVORNANOVUIGRU = 6;
        int INFOKOIGRAPRVI = 7;
        int PRVAPARTIJA = 8;
    }

    private int messageCode;
    private String senderRole;  // host ili guest
    private String senderPlayerName;
    private int response;   // tumači se u zavisnosti od messageCode
    private int i;
    private int j;

    public Message(int messageCode, String senderRole, String senderPlayerName, int response, int i, int j) {
        this.messageCode = messageCode;
        this.senderRole = senderRole;
        this.senderPlayerName = senderPlayerName;
        this.response = response;
        this.i = i;
        this.j = j;
    }

    public int getMessageCode() {
        return messageCode;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public String getSenderPlayerName() {
        return senderPlayerName;
    }

    public int getResponse() {
        return response;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public static String convertToJsonString(Message e)
    {
        Gson gson = new Gson();
        String messageJson = gson.toJson(e);
        return messageJson;
    }

    public static Message convertFromJsonString(String message) {
        Gson gson = new Gson();
        return gson.fromJson(message, Message.class);
    }
//    public static void testToJsonString() {
//        Log.d("DebugTag", convertToJsonString(new Message("Petar", 25)));
//    }
//    public static void testFromJsonString() {
//
//        Log.d("DebugTag", convertToJsonString(convertFromJsonString(convertToJsonString(new Message("Marko", 36)))));
//    }
}
