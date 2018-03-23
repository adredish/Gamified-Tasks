package GamblingTaskMain;

import DataFilePackage.DataFile;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Flow;

class namedButton extends Button {
    ProbabilitySampler P;
    String name;
}

public class GamblingTask extends BorderPane {

    @FXML private FlowPane buttonFlow;
    @FXML private Button continueButton;
    List<namedButton> myButtons = new ArrayList<namedButton>();

    private double rwdWon;  public double getRwdWon() {return rwdWon;}

    private Random R = new Random();

    private DataFile myDataFile;
    private int maxTrials;
    private int iTrial;

    Runnable whenDone;

    public GamblingTask() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GamblingTask.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

   }

    public void buildButtons(String pDist, DataFile df, int nTrials) {
        continueButton.setOnAction(e -> nextStep());

        myDataFile = df;
        myDataFile.addHash("iTrial");
        myDataFile.addHash("TOTAL_REWARD");
        myDataFile.addHash("CHOOSE");

        maxTrials = nTrials;
        iTrial = 0;

        // pDist is of format B1|B2|...|Bn, where Bi = N:p1,r1;p2,r2;...,pn,rn
        List<String> pDist0 = Arrays.asList(pDist.split("\\|"));
       for (String sB : pDist0) {
            String[] buttonInfo = sB.split(":");
            String buttonName = buttonInfo[0];
           String[] probrwdPairs = buttonInfo[1].split(";");

            namedButton B0 = new namedButton();
            buttonFlow.getChildren().add(B0);
            myButtons.add(B0);

            B0.name = buttonName;
            B0.setText(buttonName);
            B0.P = new ProbabilitySampler(probrwdPairs.length);
            for (int iP = 0; iP<probrwdPairs.length; iP++) {
                String[] pr0 = probrwdPairs[iP].split(",");
                B0.P.setP(iP, Double.valueOf(pr0[0]), Double.valueOf(pr0[1]));
            }
            B0.setOnAction(e -> oneCycle(e));
            B0.setDisable(true);

            myDataFile.addHash(B0.name);
        }
        nextStep();

    }

    public void onDone(Runnable R) {
        whenDone = R;
    }

    public void enterGame() {
        rwdWon = 0;
    }

    private void oneCycle(ActionEvent e) {
        // who got selected?
        namedButton B = (namedButton) e.getSource();

        // roll that die
        double R = B.P.getRoll();
        B.setText(String.valueOf(R));
        B.setStyle("-fx-background-color: magenta; -fx-font-weight: bold");

        // add to reward
        rwdWon += R;
        myDataFile.write("CHOOSE", B.name);

        // reveal everything
        for (namedButton B0 : myButtons) {
            if (B0 != B) {
                String revealedR = String.valueOf(B0.P.getRoll());
                myDataFile.write(B0.name, revealedR);
                B0.setText(revealedR);
                B0.setStyle("-fx-background-color: pink; -fx-font-weight: normal");
            } else {
                myDataFile.write(B0.name, String.valueOf(R));
            }
            B0.setDisable(true);
        }
    }

    private void nextStep() {
        myDataFile.write("iTrial", String.valueOf(iTrial));
       if (++iTrial > maxTrials)
           whenDone.run();

        // reset the buttons
       for (namedButton b : myButtons) {
           b.setText(b.name);
           b.setStyle("-fx-background-color: lightblue; -fx-font-weight: normal");
           b.setDisable(false);
       }
    }

}

class ProbabilitySampler {
    int nP;
    double[] p;
    double[] r;

    Random rand = new Random();

    public ProbabilitySampler(int numP) {
        nP = numP;
        p = new double[nP];
        r = new double[nP];
        for (int iP = 0; iP<nP; iP++) {
            p[iP] = 1.0/nP;
            r[iP] = 0;
        }
    }

    public void setP(int iP, double p0, double r0) {
        p[iP] = p0;
        r[iP] = r0;
    }

    public double getRoll() {
        double r0 = rand.nextDouble();
        for (int iP = 0; iP < nP; iP++)
            if (r0 < p[iP]) {
                return r[iP];
            } else {
                r0 -= p[iP];
            }
        return p[nP-1];
    }
}