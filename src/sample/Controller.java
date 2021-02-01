package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

import javafx.stage.Stage;
import net.etfbl.java.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import java.util.logging.Level;

public class Controller {

    public static Grad grad;
    @FXML
    private TextField djecaField;
    @FXML
    private TextField odrasliField;
    @FXML
    private TextField stariField;
    @FXML
    private TextField kuceField;
    @FXML
    private TextField punktoviField;
    @FXML
    private TextField ambulanteField;
    @FXML
    protected Button start=new Button();

    private int slucaj=4;

    public void dugmeOnAction() throws Exception
    {
        int djeca, odrasli, stari, kuce, punktovi, ambulante;
        try {
            Stage primaryStage= new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("MapaGrada.fxml"));

            djeca = Integer.parseInt(djecaField.getText());
            odrasli=Integer.parseInt(odrasliField.getText());
            stari=Integer.parseInt(stariField.getText());
            kuce=Integer.parseInt(kuceField.getText());
            punktovi=Integer.parseInt(punktoviField.getText());
            ambulante=Integer.parseInt(ambulanteField.getText());

            if(djeca<0 || odrasli<0 || stari<0 || punktovi<0 || ambulante<0 || kuce<0)
            {
                slucaj=2;
                throw new Exception();
            }
            else if(kuce==0)
            {
                slucaj=1;
                throw new Exception();
            }
            else if(kuce>20)
            {
                slucaj=3;
                throw new Exception();
            }
            else if(odrasli+stari < djeca)
            {
                slucaj=0;
                throw new Exception();
            }

            MapaController.inicijalizujGrad(djeca,odrasli,stari,kuce,punktovi);
            MapaController.brojAmbulantnihVozila=ambulante;

            primaryStage.setTitle("CoronaCity");
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream( "."+File.separator+MapaController.resourcesDir+File.separator+MapaController.iconFilename)));
            primaryStage.setScene(new Scene(root, 1000, 700));
            primaryStage.show();

            Stage stage = (Stage) start.getScene().getWindow();
            stage.close();

        } catch (Exception e)
        {
            switch (slucaj)
            {
                case 0:
                {
                    ErrorController.s="Djeca ne smiju biti sama u kući! Broj starih/odraslih mora da bude veći.";
                    break;
                }
                case 1:
                {
                    ErrorController.s="Broj kuća mora da bude veći od 0!";
                    break;
                }
                case 2:
                {
                    ErrorController.s="Sve unesene vrijednosti moraju da budu pozitivne.";
                    break;
                }
                case 3:
                {
                    ErrorController.s="Maksimalan broj kuća je 20.";
                    break;
                }
                default:
                {
                    ErrorController.s="Popunite sva polja brojčanim vrijednostima.";
                    break;
                }
            }

            Stage ps= new Stage();
            Parent root1 = FXMLLoader.load(getClass().getResource("Error.fxml"));
            ps.setTitle("CoronaCity");
            ps.getIcons().add(new Image(getClass().getResourceAsStream( "."+File.separator+MapaController.resourcesDir+File.separator+MapaController.iconFilename )));
            ps.setScene(new Scene(root1, 400, 400));
            ps.show();

            MyLogger.log(Level.WARNING,"Neispravan unos",e);
        }
    }
    public void ponovnoPokretanje() throws IOException {
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("grad.ser"))))
        {
            SerijalizabilniGrad sg=(SerijalizabilniGrad)ois.readObject();
//            Grad.size=sg.size;
            SerijalizabilniGrad.staticSize = sg.size;
            MapaController.brojAmbulantnihVozila=sg.ambulante.size();
            Stage primaryStage= new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("MapaGrada.fxml"));

            primaryStage.setTitle("CoronaCity");
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream( "."+File.separator+MapaController.resourcesDir+File.separator+MapaController.iconFilename)));
            primaryStage.setScene(new Scene(root, 1000, 700));
            primaryStage.show();

            MapaController.serialization = true;
            System.out.println("Izvrsena deserijalizacija.");

            MapaController.ucitajGrad(sg);

            Stage stage = (Stage) start.getScene().getWindow();
            stage.close();

        } catch (Exception e)
        {
            ErrorController.s = "Ne postoji sačuvana simulacija.";
            Stage ps= new Stage();
            Parent root1 = FXMLLoader.load(getClass().getResource("Error.fxml"));
            ps.setTitle("CoronaCity");
            ps.getIcons().add(new Image(getClass().getResourceAsStream( "."+File.separator+MapaController.resourcesDir+File.separator+MapaController.iconFilename )));
            ps.setScene(new Scene(root1, 400, 400));
            ps.show();

            MyLogger.log(Level.WARNING,"Greska pri deserijalizaciji.", e);
        }
    }
}
