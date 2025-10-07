package sys.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHandler {

    public FileHandler() {
        if (!Files.exists(Paths.get("src/main/resources/files/data.txt"))) {
            new File("src/main/resources/files/data.txt");
        }
    }

    public void Write() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/main/resources/files/data.txt", true))) {
            bw.write("First line");
            bw.newLine();             // writes a line break
            bw.write("Second line");
            bw.newLine();             // writes a line break
            System.out.println("✅ Buffered write complete.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
