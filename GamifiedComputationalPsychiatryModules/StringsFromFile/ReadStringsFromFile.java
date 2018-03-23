package StringsFromFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ReadStringsFromFile {

    private Map<String,String> myMap = new HashMap<String, String>();

    public ReadStringsFromFile(String fn) {
        try {ParseLines(Files.readAllLines(Paths.get(fn)));}
        catch (IOException e) {System.err.println(fn + " --- IOException: " + e.getMessage());};
    }

    public Set<String> keySet() {return myMap.keySet();}

    public String get(String key) {return myMap.get(key);};

    private void ParseLines(List<String> lines) {
        String S0, S1, key, value;
        StringBuilder SR = new StringBuilder(1000);
        for (ListIterator<String> iL = lines.listIterator(); iL.hasNext(); ) {
            S0 = iL.next();
            if ((S0.length() > 3) && (S0.substring(0,3).equals("%% "))) {
                key = S0.substring(3);
                SR.setLength(0); // clear result
                for (S1 = iL.next(); iL.hasNext() && (S1!=null) && !S1.equals("-----"); S1 = iL.next()) {
                    SR.append(S1); SR.append("\n");
                }
                value = SR.toString();
                myMap.put(key, value);
            }
        }
    }
}
