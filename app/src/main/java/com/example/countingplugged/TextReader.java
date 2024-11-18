package com.example.countingplugged;
import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class TextReader {
    private Context context;
    private String text;

    public TextReader(Context context) throws IOException{
        this.context = context;
    }

    public String getText() {
        return text;
    }

    public void readText(String newText){
        text = newText;
    }

    public void readTextF(String textFilename) throws IOException{
        //Read text into string, set TEXT field
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = context.getAssets().open(textFilename);
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNextLine()){
            stringBuilder.append(scanner.nextLine());
            stringBuilder.append("\n");
        }
        scanner.close();
        text = stringBuilder.toString();
    }

    public void readPDF(String pdfName) throws IOException{
        File file = new File(context.getFilesDir(), pdfName);
        PDDocument pdf = PDDocument.load(file);
        PDFTextStripper stripper = new PDFTextStripper();
        text = stripper.getText(pdf);
        pdf.close();
    }

    //Given current text field, get list of cleaned, non-common words
    public ArrayList<String> getWords(boolean needClean){
        String[] words = text.split("[ \\n]+|(?<=\\p{Punct})\\n");

        ArrayList<String> cleanedWords = new ArrayList<>();
        for (String word: words){
            if (needClean) cleanedWords.add(cleanWord(word));
            else cleanedWords.add(word);
        }
        return cleanedWords;
    }

    //Helper: Remove punctuation around a word, set to lower case
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

    public void resetText(){
        text = null;
    }

    public void findAndReplace(String find, String replaceWith, boolean caseSensitive){
        ArrayList<String> beforeWords = this.getWords(false);
        ArrayList<String> afterWords = new ArrayList<>();
        String replacement;
        String compareWord;
        String compareFind;
        for (String word: beforeWords){
            if (!caseSensitive){
                compareWord = word.toLowerCase();
                compareFind = find.toLowerCase();
            } else{
                compareWord = word;
                compareFind = find;
            }
            if (compareWord.contains(compareFind)){
                int startIndex = compareWord.indexOf(compareFind);
                int endIndex = startIndex + compareFind.length() - 1;
                replacement = word.substring(0, startIndex) + replaceWith + word.substring(endIndex+1);
                afterWords.add(replacement);
            } else {
                afterWords.add(word);
            }
        }
        this.text = String.join(" ", afterWords);
    }

    public void writeTextTo(String fname) throws IOException{
        try (FileOutputStream fos = context.openFileOutput(fname, context.MODE_PRIVATE)){
            fos.write(text.getBytes());
        }
    }













}
