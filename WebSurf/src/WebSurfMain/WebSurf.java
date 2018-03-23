package WebSurfMain;

import CanvasToListPackage.CanvasToList;
import DataFilePackage.DataFile;
import DataFilePackage.ParameterSet;
import StringsFromFile.ReadStringsFromFile;
import TextScreenPackage.TextScreen;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import ClockTimerPackage.ClockTimer;

import ZonePackage.Zone;
import TravelScreen.TravelScreen;
import TravelScreen.TravelScreenClicks;
import TravelScreen.TravelScreenDelay;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WebSurf extends Application {
    final private static String version = "1.01_compiled_on_2017-11-03";

    // flags and parameters
    final private static int height = 750;
    final private static int width = 960;

    private static DataFile dataFile = new DataFile();
    private static ParameterSet parms = new ParameterSet();
    private static void fillParms() {
        parms.put("allowStaySkip",true, ParameterSet.TYPES.BOOL);
        parms.put("allowQuit", true, ParameterSet.TYPES.BOOL);
        parms.put("shuffleVideos", true, ParameterSet.TYPES.BOOL);
        parms.put("shuffleZones", true, ParameterSet.TYPES.BOOL);
        parms.put("videosToPractice", 2, ParameterSet.TYPES.INT);
        parms.put("showMap", true, ParameterSet.TYPES.BOOL);
        parms.put("flagTravelButton", false, ParameterSet.TYPES.BOOL);
        parms.put("stepsToTakeTraveling", 1, ParameterSet.TYPES.INT);
        parms.put("minDelay", 2, ParameterSet.TYPES.INT); // seconds
        parms.put("maxDelay", 5, ParameterSet.TYPES.INT);
        parms.put("taskDuration", 1.0, ParameterSet.TYPES.DOUBLE); // minutes
    }
    private static String callThus = "WebSurfMain.WebSurf --config=cfgfile --output=datafile";

    // states
    protected enum State {SPLASH, INTRO_PRACTICE, PRACTICE, INTRO_MAIN,TRAVELING, inZONE, RANKING, CLOSING};

    private Map<State, Group> myScenes;  // these are actually implemented as roots
    private State curState;
    private int practiceCount;

    private Stage primaryStage;

    // components
    private ZonePackage.ZoneList myZones;
    private TravelScreen myTravel;

    // Main
    public static void main (String[] args) {
        launch(args);
    }

    // state transition functions
    private void gotoNextStep(State nextState) {curState=nextState; gotoNextStep();}
    private void gotoNextStep() {
        switch (curState) {
            case PRACTICE:
                if (practiceCount>0) {
                    Zone Z = myZones.getNextZone();
                    primaryStage.getScene().setRoot(Z.asGroup());
                    Z.practiceZone(this::gotoNextStep);
                    practiceCount--;
                } else
                    gotoNextStep(State.INTRO_MAIN);
                break;
            case inZONE:
                curState = State.TRAVELING;
                Zone Z =  myZones.getNextZone();
                primaryStage.getScene().setRoot(Z.asGroup());
                Z.enterZone(() -> {gotoNextStep(State.TRAVELING);});
                break;
            case TRAVELING:
                myTravel.start();
                primaryStage.getScene().setRoot(myTravel.asGroup());
                break;
            default:
                primaryStage.getScene().setRoot((Group) myScenes.get(curState));
                break;
        }
        primaryStage.show();
    }

    // initialization functions
    @Override
    public void init() {
        Parameters arguments = getParameters();
        Map<String,String> args = arguments.getNamed();

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

        dataFile.writeFlag("VERSION", version);
        dataFile.writeDate();

        parms.writeToDataFile(dataFile);
    }


    private void gotoRankingScreen() {
        for (Zone z : myZones) z.stop();
        myTravel.stop();
        dataFile.addHash("DONE"); dataFile.writeFlag("DONE","Clock");
        curState = State.RANKING;
        primaryStage.getScene().setRoot((Group) myScenes.get(State.RANKING));
    }

    private void gotoClosingScreen(CanvasToList CTL) {
        List<String> ranks = CTL.getRanking();
        dataFile.addHash("RANK");
        int iR = 0; for (String R : ranks)
            dataFile.writeFlag("RANK", String.valueOf(++iR)+":"+R);
        primaryStage.getScene().setRoot((Group) myScenes.get(State.CLOSING));
    }

    private void buildScreens() {
        myScenes = new HashMap<State,Group>();

        // Clock
        ClockTimer clock = new ClockTimer(Duration.seconds(60),
                Duration.minutes(parms.getDouble("taskDuration")), this::gotoRankingScreen);

        // travel
        if (parms.getBool("flagTravelButton")) {
            myTravel = new TravelScreenClicks(parms.getInt("stepsToTakeTraveling"));
        } else {
            myTravel = new TravelScreenDelay(parms.getInt("stepsToTakeTraveling"));
        };
        myTravel.buildDisplay(() -> {
            curState = State.inZONE; gotoNextStep();});
        myTravel.attachDataFile(dataFile);

        //ZonePackage.ZonePackage
        try {
            myZones = new ZonePackage.ZoneList("WebSurfVideos",
                    IntStream.range(parms.getInt("minDelay"), parms.getInt("maxDelay")).boxed().collect(Collectors.toList()));
            for (Zone z0 : myZones) {
                z0.buildDisplay();
                z0.setAllowQuit(parms.getBool("allowQuit"));
                z0.setAllowStaySkip(parms.getBool("allowStaySkip"));
                z0.setShuffleVideos(parms.getBool("shuffleVideos"));
            }
            myZones.attachDataFile(dataFile);
        } catch (FileNotFoundException | java.net.MalformedURLException e) {
            System.err.println("File not found for zone:" + e.getMessage());
            Platform.exit();
        }
        if (parms.getBool("shuffleZones")) myZones.shuffleZones();

        for (String n : myZones.getNames())
            dataFile.write("ZONE_ORDER", n);

        ReadStringsFromFile webSurfStrings = new ReadStringsFromFile("WebSurfStrings.txt");

        // other screens
        Runnable splashNextStep = () -> {
            if (parms.getBool("showMap")) myZones.showMap(); clock.showClock(); gotoNextStep(State.INTRO_PRACTICE);};
        TextScreen splashScreen = new TextScreen();
        splashScreen.setTitle("Welcome to WebSurfMain.WebSurf.");
        splashScreen.setText(webSurfStrings.get("splashText"));
        splashScreen.setIcon(new Image("file:///C:/Users/adredish/IdeaProjects/WebSurfMain.WebSurf/src/WebSurfMain.WebSurf.png"));
        splashScreen.setActionOK(splashNextStep);
        myScenes.put(State.SPLASH, new Group(splashScreen));

        Runnable introPracticeNextStep = () -> {gotoNextStep(State.PRACTICE);};
        TextScreen instructionsPractice = new TextScreen();
        instructionsPractice.setTitle("Welcome to WebSurfMain.WebSurf.");
        instructionsPractice.setText(webSurfStrings.get("practiceInstructionsText"));
        instructionsPractice.setActionOK(introPracticeNextStep);
        myScenes.put(State.INTRO_PRACTICE, new Group(instructionsPractice));

        Runnable introNextStep = () -> {clock.startClock(); gotoNextStep(State.inZONE);};
        TextScreen instructions = new TextScreen();
        instructions.setTitle("Welcome to WebSurfMain.WebSurf.");
        instructions.setText(webSurfStrings.get("instructionsText"));
        instructions.setActionOK(introNextStep);
        myScenes.put(State.INTRO_MAIN, new Group(instructions));

        CanvasToList CTL = new CanvasToList();
        for (Zone Z : myZones) CTL.addLabel(Z.getName());
        Runnable rankingNextStep = () -> {gotoClosingScreen(CTL);};
        CTL.setTitle("Ranking the galleries");
        CTL.setLabel(webSurfStrings.get("rankingText"));
        CTL.setActionDone(rankingNextStep);
        myScenes.put(State.RANKING, new Group(CTL));

        Runnable closingNextStep = Platform::exit;
        TextScreen closingScreen = new TextScreen();
        closingScreen.setTitle("WebSurfMain.WebSurf");
        closingScreen.setText(webSurfStrings.get("closingText"));
        closingScreen.setIcon(new Image("WebSurf.png"));
        closingScreen.setActionOK(closingNextStep);
        myScenes.put(State.CLOSING, new Group(closingScreen));
        System.err.println("Base scenes built.");

        practiceCount = parms.getInt("videosToPractice") * myZones.getnZones();
    }

    @Override
    public void start(Stage theStage) {

        buildScreens();

        curState = State.SPLASH;
        primaryStage = theStage;
        primaryStage.setTitle("Web Surf");
        primaryStage.getIcons().add(new Image("WebSurf.png"));
        Scene myScene = new Scene(new Group(), width, height);

        primaryStage.setScene(myScene);

        gotoNextStep();
    }
}
