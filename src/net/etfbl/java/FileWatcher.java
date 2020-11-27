package net.etfbl.java;
import javafx.application.Platform;
import sample.MapaController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.*;
import java.util.logging.Level;

public class FileWatcher extends Thread {

    private Path path=Paths.get("C:\\Users\\Milica\\Desktop\\CoronaCity\\info");
    private WatchService watchService;
    private MapaController mc;

    public FileWatcher()
    {
        try {
        this.watchService = FileSystems.getDefault().newWatchService();
        path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMapaController(MapaController mc) { this.mc=mc; }

    WatchKey key;
    public void run() {

        while (true)
        {
            try{
                while ((key = watchService.take()) != null)
                {
                    for (WatchEvent<?> event : key.pollEvents())
                    {
                        Path changed = (Path) event.context();

                       try(BufferedReader br=new BufferedReader(new FileReader(new File(".\\info"+File.separator+changed.toString()))))
                       {
                           String line;
                           String stanje="";
                           while ((line=br.readLine()) != null)
                           {
                               stanje+=line+"\n";
                           }
                           mc.setStanjeAmbulanti(stanje);
                       } catch (Exception e)
                       {
                           e.printStackTrace();
                       }

                    }
                    key.reset();
                }

            }catch (Exception e)
            {
                e.printStackTrace();
//                MyLogger.log(Level.WARNING,e.getMessage(),e);
            }
            try{
                sleep(400);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }
}
