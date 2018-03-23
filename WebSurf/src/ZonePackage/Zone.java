package ZonePackage;

import CountDownDelayPackage.CountDownDelay;
import RatingPackage.RateByStars;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Line;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.*;

import DataFilePackage.DataFile;

public class Zone {

    // flags
    private boolean allowStaySkip = true;
    private boolean allowQuit = true;
    private boolean shuffleVideos = true;

    public void setShuffleVideos(boolean shuffleVideos) {this.shuffleVideos = shuffleVideos; }
    public void setAllowQuit(boolean allowQuit) {
        this.allowQuit = allowQuit;
    }
    public void setAllowStaySkip(boolean allowStaySkip) {
        this.allowStaySkip = allowStaySkip;
    }

    // data file
    enum dataFileFlags {PRACTICE, ENTER, OFFER, STAY, SKIP, QUIT, SHOW, RATE, LEAVE}

    private DataFile dataFile = null;

    // parameters

    private String myName;  public String getName() {return myName;}
    private Image myIcon;   public Image getIcon() {return myIcon;}
    private List<File> myVideos = new ArrayList<File>();
    private int iVideo;
    private int nVideos;     public int getNVideos() {return nVideos;}
    private List<Integer> delays;
    private int iDelay;
    private int nDelays;
    private double maxDelay;

    private Group myRootNode;
    private BorderPane topPanel;
    private CountDownDelay myProgress;
    private MediaView myMediaView;
    private Runnable myNextStep;

    private Button OKbutton;
    private Button stayButton;
    private Button skipButton;
    private Button quitButton;

    private RateByStars myStars;

    // constructor
    public Zone(String fdName, List<Integer> delaysIN) throws FileNotFoundException, java.net.MalformedURLException {
        File sourceDirectory = new File(fdName);
        if (!sourceDirectory.exists()) throw new FileNotFoundException("Source directory not found.");

        // load data
        myName = sourceDirectory.getName();
        File[] files = sourceDirectory.listFiles();
        for (File F : files) {
            if (F.isFile()) {
                String extension = F.getName().substring(F.getName().lastIndexOf('.'));
                switch (extension) {
                    case ".png":
                        myIcon = new Image("file:"+F.getAbsolutePath());
                        break;
                    case ".mp4":
                        myVideos.add(F);
                        break;
                }
            }; // if isfile, then switch
        };
        if (shuffleVideos) Collections.shuffle(myVideos);
        iVideo = 0; nVideos = myVideos.size();
        System.err.println(myName + ": Found " + String.valueOf(nVideos) + " videos.");

        // build delays
        delays = new ArrayList<Integer>(delaysIN);
        Collections.shuffle(delays);
        iDelay = 0;
        nDelays = delays.size();
        maxDelay = Collections.max(delays);


    }

    public Group buildDisplay() {

        Media M = getMedia(0);

        // build scene
        BorderPane layout = new BorderPane();
        layout.getStylesheets().add("WebSurf.css");
        topPanel = new BorderPane();
        layout.setTop(topPanel);
        AnchorPane bottom = new AnchorPane();
        layout.setBottom(bottom);

        Label title = new Label();
        title.setText(myName);
        title.getStyleClass().add("title");
        topPanel.setRight(title);

        ImageView icon = new ImageView();
        icon.setImage(myIcon);
        topPanel.setLeft(icon);

        Line L = new Line(0f,0f,960f,0f);
        L.setStyle("-fx-stroke: black; -fx-stroke-width: 2pt");
        topPanel.setBottom(L);

        myMediaView = new MediaView();
        layout.setCenter(myMediaView);
        myMediaView.setVisible(false);

        // buttons
        HBox buttons = new HBox();
        layout.setBottom(buttons);

        buttons.setPadding(new Insets(15, 12, 15, 12));
        buttons.setSpacing(10);

        OKbutton = new Button("OK");
        OKbutton.setOnAction(e -> {showVideo();});
        OKbutton.setVisible(false); OKbutton.setDisable(true);
        buttons.getChildren().add(OKbutton);

        stayButton = new Button("STAY");
        stayButton.setOnAction(e -> {enterWaitZone();});
        stayButton.setVisible(false); stayButton.setDisable(false);
        buttons.getChildren().add(stayButton);

        quitButton = new Button("QUIT");
        quitButton.setOnAction(e -> {quitZone();});
        quitButton.setVisible(false); quitButton.setDisable(false);
        buttons.getChildren().add(quitButton);

        skipButton = new Button("SKIP");
        skipButton.setVisible(false); skipButton.setDisable(true);
        skipButton.setOnAction(e -> {skipZone();});
        buttons.getChildren().add(skipButton);

        myStars = new RateByStars(5);
        buttons.getChildren().add(myStars.asNode());
        myStars.addRunnable(() -> {ratedVideo();});
        myStars.asNode().setVisible(false);

        myRootNode = new Group(layout);
        return myRootNode;
    }

    public Group asGroup() {
        return myRootNode;
    }

    public void attachDataFile(DataFile df) {
        dataFile = df;
    }

    public void stop() {
        if (myProgress != null) myProgress.pausePlay();
    }

    public void practiceZone(Runnable nextStep) {
        myNextStep = nextStep;
        if (dataFile != null) dataFile.writeFlag(dataFileFlags.PRACTICE.toString(), myName);
        OKbutton.setVisible(true);
        OKbutton.setDisable(false);
    }

    public void enterZone(Runnable nextStep) {
        myNextStep = nextStep;
        if (dataFile != null) dataFile.writeFlag(dataFileFlags.ENTER.toString(),myName);
        myMediaView.setMediaPlayer(null);
        if (iDelay == nDelays) {iDelay=0; Collections.shuffle(delays);};

        topPanel.getChildren().remove(myProgress);
        myProgress = new CountDownDelayPackage.CountDownDelay(maxDelay, () -> showVideo());
        topPanel.setCenter(myProgress.asNode());


        if (dataFile != null) dataFile.writeFlag(dataFileFlags.OFFER.toString(),String.valueOf(delays.get(iDelay).doubleValue()));
        myProgress.play(delays.get(iDelay).doubleValue());
        myProgress.pausePlay();
        iDelay++;
        enterOfferZone();
    }

    private void enterOfferZone() {
        if (allowStaySkip) {
            stayButton.setVisible(true);
            stayButton.setDisable(false);
            skipButton.setVisible(true);
            skipButton.setDisable(false);
        } else {
            enterWaitZone();
        }
    }

    private void enterWaitZone() {
        if (dataFile != null) dataFile.writeFlag(dataFileFlags.STAY.toString(), myName);
        stayButton.setVisible(false); stayButton.setDisable(true);
        skipButton.setVisible(false); skipButton.setDisable(true);
        if (allowQuit) {
            quitButton.setVisible(true); quitButton.setDisable(false);
        }
        myProgress.continuePlay();
    }

    private Media getMedia(int iV) {
        String mediaURL = null;
        try {mediaURL = myVideos.get(iV).toURI().toURL().toString();}
        catch (MalformedURLException e) {System.err.println("Malformed URL -- " + mediaURL); Platform.exit();}
        return new Media(mediaURL);
    }

    private void showVideo() {
        quitButton.setVisible(false); quitButton.setDisable(true);
        OKbutton.setVisible(false); OKbutton.setDisable(true);

        if (iVideo == nVideos) {iVideo = 0; if (shuffleVideos) Collections.shuffle(myVideos);}; // if run through all videos, get a new set
        MediaPlayer MP = new MediaPlayer(getMedia(iVideo));
        iVideo += 1;

        if (dataFile != null) dataFile.writeFlag(dataFileFlags.SHOW.toString(),MP.getMedia().getSource());
        myMediaView.setMediaPlayer(MP);
        myMediaView.setVisible(true);
        MP.play();
        MP.setOnEndOfMedia(() -> {rateVideo();});
    }

    private void quitZone() {
        if (dataFile != null) dataFile.writeFlag(dataFileFlags.QUIT.toString(), myName);
        myProgress.pausePlay();
        myMediaView.setMediaPlayer(null);
        myMediaView.setVisible(false);
        quitButton.setVisible(false); quitButton.setDisable(true);
        myNextStep.run();
    }

    private void skipZone() {
        if (dataFile != null) dataFile.writeFlag(dataFileFlags.SKIP.toString(), myName);
        myMediaView.setMediaPlayer(null);
        myMediaView.setVisible(false);
        stayButton.setVisible(false); stayButton.setDisable(true);
        skipButton.setVisible(false); skipButton.setDisable(true);
        myNextStep.run();
    }

    private void rateVideo() {
        myStars.asNode().setVisible(true);
    }

    private void ratedVideo() {
        if (dataFile != null) dataFile.writeFlag(dataFileFlags.RATE.toString(),String.valueOf(myStars.getRating()));
        leaveZone();
    }

    private void leaveZone() {
        if (dataFile != null) dataFile.writeFlag(dataFileFlags.LEAVE.toString(), myName);
        myStars.asNode().setVisible(false);
        myMediaView.setVisible(false);
        myNextStep.run();
    }
}

