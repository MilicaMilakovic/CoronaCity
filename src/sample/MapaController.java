package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import net.etfbl.java.*;

import java.io.*;
import java.net.URL;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class MapaController implements Initializable {

    @FXML
    private Pane mapa;
    public static Label[][] matrica;
    @FXML
    public ImageView signal;
    @FXML
    private Label stanjeAmbulanti=new Label();
    @FXML
    public TextArea kretanjeInfo=new TextArea();

    private static boolean alreadyPressed=false;
    public static boolean serialization=false;
    private long start,end;
    private  String stanje="" ;

    public static int brojAmbulantnihVozila;

    // fajlovi

    public static final String serFilename = "grad.ser";
    public static final String stanjeUAmbulantamaFilename = "stanjeUAmbulantama.txt";
    public static final String resourcesDir = "resources";
    public static final String infoDir = "info";
    public static final String iconFilename = "icon.png";

    public static boolean signalSet;

    public void ispisiKretanje(String s)
    {
        Platform.runLater(() -> {
            kretanjeInfo.appendText(s);
        });
    }

    public void setSignal()
    {
        while (!Grad.alarmi.isEmpty())
        {
            signal.setVisible(true);
           // signalSet = true;
            try{
                Thread.sleep(1000);
            } catch (Exception e)
            {
                MyLogger.log(Level.WARNING,e.getMessage(),e);
            }
        }
       // signalSet = false;
    }
    public void setStanjeAmbulanti(String tekst)
    {
        Platform.runLater(()-> stanjeAmbulanti.setText(tekst));
    }

    public static void inicijalizujGrad(int djeca, int odrasli, int stari, int kuce, int punktovi)
    {
        try {
            new Grad(kuce,punktovi,stari,odrasli,djeca);
            Grad.djeca=djeca;
            Grad.odrasli=odrasli;
            Grad.stari=stari;

            System.out.println("Inicijalizovan grad.");
            new ThreadBojac().start();

        } catch (Exception e) {
            MyLogger.log(Level.WARNING,e.getMessage(),e);
        }
    }

    public static void ucitajGrad(SerijalizabilniGrad sg)
    {
        try{
            Grad.mapa=sg.mapa;
            Grad.kuceUGradu=sg.kuceUGradu;
            Grad.punktoviUGradu=sg.punktoviUGradu;
            Grad.ambulante=sg.ambulante;
            Grad.alarmi=sg.alarmi;
            Grad.size=sg.size;
            Grad.odrasli = sg.ukupnoOdrasli;
            Grad.djeca = sg.ukupnoDjeca;
            Grad.stari = sg.ukupnoStari;
            signalSet = sg.signalSet;


            Ambulanta.zarazeni = sg.zarazeni;
            Ambulanta.oporavljeni = sg.oporavljeni;
            Ambulanta.count = sg.count;
            Ambulanta.brojStanovnika = sg.brojStanovnika;
            Ambulanta.zarazeniUkupno= sg.zarazeniUkupno;
            Ambulanta.oporavljeniUkupno = sg.oporavljeniUkupno;
            Ambulanta.zarazeniDjeca = sg.zarazeniDjeca;
            Ambulanta.zarazeniOdrasli = sg.zarazeniOdrasli;
            Ambulanta.zarazeniStari = sg.zarazeniStari;
            Ambulanta.muski = sg.muski;
            Ambulanta.zenski = sg.zenski;

            StanjeAmbulantiController.red = sg.red;
            StanjeAmbulantiController.kolona = sg.kolona;
            StanjeAmbulantiController.popunjenoLijevo = sg.popunjenoLijevo;
            StanjeAmbulantiController.popunjenoDesno = sg.popunjenoDesno;
            StanjeAmbulantiController.popunjenoDole = sg.popunjenoDole;
            StanjeAmbulantiController.popunjenoGore = sg.popunjenoGore;

            Grad.prikaziBoje();
            Ambulanta.prikaziStanje();

            for (int i=0; i<Grad.size;i++)
            {
                for(int j=0;j<Grad.size;j++)
                {
                    if(Grad.mapa[i][j].getKuca()!=null)
                    {
                        Grad.mapa[i][j].getKuca().prikaziKucu();
                    } else if(Grad.mapa[i][j].getPunkt()!=null)
                    {
                        Grad.mapa[i][j].getPunkt().prikaziPunkt();
                    } else if(Grad.mapa[i][j].getAmbulanta()!=null)
                    {
                        Grad.mapa[i][j].getAmbulanta().prikaziAmbulantu();
                    }
                }
            }

            Punkt.mapa =Grad.mapa;

            for(Kuca k : Grad.kuceUGradu)
            {
                k.provjeriZarazeneThread();
                for(Stanovnik s : k.getUkucani())
                {
                   s.izracunajTemperaturu();
                }
            }

            Grad.startThreads();
            new ThreadBojac().start();
            new Thread(() ->
            {
                Grad.mc.setSignal();
            }).start();


        } catch(Exception e )
        {
            e.printStackTrace();
        }
    }
    public void posaljiVozilo()
    {
        int max = Math.min(Grad.alarmi.size(), brojAmbulantnihVozila);

        for(int i=0; i < max; i++) {

            if (!Grad.alarmi.isEmpty()) {
                Alarm alarm = Grad.alarmi.pop();
                for (Ambulanta ambulanta : Grad.ambulante) {
                    if (!ambulanta.isPopunjena()) {
                        ambulanta.setAlarm(alarm);
                        break;
                    }
                }
                Kuca kuca = Grad.kuceUGradu.get(alarm.getKucaId());

                try {
                    Grad.kuceUGradu.get(alarm.getKucaId()).prikaziKucu();
                } catch (Exception e) {
                    MyLogger.log(Level.WARNING, e.getMessage(), e);
                    e.printStackTrace();
                }

                for (Stanovnik s : kuca.getUkucani()) {
                    s.bjezi = true;
                }
            }
            signal.setVisible(false);
        }
    }

    public void prikaziStatistiku() throws Exception
    {
        Stage primaryStage= new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("Statistika.fxml"));
        primaryStage.setTitle("CoronaCity");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream( "."+File.separator+resourcesDir+File.separator+iconFilename )));
        primaryStage.setScene(new Scene(root, 800, 550));
        primaryStage.setX(290);
        primaryStage.setY(90);
        primaryStage.show();
    }

    public void stanjeUAmbulantama() throws Exception
    {
        Stage primaryStage= new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("StanjeAmbulanti.fxml"));
        primaryStage.setTitle("CoronaCity");
        primaryStage.setX(290);
        primaryStage.setY(90);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream( "."+File.separator+resourcesDir+File.separator+iconFilename )));
        primaryStage.setScene(new Scene(root, 800, 550));
        primaryStage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Grad.mc=this;
        Stanovnik.setMc(this);
        Punkt.mc=this;

        Grad.fw=new FileWatcher();
        Grad.fw.setMapaController(this);
        Grad.fw.start();

        Group g=new Group();
        Group buttons=new Group();
        Grad.size = SerijalizabilniGrad.staticSize;
        Grad.ratio= 620/Grad.size;
        Grad.imgRatio=550/Grad.size;

        //setSignal();

        matrica = new Label[Grad.size][Grad.size];

        for (int i=0; i< Grad.size; i++)
        {
            matrica[i] = new Label[Grad.size];

            for(int j=0; j<Grad.size;j++)
            {
                matrica[i][j]= new Label();
                matrica[i][j].setMinWidth(Grad.ratio);
                matrica[i][j].setMinHeight(Grad.ratio);
                matrica[i][j].setMaxWidth(Grad.ratio);
                matrica[i][j].setMaxHeight(Grad.ratio);
                matrica[i][j].setAlignment(Pos.CENTER);
                matrica[i][j].setBorder(new Border(new BorderStroke(Paint.valueOf("WHITE"), BorderStrokeStyle.SOLID, null, BorderStroke.THIN)));

                matrica[i][j].setFont(new Font(10));
                matrica[i][j].setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
                matrica[i][j].relocate(j*(680/Grad.size),i*(680/Grad.size));

                g.getChildren().add(matrica[i][j]);
            }
        }
        mapa.getChildren().add(g);

        File file=new File("."+File.separator+infoDir+File.separator+stanjeUAmbulantamaFilename);
        try(BufferedReader br=new BufferedReader(new FileReader(file)))
        {
            String line;
            while ((line=br.readLine())!=null)
            {
                stanje+=line+"\n";
            }

           stanjeAmbulanti.setText(stanje);
        } catch (Exception e)
        {
           MyLogger.log(Level.WARNING,e.getMessage(),e);
        }
    }

    public void omoguciKretanje()
    {
        if(!alreadyPressed)
        {
            Grad.startThreads();
            start=System.currentTimeMillis();
            alreadyPressed=true;
        }
    }
    public void zavrsiSimulaciju()
    {
        if(alreadyPressed || serialization)
        {
            long end=System.currentTimeMillis();
            File file=new File("SIM-JavaKov-"+System.currentTimeMillis()+".txt");
            String[] kategorije={"zarazeni","oporavljeni","zenski","muski","stari","odrasli","djeca"};
            Integer[] vrijednosti={Ambulanta.zarazeniUkupno,Ambulanta.oporavljeniUkupno,Ambulanta.zenski,Ambulanta.muski,Ambulanta.zarazeniStari
                    ,Ambulanta.zarazeniOdrasli,Ambulanta.zarazeniDjeca};

            try(BufferedWriter bw=new BufferedWriter(new FileWriter(file)))
            {
                bw.write("Trajanje simulacije [mm:ss]: "+(end-start)+"\n");

                bw.write("===Broj kreiranih objekata===\n");
                bw.write("Djeca: "+ Grad.djeca+"\n");
                bw.write("Odrasli: "+Grad.odrasli+"\n");
                bw.write("Stari: "+Grad.stari+"\n");
                bw.write("Kuce: "+Grad.kuceUGradu.size()+"\n");
                bw.write("Punktovi:"+Grad.punktoviUGradu.size()+"\n");
                bw.write("Ambulante: "+Grad.ambulante.size()+"\n");


                bw.write("===Statistički podaci===\n");
                for(int i=0; i<kategorije.length ;i++)
                    bw.write(kategorije[i]+": "+vrijednosti[i]+"\n");

            } catch (Exception e)
            {
                MyLogger.log(Level.WARNING,e.getMessage(),e);
            }

            System.out.println("Simulacija zavrsena.");
            Platform.exit();
            System.exit(0);
        }
    }

    public void zaustaviSimulaciju()
    {
        Grad.running=false;
        try{
            Thread.sleep(1500);
        } catch (Exception e)
        {
            MyLogger.log(Level.WARNING,e.getMessage(),e);
        }
        System.out.println("Svi tredovi zaustavljeni.");

        SerijalizabilniGrad sg= new SerijalizabilniGrad(Grad.kuceUGradu,Grad.alarmi,Grad.ambulante,Grad.punktoviUGradu,Grad.size,Grad.mapa,
                Ambulanta.oporavljeni, Ambulanta.zarazeni, Ambulanta.count, Ambulanta.brojStanovnika, Ambulanta.zarazeniUkupno, Ambulanta.oporavljeniUkupno
                , Ambulanta.zarazeniOdrasli, Ambulanta.zarazeniStari, Ambulanta.zarazeniDjeca, Ambulanta.zenski, Ambulanta.muski, Grad.djeca,
                Grad.odrasli,Grad.stari, signalSet, StanjeAmbulantiController.red, StanjeAmbulantiController.kolona,StanjeAmbulantiController.popunjenoGore,
                StanjeAmbulantiController.popunjenoDole, StanjeAmbulantiController.popunjenoDesno, StanjeAmbulantiController.popunjenoLijevo);

        try(ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(new File(serFilename))))
        {
            oos.writeObject(sg);

        } catch (Exception e)
        {
            MyLogger.log(Level.WARNING,e.getMessage(),e);
        }

        System.out.println("Izvrsena serijalizacija.");

        Platform.exit();
        System.exit(0);
    }
}
