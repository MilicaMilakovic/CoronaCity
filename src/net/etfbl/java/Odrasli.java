package net.etfbl.java;

import java.util.Calendar;
import java.util.Random;

public class Odrasli extends Stanovnik {

    private int limit=Math.round(Grad.size*25/100);
    private int desniLimit,lijeviLimit, donjiLimit, gornjiLimit;

    public Odrasli(int id, Polje[][] mapa)
    {
        super(id,"O", mapa);
   }

    protected int izracunajGodinuRodjenja()
    {
        return Calendar.getInstance().get(Calendar.YEAR) - (new Random().nextInt(47)+18);
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
                            if (s.izracunajGodine() >= 18 && this.kucaId != s.kucaId)
                                return true;
                        }
                    }
                }
            }
        return false;
    }

    protected void postaviLimite()
    {
        lijeviLimit=pocetnaKolona-limit;
        desniLimit=pocetnaKolona+limit;
        gornjiLimit=pocetniRed-limit;
        donjiLimit=pocetniRed+limit;

        if(lijeviLimit < 0)
        {
            desniLimit += Math.abs(lijeviLimit);
            lijeviLimit= 0;
        }
        if(gornjiLimit < 0)
        {
            donjiLimit +=Math.abs(gornjiLimit);
            gornjiLimit=0;
        }
        if(desniLimit > Grad.size-1)
        {
            int pom=Grad.size-1-pocetnaKolona;
            lijeviLimit -=(limit-pom);
            desniLimit=Grad.size-1;
        }
        if(donjiLimit > Grad.size-1)
        {
            int pom=Grad.size-1-pocetniRed;
            gornjiLimit -= (limit-pom);
            donjiLimit=Grad.size-1;
        }

    }

    @Override
    protected boolean ispravanRaspored() {

        if(nextRed > donjiLimit || nextKolona > desniLimit || nextRed < gornjiLimit || nextKolona < lijeviLimit)
            return false;
        return true;
    }
}
