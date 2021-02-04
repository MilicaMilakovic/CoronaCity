package net.etfbl.java;

import java.util.Calendar;
import java.util.Random;

public class Dijete extends Stanovnik {


    public Dijete(int id, Polje[][] mapa)
    {
        super(id,"D",mapa);
    }

    protected int izracunajGodinuRodjenja()
    {
        return Calendar.getInstance().get(Calendar.YEAR) - (new Random().nextInt(18));
    }

    protected boolean isNarusenaDistanca() {

        int redPocetak= nextRed-2;
        int redKraj=nextRed+2;

        int kolonaPocetak=nextKolona-2;
        int kolonaKraj=nextKolona+2;

        if(redPocetak < 0 )
            redPocetak= 1;
        if(redKraj >= Grad.size-1)
            redKraj= Grad.size-2;
        if(kolonaPocetak < 0 )
            kolonaPocetak=1;
        if(kolonaKraj >= Grad.size-1)
            kolonaKraj= Grad.size-2;


        for (int i = redPocetak; i <= redKraj; i++)
        {
            for (int j = kolonaPocetak; j <= kolonaKraj; j++)
            {
                    if (mapa[i][j].getKuca()==null && !mapa[i][j].getStanovnici().isEmpty())
                    {
                        for (Stanovnik s : mapa[i][j].getStanovnici())
                        {
                            if (s.izracunajGodine() > 65 && this.kucaId != s.kucaId)
                                return true;
                        }
                    }
                }
        }
        return false;
    }

    @Override
    protected void postaviLimite() {

    }

    @Override
    protected boolean ispravanRaspored() {
        return true;
    }
}
