package net.etfbl.java;

import com.sun.webkit.BackForwardList;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import sample.MapaController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import static java.lang.Thread.sleep;

public class Kuca {

    private int id;
    private int count;
    private CopyOnWriteArrayList<Stanovnik> ukucani=new CopyOnWriteArrayList<>();
    private int red,kolona;
    public volatile boolean zarazena=false;

    public Color fasada;

    public Background background;

    public Kuca(int id, int red, int kolona)
    {
        this.id=id;
        this.red=red;
        this.kolona=kolona;

        fasada=Boja.boje.get(id);

        background = new Background(new BackgroundFill(fasada, null, null));
        //provjerava da li u kuci ima zarazenih ukucana
        Thread thread=new Thread( () ->
        {
            while (true) {
                if (!ukucani.isEmpty()) {
                    if(ukucani.stream()
                              .anyMatch(stanovnik -> stanovnik.zarazen))
                        zarazena=true;
                    else zarazena=false;
                }
                if(!zarazena)
                {
                    for (Stanovnik s: ukucani)
                        s.bjezi=false;
                    prikaziKucu();
                }
                try {
                    sleep(1000);
                } catch (Exception e)
                {
                    MyLogger.log(Level.SEVERE,e.getMessage(),e);
                    e.printStackTrace();
                }
            }

        });
        thread.start();



    }

    public void dodajUkucana(Stanovnik stanovnik)
    {
        ukucani.add(stanovnik);
        ++count;
    }

    public int getId() {
        return id;
    }

    public int getRed() {
        return red;
    }

    public int getKolona() {
        return kolona;
    }

    public int getCount() {
        return count;
    }

    public CopyOnWriteArrayList<Stanovnik> getUkucani() {
        return ukucani;
    }

   public void prikaziKucu()
    {
        if(zarazena)
        {
            Platform.runLater(() -> {
                Image img = null;
                try {
                    img = new Image(new FileInputStream(new File(".\\resources\\home2.png")));
                } catch (FileNotFoundException e) {
                    MyLogger.log(Level.SEVERE,e.getMessage(),e);
                    e.printStackTrace();
                }
                ImageView view = new ImageView(img);
                view.setFitHeight(Grad.imgRatio);
                view.setPreserveRatio(true);
                MapaController.matrica[red][kolona].setGraphic(view);
            });
        }
        else
        {
            Platform.runLater(()-> {
                Image img = null;
                try {
                    img = new Image(new FileInputStream(new File(".\\resources\\home.png")));
                } catch (FileNotFoundException e) {
                    MyLogger.log(Level.SEVERE,e.getMessage(),e);
                    e.printStackTrace();
                }
                ImageView view = new ImageView(img);
                view.setFitHeight(Grad.imgRatio);
                view.setPreserveRatio(true);
                MapaController.matrica[red][kolona].setGraphic(view);
            });
        }
    }
}
