package com.example.countingplugged;

import android.app.Application;

import java.util.HashMap;

public class DataHolder extends Application {
    private HashMap<String, HashMap<String, Integer>> biGramTable;
    private TextReader textReader;
    String randomParagraph;



    public HashMap<String, HashMap<String, Integer>> getBiGramTable() {
        return biGramTable;
    }

    public void setBiGramTable(HashMap<String, HashMap<String, Integer>> biGramTable) {
        this.biGramTable = biGramTable;
    }

    public TextReader getTextReader(){
        return textReader;
    }
    public void setTextReader(TextReader textReader){
        this.textReader = textReader;
    }

    public String getRandomParagraph(){
        return randomParagraph;
    }

    public void setRandomParagraph(String randomParagraph){
        this.randomParagraph = randomParagraph;
    }





}
