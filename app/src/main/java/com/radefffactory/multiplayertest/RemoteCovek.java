package com.radefffactory.multiplayertest;

public class RemoteCovek extends  Igrac {
    public RemoteCovek(boolean prviNaPotezu) {
        super(prviNaPotezu);
    }

    String playerName = "";
    String roomName = "";
    String role = "";

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public void odigrajPotez() {
        while (! igra.isRemoteCovekOdigraoPotez()) {
            //    System.out.println("Uslo se u while petlju metode odigrajPotez() klase Covek.");
            try {
                synchronized(this.igra) { igra.wait(); }
            } catch (InterruptedException ie) {}
            //    System.out.println("Nit je probudjena.");
        }
        int i = igra.getTabla().getOdigranPotezI(),
                j = igra.getTabla().getOdigranPotezJ();
        igra.getTabla().postaviPolje(i, j, this.getOznaka());
        igra.setRemoteCovekOdigraoPotez(false);
    }
}
