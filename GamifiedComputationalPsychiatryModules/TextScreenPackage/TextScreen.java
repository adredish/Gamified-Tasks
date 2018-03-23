package TextScreenPackage;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class TextScreen extends BorderPane {
    @FXML private Label titleText;
    @FXML private TextArea labelText;
    @FXML private ImageView iconImage;
    @FXML private Button okButton;
    @FXML private Button quitButton;


    public TextScreen() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TextScreen.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        setActionQuit(Platform::exit);
    }

    @FXML public void setTitle(String newText) { titleText.setText(newText); }
    @FXML public void setText(String newText) { labelText.setText(newText); }
    @FXML public void setIcon(Image I) {iconImage.setImage(I); }
    @FXML public void setActionOK(Runnable A) {okButton.setOnAction(e -> A.run());}
    @FXML public void setActionQuit(Runnable A) {quitButton.setOnAction(e -> A.run());}

}
