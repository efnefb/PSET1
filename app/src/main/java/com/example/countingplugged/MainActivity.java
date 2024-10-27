package com.example.countingplugged;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;

import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

//Issue is reading filename from UI, then using filename to read file
//Can read filename from UI
//**Reading the file using user inputted filename is the problem**
public class MainActivity extends AppCompatActivity {
    private TextView display;
    private EditText filename;
    private Button mostCommon;
    private Button top5;
    private String output;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        display = (TextView) findViewById(R.id.textView);
        filename = (EditText) findViewById(R.id.editTextText);
        mostCommon = (Button) findViewById(R.id.button);
        top5 = (Button) findViewById(R.id.button2);


        mostCommon.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String fname = filename.getText().toString();

                try {
                    ArrayList<String> commonWords = Counter.getCommonWords(MainActivity.this, "commonWords.txt");
                    ArrayList<String> text = Counter.readText(MainActivity.this, fname, commonWords);
                    HashMap<String, Integer> freqTable = Counter.getFrequencies(text);
                    ArrayList<String> rank = Counter.sortWordsByFreq(freqTable);
                    ArrayList<Integer> rank_freqs = Counter.getCorrespondingFrequencies(rank, freqTable);

                    String mostCommonWord = rank.get(rank.size()-1);
                    Integer mostCommonWordFreq = rank_freqs.get(rank_freqs.size()-1);
                    String msg = "The most common word in \"" + fname + "\" is \"" + mostCommonWord + "\" with " + mostCommonWordFreq + " occurences";
                    display.setText(msg);


                } catch (IOException e){
                    e.printStackTrace();
                }

            }
        });


        top5.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String fname = filename.getText().toString();

                try {
                    ArrayList<String> commonWords = Counter.getCommonWords(MainActivity.this, "commonWords.txt");
                    ArrayList<String> text = Counter.readText(MainActivity.this, fname, commonWords);
                    HashMap<String, Integer> freqTable = Counter.getFrequencies(text);
                    ArrayList<String> rank = Counter.sortWordsByFreq(freqTable);
                    ArrayList<Integer> rank_freqs = Counter.getCorrespondingFrequencies(rank, freqTable);

                    List<String> top5Words = rank.subList(rank.size()-5, rank.size());
                    List<Integer> top5WordsFreqs = rank_freqs.subList(rank_freqs.size()-5, rank_freqs.size());

                    String msg0 = "The top 5 most common words in \"" + fname + "\" are:\n\n";
                    String msg1 = "1. \"" + top5Words.get(4) + "\" with " + top5WordsFreqs.get(4) + " occurences\n";
                    String msg2 = "2. \"" + top5Words.get(3) + "\" with " + top5WordsFreqs.get(4) + " occurences\n";
                    String msg3 = "3. \"" + top5Words.get(2) + "\" with " + top5WordsFreqs.get(4) + " occurences\n";
                    String msg4 = "4. \"" + top5Words.get(1) + "\" with " + top5WordsFreqs.get(1) + " occurences\n";
                    String msg5 = "5. \"" + top5Words.get(0) + "\" with " + top5WordsFreqs.get(0) + " occurences\n";
                    String msg = msg0 + msg1 + msg2 + msg3 + msg4 + msg5;
                    display.setText(msg);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });

    }



}













