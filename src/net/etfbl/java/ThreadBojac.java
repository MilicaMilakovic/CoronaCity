package net.etfbl.java;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import sample.MapaController;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ThreadBojac extends Thread {

    @Override
    public void run() {

        ArrayList<Polje> grad = new ArrayList<>();
        for (int i=1 ; i <=Grad.size-2; i++) {
            for (int j = 1; j <= Grad.size - 2; j++) {
                grad.add(Grad.mapa[i][j]);
            }
        }
        while(true) {
            grad.parallelStream().forEach(e ->
            {
                if (e.getKuca() == null && e.getPunkt() == null) {

                    if (!e.getStanovnici().isEmpty()) {
                        Stanovnik s = e.getStanovnici().get(0);

                        Platform.runLater(() ->
                        {
                            MapaController.matrica[e.red][e.kolona].setBackground(s.background);
                            MapaController.matrica[e.red][e.kolona].setText(s.ime);
                            MapaController.matrica[e.red][e.kolona].setTextFill(Color.WHITE);
                        });
                    } else {
                        Label l = MapaController.matrica[e.red][e.kolona];
                        Platform.runLater(() ->
                        {
                            l.setBackground(Grad.DEFAULT_BACKGROUND);
                            l.setText(" ");
                            l.setTextFill(Color.WHITE);
                        });
                    }
                } else if (e.getPunkt() != null) {
                    Label l = MapaController.matrica[e.red][e.kolona];
                    Platform.runLater(() ->
                    {
                        l.setBackground(Grad.DEFAULT_BACKGROUND);
                    });

                } else if (e.getKuca() != null) {
                    Label l = MapaController.matrica[e.red][e.kolona];
                    Kuca k = e.getKuca();
                    Platform.runLater(() ->
                    {
                        l.setBackground(k.background);
                    });

                }


            });

            try
            {
                sleep(50);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }


//        while (true)
//        {
//            for (int i=1 ; i <=Grad.size-2; i++)
//            {
//                for (int j=1; j<= Grad.size-2; j++)
//                {
//                    if (Grad.mapa[i][j].getKuca()==null && Grad.mapa[i][j].getPunkt()==null)
//                    {
//
//                        if(!Grad.mapa[i][j].getStanovnici().isEmpty())
//                        {
//                            Stanovnik s= Grad.mapa[i][j].getStanovnici().get(0);
//
//                            Platform.runLater( () ->
//                            {
//                                MapaController.matrica[s.getRed()][s.getKolona()].setBackground(new Background(new BackgroundFill(s.getBoja(), null, null)));
//                                MapaController.matrica[s.getRed()][s.getKolona()].setText(s.ime);
//                                MapaController.matrica[s.getRed()][s.getKolona()].setTextFill(Color.WHITE);
//                            });
//                        }
//                        else {
//                            Label l = MapaController.matrica[i][j];
//                            Platform.runLater( () ->
//                            {
//                                l.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
//                                l.setText(" ");
//                                l.setTextFill(Color.WHITE);});
//                        }
//                    }
//                    else if(Grad.mapa[i][j].getPunkt()!=null)
//                    {
//                        Label l = MapaController.matrica[i][j];
//                        Platform.runLater( () ->
//                        {
//                            l.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
//                        } );
//
//                    }
//                    else if(Grad.mapa[i][j].getKuca()!=null)
//                    {
//                        Label l= MapaController.matrica[i][j];
//                        Kuca k= Grad.mapa[i][j].getKuca();
//                        Platform.runLater( () ->
//                        {
//                            l.setBackground(new Background(new BackgroundFill(k.fasada, null, null)));
//                        } );
//
//                    }
////                    else if(Grad.mapa[i][j].getAmbulanta()!=null)
////                    {
////                        Image img1 = null;
////                        try {
////                            img1 = new Image(new FileInputStream(new File(".\\resources\\hospital.png")));
////                        } catch (FileNotFoundException e) {
////                            e.printStackTrace();
////                        }
////                        ImageView view1 = new ImageView(img1);
////                        view1.setFitHeight(Grad.imgRatio);
////                        view1.setPreserveRatio(true);
////                        int red=i, kolona=j;
////
////                        Platform.runLater( () -> {
////                            try {
////
////                                    MapaController.matrica[red][kolona].setGraphic(view1);
////                            } catch (Exception e)
////                            {
////                                e.printStackTrace();
////                            }
////                        } );
////                    }
//                }
//            }
//
//            try
//            {
//                sleep(70);
//            }
//            catch (Exception e)
//            {
//                //
//            }
//        }
    }
}
