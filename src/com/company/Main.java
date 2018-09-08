package com.company;
import java.io.*;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Iterator;

public class Main {

    public static void main(String[] args) {

        File stopWords = new File("/Users/ethananderson/Downloads/stop-word-list.txt");

        // Set the path to the documentset folder
        File documentSet = new File("/Users/ethananderson/Downloads/documentset");

        // Create a TreeSet from the provided stopWords file
        TreeSet<String> stopWordsTree= getStopWordsAsTree(stopWords);

        // Initial wordMap, for #2. 
        HashMap<String, Integer> wordMap1 = createMapFromDocs(documentSet);
        displayResults(wordMap1);

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

    public static int numKeyWords(HashMap<String, Integer> wordMap) {
        return wordMap.size();
    }

    public static void displayResults(HashMap<String, Integer> wordMap) {
        System.out.println("2a) There are " + numKeyWords(wordMap) + " keywords");
        sortMap(wordMap);
    }

    public static void sortMap(HashMap<String, Integer> wordMap) {
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

    public static TreeSet<String> getStopWordsAsTree(File stopWords) {

        TreeSet<String> ts = new TreeSet<>();

        try (BufferedReader buff = new BufferedReader(new FileReader(stopWords))) {
            String entry;

            while ((entry = buff.readLine()) != null) {
                ts.add(entry.toLowerCase());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ts;
    }

    public static HashMap<String, Integer> createMapFromDocs(File folder) {

        // Data structure to hold our words e.g key => value
        HashMap<String, Integer> wordMap = new HashMap<>();
        String word = "";
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
        return wordMap;

    }

}