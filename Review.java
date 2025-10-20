import java.util.Scanner;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Class that contains helper methods for the Review Lab
 **/
public class Review {

    private static HashMap<String, Double> sentiment = new HashMap<String, Double>();
    private static ArrayList<String> posAdjectives = new ArrayList<String>();
    private static ArrayList<String> negAdjectives = new ArrayList<String>();

    static {
        try {
            Scanner input = new Scanner(new File("cleanSentiment.csv"));
            while (input.hasNextLine()) {
                String[] temp = input.nextLine().split(",");
                sentiment.put(temp[0], Double.parseDouble(temp[1]));
            }
            input.close();
        } catch (Exception e) {
            System.out.println("Error reading or parsing cleanSentiment.csv");
        }

        // Read in the positive adjectives
        try {
            Scanner input = new Scanner(new File("positiveAdjectives.txt"));
            while (input.hasNextLine()) {
                posAdjectives.add(input.nextLine().trim());
            }
            input.close();
        } catch (Exception e) {
            System.out.println("Error reading or parsing positiveAdjectives.txt\n" + e);
        }

        // Read in the negative adjectives
        try {
            Scanner input = new Scanner(new File("negativeAdjectives.txt"));
            while (input.hasNextLine()) {
                negAdjectives.add(input.nextLine().trim());
            }
            input.close();
        } catch (Exception e) {
            System.out.println("Error reading or parsing negativeAdjectives.txt");
        }
    }

    /**
     * Returns a string containing all of the text in fileName (including
     * punctuation),
     * with words separated by a single space
     */
    public static String textToString(String fileName) {
        String temp = "";
        try {
            Scanner input = new Scanner(new File(fileName));

            // Add words from the file to the string, separated by a single space
            while (input.hasNext()) {
                temp = temp + input.next() + " ";
            }
            input.close();

        } catch (Exception e) {
            System.out.println("Unable to locate " + fileName);
        }
        return temp.trim();
    }

    /**
     * Returns the sentiment value of word as a number between -1 (very negative)
     * to 1 (very positive sentiment)
     */
    public static double sentimentVal(String word) {
        try {
            return sentiment.get(word.toLowerCase());
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Returns the ending punctuation of a string, or the empty string if there is
     * none
     */
    public static String getPunctuation(String word) {
        String punc = "";
        for (int i = word.length() - 1; i >= 0; i--) {
            if (!Character.isLetterOrDigit(word.charAt(i))) {
                punc = punc + word.charAt(i);
            } else {
                return punc;
            }
        }
        return punc;
    }

    /**
     * Returns the word after removing any beginning or ending punctuation
     */
    public static String removePunctuation(String word) {
        while (word.length() > 0 && !Character.isAlphabetic(word.charAt(0))) {
            word = word.substring(1);
        }
        while (word.length() > 0 && !Character.isAlphabetic(word.charAt(word.length() - 1))) {
            word = word.substring(0, word.length() - 1);
        }

        return word;
    }

    /** Randomly picks a positive adjective */
    public static String randomPositiveAdj() {
        int index = (int) (Math.random() * posAdjectives.size());
        return posAdjectives.get(index);
    }

    /** Randomly picks a negative adjective */
    public static String randomNegativeAdj() {
        int index = (int) (Math.random() * negAdjectives.size());
        return negAdjectives.get(index);
    }

    /** Randomly picks a positive or negative adjective */
    public static String randomAdjective() {
        boolean positive = Math.random() < .5;
        if (positive) {
            return randomPositiveAdj();
        } else {
            return randomNegativeAdj();
        }
    }

    /** Calculates total sentiment from a text file */
    public static double totalSentiment(String fileName) {
        String text = textToString(fileName);
        String[] words = text.split(" ");
        double total = 0;
        for (String word : words) {
            total += sentimentVal(removePunctuation(word));
        }
        return total;
    }

    /** Calculates star rating from total sentiment */
    public static int starRating(String fileName) {
        double total = totalSentiment(fileName);
        if (total < -3) {
            return 1;
        } else if (total < 2) {
            return 2;
        } else if (total < 3) {
            return 3;
        } else if (total < 7) {
            return 4;
        } else {
            return 5;
        }
    }

    /**
     * Calculates total sentiment directly from text (not a file)
     */
    public static double totalSentimentFromText(String reviewText) {
        String[] words = reviewText.split(" ");
        double total = 0;
        for (String word : words) {
            total += sentimentVal(removePunctuation(word));
        }
        return total;
    }

    /**
     * Reads a CSV file and analyzes Spotify vs Apple Music sentiment
     */
    public static void analyzeCSV(String fileName) {
        try {
            Scanner input = new Scanner(new File(fileName));
            input.nextLine(); // Skip header line

            double spotifyTotal = 0;
            int spotifyCount = 0;
            double appleTotal = 0;
            int appleCount = 0;

            while (input.hasNextLine()) {
                String line = input.nextLine();
                // Split on commas but keep quoted text together
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (parts.length < 4)
                    continue;

                String app = parts[0].trim();
                String reviewText = parts[3].replaceAll("\"", "").trim();

                if (app.equalsIgnoreCase("Spotify")) {
                    spotifyTotal += totalSentimentFromText(reviewText);
                    spotifyCount++;
                } else if (app.equalsIgnoreCase("Apple Music")) {
                    appleTotal += totalSentimentFromText(reviewText);
                    appleCount++;
                }
            }
            input.close();

            System.out.println("===== Sentiment Analysis Results =====");
            if (spotifyCount > 0)
                System.out.println("Spotify: " + spotifyCount + " reviews, Avg sentiment = " + (spotifyTotal / spotifyCount));
            else
                System.out.println("No Spotify reviews found.");

            if (appleCount > 0)
                System.out.println("Apple Music: " + appleCount + " reviews, Avg sentiment = " + (appleTotal / appleCount));
            else
                System.out.println("No Apple Music reviews found.");

            if (spotifyCount > 0 && appleCount > 0) {
                System.out.println("--------------------------------------");
                if ((spotifyTotal / spotifyCount) > (appleTotal / appleCount)) {
                    System.out.println("People seem to prefer Spotify!");
                } else if ((spotifyTotal / spotifyCount) < (appleTotal / appleCount)) {
                    System.out.println("People seem to prefer Apple Music!");
                } else {
                    System.out.println("Both are about equally liked.");
                }
            }

        } catch (Exception e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }
    }

    /** Main method — runs sentiment comparison on CSV file */
    public static void main(String[] args) {
        analyzeCSV("app_store_music_reviews.csv");
    }
}
