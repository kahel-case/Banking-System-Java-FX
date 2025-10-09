// sys/utility/FraudDetection.java
package sys.utility;

import java.io.*;
import java.util.*;

public class FraudDetection {

    // ========== ISOLATION TREE ==========
    public static class iTree {
        private int attribute;
        private double splitValue;
        private int currentHeight;
        private int nodesNum;
        private iTree leftTree;
        private iTree rightTree;

        public static iTree createTree(double[][] data, int currentHeight, int heightLimit) {
            iTree tree = new iTree();
            if (currentHeight >= heightLimit || data.length <= 1) {
                tree.currentHeight = currentHeight;
                tree.nodesNum = data.length;
                return tree;
            }

            int attrNum = data[0].length;
            int attr = (int)(Math.random() * attrNum);
            tree.attribute = attr;

            double min = data[0][attr], max = data[0][attr];
            for (int i = 1; i < data.length; i++) {
                if (data[i][attr] < min) min = data[i][attr];
                if (data[i][attr] > max) max = data[i][attr];
            }

            if (min == max) {
                tree.currentHeight = currentHeight;
                tree.nodesNum = data.length;
                return tree;
            }

            double sv = min + (max - min) * Math.random();
            tree.splitValue = sv;

            List<double[]> left = new ArrayList<>();
            List<double[]> right = new ArrayList<>();
            for (double[] row : data) {
                if (row[attr] < sv) left.add(row);
                else right.add(row);
            }

            tree.currentHeight = currentHeight;
            tree.nodesNum = data.length;
            tree.leftTree = createTree(left.toArray(new double[0][]), currentHeight + 1, heightLimit);
            tree.rightTree = createTree(right.toArray(new double[0][]), currentHeight + 1, heightLimit);

            return tree;
        }

        public static double pathLength(double[] x, iTree tree, int currentPathLength) {
            if (tree.leftTree == null || tree.rightTree == null) {
                return currentPathLength + c(tree.nodesNum);
            }
            if (x[tree.attribute] < tree.splitValue) {
                return pathLength(x, tree.leftTree, currentPathLength + 1);
            } else {
                return pathLength(x, tree.rightTree, currentPathLength + 1);
            }
        }

        public static double c(double n) {
            if (n <= 1) return 0;
            return 2 * (Math.log(n - 1) + 0.5772156649) - 2 * (n - 1) / n;
        }
    }

    // ========== ISOLATION FOREST ==========
    public static class iForest {
        protected int treeNum;
        protected int subsampleSize;
        protected double[][] data;
        protected List<iTree> trees;

        protected iForest() {
            trees = new ArrayList<>();
        }

        public static iForest createForest(double[][] data, int treeNum, int subsampleSize) {
            iForest forest = new iForest();
            int heightLimit = (int)Math.ceil(Math.log(subsampleSize) / Math.log(2));

            Random rand = new Random();
            int attrNum = data[0].length;
            int dataNum = data.length;

            for (int i = 0; i < treeNum; i++) {
                double[][] subData = new double[subsampleSize][attrNum];
                for (int j = 0; j < subsampleSize; j++) {
                    subData[j] = data[rand.nextInt(dataNum)];
                }
                forest.trees.add(iTree.createTree(subData, 0, heightLimit));
            }

            forest.treeNum = treeNum;
            forest.subsampleSize = subsampleSize;
            forest.data = data;

            return forest;
        }

        public double[] score() {
            double[] score = new double[data.length];
            for (int i = 0; i < data.length; i++) {
                double acc = 0;
                for (iTree t : trees) {
                    acc += iTree.pathLength(data[i], t, 0);
                }
                double avg = acc / trees.size();
                score[i] = Math.pow(2, -avg / iTree.c(subsampleSize));
            }
            return score;
        }

        public double scorePoint(double[] point) {
            double totalPathLength = 0;
            for (iTree tree : trees) {
                totalPathLength += iTree.pathLength(point, tree, 0);
            }
            double avgPathLength = totalPathLength / trees.size();
            return Math.pow(2, -avgPathLength / iTree.c(subsampleSize));
        }
    }

    // ========== USER STATISTICS ==========
    public static class UserStats {
        double avgAmount = 0;
        double stdAmount = 1;
        int totalTransactions = 0;
        Map<String, Integer> typeFrequency = new HashMap<>();
    }

    // ========== HELPER METHODS ==========

    private static double parseAmount(String amountStr) {
        try {
            return Double.parseDouble(amountStr.replace("₱", "").replace("$", "").replace(",", "").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private static List<String> getAllUsers() {
        List<String> users = new ArrayList<>();
        try {
            File folder = new File("transactions/");
            if (!folder.exists()) {
                folder.mkdirs();
                return users;
            }
            File[] files = folder.listFiles((dir, name) -> name.endsWith("_transactions.txt"));
            if (files != null) {
                for (File file : files) {
                    String user = file.getName().replace("_transactions.txt", "");
                    users.add(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    private static String getTransactions(String username) {
        StringBuilder content = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("transactions/" + username + "_transactions.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    private static List<String[]> parseTransactions(String transactionHistory) {
        List<String[]> transactions = new ArrayList<>();
        if (transactionHistory == null || transactionHistory.trim().isEmpty()) return transactions;

        for (String line : transactionHistory.split("\n")) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("Transaction") || line.startsWith("---")) continue;

            String[] parts = line.split("\\|");
            if (parts.length >= 2) {
                String timestamp = parts.length >= 3 ? parts[0].trim() : "2024-01-01 12:00:00";
                String type = parts.length >= 3 ? parts[1].trim() : parts[0].trim();
                String amount = (parts.length >= 3 ? parts[2] : parts[1]).trim().split("Balance:")[0].trim();
                transactions.add(new String[]{timestamp, type, amount});
            }
        }
        return transactions;
    }

    private static UserStats calculateUserBaseline(String username, List<String[]> transactions) {
        UserStats stats = new UserStats();
        if (transactions.isEmpty()) return stats;

        List<Double> amounts = new ArrayList<>();
        for (String[] txn : transactions) {
            double amount = parseAmount(txn[2]);
            amounts.add(amount);
            String type = txn[1].toLowerCase();
            stats.typeFrequency.put(type, stats.typeFrequency.getOrDefault(type, 0) + 1);
        }

        double sum = 0;
        for (double amt : amounts) sum += amt;
        stats.avgAmount = sum / amounts.size();

        double variance = 0;
        for (double amt : amounts) variance += Math.pow(amt - stats.avgAmount, 2);
        stats.stdAmount = Math.max(1, Math.sqrt(variance / amounts.size()));
        stats.totalTransactions = transactions.size();

        return stats;
    }

    private static double calculateVelocity(List<String[]> transactions, int currentIndex) {
        int windowCount = 0;
        try {
            String currentTime = transactions.get(currentIndex)[0];
            for (int i = Math.max(0, currentIndex - 10); i < currentIndex; i++) {
                if (isWithinOneHour(transactions.get(i)[0], currentTime)) windowCount++;
            }
            return Math.min(windowCount / 5.0, 1.0);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private static boolean isWithinOneHour(String time1, String time2) {
        try {
            String[] p1 = time1.split(" "), p2 = time2.split(" ");
            if (p1.length >= 2 && p2.length >= 2 && p1[0].equals(p2[0])) {
                int h1 = Integer.parseInt(p1[1].split(":")[0]);
                int h2 = Integer.parseInt(p2[1].split(":")[0]);
                return Math.abs(h1 - h2) <= 1;
            }
        } catch (Exception e) {}
        return false;
    }

    private static double extractTimeFeature(String timestamp) {
        try {
            if (timestamp.contains(":")) {
                int hour = Integer.parseInt(timestamp.split(" ")[1].split(":")[0]);
                if (hour >= 0 && hour < 6) return 0.9;
                else if (hour >= 22 || hour < 8) return 0.6;
                else return 0.2;
            }
        } catch (Exception e) {}
        return 0.2;
    }

    private static double calculateBalanceImpact(String[] transaction, double avgAmount) {
        try {
            double amount = parseAmount(transaction[2]);
            String type = transaction[1].toLowerCase();
            double mult = type.contains("transfer") ? 2.0 : (type.contains("withdraw") ? 1.5 : 0.5);
            return Math.min((amount / Math.max(avgAmount, 1.0)) * mult / 10.0, 1.0);
        } catch (Exception e) {
            return 0.0;
        }
    }

    // ========== FEATURE EXTRACTION ==========

    private static List<double[]> extractFeaturesFromTransactions(String username, String transactionHistory) {
        List<double[]> features = new ArrayList<>();
        if (transactionHistory == null || transactionHistory.trim().isEmpty()) return features;

        try {
            List<String[]> parsedTransactions = parseTransactions(transactionHistory);
            if (parsedTransactions.isEmpty()) return features;

            UserStats baseline = calculateUserBaseline(username, parsedTransactions);

            for (int i = 0; i < parsedTransactions.size(); i++) {
                String[] txn = parsedTransactions.get(i);
                double amount = parseAmount(txn[2]);

                // Feature 1: Amount deviation from user average
                double amountDev = Math.min(Math.abs(amount - baseline.avgAmount) / baseline.stdAmount, 5.0) / 5.0;

                // Feature 2: Normalized amount
                double normAmount = Math.min(Math.log1p(amount) / Math.log1p(100000), 1.0);

                // Feature 3: Type rarity
                String type = txn[1].toLowerCase();
                int typeCount = baseline.typeFrequency.getOrDefault(type, 0);
                double typeRarity = 1.0 - (typeCount / (double)baseline.totalTransactions);

                // Feature 4-6: Velocity, time, balance impact
                double velocity = calculateVelocity(parsedTransactions, i);
                double timeAnomaly = extractTimeFeature(txn[0]);
                double balanceImpact = calculateBalanceImpact(txn, baseline.avgAmount);

                features.add(new double[]{amountDev, normAmount, typeRarity, velocity, timeAnomaly, balanceImpact});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return features;
    }

    // ========== MAIN FRAUD DETECTION ==========

    public static List<String> detectSuspiciousUsers() {
        List<String> suspiciousUsers = new ArrayList<>();
        try {
            List<String> allUsers = getAllUsers();
            Map<String, List<double[]>> userTransactions = new HashMap<>();
            Map<String, List<String[]>> userParsedTransactions = new HashMap<>();

            for (String user : allUsers) {
                if (user.equals("admin")) continue;
                String txnHistory = getTransactions(user);
                List<String[]> parsed = parseTransactions(txnHistory);
                userParsedTransactions.put(user, parsed);
                List<double[]> features = extractFeaturesFromTransactions(user, txnHistory);
                if (!features.isEmpty()) userTransactions.put(user, features);
            }

            List<double[]> allFeatures = new ArrayList<>();
            for (List<double[]> f : userTransactions.values()) allFeatures.addAll(f);

            if (allFeatures.size() > 10) {
                double[][] featureArray = allFeatures.toArray(new double[0][]);
                iForest forest = iForest.createForest(featureArray, 100, Math.min(256, featureArray.length));

                for (String user : userTransactions.keySet()) {
                    List<double[]> userFeats = userTransactions.get(user);
                    List<String[]> userTxns = userParsedTransactions.get(user);

                    int suspCount = 0;
                    double maxScore = 0;
                    List<String> suspTxns = new ArrayList<>();

                    for (int i = 0; i < userFeats.size(); i++) {
                        double score = forest.scorePoint(userFeats.get(i));
                        if (score > maxScore) maxScore = score;
                        if (score > 0.60) {
                            suspCount++;
                            if (suspTxns.size() < 3) {
                                suspTxns.add(userTxns.get(i)[1] + " " + userTxns.get(i)[2]);
                            }
                        }
                    }

                    if (suspCount >= 3 || maxScore > 0.65) {
                        String detail = String.format("%s (Score: %.2f, Suspicious: %d)", user, maxScore, suspCount);
                        if (!suspTxns.isEmpty()) detail += " - " + String.join(", ", suspTxns);
                        suspiciousUsers.add(detail);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return suspiciousUsers;
    }
}