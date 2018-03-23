package ClockTimerPackage;

/* CLOCK TIMER
implements a clock countdown timer that returns after a set delay
If showClock flag is true then displays (in a new window) a clock
with correct time left.
 */

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ClockTimer {

    // flags
    private boolean flagShowClock;

    // parameters
    private static int r = 100;
    private Timeline myTimer;
    private Stage clockStage;

    // constructor

    public ClockTimer(Duration timeToRun, Runnable onFinished) throws RuntimeException {
        this(Duration.seconds(3600), timeToRun, onFinished);
    }

    public ClockTimer(Duration timeToShow, Duration timeToRun, Runnable onFinished) throws RuntimeException {

        if (timeToRun.toSeconds() > 60 * 60) {
            throw new RuntimeException("Clock timer cannot handle " + String.valueOf(timeToRun) + ". Max is one hour.");
        }

        clockStage = new Stage();
        clockStage.setTitle("Time left");

        StackPane layout = new StackPane();
        layout.setPrefSize(2*r, 2*r);

        Circle face = new Circle(r,r,r);
        face.setFill(Paint.valueOf("wheat"));
        layout.getChildren().add(face);

        Line endTo = new Line(0,r/2,0,-r/2);
        endTo.setTranslateY(-r/2);
        endTo.setStyle("-fx-stroke-width: 3; -fx-stroke: red");
        layout.getChildren().add(endTo);

        Line timeLeft = new Line(0,r/2,0, -r/2);
        timeLeft.setStyle("-fx-stroke-width: 3;  -fx-stroke: blue");
        timeLeft.setTranslateY(-r/2);
        layout.getChildren().add(timeLeft);

        Rotate timeLeftRotate = new Rotate();
        timeLeftRotate.setPivotY(r/2); timeLeftRotate.setPivotX(0);
        timeLeftRotate.setAngle(360f*timeToRun.toSeconds()/timeToShow.toSeconds());
        timeLeft.getTransforms().add(timeLeftRotate);

        for (int iL = 1; iL<13; iL++) {
            Label K = new Label();
            K.setText(String.valueOf(iL));
            K.setStyle("-fx-font-family: sans-serif; -fx-font-size: 12pt");
            K.setTranslateX(0.8*r*Math.cos(Math.toRadians(iL*30-90)));
            K.setTranslateY(0.8*r*Math.sin(Math.toRadians(iL*30-90)));
            layout.getChildren().add(K);
        }
        myTimer = new Timeline();
        myTimer.setAutoReverse(false);
        myTimer.setCycleCount(1);

        KeyValue timeLeftProgressValue = new KeyValue(timeLeftRotate.angleProperty(), 0);
        KeyFrame timeLeftProgressFrame = new KeyFrame(timeToRun, timeLeftProgressValue);

        myTimer.getKeyFrames().addAll(timeLeftProgressFrame);
        myTimer.setOnFinished(e -> onFinished.run());
        myTimer.jumpTo(timeToRun.divide(timeToShow.toSeconds()));

        clockStage.setScene(new Scene(layout, 2*r, 2*r));

        if (flagShowClock) {showClock();} else {hideClock();}

    }

    // start and stop Clock
    public void startClock() {myTimer.play();}
    public void stopClock() {myTimer.pause();}

    // show and hide Clock
    public void showClock() {flagShowClock = true; clockStage.show();};
    public void hideClock() {flagShowClock = false; clockStage.hide();}
}
