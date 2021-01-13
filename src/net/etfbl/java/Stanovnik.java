package net.etfbl.java;

import com.sun.webkit.BackForwardList;
import javafx.application.Platform;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import sample.MapaController;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Random;
import java.util.Stack;
import java.util.logging.Level;

public abstract  class Stanovnik extends Thread implements Serializable {

    protected int id;
    protected String ime;
    protected String prezime;
    protected int godinaRodjenja;
    protected Pol pol;
    protected int kucaId;
    protected int temperatura;
    public volatile boolean zarazen=false;
    public volatile boolean bjezi=false;

    protected Polje[][] mapa;

    protected static Object lock = new Object();

    private Random random = new Random();
    private Stack<Integer> temperatureLog = new Stack<>();

    private int red, kolona;
    protected int nextRed, nextKolona;
    protected int pocetniRed, pocetnaKolona;

    private transient Color boja;
    public transient Background background;
    private static MapaController mc;

    public Stanovnik(int id, String grupa, Polje[][] mapa) {
        this.id = id;
        ime = grupa + id;
        prezime = "Prezime"+ id;
        godinaRodjenja = izracunajGodinuRodjenja();

        Pol[] polovi = Pol.values();
        pol = polovi[random.nextInt(2)];

        this.mapa = mapa;

        // temperatureLog ovako nikad nece biti prazan, odnosno uvijek ce se moci dobiti bar 3 posljednje vrijednosti
        // temperature stanovnika, u slucaju da se zaustavi na punktu cim krene
        // pretpostavljam da je bio zdrav na pocetku, stoga na stek ostavljam vrijednosti 36 i 35

        temperatureLog.push(37);
        temperatureLog.push(36);

        izracunajTemperaturu();

    }

    public void setKucaId(int kucaId) {
        this.kucaId = kucaId;
    }

    public void setRed(int red) {
        this.red = red;
        pocetniRed=red;
    }

    public void setKolona(int kolona) {
        this.kolona = kolona;
        pocetnaKolona=kolona;
    }

    public int getRed() {
        return red;
    }

    public int getKolona() {
        return kolona;
    }

    public Color getBoja() {
        return boja;
    }

    public void setBoja(Color boja) {

        this.boja = boja;
        background = new Background(new BackgroundFill(boja, null, null));
    }

    public int getTemperatura() {
        return temperatura;
    }

    public Stack<Integer> getTemperatureLog() {
        return temperatureLog;
    }

    protected abstract void postaviLimite();
    protected abstract int izracunajGodinuRodjenja();

    public int izracunajGodine() {
        return Calendar.getInstance().get(Calendar.YEAR) - godinaRodjenja;
    }

    public void vratiSeKuci(int redK, int kolonaK)
    {
        while (red != redK || kolona != kolonaK) {

            if (red < redK) {
                narednaPozicija(Pravac.DOLE);
            } else if (red > redK) {
                narednaPozicija(Pravac.GORE);
            } else if (kolona < kolonaK) {
                narednaPozicija(Pravac.DESNO);
            } else if (kolona > kolonaK) {
                narednaPozicija(Pravac.LIJEVO);
            }
            try {
                sleep(500);
            } catch (Exception e)
            {
                MyLogger.log(Level.SEVERE,e.getMessage(),e);
                e.printStackTrace();
            }

            pomjeriSe();
        }

    }
    private void izracunajTemperaturu() {
        Thread thread = new Thread(() ->
        {
            while (true) {
                try {
                    temperatura = random.nextInt(6) + 35;
                    temperatureLog.push(temperatura);
                    sleep(30000);
                } catch (Exception e) {
                    MyLogger.log(Level.SEVERE,e.getMessage(),e);
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    protected void pomjeriSe() {

        // uklanjanje sa trenutne pozicije
        mapa[red][kolona].ukloniStanovnika(this);

        // dodavanje na sljedecu poziciju

        red = nextRed;
        kolona = nextKolona;

        mapa[red][kolona].dodajStanovnika(this);
    }

    protected abstract boolean isNarusenaDistanca();
    protected abstract boolean ispravanRaspored();

    private void narednaPozicija(Pravac pravac) {
        nextRed = red;
        nextKolona = kolona;

        switch (pravac.toString()) {
            case "GORE": {
                nextRed = red - 1;
                break;
            }
            case "DOLE": {
                nextRed = red + 1;
                break;
            }
            case "LIJEVO": {
                nextKolona = kolona - 1;
                break;
            }
            case "DESNO": {
                nextKolona = kolona + 1;
                break;
            }
        }
    }

    private boolean isValidnaPozicija() {
        return nextRed > 0 && nextKolona > 0 && nextRed < Grad.size-1 && nextKolona < Grad.size-1 ;
    }

    @Override
    public void run() {

        while (Grad.running) {
//            System.out.println("run");
            try {
                sleep(700 + new Random().nextInt(500));

                while (zarazen)
                {
                    sleep(1000);
                }

                if (bjezi) {
//                    System.out.println("Mozda bjezi");
                    vratiSeKuci(Grad.kuceUGradu.get(kucaId).getRed(), Grad.kuceUGradu.get(kucaId).getKolona());
                }
                else {
                    synchronized (lock) {
//                      System.out.println("Hoda");

                        Pravac pravac = Pravac.values()[random.nextInt(4)];
                        narednaPozicija(pravac);

                        if (isNarusenaDistanca() || !isValidnaPozicija() || !ispravanRaspored())
                            continue;

                        else {
                            mc.ispisiKretanje(toString()+pravac.toString()+"\n");
                            pomjeriSe();
                        }
                    }
                }

            } catch (Exception e) {
                MyLogger.log(Level.WARNING,e.getMessage(),e);
            }
        }
    }

    public String getIme() {
        return ime;
    }

    public static void setMc(MapaController mc) {
        Stanovnik.mc = mc;
    }

    @Override
    public String toString()
    {
        return "•"+ime+"|"+ izracunajGodine() + "|" + temperatura+ "|" ;
    }

}