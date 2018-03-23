package DataFilePackage;

// provides type-checking parameter set that can store and recall from a config file
// and knows how to write its set to a data file

import javafx.application.Platform;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Properties;

public class ParameterSet {
    public enum TYPES {BOOL, INT, DOUBLE, STRING};

    private HashMap<String, Object> parmsMap = new HashMap();
    private HashMap<String, TYPES> typesMap = new HashMap();

    public void put(String K, Object V, TYPES T) {
        parmsMap.put(K, V);
        typesMap.put(K, T);
    }

    public boolean getBool(String K) {
        assert typesMap.get(K) == TYPES.BOOL;
        return (boolean) parmsMap.get(K);
    }

    public int getInt(String K) {
        assert typesMap.get(K) == TYPES.INT;
        return (int) parmsMap.get(K);
    }

    public double getDouble(String K) {
        assert typesMap.get(K) == TYPES.DOUBLE;
        return (double) parmsMap.get(K);
    }

    public String getString(String K) {
        assert typesMap.get(K) == TYPES.STRING;
        return (String) parmsMap.get(K);
    }

    public void writeToDataFile(DataFile fp) {
        for (String K : parmsMap.keySet()) {
            fp.write("#" + K, parmsMap.get(K).toString());
        }
    }

    public void writeConfig(String fn) {
        try {
            FileOutputStream fp = new FileOutputStream(fn);
            writeConfig(fp);
            fp.close();
        } catch (IOException e) {System.err.println("Cannot access " + fn); Platform.exit();}
        catch (RuntimeException e) {System.err.println(e.getMessage()); Platform.exit();}
    }

    public void writeConfig(OutputStream fp) throws IOException {
        Properties P = new Properties();
        for (String K : parmsMap.keySet())
            P.setProperty(K, parmsMap.get(K).toString());
        P.store(fp, "Saved on " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    public void readConfig(String fn) {
        try {
            FileInputStream fp = new FileInputStream(fn);
            readConfig(fp);
            fp.close();
        } catch (IOException e) {System.err.println("Cannot access " + fn); Platform.exit();}
        catch (RuntimeException e) {System.err.println(e.getMessage()); Platform.exit();}
    }

    public void readConfig(InputStream fp) throws IOException, RuntimeException {
        Properties P = new Properties();
        P.load(fp);
        for (Object KO : P.keySet()) {
            String K = (String) KO;
            String V = (String) P.get(K);
            if (parmsMap.containsKey(K))
                switch (typesMap.get(K)) {
                    case BOOL: parmsMap.put(K, Boolean.valueOf(V)); break;
                    case INT: parmsMap.put(K, Integer.valueOf(V)); break;
                    case DOUBLE: parmsMap.put(K, Double.valueOf(V)); break;
                    case STRING: parmsMap.put(K, V); break;
                    default: throw new RuntimeException("Unknown parameter type.");
                }
            else throw new RuntimeException("Tried to load invalid parameter: " + K);
        }
    }
}

