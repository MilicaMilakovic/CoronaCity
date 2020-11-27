package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import net.etfbl.java.Ambulanta;
import net.etfbl.java.MyLogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class StatistikaController implements Initializable {

    @FXML
    public Button closeButton;
    @FXML
    private Label ukupnoZarazeno;
    @FXML
    private Label ukupnoOzdravilo;
    @FXML
    private PieChart poPolu;
    @FXML
    private PieChart poStarosnimGrupama;
    @FXML
    private Label izvjestajPreuzet;
    public static int id;

    @FXML
    public void handleCloseButtonAction(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ukupnoZarazeno.setText("• Ukupno zaraženih: "+Ambulanta.zarazeniUkupno);
        ukupnoOzdravilo.setText("• Ukupno oporavljenih: "+Ambulanta.oporavljeniUkupno);


        ObservableList<PieChart.Data> zarazeniPoPolu = FXCollections.observableArrayList(
                new PieChart.Data("Ženski", Ambulanta.zenski),
                new PieChart.Data("Muški", Ambulanta.muski));
        poPolu.setData(zarazeniPoPolu);

        ObservableList<PieChart.Data> zarazeniPoGodinama = FXCollections.observableArrayList(
                new PieChart.Data("Djeca", Ambulanta.zarazeniDjeca),
                new PieChart.Data("Odrasli", Ambulanta.zarazeniOdrasli),
                new PieChart.Data("Stari", Ambulanta.zarazeniStari));
        poStarosnimGrupama.setData(zarazeniPoGodinama);

    }

    public void preuzmiIzvjestaj()
    {
       File file=new File("izvjestaj"+id+".csv");
        ++id;
        String[] kategorije={"zarazeni","oporavljeni","zenski","muski","stari","odrasli","djeca"};
        Integer[] vrijednosti={Ambulanta.zarazeniUkupno,Ambulanta.oporavljeniUkupno,Ambulanta.zenski,Ambulanta.muski,Ambulanta.zarazeniStari
                                ,Ambulanta.zarazeniOdrasli,Ambulanta.zarazeniDjeca};
        try(PrintWriter bw=new PrintWriter(new FileWriter(file)))
        {
            for(int i=0; i<kategorije.length ; i++)
                bw.write(kategorije[i]+","+vrijednosti[i]+"\n");

        } catch (Exception e)
        {
            MyLogger.log(Level.WARNING,e.getMessage(),e);
            e.printStackTrace();
        }

        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                Platform.runLater( () -> izvjestajPreuzet.setText("Uspješno ste preuzeli izvještaj!"));
                try {
                    Thread.sleep(1300);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                Platform.runLater( () -> izvjestajPreuzet.setText(" "));
            }
        });
        t.start();

    }
}
