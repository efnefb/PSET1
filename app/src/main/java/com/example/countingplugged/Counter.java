package com.example.countingplugged;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class Counter {

    //1. From the arraylist of words ––> create a frequency table of the form {word : frequency}
    public static HashMap<String, Integer> getFrequencies(ArrayList<String> words){
        HashMap<String, Integer> freqTable = new HashMap<>();
        for (String word: words){
            if (freqTable.containsKey(word)){
                freqTable.put(word, freqTable.get(word)+1);
            }
            else{
                freqTable.put(word, 1);
            }
        }
        return freqTable;
    }


    //2. Sorts the list of unique words by frequency
    public static ArrayList<String> sortWordsByFreq(HashMap<String, Integer> freqTable){
        ArrayList<String> uniqueWords = new ArrayList<>();
        for (String word : freqTable.keySet()) {
            uniqueWords.add(word);
        }
        for (int j=0; j < uniqueWords.size(); j++) {
            for (int i = 0; i < uniqueWords.size() - 1; i++) {
                if (freqTable.get(uniqueWords.get(i)) > freqTable.get(uniqueWords.get(i + 1))) {
                    String temp = uniqueWords.get(i + 1);
                    uniqueWords.set(i + 1, uniqueWords.get(i));
                    uniqueWords.set(i, temp);
                }
            }
        }

        return uniqueWords;
    }

    //3. From the sorted arraylist of words, get the corresponding frequencies ––> returns a parallel array to what sortSordsbyFreq returns
    public static ArrayList<Integer> getCorrespondingFrequencies(ArrayList<String> sortedWords, HashMap<String, Integer> freqTable){
        ArrayList<Integer> uniqueFrequencies = new ArrayList<>();
        for (String word: sortedWords){
            uniqueFrequencies.add(freqTable.get(word));
        }
        return uniqueFrequencies;

    }





    //read text into an arraylist of words
    public static ArrayList<String> readText(Context context, String filename, ArrayList<String> commonWords) throws IOException{
        ArrayList<String> words = new ArrayList<>();

        InputStream inputStream = context.getAssets().open(filename);
        Scanner scanner = new Scanner(inputStream);
        String word;
        while (scanner.hasNext()){
            word = cleanWord(scanner.next());
            if (Collections.binarySearch(commonWords, word) < 0){
                words.add(word);
            }
        }
        scanner.close();
        return words;
    }

    //Get list of common words as arraylist
    public static ArrayList<String> getCommonWords(Context context, String filename) throws IOException{
        ArrayList<String> commonWords = new ArrayList<>();

        InputStream inputStream = context.getAssets().open(filename);
        Scanner scanner = new Scanner(inputStream);
        String commonWord;
        while (scanner.hasNext()){
            commonWord = scanner.next();
            commonWords.add(commonWord);
        }
        return selectionSort(commonWords);
    }


    //Remove punctuation around a word
    public static String cleanWord(String word){
        word = word.toLowerCase();
        String cleanedWord = "";
        for (int i=0; i<word.length(); i++){
            if ((int) word.charAt(i) >= 97 && (int) word.charAt(i) <= 122){
                cleanedWord += word.charAt(i);
            }
        }
        return cleanedWord;
    }

    //Sort the list of common words so that we can do binary search to see if a word in text is common
    public static ArrayList<String> selectionSort(ArrayList<String> list) {
        String temp;
        int mIndex;
        for (int i=0; i<list.size(); i++){
            mIndex = i;
            for (int j=i+1; j<list.size(); j++){
                if (list.get(j).toLowerCase().compareTo(list.get(mIndex).toLowerCase()) < 0){
                    mIndex = j;
                }
            }
            temp = list.get(mIndex);
            list.set(mIndex, list.get(i));
            list.set(i, temp);
        }
        return list;
    }

}

