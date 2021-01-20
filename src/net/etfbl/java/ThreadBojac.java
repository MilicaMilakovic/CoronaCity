package net.etfbl.java;

import javafx.application.Platform;
import javafx.scene.control.Label;

import javafx.scene.paint.Color;
import sample.MapaController;

import java.util.ArrayList;
import java.util.logging.Level;

public class ThreadBojac extends Thread {

    public static Polje[][] mapa;
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
                MyLogger.log(Level.WARNING,e.getMessage(),e);
            }
        }
    }
}
