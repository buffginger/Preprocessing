package com.company;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// ./Main <Document set> <Query file>

public class Main {

    static File stopWords = new File("/Users/ethananderson/Downloads/stop-word-list.txt");

    // Create a TreeSet from the provided stopWords file. This is class-defined since only one instance is needed
    static TreeSet<String> stopWordsTree= getStopWordsAsTree(stopWords);

    static Porter porter = new Porter();

    static ArrayList<String> queryList = new ArrayList<>();
    public static void main(String[] args) {
        /*
        if (args.length != 2) {
            System.out.println("Please use two command line arguments.\n" +
                    "Example: ./Main <Document Set> <Query Terms File>");
            System.exit(0); }
            */

        String fileName;

        // Set the path to the documentset folder
        //File documentSet = new File(args[0]);
        //File queryDocument = new File(args[1]);
        File queryDocument = new File("/Users/ethananderson/Downloads/query.txt");
        File documentSet = new File("/Users/ethananderson/Downloads/documentset");
        File[] fileList = documentSet.listFiles();   // Get the # of total files

        createQueryTermList(queryDocument);

        ExecutorService executorService = Executors.newFixedThreadPool(15);

        for (String query: queryList) {
            executorService.execute(new Runnable() {
                public void run() {
                    long start = System.currentTimeMillis();
                    // call query method
                    HashMap<String, Double> docRanks = createPageFromDocs(fileList, query);
                    // Sort docRanks by value and Print top 10
                    displayResults(docRanks, query, start);
                }
            });
        }

        executorService.shutdown();

    }


    public static void createQueryTermList (File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;    // the line of text from the file
            // Read each line in the file
            while ((line = br.readLine()) != null) {

                // Stop and Stem the line
                StringTokenizer st = new StringTokenizer(line, " ");
                String word;
                String phrase = "";
                while (st.hasMoreElements()) {
                    word = st.nextToken().toLowerCase();
                    if (isStopWord(word) && !word.equals("")) {
                        // don't add it
                    }
                    // Send to Stemmer if needed
                    else {
                        if (!word.equals("") && !isStopWord(word)) {
                            word = porter.stripAffixes(word);
                            phrase += word + " ";
                        }
                    }
                }
                // Capture each query
                queryList.add(phrase);
            }
        }catch(IOException e) {
            System.err.println(e);
        }
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

    public static synchronized void displayResults(HashMap<String, Double> wordMap, String query, long start) {
        Object[] sortedMap = sortMap(wordMap);
        long time = System.currentTimeMillis() - start;
        System.out.println("Query: \"" + query + "\", time to process: " + time);
        for (int i = 0; i < 10; i++) {
            System.out.println(sortedMap[i].toString().replaceFirst("\\.txt", "").replaceFirst("=", ":\t"));
        }
        System.out.println();
    }

    public static Object[] sortMap(HashMap<String, Double> wordMap) {
        Object[] sortedMap = wordMap.entrySet().toArray();
        Arrays.sort(sortedMap, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Map.Entry<String, Double>) o2).getValue()
                        .compareTo(((Map.Entry<String, Double>) o1).getValue());
            }
        });

        return sortedMap;
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

    public static HashMap<String, Double> createPageFromDocs(File[] fileList, String query) {
        // Data structure to hold our words e.g key => value
        HashMap<String, Double> wordMap = new HashMap<>();

        for (File file : fileList) {
            // Iterate through each file
            if (file.isFile()) {
                wordMap.put(file.getName(), calculateRank(file, query));
            }
        }

        return wordMap;
    }

    public static Double calculateRank(File file, String query) {
        String word = "";
        double totalWords = 0;
        HashMap<String, Integer> keywords = new HashMap<>();
        TreeSet<String> queryTerms = new TreeSet<>();

        StringTokenizer st = new StringTokenizer(query, " ");
        while (st.hasMoreElements()) {
            word = st.nextToken().toLowerCase();
            queryTerms.add(word);
        }


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

                StringTokenizer str = new StringTokenizer(line, " ");
                while (str.hasMoreElements()) {
                    word = str.nextToken().toLowerCase();

                    // Filter out tags
                    if (isHTMLTag(word) == false) {
                        word = removePunctuation(word);


                        // Send to Stopwords if needed
                        if (isStopWord(word) && !word.equals("")) {
                            // don't add it
                        }
                        // Send to Stemmer if needed
                        else {
                            if (!word.equals("") && !isStopWord(word)) {
                                word = porter.stripAffixes(word);
                                totalWords++;

                                if (queryTerms.contains(word)) {
                                    keywords.put(word, keywords.getOrDefault(word, 0) + 1);
                                }
                            }

                        }
                    }

                }

            }
        } catch (IOException x) {
            System.err.println(x);
        }

        double rank = 0;
        String queryTerm = "";
        st = new StringTokenizer(query, " ");
        while (st.hasMoreElements()) {

            queryTerm = st.nextToken();

            if(keywords.containsKey(queryTerm)) {
                rank += (keywords.get(queryTerm) / totalWords);
                //System.out.println(rank);
                //System.out.println(queryTerm + " value: " + keywords.get(queryTerm) + ", total words: " + totalWords);
            }
        }
        return rank;
    }

    // Returns false if the word is not a stopword
    public static boolean isStopWord(String word) {
        if (!stopWordsTree.contains(word)) {
            return false;
        } else {
            return true;
        }
    }


}