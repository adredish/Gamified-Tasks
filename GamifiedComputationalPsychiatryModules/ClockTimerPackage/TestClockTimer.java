package ClockTimerPackage;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TestClockTimer extends Application {
    public static void main (String[] args) {
        launch(args);
    }
    @Override
    public void start (Stage S) {
        System.err.println("at 1");
        S.setTitle("Hello World!");
        StackPane root = new StackPane();
        S.setScene(new Scene(root, 50, 50));
        System.err.println("At 2");
        S.show();
        ClockTimer CT = new ClockTimer(Duration.millis(12000), Duration.millis(5000), Platform::exit);
        CT.showClock();
        System.err.println("At 3");


        Button OKbutton = new Button();
        OKbutton.setText("GO");
        OKbutton.setOnAction(e -> {CT.startClock();});
        root.getChildren().add(OKbutton);

    }
}

