/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radefffactory.multiplayertest;

import com.google.firebase.database.DatabaseReference;

/**
 *
 * @author bojke
 */
public class Igra extends Thread {
    
    private Igrac igrac1, igrac2; // igrac1 je onaj koji igra prvi, a igrac koji igra prvi obeležava svoje poteze sa X
    private Tabla tabla;
    private Igrac igracNaPotezu; // koji je igrač na potezu, menja se od igrac1 na igrac2 pa naizmenično
    private boolean uToku = false, zavrsena = false;
    private boolean covekOdigraoPotez = false;
    private boolean igraRegularnoZavrsena = true;
    private boolean remoteCovekOdigraoPotez = false;
    private boolean igraZavrsenaRemijem = false;

    private DatabaseReference messageRef;

    public Igra(Igrac igrac1, Igrac igrac2) {
        this.igrac1 = igrac1;
        this.igrac2 = igrac2;
//        this.igracNaPotezu = igrac1;  // Ovo je bio veliki bug koji sam teško otkrio
        if (igrac1.isPrviNaPotezu()) igracNaPotezu = igrac1;
        else if (igrac2.isPrviNaPotezu()) igracNaPotezu = igrac2;
    }

    public Tabla getTabla() {
        return tabla;
    }

    public void setTabla(Tabla tabla) {
        this.tabla = tabla;
    }

    public DatabaseReference getMessageRef() {
        return messageRef;
    }

    public void setMessageRef(DatabaseReference messageRef) {
        this.messageRef = messageRef;
    }

    public synchronized boolean isZavrsena() {
        if (this.zavrsena) { notifyAll(); return true; }
        if (tabla.konacnoStanje()) {
            zavrsena = true;
            notifyAll();
            return true;
        }
        return false;
    }
    
    public synchronized void prekiniIgru() {
        if (this.zavrsena) return;
        this.igraRegularnoZavrsena = false;
        this.zavrsena = true;
        notifyAll();
    }

    public synchronized void zakljucenRemi() {
        if (this.zavrsena) return;
        this.igraRegularnoZavrsena = true;
        this.igraZavrsenaRemijem = true;
        this.zavrsena = true;
        notifyAll();
    }
    public synchronized boolean isIgraRegularnoZavrsena() {
        return this.igraRegularnoZavrsena;
    }
    public synchronized boolean naPotezu(Igrac igrac) {
        return (this.igracNaPotezu == igrac); 
    }
    
    public synchronized void setIgracNaPotezu(Igrac igrac) {
        if (igrac != igrac1 && igrac != igrac2) return;
        this.igracNaPotezu = igrac;
        notifyAll();
    }
    
    public synchronized boolean isCovekNaPotezu() {
        if (this.igracNaPotezu instanceof Covek) return true;
        else return false;
    }

    public synchronized boolean isRemoteCovekNaPotezu() {
        if (this.igracNaPotezu instanceof RemoteCovek) return true;
        else return false;
    }
    public synchronized void sledeciIgracNaPotezu() {        
        if (this.igracNaPotezu == this.igrac1) setIgracNaPotezu(this.igrac2);
        else setIgracNaPotezu(this.igrac1);
     //   System.out.println("Sada je na potezu igrac " + Tabla.oznakaPolja(igracNaPotezu.getOznaka()));
        
    }
    public synchronized void setCovekOdigraoPotez(boolean vrednost) {
    //    System.out.println("Uslo se u setCovekOdigraoPotez(boolean vrednost)");
        this.covekOdigraoPotez = vrednost;
        if (vrednost)  notifyAll();
//        if (vrednost) {
//            igrac1.notify();
//            igrac2.notify();
//        }
    }

    public synchronized void setRemoteCovekOdigraoPotez(boolean vrednost) {
        //    System.out.println("Uslo se u setCovekOdigraoPotez(boolean vrednost)");
        this.remoteCovekOdigraoPotez = vrednost;
        if (vrednost)  notifyAll();
//        if (vrednost) {
//            igrac1.notify();
//            igrac2.notify();
//        }
    }
    
    public synchronized boolean isCovekOdigraoPotez() {
        return this.covekOdigraoPotez;
    }

    public synchronized boolean isRemoteCovekOdigraoPotez() {
        return this.remoteCovekOdigraoPotez;
    }
    
    public String generisiPorukuOPobedniku() {
        if (!this.zavrsena) return "";
        String poruka = null;
        Matrica matrica = new Matrica(tabla);
        Igrac pobednik = null, gubitnik = null;
        if (matrica.isPobednik(igrac1.getOznaka())) {
            pobednik = igrac1;
            gubitnik = igrac2;
        } else if (matrica.isPobednik(igrac2.getOznaka())) {
            pobednik = igrac2;
            gubitnik = igrac1;
        } 
        if (pobednik != null) {
            if (pobednik instanceof Covek && gubitnik instanceof Covek) {
                poruka = "Pobedio je igrač " + Tabla.oznakaPolja(pobednik.getOznaka());
            } else if (pobednik instanceof Covek && gubitnik instanceof RemoteCovek) {
                poruka = "Čestitam, pobedili ste!";
            } else if (pobednik instanceof RemoteCovek && gubitnik instanceof Covek) {
                poruka = "Protivnik je pobedio!";
            } else if (pobednik instanceof Covek && gubitnik instanceof Kompjuter) {
                poruka = "Čestitam, pobedili ste!";
            } else if (pobednik instanceof Kompjuter && gubitnik instanceof Covek)
                poruka = "Žao mi je, ali izgubili ste!";
        } else if (igraZavrsenaRemijem) {
            poruka = "Igra je završena remijem.";
        } else {
            poruka = "Rezultat je nerešen.";
        }
        return poruka;
    }
        
    @Override
    public void run() {
        igrac1.start();
        igrac2.start();
        try {
            igrac1.join();
            igrac2.join();
        } catch (InterruptedException ie) {}
    }
    
}
