package net.etfbl.java;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Polje implements Serializable {

    private CopyOnWriteArrayList<Stanovnik> stanovnici=new CopyOnWriteArrayList<>();
    private Kuca kuca;
    private Punkt punkt;
    private Ambulanta ambulanta;
    public int red,kolona;

    public Polje(int red, int kolona)
    {
        this.red=red;
        this.kolona=kolona;
    }

    public Polje (Kuca kuca, Punkt punkt, Ambulanta ambulanta)
    {
        this.kuca=kuca;
        this.punkt=punkt;
        this.ambulanta=ambulanta;
        if(kuca!=null)
        {
            red=kuca.getRed();
            kolona=kuca.getKolona();
        }
        else if(punkt!=null)
        {
            red=punkt.red;
            kolona= punkt.kolona;
        }
    }

    public CopyOnWriteArrayList<Stanovnik> getStanovnici() {
        return stanovnici;
    }

    public Kuca getKuca() {
        return kuca;
    }

    public Punkt getPunkt() {
        return punkt;
    }

    public Ambulanta getAmbulanta() {
        return ambulanta;
    }

    public void dodajStanovnika(Stanovnik s)
    {
        stanovnici.add(s);
    }
    public void ukloniStanovnika(Stanovnik s)
    {
        stanovnici.remove(s);
    }

}

