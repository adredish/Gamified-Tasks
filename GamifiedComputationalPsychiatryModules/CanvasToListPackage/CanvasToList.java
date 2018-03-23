package CanvasToListPackage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CanvasToList extends BorderPane {
    @FXML private Label titleText;
    @FXML private Label instructionsLabel;
    @FXML private Pane myCanvas;

    @FXML private Button doneButton;

    @FXML private List<Button> myButtons = new ArrayList<Button>();
    @FXML private ListView<String> myListView;

    private ObservableList<String> listedStrings = FXCollections.observableArrayList();

    public CanvasToList() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CanvasToList.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        listedStrings.clear();
        doneButton.setDisable(true);
    }

    @FXML public void addLabel(String S) {
        Random R = new Random(); R.nextDouble();
        Button B = new Button(S);
        myButtons.add(B);
        B.setDisable(false);
        B.getStyleClass().add("optionButton");
        B.setTranslateX(R.nextDouble() * 400 - 0.5*B.getWidth());
        B.setTranslateY(R.nextDouble() * 300 - 0.5*B.getHeight());
        B.setOnAction(e -> {moveToList(B);});
        myCanvas.getChildren().add(B);
    }

    @FXML public void moveToList(Button B) {
        listedStrings.add(B.getText());
        myListView.setItems(listedStrings);
        B.setDisable(true);

        if (listedStrings.size() == myButtons.size())
            doneButton.setDisable(false);
    }

    @FXML public void clearList() {
        listedStrings.clear();
        myListView.setItems(listedStrings);
        for (Button B : myButtons) B.setDisable(false);
        doneButton.setDisable(true);
    }

    @FXML public void redistributeButtons() {
        Random R = new Random(); R.nextDouble();
        for (Button B : myButtons) {
            B.setTranslateX(R.nextDouble() * 200);
            B.setTranslateY(R.nextDouble() * 300);
        }
    }
    @FXML public void setTitle(String newText) { titleText.setText(newText); }
    @FXML public void setLabel(String newText) { instructionsLabel.setText(newText);}
    @FXML public void setActionDone(Runnable A) {doneButton.setOnAction(e -> A.run());}

    public List<String> getRanking() {return listedStrings;};

}
