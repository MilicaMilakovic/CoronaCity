package net.etfbl.java;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import sample.MapaController;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Grad {

    private static Random random=new Random();
    public static MapaController mc;
    public static FileWatcher fw;
    public static Polje[][] mapa;

    public static int stari,odrasli,djeca;
    public static boolean running = true;

    public static final Background DEFAULT_BACKGROUND = new Background(new BackgroundFill(Color.WHITE, null, null));

//    public static  int size;
    public static  int size= random.nextInt(16)+15;
//    public static int size;

    public static  double ratio= 620/size;
    public static  double imgRatio=550/size;

//    public static ConcurrentHashMap<Integer,Polje> kuceUGradu=new ConcurrentHashMap<>();
    public static CopyOnWriteArrayList<Kuca> kuceUGradu=new CopyOnWriteArrayList<>();
    public static Stack<Alarm> alarmi=new Stack<>();
    public static CopyOnWriteArrayList<Ambulanta> ambulante=new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<Punkt> punktoviUGradu=new CopyOnWriteArrayList<>();

    public void setMc(MapaController mc) {
        this.mc = mc;
    }

    public Grad(int kuce, int punktovi, int stari, int odrasli, int djeca) throws Exception
    {
        mapa=new Polje[size][size];

        for (int i=0; i<size; i++ ) {
            for (int j = 0; j < size; j++) {
                mapa[i][j] = new Polje(i, j);
            }
        }

        Ambulanta.brojStanovnika=stari+odrasli+djeca;
        postaviAmbulante();

        //napravi prvo kuce, postavi ih na nasumicne lokacije
        int n=0;

        while(n<kuce) {

            int i = random.nextInt(size-2)+1; //prvi i posljednji red/kolona rezervisani za ambulante
            int j = random.nextInt(size-2)+1;

            if (mapa[i][j].getKuca()==null) {

                mapa[i][j] = new Polje(new Kuca(n,i,j), null, null);
//                kuceUGradu.put(n,mapa[i][j]);
                kuceUGradu.add(mapa[i][j].getKuca());

                ++n;
                MapaController.matrica[i][j].setBackground(new Background(new BackgroundFill(mapa[i][j].getKuca().fasada, null, null)));
                mapa[i][j].getKuca().prikaziKucu();

            }
        }

        n=0;
        //napravi punktove
        while(n<punktovi)
        {
            int i = random.nextInt(size-2)+1; //prvi i posljednji red/kolona rezervisani za ambulante
            int j = random.nextInt(size-2)+1;

            if (mapa[i][j].getKuca()==null && mapa[i][j].getPunkt()==null) //kuca i punkt ne mogu da se nadju na istoj poziciji
            {
                mapa[i][j]=new Polje(null, new Punkt(i,j,mapa), null);
                punktoviUGradu.add(mapa[i][j].getPunkt());
                //mapa[i][j].getPunkt().setMc(mc);
//                mapa[i][j].getPunkt().start();
                ++n;

                MapaController.matrica[i][j].setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

                Image img = new Image(new FileInputStream(new File(".\\resources\\doctor.png")));
                ImageView view = new ImageView(img);
                view.setFitHeight(imgRatio);
                view.setPreserveRatio(true);
                MapaController.matrica[i][j].setGraphic(view);
            }
        }

        PriorityQueue<Stanovnik> stanovnici=dodajURed(stari,odrasli,djeca);
        // Rasporedjivanje stanovnika po kucama. Stanovnici se postavljaju u prioritetni red po godinama, dok se kuce
        // stavljaju u hash mapu <idKuce,Polje>. Uzima se stanovnik iz reda, setuju polja koja nisu postavljena i dodaje na polje
        // na kome se nalazi kuca
        n=0;
        if(stari+odrasli>0)
        {
            while (!stanovnici.isEmpty())
            {
                    if (n == kuceUGradu.size())  // n se postavlja na 0 kako bi se dodjela vrsila ukrug
                        n = 0;

                    Stanovnik s = stanovnici.remove();
//                    Polje polje = kuceUGradu.get(n);
                    Kuca kuca=kuceUGradu.get(n);

                    // Djeca ne mogu biti sama u kuci, stoga ako je trenutni stanovnik dijete, sve dok je kuca prazna, uzimace
                    // se naredna kuca, dok se ne dodje do one koja u sebi vec ima nekog od starih ili odraslih
                    if (s.izracunajGodine() < 18)
                    {
                        while (kuca.getUkucani().isEmpty()) {
                            ++n;
                            if (n == kuceUGradu.size())
                                n = 0;

                            kuca = kuceUGradu.get(n);
                        }
                    }

                    s.setKucaId(kuca.getId());

                    s.setBoja(kuca.fasada);
                    s.setKolona(kuca.getKolona());
                    s.setRed(kuca.getRed());
                    s.postaviLimite();

                    kuca.dodajUkucana(s);
                    mapa[kuca.getRed()][kuca.getKolona()].dodajStanovnika(s);

                    ++n;
            }
        }
        else
        {
            System.out.println("Djeca ne mogu biti sama u kucama. Potrebno je unijeti bar jednu odraslu ili staru osobu.");
        }

//        n=0;
//        while (kuceUGradu.size()>n)
//        {
//            Polje polje=kuceUGradu.get(n);
//            System.out.println("Kuca: "+polje.getKuca().getId()+" count: "+polje.getKuca().getCount());
//            for(Stanovnik s : polje.getKuca().getUkucani())
//                System.out.println(s.toString());
//            ++n;
//        }

    }
    private void postaviAmbulante() throws Exception
    {
        ambulante.add((mapa[0][0]=new Polje(null,null,new Ambulanta(0,0))).getAmbulanta());
        ambulante.add((mapa[0][size-1]=new Polje(null,null,new Ambulanta(0,size-1))).getAmbulanta());
        ambulante.add((mapa[size-1][0]=new Polje(null,null,new Ambulanta(size-1,0))).getAmbulanta());
        ambulante.add((mapa[size-1][size-1]=new Polje(null,null,new Ambulanta(size-1,size-1))).getAmbulanta());

    }
    private PriorityQueue<Stanovnik> dodajURed(int stari, int odrasli, int djeca)
    {
        int n=0;

        PriorityQueue<Stanovnik> stanovnici= new PriorityQueue<>(new Comparator<Stanovnik>() {
            @Override
            public int compare(Stanovnik o1, Stanovnik o2) {
                return o2.izracunajGodine()-o1.izracunajGodine();
            }
        });

        while (stari>0)
        {
            stanovnici.add(new Stari(n++,mapa));
            --stari;
        }
        n=0;
        while (odrasli>0)
        {
            stanovnici.add(new Odrasli(n++,mapa));
            --odrasli;
        }
        n=0;
        while (djeca>0)
        {
            stanovnici.add(new Dijete(++n,mapa));
            --djeca;
        }
        return stanovnici;
    }

    public static void startThreads()
    {
        int n=0;
        while (n<kuceUGradu.size())
        {
            Kuca polje=kuceUGradu.get(n);
            for(Stanovnik stanovnik : polje.getUkucani())
            {
                stanovnik.start();
//                System.out.println(stanovnik.toString()+" pokrenut.");
            }
            ++n;
        }
        punktoviUGradu.forEach(Thread::start);
        ambulante.forEach(ambulanta -> {
            if(!ambulanta.isAlive())
                ambulanta.start();
        });
    }

    public static void prikaziBoje()
    {
        for(Kuca k : kuceUGradu)
        {
            k.setFasada(Boja.boje.get(k.getId()));
            for(Stanovnik s : k.getUkucani())
            {
                s.setBoja(k.fasada);
            }
        }
    }
}
