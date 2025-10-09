package sys.utility;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileHandler {

    private static final String USERS_FILE = "user_files.txt";
    private static final String TRANSACTION_FOLDER = "transactions/";
    private static final String RUNTIME_PERFORMANCE = "runtime_performance.txt";

    static {
        File dir = new File(TRANSACTION_FOLDER);
        if (!dir.exists()) dir.mkdir();
    }

    public static void saveRuntime(String text) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUNTIME_PERFORMANCE, true))) {
            bw.write(text);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("file handler, save runtime error");
        }
    }

    public static void addUser(String username) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
            bw.write(username);
            bw.newLine();

            File userFile = new File(TRANSACTION_FOLDER + username + "_transactions.txt");
            if (!userFile.exists()) userFile.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void recordTransaction(String username, String type, double amount, double newBalance) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(TRANSACTION_FOLDER + username + "_transactions.txt", true))) {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            bw.write(time + " | " + type + " | ₱" + String.format("%,.2f", amount) + " | Balance: ₱" + String.format("%,.2f", newBalance));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getTransactions(String username) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(TRANSACTION_FOLDER + username + "_transactions.txt"))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append("\n");
        } catch (IOException e) {
            sb.append("No transaction history found.");
        }
        return sb.toString();
    }
}
