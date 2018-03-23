package TravelScreen;

import DataFilePackage.DataFile;
import javafx.scene.Group;
import javafx.scene.layout.*;

public abstract class TravelScreen {

    protected Group myRootNode;
    protected BorderPane myCanvas;
    protected Runnable nextStep;

    public abstract void start();

    public Group buildDisplay(Runnable nextStep) {

        myCanvas = new BorderPane();
        myCanvas.setMinSize(500,500);

        this.nextStep = nextStep;

        myRootNode = new Group(myCanvas);
        return myRootNode;
    }

    public Group asGroup() {
        return myRootNode;
    }

    public void stop() {};

    //data files
    DataFile dataFile = null;
    enum dataFileFlags {TRAVELING};
    public void attachDataFile(DataFile df) {
        dataFile = df;
        for (dataFileFlags dff : dataFileFlags.values()) dataFile.addHash(dff.toString());
    }

}

