package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.etfbl.java.FileWatcher;
import net.etfbl.java.MyLogger;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("VirusCenter.fxml"));
        primaryStage.setTitle("CoronaCity");
        primaryStage.setScene(new Scene(root, 900, 700));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream( "."+ File.separator+MapaController.resourcesDir+File.separator+MapaController.iconFilename)));

        MyLogger.setup();

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
