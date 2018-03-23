package TravelScreen;

import DataFilePackage.DataFile;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

public class TravelScreenDelay extends TravelScreen {
    private double delay; // seconds
    private CountDownDelayPackage.CountDownDelay progress;

    public TravelScreenDelay(float delay) {this.delay = delay;}

    enum dataFileFlags {TRAVELING}
    public void attachDataFile(DataFile df) {
        dataFile = df;
        for (dataFileFlags dff : dataFileFlags.values()) dataFile.addHash(dff.toString());
    }

    public void start() {
        if (dataFile != null) dataFile.writeFlag(dataFileFlags.TRAVELING.toString(), String.valueOf(delay));
        progress = new CountDownDelayPackage.CountDownDelay(delay, nextStep);
        myCanvas.setCenter(progress.asNode());
        progress.play(delay);
    }

    public void stop() {if (progress != null) progress.pausePlay();}

    public Group buildDisplay(Runnable nextStep) {
        super.buildDisplay(nextStep);

        Label nameLabel = new Label();
        nameLabel.setText("TRAVELING");
        nameLabel.setFont(new Font(40));
        myCanvas.setTop(nameLabel);

        myRootNode = new Group(myCanvas);
        return myRootNode;
    }


}
