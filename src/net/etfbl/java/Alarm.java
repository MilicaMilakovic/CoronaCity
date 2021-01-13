package net.etfbl.java;

import java.io.Serializable;

public class Alarm implements Serializable {

    private int red,kolona;
    private int kucaId;

    public Alarm(int red, int kolona, int kucaId)
    {
        this.red=red;
        this.kolona=kolona;
        this.kucaId=kucaId;
    }

    public int getRed() {
        return red;
    }

    public int getKolona() {
        return kolona;
    }

    public int getKucaId() {
        return kucaId;
    }
}
