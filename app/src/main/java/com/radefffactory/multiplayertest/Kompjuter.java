/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radefffactory.multiplayertest;

import java.util.ArrayList;

/**
 *
 * @author Petar
 */
public class Kompjuter extends Igrac {

    private static final int [] levelBestMoveprobability = {30, 50, 80, 90}; // za nivoe 1, 2, 3, 4 verovatnoca da se odigra najbolji naspram slučajnog poteza
    private int level = 0;
    public Kompjuter(boolean prviNaPotezu) {
        super(prviNaPotezu);
    }
    
    public Kompjuter(boolean prviNaPotezu, int level) {
        this(prviNaPotezu);
        this.level = level;
    }
    
    @Override
    public void odigrajPotez() {
        if (level == 4) odigrajNajboljiPotez();
        else {
            if (trebaOdigratiNajboljiPotez()) 
                odigrajNajboljiPotez();
            else 
                odigrajSlucajanPotez();
        }
        
    }
    
    private boolean trebaOdigratiNajboljiPotez() {
        double r = Math.random();
        if (r < ((double) levelBestMoveprobability[level]) / 100.0) return true;
        else return false;
    }
    
    private void odigrajNajboljiPotez() {
        Matrica matrica = new Matrica(this.igra.getTabla());
        ArrayList<Koordinate> najboljiPotezi = matrica.najboljiPotez(this.getOznaka());
        int index = slucajanIndex(najboljiPotezi.size());
        Koordinate koord = najboljiPotezi.get(index);
        igra.getTabla().postaviPolje(koord.i, koord.j, this.getOznaka());  
        
    }
    
    private void odigrajSlucajanPotez() {
        Matrica matrica = new Matrica(this.igra.getTabla());
        Koordinate potez = matrica.slucajanPotez();
        igra.getTabla().postaviPolje(potez.i, potez.j, this.getOznaka());  
        
    }
    
    private int slucajanIndex(int opseg) {
        double r = Math.random();
        double odseceno = Math.floor(r * opseg);
        int zaokruzeno = (int) Math.round(odseceno);
        if (zaokruzeno < 0) {
         //   System.out.println("Zaokruzeni index je negativan!"); zaokruzeno = 0;
        }
        if (zaokruzeno == opseg) zaokruzeno--; // za svaki slucaj
        return zaokruzeno;
    }
    
}
