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
public class Matrica {
    public int[][] polja;
    public int bestI, bestJ; // ova polja se setuju posle poziva metode najboljiPotez
    
    public Matrica() {
        polja = new int[3][3];
    }
    public Matrica(Tabla tabla) {
        polja = new int[3][3];
        int [][] t = tabla.getPolja();
        for (int i = 0; i < 3; i++) 
            for (int j = 0; j < 3; j++)
                polja[i][j] = t[i][j];
    }
    
    public Matrica(Matrica a) {
        polja = new int[3][3];
        for (int i = 0; i < 3; i++) 
            for (int j = 0; j < 3; j++)
                polja[i][j] = a.polja[i][j];
    }
    
    public boolean isPobednik(int oznaka) {
        for (int i = 0; i < 3; i++)
            if (sviIstiURedu(i, oznaka)) return true;
        for (int i = 0; i < 3; i++)
            if (sviIstiUKoloni(i, oznaka)) return true;
        if (sviIstiNaDijagonali(oznaka)) return true;
        return false;
    }
    /**
     * Ako su sva polja popunjena i niko nije pobednik, tj. nije pobednik ni X ni O
     * @return 
     */
    public boolean neresenoKonacnoStanje() {
        for (int i = 0; i < 3; i++) 
            for (int j = 0; j < 3; j++)
                if (polja[i][j] == Tabla.PRAZNO) return false;
        if (isPobednik(Tabla.X)) return false;
        else if (isPobednik(Tabla.O)) return false;
        else return true;
    }
    private boolean sviIstiURedu(int i, int oznaka) {
        if (polja[i][0] == oznaka && polja[i][1] == oznaka && polja[i][2] == oznaka) return true;
        else return false;
    }
    
    private boolean sviIstiUKoloni(int j, int oznaka) {
        if (polja[0][j] == oznaka && polja[1][j] == oznaka && polja[2][j] == oznaka) return true;
        else return false;
    }
    
    private boolean sviIstiNaDijagonali(int oznaka) {
        if (polja[0][0] == oznaka && polja[1][1] == oznaka && polja[2][2] == oznaka) return true;
        if (polja[0][2] == oznaka && polja[1][1] == oznaka && polja[2][0] == oznaka) return true;
        return false;
    }
    /**
     * 
     * @param oznaka Igrac koji koristi oznaku je na potezu
     * @return true ako i samo ako pobedjuje u prvom potezu
     */
    public boolean isPobednikUPrvomPotezu(int oznaka) {
        for (int i = 0; i < 3; i++) 
            for (int j = 0; j < 3; j++)
                if (polja[i][j] == Tabla.PRAZNO) {
                    Matrica nova = new Matrica(this);
                    nova.polja[i][j] = oznaka;
                    if (nova.isPobednik(oznaka)) return true;
                }
        return false;
    }
    
    public boolean isNeresenoUPrvomPotezu(int oznaka) {
        int i = 0; int j = 0; int brojPraznih = 0;
        int praznoI = 0, praznoJ = 0;
        for (i = 0; i < 3; i++)
            for (j = 0; j < 3; j++) {
                if (polja[i][j] == Tabla.PRAZNO) {
                    brojPraznih++;
                    if (brojPraznih > 1) return false;
                    praznoI = i; praznoJ = j;
                }
            }
        Matrica nova = new Matrica(this);
        nova.polja[praznoI][praznoJ] = oznaka;
        return nova.neresenoKonacnoStanje();
    }
    
    public static int suprotnaOznaka(int oznaka) {
        if (oznaka == Tabla.X) return Tabla.O;
        else return Tabla.X;
    }
    /**
     * 
     * @param oznaka Igrac sa ozakom je na potezu
     * @return 1 ako igrac sa oznakom oznaka dobija, 0 ako je nerešeno, -1 ako gubi
     */
    public int procenaIshoda(int oznaka) {
        // prvo proverimo da nije igra vec zavrsena
        if (isPobednik(oznaka)) return 1;
        if (neresenoKonacnoStanje()) return 0;
        if (isPobednik(suprotnaOznaka(oznaka))) return -1;
        if (isPobednikUPrvomPotezu(oznaka)) return 1;
        if (isNeresenoUPrvomPotezu(oznaka)) return 0;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                if (polja[i][j] == Tabla.PRAZNO) {
                    Matrica nova = new Matrica(this);
                    nova.polja[i][j] = oznaka;
                    if (nova.procenaIshoda(suprotnaOznaka(oznaka)) == -1) return 1; // ako postoji bar jedan potez za koji drugi igrac gubi, onda ovaj dobija
                }
            }        
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                if (polja[i][j] == Tabla.PRAZNO) {
                    Matrica nova = new Matrica(this);
                    nova.polja[i][j] = oznaka;
                    if (nova.procenaIshoda(suprotnaOznaka(oznaka)) == 0) return 0; // ako ne postoji potez za koji dobija, a postoji bar jedan za koje je nerešeno onda je nerešeno                                           
                }
            }
        
        // inače gubi
        return -1;
    }
    
    public ArrayList<Koordinate> najboljiPotez(int oznaka) {
        int procena = procenaIshoda(oznaka);
        ArrayList<Koordinate> result = new ArrayList<>();
        if (procena == 1) {
            // nalazimo sve poteze za koje dobija
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (polja[i][j] == Tabla.PRAZNO) {
                        Matrica nova = new Matrica(this);
                        nova.polja[i][j] = oznaka;
                        if (nova.procenaIshoda(suprotnaOznaka(oznaka)) == -1) {
                            result.add(new Koordinate(i, j));
                        }
                    }
                }
            }
        } else if (procena == 0) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (polja[i][j] == Tabla.PRAZNO) {
                        Matrica nova = new Matrica(this);
                        nova.polja[i][j] = oznaka;
                        if (nova.procenaIshoda(suprotnaOznaka(oznaka)) == 0) {
                            result.add(new Koordinate(i, j));
                        }
                    }
                }
            }
        } else {
            // igrac gubi, svi potezi su jednaki, ovde ne radimo pokusaj da se izvuce
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (polja[i][j] == Tabla.PRAZNO) {                                                
                            result.add(new Koordinate(i, j));                        
                    }
                }
            }
        }
        return result;
    }
    
    public Koordinate slucajanPotez() {
        ArrayList<Koordinate> praznaPolja = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (polja[i][j] == Tabla.PRAZNO) {
                    praznaPolja.add(new Koordinate(i, j));
                }
            }
        }        
        return praznaPolja.get(slucajanIndex(praznaPolja.size()));        
    }
    
    private int slucajanIndex(int opseg) {
        double r = Math.random();
        double odseceno = Math.floor(r * opseg);
        int zaokruzeno = (int) Math.round(odseceno);
        if (zaokruzeno < 0) {
        //    System.out.println("Zaokruzeni index je negativan!"); zaokruzeno = 0;
        }
        if (zaokruzeno == opseg) zaokruzeno--; // za svaki slucaj
        return zaokruzeno;
    }
    
    public static void test() {
        Matrica a = new Matrica();
        for (int i = 0; i < 3; i++) 
            for (int j = 0; j < 3; j++)
                a.polja[i][j] = Tabla.PRAZNO;
        int procenaX = a.procenaIshoda(Tabla.X);
        int procenaO = a.procenaIshoda(Tabla.O);
    //    System.out.println("Procene su " + procenaX + " i " + procenaO);
    }
    public static void main(String [] args) {
        test();
   //     System.out.println("Test zavrsen.");
    }
}

