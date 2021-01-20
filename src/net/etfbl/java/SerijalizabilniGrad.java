package net.etfbl.java;

import java.io.Serializable;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;

public class SerijalizabilniGrad implements Serializable {

    public  CopyOnWriteArrayList<Kuca> kuceUGradu;
    public  Stack<Alarm> alarmi;
    public  CopyOnWriteArrayList<Ambulanta> ambulante;
    public  CopyOnWriteArrayList<Punkt> punktoviUGradu;
    public  Polje[][] mapa;
    public int size;


    //polja ambulante

    public int oporavljeni;
    public int zarazeni;

    public  int zarazeniUkupno;
    public  int oporavljeniUkupno;
    public  int zarazeniOdrasli;
    public  int zarazeniStari;
    public  int zarazeniDjeca;
    public  int zenski;
    public  int muski;

    public SerijalizabilniGrad(CopyOnWriteArrayList<Kuca> kuceUGradu, Stack<Alarm> alarmi,
                               CopyOnWriteArrayList<Ambulanta> ambulante, CopyOnWriteArrayList<Punkt> punktoviUGradu,int size,Polje[][] mapa,
                               int oporavljeni, int zarazeni, int zarazeniUkupno, int oporavljeniUkupno, int zarazeniOdrasli,
                               int zarazeniStari, int zarazeniDjeca, int zenski, int muski)
    {
        this.kuceUGradu=kuceUGradu;
        this.alarmi=alarmi;
        this.ambulante=ambulante;
        this.punktoviUGradu=punktoviUGradu;
        this.size=size;
        this.mapa=mapa;
        this.oporavljeni=oporavljeni;
        this.zarazeni=zarazeni;
        this.zarazeniUkupno=zarazeniUkupno;
        this.oporavljeniUkupno=oporavljeniUkupno;
        this.zarazeniOdrasli=zarazeniOdrasli;
        this.zarazeniStari=zarazeniStari;
        this.zarazeniDjeca=zarazeniDjeca;
        this.zenski=zenski;
        this.muski=muski;
    }
}
