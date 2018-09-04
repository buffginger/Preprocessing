package com.company;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Arrays;
import java.util.Comparator;
public class Main {

    // Data structure to hold our words e.g key => value
    static Map<String, Integer> wordMap = new HashMap<>();

    public static void main(String[] args) {


        String word = "";

        // Set the path to the documentset folder
        File folder = new File("/Users/ethananderson/Downloads/test");
        File[] fileList = folder.listFiles();   // Get the # of total files

        // Iterate through each file
        for (File file : fileList) {
            if (file.isFile()) {
                // Open the file for reading
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;    // the line of text from the file
                    // Skip the first 12 lines of the file, as they aren't needed
                    for (int i = 0; i < 12; i++) {
                        br.readLine();
                    }
                    // Read each line in the file
                    while ((line = br.readLine()) != null) {

                        /* Preprocess the line
                        a) eliminate the HTML tags
                        b) remove punctuation
                        c) tokenize the text
                        */

                        StringTokenizer st = new StringTokenizer(line, " ");
                        while (st.hasMoreElements()) {
                            word = st.nextToken().toLowerCase();

                            // Filter out tags
                            if (isHTMLTag(word) == false) {
                                word = removePunctuation(word);
                                wordMap.put(word, wordMap.getOrDefault(word, 0) + 1);
                            }

                            /* a.   How many unique keywords are there in this collection?
                            b.   What are the top 10 most frequently occurring keywords in the collection?
                            c.   For each of the top 10 keywords, indicate which ones are meaningful.
                            d.   What are the bottom 10 keywords in the collection?*/
                        }

                    }
                } catch (IOException x) {
                    System.err.println(x);
                }

            }
        } // end for

        displayResults();

        /*for (Map.Entry entry : wordMap.entrySet())
        {
            System.out.println("key: " + entry.getKey() + "; value: " + entry.getValue());
        }*/

    }

    public static boolean isHTMLTag(String word) {
        if (word.matches("<.+?>")) {
            return true;
        } else {
            return false;
        }
    }

    public static String removePunctuation(String word) {
        return word.replaceAll("[^\\w\\s]|_", "");
    }

    public static int numKeyWords() {
        return wordMap.size();
    }

    public static void displayResults() {
        System.out.println("2a) There are " + numKeyWords() + " keywords");
        sortMap();
    }

    public static void sortMap() {
        Object[] sortedMap = wordMap.entrySet().toArray();
        Arrays.sort(sortedMap, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Map.Entry<String, Integer>) o2).getValue()
                        .compareTo(((Map.Entry<String, Integer>) o1).getValue());
            }
        });
        System.out.print("2b) The top ten words are: ");
        for (int i = 0; i < 10; i++) {
            System.out.print(sortedMap[i].toString().replaceFirst("=.*", "") );
            if (i != 9) {
                System.out.print(", ");
            }
        }
        System.out.print("\n2c) The bottom ten words are: ");
        for (int i = sortedMap.length - 1; i > sortedMap.length -11; i--) {
            System.out.print(sortedMap[i].toString().replaceFirst("=.*",""));
            if (i != sortedMap.length -10) {
                System.out.print(", ");
            }
        }

    }


}