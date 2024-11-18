package com.example.countingplugged;
import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Analyzer {
    private Context context;

    private TextReader textReader;
    private int wordCount;
    private int sentenceCount;
    private LinkedHashMap<String, Integer> wordFreqTable; //Sorted by freq
    private HashMap<String, HashMap<String, Integer>> biGramTable;
    private ArrayList<String> commonWords;

    public Analyzer(Context context, TextReader textReader, String commonWords) throws IOException{
        this.context = context;
        this.textReader = textReader;
        setCommonWords(commonWords);
    }

    public Analyzer(Context context, TextReader textReader, ArrayList<String> commonWords) throws IOException{
        this.context = context;
        this.textReader = textReader;
        setCommonWords(commonWords);
    }

    public TextReader getTextReader(){
        return textReader;
    }
    public void setTextReader(TextReader newTextReader){
        textReader = newTextReader;
        clearData();
    }
    public int getWordCount(){
        return wordCount;
    }
    public int getSentenceCount() {
        return sentenceCount;
    }

    public LinkedHashMap<String, Integer> removedCommon(LinkedHashMap<String, Integer> original){
        LinkedHashMap<String, Integer> copy = new LinkedHashMap<>(original);
        copy.keySet().removeAll(commonWords);
        return copy;
    }

    public LinkedHashMap<String, Integer> getWordFreqTable(boolean noCommon){
        if (!noCommon) return wordFreqTable;
        else return removedCommon(wordFreqTable);
    }

    public List<String> getUniqueWords(boolean noCommon) {
        if (!noCommon) return new ArrayList<>(wordFreqTable.keySet());
        else return new ArrayList<>(removedCommon(wordFreqTable).keySet());
    }

    public List<Map.Entry<String, Integer>> getMostFreqN(int n, boolean noCommon){
        HashMap<String, Integer> table;
        if (!noCommon) table = wordFreqTable;
        else table = removedCommon(wordFreqTable);
        List<Map.Entry<String, Integer>> wordFreqsList = new ArrayList<>(table.entrySet());
        return wordFreqsList.subList(wordFreqsList.size()-n, wordFreqsList.size());
    }

    public HashMap<String, HashMap<String, Integer>> getBiGramTable() {
        return biGramTable;
    }



    public void analyze(){
        findWordSentenceCount();
        makeWordFreqTable(textReader.getWords(true));
        makeBiGramTable(textReader.getWords(true));
    }

    public void findWordSentenceCount(){
        String text = textReader.getText();
        String regex = "(?<=[.!?])\\s+(?=[A-Z])";
        sentenceCount = Pattern.compile(regex).split(text.trim()).length;
        wordCount = text.split("[ \\n]+|(?<=\\p{Punct})\\n").length;
    }

    public void makeWordFreqTable(ArrayList<String> words){
        HashMap<String, Integer> freqMap = new HashMap<>();
        for (String word: words){
            if (freqMap.containsKey(word)){
                freqMap.put(word, freqMap.get(word)+1);
            }
            else{
                freqMap.put(word, 1);
            }
        }

        //Sort keys by value
        List<String> keys = new ArrayList<>(freqMap.keySet());
        String temp;
        boolean flag = false;
        while (!flag){
            flag = true;
            for (int i=0; i<keys.size()-1; i++){
                if (freqMap.get(keys.get(i+1)) < freqMap.get(keys.get(i))){
                    flag = false;
                    temp = keys.get(i+1);
                    keys.set(i+1, keys.get(i));
                    keys.set(i, temp);
                }
            }
        }

        //Note: Order of insertion = Ascending order by frequency
        LinkedHashMap<String, Integer> sortedFreqMap = new LinkedHashMap<>();
        for (String key: keys){
            sortedFreqMap.put(key, freqMap.get(key));
        }

        wordFreqTable = sortedFreqMap;
    }

    public void makeBiGramTable(ArrayList<String> words){
        HashMap<String, HashMap<String, Integer>> biGrams = new HashMap<>();

        String word;
        String nextWord;
        HashMap<String, Integer> nextWordMap = new HashMap<>();
        for (int i=0; i<words.size()-1; i++){
            word = words.get(i);
            nextWord = words.get(i+1);
            if (!biGrams.containsKey(word)){
                nextWordMap.put(nextWord, 1);
                biGrams.put(word, nextWordMap);
                nextWordMap = new HashMap<>();
            } else{
                nextWordMap = biGrams.get(word);
                if (nextWordMap.containsKey(nextWord)) nextWordMap.put(nextWord, nextWordMap.get(nextWord) + 1);
                else nextWordMap.put(nextWord, 1);
            }
        }
        biGramTable = biGrams;
    }




    public void setCommonWords(ArrayList<String> common){
        commonWords = common;
    }
    public void setCommonWords(String commonWordsFilename) throws IOException {
        ArrayList<String> common = new ArrayList<>();
        InputStream inputStream = context.getAssets().open(commonWordsFilename);
        Scanner scanner = new Scanner(inputStream);
        String commonWord;
        while (scanner.hasNext()){
            commonWord = scanner.next();
            common.add(commonWord);
        }
        Collections.sort(common);
        commonWords = common;
    }

    public void clearData(){
        wordCount = 0;
        sentenceCount = 0;
        wordFreqTable = null;
        biGramTable = null;
    }

}

