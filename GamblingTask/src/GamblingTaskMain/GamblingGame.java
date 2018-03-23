package GamblingTaskMain;

import ClockTimerPackage.ClockTimer;
import DataFilePackage.DataFile;
import DataFilePackage.ParameterSet;
import StringsFromFile.ReadStringsFromFile;
import TextScreenPackage.TextScreen;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class GamblingGame extends Application {

    final private static String version = "1.01_compiled_on_2017-11-03";

    // flags and parameters
    final private static int height = 200;
    final private static int width = 300;

    private static DataFile dataFile = new DataFile();
    private static ParameterSet parms = new ParameterSet();
    private static void fillParms() {
        parms.put("pDist","A:0.75,-1;0.25,+5|B:0.25,-0.5;0.75,0.25", ParameterSet.TYPES.STRING);
        //parms.put("pDist","A:0.75,-1;0.25,+5|B:0.25,-0.5;0.75,0.25|C:0.5,2;0.25,3;0.25,-10", ParameterSet.TYPES.STRING);
        parms.put("nTrials", 5, ParameterSet.TYPES.INT);
    }
    private static String callThus = "GamblingTaskMain.GamblingTask --config=cfgfile --output=datafile";

    // states
    protected enum State {SPLASH, GAME, CLOSING};
    private State curState;
    private Map<State, Group> myScenes;  // these are actually implemented as roots
    private Stage primaryStage;

    // Main
    public static void main (String[] args) {
        launch(args);
    }

    private void gotoNextStep(State nextState) {curState=nextState; gotoNextStep();}
    private void gotoNextStep() {
        primaryStage.getScene().setRoot((Group) myScenes.get(curState));
        primaryStage.show();
    }

    @Override
    public void init() {

        Parameters arguments = getParameters();
        Map<String,String> args = arguments.getNamed();

        dataFile.writeFlag("VERSION", version);
        dataFile.writeDate();
        fillParms();

        for (String k : args.keySet())
            switch(k) {
                case "config":
                    System.err.println("Reading config file: " + args.get(k));
                    parms.readConfig(args.get(k));
                    break;
                case "saveConfig":
                    System.err.println("Saving config file: " + args.get(k));
                    parms.writeConfig(args.get(k));
                    break;
                case "output":
                    System.err.println("Writing data output to: " + args.get(k));
                    dataFile.setFile(args.get(k));
                    break;
            }

        parms.writeToDataFile(dataFile);

    }

    private void buildScreens() {
        myScenes = new HashMap<State,Group>();

        ReadStringsFromFile gamblingGameStrings = new ReadStringsFromFile("GamblingTaskStrings.txt");


        GamblingTask C2S = new GamblingTask();
        C2S.buildButtons(parms.getString("pDist"), dataFile, parms.getInt("nTrials"));
        myScenes.put(State.GAME, new Group (C2S));

        Runnable splashNextStep = () -> {
                    gotoNextStep(State.GAME);
                    C2S.enterGame();
                };
        TextScreen splashScreen = new TextScreen();
        splashScreen.setTitle("Welcome to the Gambling task.");
        splashScreen.setText(gamblingGameStrings.get("splashText"));
        splashScreen.setActionOK(splashNextStep);
        myScenes.put(State.SPLASH, new Group(splashScreen));

       Runnable closingNextStep = () -> {
           dataFile.write("TOTAL_REWARD", String.valueOf(C2S.getRwdWon()));
           dataFile.close();
           Platform.exit();};
       C2S.onDone(closingNextStep);

    }

    @Override
    public void start(Stage theStage) {
        buildScreens();

        primaryStage = theStage;
        primaryStage.setTitle("2-step task");
        Scene myScene = new Scene(new Group());

        primaryStage.setScene(myScene);

        gotoNextStep(State.SPLASH);
    }

}
