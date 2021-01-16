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

    public SerijalizabilniGrad(CopyOnWriteArrayList<Kuca> kuceUGradu, Stack<Alarm> alarmi,
                               CopyOnWriteArrayList<Ambulanta> ambulante, CopyOnWriteArrayList<Punkt> punktoviUGradu,int size,Polje[][] mapa)
    {
        this.kuceUGradu=kuceUGradu;
        this.alarmi=alarmi;
        this.ambulante=ambulante;
        this.punktoviUGradu=punktoviUGradu;
        this.size=size;
        this.mapa=mapa;
    }
}
