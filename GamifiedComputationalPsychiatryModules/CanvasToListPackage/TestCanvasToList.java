package CanvasToListPackage;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class TestCanvasToList extends Application {

    public static void main(String[] args)
    {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage)throws Exception
    {
        // Create the FXMLLoader
        CanvasToList T = new CanvasToList();

        T.setActionDone(() -> {
            List<String> rankings = T.getRanking();
            int iS = 0;
            for (String S : rankings)
                System.out.println(String.valueOf(++iS) + ": " + S);
            Platform.exit();
            });

        T.addLabel("abc");
        T.addLabel("xyz");

        T.setTitle("Testing");
        T.setLabel("Please rank by clicking the buttons \n in order from most favored to least favored.");

        // Create the Scene
        Scene scene = new Scene(T);
        // Set the Scene to the Stage
        stage.setScene(scene);
        // Set the Title to the Stage
        stage.setTitle("A FXML Example with a Controller");
        // Display the Stage
        stage.show();

    }
}
