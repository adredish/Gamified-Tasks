package TravelScreen;

import DataFilePackage.DataFile;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.util.Random;

public class TravelScreenClicks extends TravelScreen {

    private int stepsToTakeEachCycle;
    private int stepsThisCycle;

    private Pane subCanvas;

    enum dataFileFlags {TRAVELING, TRAVELCLICK}
    public void attachDataFile(DataFile df) {
        dataFile = df;
        for (dataFileFlags dff : dataFileFlags.values()) dataFile.addHash(dff.toString());
    }

    public TravelScreenClicks(int nSteps) {
        stepsToTakeEachCycle = nSteps;
    }

    public void start() {
        if (dataFile != null) dataFile.writeFlag(dataFileFlags.TRAVELING.toString(), String.valueOf(stepsThisCycle));
        stepsThisCycle=0;
        oneStep();
    }

    public Group buildDisplay(Runnable nextStep) {

        myRootNode = super.buildDisplay(nextStep);
        subCanvas = new Pane();
        myCanvas.setCenter(subCanvas);
        subCanvas.setStyle("-fx-border-width: 2%; -fx-border-color: black");
        return myRootNode;
    }

    private void oneStep() {
        if (stepsThisCycle < stepsToTakeEachCycle) {
            stepsThisCycle++;
            Random R = new Random(); R.nextDouble();
            Button B = new Button(String.valueOf(stepsThisCycle));
            B.setFocusTraversable(false);
            B.setLayoutX(R.nextDouble() * (subCanvas.getWidth()));
            B.setLayoutY(R.nextDouble() * (subCanvas.getHeight()));
            B.setOnAction(e -> {
                if (dataFile != null) dataFile.writeFlag(dataFileFlags.TRAVELCLICK.toString(), String.valueOf(stepsThisCycle));
                subCanvas.getChildren().remove(B); oneStep();});
            subCanvas.getChildren().add(B);
        }
        else {
            nextStep.run();
        }

    }
}
