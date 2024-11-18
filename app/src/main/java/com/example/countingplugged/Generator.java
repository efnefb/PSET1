package com.example.countingplugged;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Generator {

    public static HashMap<String, HashMap<String, Integer>> adjustedTemp(int temp, HashMap<String, HashMap<String, Integer>> original) { // temp = 1, 2, 3, 4, 5
        HashMap<String, HashMap<String, Integer>> origCopy = new HashMap<>(original);
        int lowLim = temp + 1;
        int highLim;
        if (temp <= 3) highLim = 2 * lowLim;
        else highLim = 3 * lowLim;
        origCopy.entrySet().removeIf(entry -> entry.getKey().length() < lowLim || entry.getKey().length() > highLim);
        for (HashMap<String, Integer> value : origCopy.values()) {
            value.entrySet().removeIf(entry -> entry.getKey().length() < lowLim || entry.getKey().length() > highLim);
        }
        return origCopy;
    }

    public static String getRandomParagraph(int temp, HashMap<String, HashMap<String, Integer>> biGramTable, int numWords) {
        HashMap<String, HashMap<String, Integer>> tempAdjTable = adjustedTemp(temp, biGramTable);

        int sentLowLim = 5 * (temp - 1);
        int sentHighLim = 5 * temp;
        Random random = new Random();

        StringBuilder randText = new StringBuilder();

        List<String> firstWords = new ArrayList<>(tempAdjTable.keySet());

        int bigWordCounter = 0;

        while (bigWordCounter < numWords) {
            int sentenceLength = sentLowLim + random.nextInt(sentHighLim - sentLowLim);
            int sentWordCounter = 0;

            String firstWord = firstWords.get(random.nextInt(firstWords.size()));
            int repetitions = 0;

            while (sentWordCounter < sentenceLength && bigWordCounter < numWords) {
                HashMap<String, Integer> secondWordsMap = tempAdjTable.get(firstWord);
                while (secondWordsMap.isEmpty() || secondWordsMap == null) {
                    firstWord = firstWords.get(random.nextInt(firstWords.size()));
                    secondWordsMap = tempAdjTable.get(firstWord);
                    if (randText != null && randText.charAt(randText.length() - 1) != ',' && randText.charAt(randText.length() - 1) != '.') {
                        randText.append(",");
                    }
                }
                randText.append(" " + firstWord);
                bigWordCounter++;
                sentWordCounter++;

                String secondWord = sampleSecondWord(secondWordsMap);
                if (repetitions >= 1) {
                    while (firstWord.equals(secondWord)) {
                        secondWord = firstWords.get(random.nextInt(firstWords.size()));
                    }
                }
                if (firstWord.equals(secondWord)) repetitions++;

                firstWord = secondWord;
            }
            randText.append(".");
        }

        return capitalize(randText.toString().substring(1));
    }

    public static String sampleSecondWord(HashMap<String, Integer> secondWordsMap) {
        Random random = new Random();
        String secondWordChosen;
        int totalWeight = 0;
        for (Integer value : secondWordsMap.values()) totalWeight += value;
        int randIdx = random.nextInt(totalWeight);
        int iterSum = 0;
        for (Map.Entry<String, Integer> entry : secondWordsMap.entrySet()) {
            iterSum += entry.getValue();
            if (iterSum > randIdx) {
                secondWordChosen = entry.getKey();
                return secondWordChosen;
            }
        }
        return null;
    }

    public static String capitalize(String text) {
        Pattern pattern = Pattern.compile("((?<=\\.\\s)|^)(\\w)");
        Matcher matcher = pattern.matcher(text);
        StringBuffer capitalizedText = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(capitalizedText, matcher.group(2).toUpperCase());
        }
        matcher.appendTail(capitalizedText);
        return capitalizedText.toString();
    }



}
