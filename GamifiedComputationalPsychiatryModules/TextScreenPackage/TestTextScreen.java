package TextScreenPackage;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestTextScreen extends Application {

    public static void main(String[] args)
    {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage)throws Exception
    {
        // Create the FXMLLoader
        TextScreen T = new TextScreen();
        T.setTitle("a title.");
        T.setText("State 1. \n State 2. \n State 3.\n");
        T.setActionOK(() -> System.out.println("OK."));
        T.setActionQuit(() -> Platform.exit());

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