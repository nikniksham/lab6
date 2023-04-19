package my_programm;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class CustomFileWriter {
    public static void writeString(String filename, ArrayList<String> strings) throws IOException {
        OutputStream out = new FileOutputStream(filename);
        for (String s: strings) {
            out.write((s+"\n").getBytes());
        }
    }
}
