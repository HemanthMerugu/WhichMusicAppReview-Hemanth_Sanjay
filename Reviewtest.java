import java.util.Scanner;
import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Class that contains helper methods for the Review Lab
 **/
public class Reviewtest {

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

        // Read in the positive adjectives in postiveAdjectives.txt
        try {
            Scanner input = new Scanner(new File("positiveAdjectives.txt"));
            while (input.hasNextLine()) {
                posAdjectives.add(input.nextLine().trim());
            }
            input.close();
        } catch (Exception e) {
            System.out.println("Error reading or parsing positiveAdjectives.txt\n" + e);
        }

        // Read in the negative adjectives in negativeAdjectives.txt
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
     * @returns the sentiment value of word as a number between -1 (very negative) to 1 (very positive sentiment)
     */
    public static double sentimentVal(String word) {
        try {
            return sentiment.get(word.toLowerCase());
        } catch (Exception e) {
            return 0; // If word isn't in the sentiment map, return neutral value (0)
        }
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

    /**
     * Calculates the total sentiment of a single review (file)
     */
    public static double reviewSentiment(String reviewText) {
        String[] words = reviewText.split(" ");
        double totalSentiment = 0;
        for (String word : words) {
            totalSentiment += sentimentVal(removePunctuation(word));
        }
        return totalSentiment;
    }

    /**
     * Processes the reviews from a file, calculates sentiment for each review, and prints the results.
     */
    public static void analyzeReviews(String fileName) {
        try {
            Scanner input = new Scanner(new File(fileName));
            int reviewCount = 0;
            double totalSentiment = 0;
            while (input.hasNextLine()) {
                String review = input.nextLine().trim(); // Read each review line-by-line
                if (!review.isEmpty()) {
                    reviewCount++;
                    double sentimentValue = reviewSentiment(review); // Calculate sentiment for the review
                    totalSentiment += sentimentValue;
                    System.out.println("Review " + reviewCount + " sentiment: " + sentimentValue);
                }
            }
            input.close();

            // Calculate the average sentiment for all reviews
            if (reviewCount > 0) {
                double averageSentiment = totalSentiment / reviewCount;
                System.out.println("Average sentiment for " + reviewCount + " reviews: " + averageSentiment);
            } else {
                System.out.println("No reviews to process.");
            }

        } catch (Exception e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Pass the path of your reviews file to the analyzeReviews method
        analyzeReviews("reviews.txt");
    }
}
