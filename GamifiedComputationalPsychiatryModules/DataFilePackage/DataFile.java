package DataFilePackage;

import javafx.application.Platform;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class DataFile {

    // provides enumerated file IO
    // all lines are time HashFLAG number

    private PrintStream fp;
    private Collection<String> hashFlags = new HashSet<String>();
    private long startTime;

    public DataFile() {
        setFile(System.out); init();
    }

    public DataFile(String fn) {
        setFile(fn); init();
    }

    private void init() {
        addHash("VERSION");
        addHash("DATE");
    }

    public void setFile(String fn) {
        try {
            setFile(new PrintStream(new FileOutputStream(fn)));
        } catch (Exception e) {
            System.err.println("Cannot open file " + fn);
            Platform.exit();
        }
    }

    public void setFile(PrintStream fp) {
        this.fp = fp;
        setTimer();
    }

    public void close() {
        write("DONE");
        if (fp != null) fp.close();
        fp = null;
    }

    public void addHash(String S) {
        hashFlags.add(S);
    }
    public void addHash(String[] S) { for (String s : S) hashFlags.add(s);}

    private boolean checkHash(String S) {
        return hashFlags.contains(S);
    }

    public void setTimer() {
        startTime = System.currentTimeMillis();
        write("START");
    }

    public void write(String S) {write(S, "---");}
    public void writeDate() {writeFlag("DATE", DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now()));}

    public void write(String S, String P) {
        long dT = System.currentTimeMillis() - startTime;
        fp.println(String.valueOf(dT) + "   " + S + "   " + P);
        fp.flush();
    }

    public void writeFlag(String S, String P) {
        if (checkHash(S)) write(S, P);
        else System.err.println("Unknown flag " + S);
    }
}
