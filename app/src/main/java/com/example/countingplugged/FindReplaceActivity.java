package com.example.countingplugged;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FindReplaceActivity extends AppCompatActivity {
    private Button back;
    private EditText findWord;
    private EditText replaceWithWord;
    private TextView newTextPreview;
    private TextView newTextPreviewDisp;
    private EditText saveToFile;
    private Button saveNewText;
    private Button execute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_find_replace);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        back = (Button) findViewById(R.id.BackFromFindReplace);
        findWord = (EditText) findViewById(R.id.Find);
        replaceWithWord = (EditText) findViewById(R.id.replaceWith);
        newTextPreview = (TextView) findViewById(R.id.newTextPreview);
        newTextPreviewDisp = (TextView) findViewById(R.id.newTextPreviewDisp);
        saveToFile = (EditText) findViewById(R.id.saveToFile);
        saveNewText = (Button) findViewById(R.id.saveNewText);
        execute = (Button) findViewById(R.id.execute);

        Intent intent = getIntent();



        execute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Getting replaced text
                String find = findWord.getText().toString();
                String replaceWith = replaceWithWord.getText().toString();
                TextReader textReader = ((DataHolder) getApplication()).getTextReader();
                textReader.findAndReplace(find, replaceWith, false);
                String newText = textReader.getText();
                if (newText.length()>700) newTextPreviewDisp.setText(newText.substring(0,700));
                else newTextPreviewDisp.setText(newText);

                //CACHING DATA
                ((DataHolder) getApplication()).setTextReader(textReader);
            }
        });



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        saveNewText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextReader textReader = ((DataHolder) getApplication()).getTextReader();
                String filename = saveToFile.getText().toString();
                writeToFile(filename, textReader.getText());
            }
        });

    }



    private void writeToFile(String fileName, String content) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content.getBytes());
            Toast.makeText(this, "File saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving file", Toast.LENGTH_SHORT).show();
        }
    }





}