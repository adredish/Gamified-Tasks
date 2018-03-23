package ZonePackage;

import DataFilePackage.DataFile;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ZoneList implements Iterable<Zone> {
    // parameters
    private List<Zone> zones = new ArrayList<Zone>();
    private int iZone;
    private int nZones;

    public ZoneList(String fdName, List<Integer> delays) throws FileNotFoundException, java.net.MalformedURLException {
        File sourceDirectory = new File(fdName);
        if (!sourceDirectory.exists()) throw new FileNotFoundException("Source directory not found.");

        File[] zoneDirs = sourceDirectory.listFiles();
        for (File zD : zoneDirs) {
            if (zD.isDirectory()) {
                Zone newZone = new Zone(zD.getPath(), delays);
                if (newZone.getNVideos() == 0) {
                    throw new FileNotFoundException(zD.getName() + " found no videos.");
                }
                ;
                zones.add(newZone);
            }
        }

        iZone = 0;
        nZones = zones.size();
        System.err.println("Running game with " + String.valueOf(nZones) + " zones");
    }

    public Zone getNextZone() {
        iZone++;
        if (iZone < nZones) {
            return zones.get(iZone);
        } else {
            return zones.get(iZone = 0);
        }
    }

    public void shuffleZones() {
        Collections.shuffle(zones);
    }

    public int getnZones() {return nZones;};

    public List<String> getNames() {
        List<String> nameSet = new ArrayList<String>();
        for (Zone Z : zones) nameSet.add(Z.getName());
        return nameSet;
    }

    @Override
    public Iterator<Zone> iterator() {
        return zones.iterator();
    }

    public void attachDataFile(DataFile df) {
        for (Zone Z : zones) Z.attachDataFile(df);
        for (Zone.dataFileFlags dff : Zone.dataFileFlags.values()) {
            df.addHash(dff.toString());
        }
    }

    public void showMap() {
        final double r = 200;
        Stage mapStage = new Stage();
        mapStage.setTitle("Gallery sequence");
        Pane layout = new Pane();
        layout.setPrefSize(2 * r, 2 * r);

        int iZ = 0;
        for (Zone z : zones)  {
            ImageView icon = new ImageView(z.getIcon());
            icon.setTranslateX(r-0.5*icon.getImage().getWidth() + 0.8*r*Math.cos(2.0 * Math.PI * iZ/nZones));
            icon.setTranslateY(r-0.5*icon.getImage().getHeight()+ 0.8*r*Math.sin(2.0 * Math.PI * iZ/nZones));
            layout.getChildren().add(icon);
            iZ++;
        }

        Circle path = new Circle(r, r, 0.8*r);
        path.setFill(null);
        path.setStyle("-fx-stroke-width: 25%; -fx-stroke: black");
        layout.getChildren().add(path);

        ImageView clockwise = new ImageView(new Image("clockwise.png"));
        clockwise.setTranslateX(r - 0.5*clockwise.getImage().getWidth());
        clockwise.setTranslateY(r- 0.5*clockwise.getImage().getHeight());
        layout.getChildren().add(clockwise);

        mapStage.setScene(new Scene(new Group(layout)));
        mapStage.show();
    }
}
