package DataFilePackage;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

public class TestDataFile extends Application {
    public static void main (String[] args) {
        launch(args);
    }

    enum FLAGS {APPLES, ORANGES, CHERRIES, BANANAS}
    @Override
    public void start (Stage S) throws Exception {
        DataFile fp = new DataFile();

        for (FLAGS flag : FLAGS.values()) fp.addHash(flag.toString());

        fp.setTimer();
        TimeUnit.SECONDS.sleep(2);
        fp.writeFlag(FLAGS.APPLES.toString(), "5");

        TimeUnit.SECONDS.sleep(2);
        fp.writeFlag(FLAGS.BANANAS.toString(), "5");

        TimeUnit.SECONDS.sleep(2);
        fp.writeFlag(FLAGS.ORANGES.toString(), "5");

        System.out.println("-----------------------------------");

        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%5

        ParameterSet P = new ParameterSet();
        P.put("one", 1, ParameterSet.TYPES.INT);
        P.put("true", true, ParameterSet.TYPES.BOOL);
        P.put("abc","xyz",ParameterSet.TYPES.STRING);

        FileOutputStream fpo = new FileOutputStream("tmp.parms");
        P.writeConfig(fpo);
        fpo.close();

        P.put("one", 2, ParameterSet.TYPES.INT);
        P.writeConfig(System.out);

        FileInputStream fpi = new FileInputStream("tmp.parms");
        P.readConfig(fpi);
        fpi.close();
        P.writeConfig(System.out);

        Platform.exit();
    }
}
