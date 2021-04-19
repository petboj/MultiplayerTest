/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radefffactory.multiplayertest;

/**
 *
 * @author Petar
 */
public class Covek extends Igrac {

    public Covek(boolean prviNaPotezu) {
        super(prviNaPotezu);
    }

    String playerName = "";
    String roomName = "";
    String role = "";

    @Override
    public void odigrajPotez() {
        while (! igra.isCovekOdigraoPotez()) {
        //    System.out.println("Uslo se u while petlju metode odigrajPotez() klase Covek.");
            try {                
                synchronized(this.igra) { igra.wait(); }
            } catch (InterruptedException ie) {}
        //    System.out.println("Nit je probudjena.");
        }
        int i = igra.getTabla().getOdigranPotezI(),
                j = igra.getTabla().getOdigranPotezJ();
        igra.getTabla().postaviPolje(i, j, this.getOznaka());        
        igra.setCovekOdigraoPotez(false);
    }
    
}
