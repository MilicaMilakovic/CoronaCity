package net.etfbl.java;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sample.MapaController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.logging.Level;

public class Punkt extends Thread implements Serializable {

    public int red, kolona;
    public static Polje[][] mapa;
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

    public void prikaziPunkt()
    {
        Platform.runLater(()-> {
            Image img = null;
            try {
                img = new Image(new FileInputStream(new File(".\\resources\\doctor.png")));
            } catch (FileNotFoundException e) {
                MyLogger.log(Level.SEVERE, e.getMessage(), e);
                e.printStackTrace();
            }
            ImageView view = new ImageView(img);
            view.setFitHeight(Grad.imgRatio);
            view.setPreserveRatio(true);
            MapaController.matrica[red][kolona].setGraphic(view);
        });
    }


}