package CountDownDelayPackage;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Duration;

import javafx.event.EventHandler;

public class CountDownDelay {

    GridPane myCanvas;
    private Timeline timer;

    private double maxDelay;

    public CountDownDelay(double maxDelay, Runnable onFinished) {
        ProgressBar progress = new ProgressBar(1);
        Label progressLabel = new Label();
        IntegerProperty timeLeft = new SimpleIntegerProperty((int) Math.ceil(maxDelay));

        myCanvas = new GridPane();
        myCanvas.setAlignment(Pos.CENTER);
        myCanvas.add(progress, 0,2);

        progressLabel.textProperty().bind(timeLeft.asString());
        progressLabel.setFont(new Font(40));
        progressLabel.setAlignment(Pos.CENTER);
        myCanvas.add(progressLabel, 0, 1);

        timer = new Timeline();
        timer.setAutoReverse(false);
        timer.setCycleCount(1);

        KeyValue timeLeftProgressValue = new KeyValue(progress.progressProperty(), 0);
        KeyFrame timeLeftProgressFrame = new KeyFrame(Duration.seconds(maxDelay), timeLeftProgressValue);
        KeyValue timeLeftLabelValue = new KeyValue(timeLeft, 0);
        KeyFrame timeLeftLabelFrame = new KeyFrame(Duration.seconds(maxDelay), timeLeftLabelValue);
        timer.getKeyFrames().addAll(timeLeftProgressFrame, timeLeftLabelFrame);
        timer.setOnFinished(e -> onFinished.run());
        this.maxDelay = maxDelay;
    }

    public Node asNode() {return myCanvas;}

    public void play() {play(maxDelay);}

    public void play(double delay) {
        if (delay <= maxDelay) {
            timer.playFrom(Duration.seconds(maxDelay - delay));
        } else {
            System.out.println("not ok");
            Alert warning = new Alert(Alert.AlertType.WARNING);
            warning.setTitle("Error");
            warning.setHeaderText("CountDownTimer");
            warning.setContentText("Cannot play " + String.valueOf(delay) + " seconds.");
            warning.showAndWait();
            timer.jumpTo(Duration.seconds(0));
            timer.play();
        };
    }

    public void pausePlay() {
        timer.pause();
    }
    public void continuePlay() {
        timer.play();
    }
}
