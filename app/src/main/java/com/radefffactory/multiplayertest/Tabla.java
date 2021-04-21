/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radefffactory.multiplayertest;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

/**
 *
 * @author bojke
 */
public class Tabla {
    public static int X = 0, O = 1, PRAZNO = -1;
    private int[][] polja = new int[3][3];
    private Button [][] dugmad = new Button[3][3];
    private Igra igra; // igra koja se igra na tabli
    
    private int odigranPotezI, odigranPotezJ;
    public Tabla(Igra igra) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                polja[i][j] = PRAZNO;
//                dugmad[i][j] = new JButton("  ");
//                dugmad[i][j].setFont(new Font("Tahoma", Font.PLAIN, 50));
//                dugmad[i][j].setSize(60, 60);
//                dugmad[i][j].addActionListener(buttonAL);
            }
        }
        this.igra = igra;
    }
    
    public void setDugmad(Button button00, Button button01, Button button02,
                            Button button10, Button button11, Button button12,
                            Button button20, Button button21, Button button22) {
        dugmad[0][0] = button00; dugmad[0][1] = button01; dugmad[0][2] = button02;
        dugmad[1][0] = button10; dugmad[1][1] = button11; dugmad[1][2] = button12;
        dugmad[2][0] = button20; dugmad[2][1] = button21; dugmad[2][2] = button22;
        for (int i = 0; i < 3; i++) 
            for (int j = 0; j < 3; j++) {
                dugmad[i][j].setOnClickListener(buttonAL);
                dugmad[i][j].setText("");
            }
    }

    public int[][] getPolja() {
        return polja;
    }
    
    public static String oznakaPolja(int xo) {
        if (xo == X) return "X";
        if (xo == O) return "O";
        return null;
    }
    
    public Button[][] getDugmad() {
        return dugmad;
    }

    public int getOdigranPotezI() {
        return odigranPotezI;
    }

    public int getOdigranPotezJ() {
        return odigranPotezJ;
    }

    public void setOdigranPotezI(int odigranPotezI) {
        this.odigranPotezI = odigranPotezI;
    }

    public void setOdigranPotezJ(int odigranPotezJ) {
        this.odigranPotezJ = odigranPotezJ;
    }

    private View.OnClickListener buttonAL = new View.OnClickListener() {

        @Override
        public void onClick(View v)  {
            if (igra.isZavrsena()) return;
            if (! igra.isCovekNaPotezu()) return;
            int i = 0, j = 0;
            Button src = (Button) v;
            boolean nadjeno = false;
            for (i = 0; i < 3; i++) {
                for (j = 0; j < 3; j++) {
                    if (dugmad[i][j] == src) {
                        nadjeno = true;
                        break;
                    }                    
                }
                if (nadjeno) break;
            }
            if (polja[i][j] != PRAZNO) return;
            odigranPotezI = i;
            odigranPotezJ = j;
           // System.out.println("Pritisnuto " + i + " " + j);

            Message messageObject = new Message(Message.MessageCodes.REGULARANPOTEZ, PlayActivity.playActivity.role, "", 0, i, j );
            igra.getMessageRef().setValue(Message.convertToJsonString(messageObject));
            igra.setCovekOdigraoPotez(true);
        }
    };
    public void postaviPolje(final int i,final  int j, final int vrednost) {
        if (i < 0 || i > 2) return;
        if (j < 0 || j > 2) return;
        if (polja[i][j] != PRAZNO) return;
        if (vrednost != X && vrednost != O) return;
        polja[i][j] = vrednost;

        PlayActivity.playActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // Stuff that updates the UI
                dugmad[i][j].setText(oznakaPolja(vrednost));
            }
        });

//        dugmad[i][j].setText(oznakaPolja(vrednost));

    }
    public static void main(String [] args) {
        // Tabla t = new Tabla();
      //  System.out.println("Hello world.");
    }
    
    private boolean redSaIstomOznakom(int i) {
        if (polja[i][0] != PRAZNO && polja[i][0] == polja[i][1] && polja[i][0] == polja[i][2]) return true;
        return false;
    }
    private boolean kolonaSaIstomOznakom(int j) {
        if (polja[0][j] != PRAZNO && polja[0][j] == polja[1][j] && polja[0][j] == polja[2][j]) return true;
        return false;
    }
    
    private boolean dijagonalaSaIstomOznakom() {
        if (polja[0][0] != PRAZNO && polja[0][0] == polja[1][1] && polja[0][0] == polja[2][2]) return true;
        if (polja[0][2] != PRAZNO && polja[0][2] == polja[1][1] && polja[0][2] == polja[2][0]) return true;
        return false;
    }
    private boolean svaPoljaPopunjena() {
        for (int i = 0; i < 3; i++) 
                for (int j = 0; j < 3; j++) 
                    if (polja[i][j] == Tabla.PRAZNO) return false;
        return true;
    }
    
    public boolean konacnoStanje() {
        for (int i = 0; i < 3; i++)
            if (redSaIstomOznakom(i)) return true;
        for (int i = 0; i < 3; i++)
            if (kolonaSaIstomOznakom(i)) return true;
        if (dijagonalaSaIstomOznakom()) return true;
        if (svaPoljaPopunjena()) return true;
        return false;
    }
    
}
