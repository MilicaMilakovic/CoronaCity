package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.etfbl.java.Ambulanta;
import net.etfbl.java.Grad;
import net.etfbl.java.MyLogger;
import net.etfbl.java.Polje;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class StanjeAmbulantiController implements Initializable {

    @FXML
    public Button closeButton;
    @FXML
    private Label greska;
    @FXML
    private TextArea stanjeAmbulanti;

    private static int red=0, kolona=1;
    private static boolean popunjenoGore, popunjenoDole, popunjenoDesno, popunjenoLijevo;

    @FXML
    public void handleCloseButtonAction(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void kreirajNovuAmbulantu() throws Exception
    {
        if(!popunjenoGore)
        {
            napraviAmbulantu(0, kolona);
            ispisPoruke("Napravljena nova ambulanta.");
            ++kolona;
            if(kolona==Grad.size-1)
            {
                popunjenoGore=true;
                red=1;
            }
        }
        else if (!popunjenoDesno)
        {
                napraviAmbulantu(red, Grad.size-1);
                ispisPoruke("Napravljena nova ambulanta.");
                ++red;
            if(red==Grad.size-1)
            {
                popunjenoDesno=true;
                kolona=Grad.size-2;
            }
        }
        else if(!popunjenoDole)
        {
            napraviAmbulantu(Grad.size-1, kolona);
            ispisPoruke("Napravljena nova ambulanta.");
            --kolona;

            if(kolona==0)
            {
                popunjenoDole=true;
                red=Grad.size-2;
            }
        }
        else if(!popunjenoLijevo)
        {
            napraviAmbulantu(red, 0);
            ispisPoruke("Napravljena nova ambulanta.");
            --red;
            if(red==0)
            {
                popunjenoLijevo=true;
            }
        }
        else {
           ispisPoruke("Greška! Napravljen maksimalan broj ambulanti!");
        }
    }

    private void ispisPoruke(String s)
    {
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {

                Platform.runLater( () -> {
                    if(s.equals("Napravljena nova ambulanta."))
                        greska.setTextFill(Color.web("#108e59"));
                    else
                        greska.setTextFill(Color.web("#f32f4b"));

                    greska.setText(s);
                });

                try {
                    Thread.sleep(1300);
                } catch (Exception e)
                {
                    MyLogger.log(Level.WARNING,e.getMessage(),e);
                }

                Platform.runLater( () -> greska.setText(" "));
            }
        });
        t.start();
    }
    private void napraviAmbulantu(int red, int kolona) throws Exception
    {
        Ambulanta a=new Ambulanta(red,kolona);
        Grad.mapa[red][kolona]=new Polje(null, null, a);
        Grad.ambulante.add(a);
        a.start();
        Platform.runLater( () -> {
            stanjeAmbulanti.appendText(a.toString()+" | Kapacitet: "+ a.getKapacitet() + " | Popunjeno: " + a.getTrenutnoStanje()+"\n");
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        for (Ambulanta a : Grad.ambulante)
        {
            Platform.runLater( () -> {
                stanjeAmbulanti.appendText(a.toString()+" | Kapacitet: "+ a.getKapacitet() + " | Popunjeno: " + a.getTrenutnoStanje()+"\n");
            });
        }
    }
}
