package my_programm;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CustomFileReader {
    public static List<String> readFile(String filename) {
        try {
            return readAllCharactersOneByOne(new FileReader(new File(filename)));
        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        }

    }

    private static List<String> readAllCharactersOneByOne(Reader reader) throws IOException {
        StringBuilder content = new StringBuilder();
        List<String> stroki = new ArrayList<>();
        int nextChar;
        while ((nextChar = reader.read()) != -1) {
            if (nextChar == 10) {
                stroki.add(String.valueOf(content));
                content = new StringBuilder();
            } else {
                content.append((char) nextChar);
            }
        }
        return stroki;
    }
}
