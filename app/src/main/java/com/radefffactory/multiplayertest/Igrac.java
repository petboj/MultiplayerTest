/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radefffactory.multiplayertest;

/**
 *
 * @author bojke
 */
public abstract class Igrac extends Thread {
    protected Igra igra; // trenutna igra koju igrač igra, mora biti isti objekat za dva igrača koji učestvuju u istoj igri. Preko nje se dolazi i do odgovarajuće table igre
    protected boolean prviNaPotezu; // true, ako igrač igra prvi, odnosno ako koristi oznaku X
    protected int oznaka; // X ili O
    private boolean naPotezu() {
        return igra.naPotezu(this);
    }

    public Igrac(boolean prviNaPotezu) {
        this.prviNaPotezu = prviNaPotezu;
        if (prviNaPotezu) oznaka = Tabla.X;
        else oznaka = Tabla.O;
    }

    public Igra getIgra() {
        return igra;
    }

    public int getOznaka() {
        return oznaka;
    }

    public boolean isPrviNaPotezu() {
        return prviNaPotezu;
    }

    public void setIgra(Igra igra) {
        this.igra = igra;
    }

    @Override
    public void run() {
        while (! igra.isZavrsena()) {
            while (! (naPotezu() || igra.isZavrsena())) 
                try {
                     synchronized(igra) { igra.wait(); }
                } catch (InterruptedException ie) {}
            if (!igra.isZavrsena()) {
           //     System.out.println("Igrac " + Tabla.oznakaPolja(this.getOznaka()) + " je spreman da odigra.");
                odigrajPotez();
            //    System.out.println("Igrac " + Tabla.oznakaPolja(this.getOznaka()) + " je odigrao.");
            }
            if (! igra.isZavrsena()) {
           //     System.out.println("Igra još nije završena, prebacujemo na sledećeg igrača.");
                igra.sledeciIgracNaPotezu();
            }
        } 
      //  System.out.println("Igrac " + Tabla.oznakaPolja(this.getOznaka()) + " je završio igru.");
    }
    
    public abstract void odigrajPotez();   
    
}
