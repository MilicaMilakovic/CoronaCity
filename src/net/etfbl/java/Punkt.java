package net.etfbl.java;

import javafx.application.Platform;
import sample.MapaController;

import java.io.Serializable;
import java.util.logging.Level;

public class Punkt extends Thread implements Serializable {

    public int red, kolona;
    private static Polje[][] mapa;
    public  static MapaController mc;

    public Punkt(int red, int kolona, Polje[][] mapa) {
        this.red = red;
        this.kolona = kolona;
        this.mapa = mapa;
    }


    public void run() {
        try
        {
            sleep(5000);
        }
        catch (Exception e) {
            MyLogger.log(Level.SEVERE,e.getMessage(),e);
        }
        while (Grad.running) {
            for (int i = red - 1; i <= red + 1; i++) {
                for (int j = kolona - 1; j <= kolona + 1; j++) {
                    if (mapa[i][j].getKuca()==null && !mapa[i][j].getStanovnici().isEmpty()) { //punkt ne uzima u obzir kuce
                        for (Stanovnik stanovnik : mapa[i][j].getStanovnici()) {
                            if (stanovnik.getTemperatura() > 37 && !stanovnik.zarazen) {
                                stanovnik.zarazen=true;
                                Grad.alarmi.push(new Alarm(i,j,stanovnik.kucaId));

                                mc.setSignal();


//                                System.out.println(stanovnik.toString() + " R I P" + " punkt na " + red + " " + kolona);
                            }
                        }
                    }
                }
            }
            try {
                sleep(400);
            } catch (InterruptedException e) {
                MyLogger.log(Level.SEVERE,e.getMessage(),e);
                e.printStackTrace();
            }
        }

    }
}