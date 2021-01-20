package net.etfbl.java;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import sample.MapaController;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

public class Ambulanta extends Thread implements Serializable {

    public static int count;

    private int id;
    private long kapacitet;
    private int trenutnoStanje;

    private double trenutnaTemp;
    private CopyOnWriteArrayList<Stanovnik> zarazeniStanovnici=new CopyOnWriteArrayList<>();
    private int red,kolona;
    private ArrayList<Integer> temperature=new ArrayList<>();

    public static int brojStanovnika;
    public static File file=new File(".\\info\\stanjeUAmbulantama.txt");
    public static int oporavljeni;
    public static int zarazeni;

    public static int zarazeniUkupno;
    public static int oporavljeniUkupno;
    public static int zarazeniOdrasli;
    public static int zarazeniStari;
    public static int zarazeniDjeca;
    public static int zenski;
    public static int muski;


    public static void prikaziStanje() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("Broj zaraženih:  "+ zarazeni);
            bw.write("\nBroj oporavljenih: " +oporavljeni);
        } catch (Exception e) {
            MyLogger.log(Level.SEVERE, e.getMessage(), e);
        }
    }


    public Ambulanta(int red, int kolona)
    {
        id= count;
        ++count;

        this.red=red;
        this.kolona=kolona;
        kapacitet=Math.round(((new Random()).nextInt(6)+10)*brojStanovnika/100.0);


        Image img1 = null;
        try {
            img1 = new Image(new FileInputStream(new File(".\\resources\\hospital.png")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ImageView view1 = new ImageView(img1);
        view1.setFitHeight(Grad.imgRatio);
        view1.setPreserveRatio(true);

        Platform.runLater( () -> {
            try {
                MapaController.matrica[red][kolona].setGraphic(view1);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        } );

        prikaziStanje();
    }

    public int getID() {
        return id;
    }

    public long getKapacitet() {
        return kapacitet;
    }

    public int getTrenutnoStanje() {
        return trenutnoStanje;
    }

    private void sakupiPodatke(Stanovnik s)
    {
        if(s.izracunajGodine()<18)
        {
            ++zarazeniDjeca;

        } else if(s.izracunajGodine()>65)
        {
            ++zarazeniStari;
        }
        else
            ++zarazeniOdrasli;

        if(s.pol.toString().equals("ZENSKI"))
            ++zenski;
        else
            ++muski;

    }

    public void setAlarm(Alarm alarm)
    {
       // this.alarm=alarm;
        for( Stanovnik s: Grad.mapa[alarm.getRed()][alarm.getKolona()].getStanovnici()) {
            if (s.zarazen) {

                sakupiPodatke(s);

                System.out.println(s.getIme() +" primljen u ambulantu" +"["+red+"]["+kolona+"]"+ "temp:"+s.getTemperatura()+" "+s.kucaId);
                zarazeniStanovnici.add(s);

                Grad.mapa[alarm.getRed()][alarm.getKolona()].ukloniStanovnika(s);
                MapaController.matrica[alarm.getRed()][alarm.getKolona()].setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

                ++trenutnoStanje;
                ++zarazeni;
                ++zarazeniUkupno;

                azurirajFajl();
            }
        }
    }

    public void run()
    {
        while (Grad.running) {

             if(!zarazeniStanovnici.isEmpty())
             {
                    for (Stanovnik stanovnik : zarazeniStanovnici)
                    {
                        int sum = 0;
                        for (int i = 0; i < 3; i++) {
                            int temp= stanovnik.getTemperatureLog().pop();
                            temperature.add(temp);
                            sum += temp;
                        }
                        if ((trenutnaTemp=sum / 3.0) < 37) {

                            zarazeniStanovnici.remove(stanovnik);
                            --trenutnoStanje;

                            stanovnik.getTemperatureLog().push(35);
                            stanovnik.getTemperatureLog().push(35);
                            stanovnik.getTemperatureLog().push(36);

                            posaljiKuci(stanovnik);
                            azurirajFajl();
                        }
                        else {
                            for(Integer t: temperature)
                                stanovnik.getTemperatureLog().push(t);
                        }
                    }
             }
            try {
                sleep(400);
            } catch (Exception e) {
                MyLogger.log(Level.SEVERE,e.getMessage(),e);
                e.printStackTrace();
            }
        }
    }

    private  void azurirajFajl()
    {
        synchronized (file) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write("Broj zaraženih: " + zarazeni + "\n");
                bw.write("Broj oporavljenih: " + oporavljeni);
            } catch (Exception e) {
                MyLogger.log(Level.SEVERE, e.getMessage(), e);
                e.printStackTrace();
            }
        }
    }
    private void posaljiKuci(Stanovnik stanovnik)
    {
        ++oporavljeniUkupno;
        ++oporavljeni;
        --zarazeni;

        System.out.println(stanovnik.toString() + " otpusten iz ambulante "+id + "temp:"+trenutnaTemp+" "+stanovnik.kucaId);

        Kuca kuca = Grad.kuceUGradu.get(stanovnik.kucaId);
        Grad.mapa[kuca.getRed()][kuca.getKolona()].dodajStanovnika(stanovnik);

        stanovnik.setRed(kuca.getRed());
        stanovnik.setKolona(kuca.getKolona());

        stanovnik.zarazen = false;
    }

    public boolean isPopunjena()
    {
        if(trenutnoStanje==kapacitet)
            return true;
        return false;
    }

    @Override
    public String toString(){
        return "• Ambulanta"+id;
    }

    public void prikaziAmbulantu() {
        Platform.runLater(() -> {
            Image img = null;
            try {
                img = new Image(new FileInputStream(new File(".\\resources\\hospital.png")));
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
