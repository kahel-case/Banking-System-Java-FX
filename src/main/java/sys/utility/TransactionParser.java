package sys.utility;

import java.util.ArrayList;
import java.util.List;

public class TransactionParser {

    public static List<String[]> parseTransactions(String logText) {
        List<String[]> transactions = new ArrayList<>();

        // Step 1: Split the long text into individual transaction lines
        // We can split by regex using lookahead for the timestamp pattern
        String[] lines = logText.split("(?=\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2})");

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // Step 2: Split each line into fields by the " | " separator
            String[] parts = line.split("\\|");

            // Trim spaces around each field
            for (int i = 0; i < parts.length; i++) {
                if (i == 3) {
                    parts[i] = parts[i].replace("Balance:", "").replace("₱", "").trim();
                    continue;
                }
                parts[i] = parts[i].trim();
            }

            transactions.add(parts);
        }

        return transactions;
    }

    public static void main(String[] args) {
        String log = "2025-10-09 05:40:11 | Deposit | ₱100.00 | Balance: ₱100,100.00 " +
                "2025-10-09 05:42:39 | Deposit | ₱200.00 | Balance: ₱100,200.00 " +
                "2025-10-09 05:42:45 | Withdraw | ₱1,099.00 | Balance: ₱99,101.00 " +
                "2025-10-09 05:43:00 | Transfer to m | ₱99.00 | Balance: ₱99,002.00 " +
                "2025-10-09 05:43:11 | Transfer to u | ₱678.00 | Balance: ₱98,324.00";

        List<String[]> parsed = parseTransactions(log);

        // Print each array to verify
        for (String[] arr : parsed) {
            System.out.println("---- Transaction ----");
            for (String part : arr) {
                System.out.println(part);
            }
        }
    }
}