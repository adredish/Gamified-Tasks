package RatingPackage;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import static javafx.application.Application.launch;

public class TestRating extends Application {
    public static void main (String[] args) {
        launch(args);
    }

    @Override
    public void start (Stage S) {
        S.setTitle("Hello World!");
        FlowPane root = new FlowPane();
        S.setScene(new Scene(root, 32*5, 50));

        RateByStars stars = new RateByStars(5);
        stars.addRunnable(() -> {
            System.out.println("You gave this " + String.valueOf(stars.getRating()) + " stars.");
            Platform.exit();});
        root.getChildren().add(stars.asNode());

        S.show();

    }
}
