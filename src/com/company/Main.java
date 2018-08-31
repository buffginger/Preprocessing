package com.company;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Main {

    public static void main(String[] args) {

        // Data structure to hold our words e.g key => value
        Map<String,Integer> wordMap = new HashMap<>();
        String key = "";
        Integer value = 0;

        // Set the path to the documentset folder
        File folder = new File("/Users/ethananderson/Downloads/documentset");
        File[] fileList = folder.listFiles();   // Get the # of total files

        // Iterate through each file
        for (File file : fileList) {
            if (file.isFile()) {
                // Open the file for reading
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;    // the line of text from the file
                    // Read each line in the file
                    while ((line = br.readLine()) != null) {

                        /* Preprocess the line
                        a) eliminate the HTML tags
                        b) remove punctuation
                        c) tokenize the text
                        */

                        StringTokenizer st = new StringTokenizer(line, " ");
                        while (st.hasMoreElements()) {
                            // Filter out tags and punctuation with a whitelist function

                            // If the token passes, add to wordMap

                            // Use variables to check for criteria such as most frequent, etc...
                        }





                    }
                } catch (IOException x) {
                    System.err.println(x);
                }

            }
        }
    }
}
