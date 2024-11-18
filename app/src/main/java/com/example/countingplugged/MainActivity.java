package com.example.countingplugged;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.content.res.AssetManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private TextView welcome;
    private EditText enterFilename;
    private TextView wordCount;
    private TextView sentenceCount;
    private TextView numUniqueWords;
    private TextView wordCountDisp;
    private TextView sentenceCountDisp;
    private TextView numUniqueWordsDisp;
    private TextView top5;
    private TextView top5Disp;
    private Button getStats;
    private TextView errMsg;
    private Button savePDF;
    private Button goToNext;
    private Button getFindAndReplace;

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

        welcome = (TextView) findViewById(R.id.welcome);
        enterFilename = (EditText) findViewById(R.id.enterFilename);
        wordCount = (TextView) findViewById(R.id.wordCount);
        sentenceCount = (TextView) findViewById(R.id.sentenceCount);
        numUniqueWords = (TextView) findViewById(R.id.numUniqueWords);
        wordCountDisp = (TextView) findViewById(R.id.wordCountDisp);
        sentenceCountDisp = (TextView) findViewById(R.id.sentenceCountDisp);
        numUniqueWordsDisp = (TextView) findViewById(R.id.numUniqueWordsDisp);
        top5 = (TextView) findViewById(R.id.top);
        top5Disp = (TextView) findViewById(R.id.topDisp);
        errMsg = (TextView) findViewById(R.id.errMsg);
        getStats = (Button) findViewById(R.id.getStats);
        savePDF = (Button) findViewById(R.id.savePDF);
        goToNext = (Button) findViewById(R.id.goToNextPage);
        getFindAndReplace = (Button) findViewById(R.id.getFindAndReplace);

        goToNext.setVisibility(View.GONE);
        getFindAndReplace.setVisibility(View.GONE);

        getStats.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                resetDisplays();
                try {
                    //File reading logic
                    TextReader textReader = new TextReader(MainActivity.this);
                    String filename = enterFilename.getText().toString();
                    if (!fileExists(filename)){
                        setErrMsg("Please enter a file that exists!");
                        return;
                    }
                    errMsg.setText("");

                    if (filename.endsWith(".txt")){
                        textReader.readTextF(filename);
                    } else if (filename.endsWith(".pdf")){
                        textReader.readPDF(filename);
                    } else {
                        setErrMsg("Only text or pdf files accepeted!");
                        return;
                    }

                    //Getting stats using Analyzer + displaying them
                    Analyzer analyzer = new Analyzer(MainActivity.this, textReader, "commonWords.txt");
                    analyzer.analyze();
                    wordCountDisp.setText(Integer.toString(analyzer.getWordCount()));
                    sentenceCountDisp.setText(Integer.toString(analyzer.getSentenceCount()));
                    numUniqueWordsDisp.setText(Integer.toString(analyzer.getUniqueWords(true).size()));

                    List<Map.Entry<String, Integer>> top5 = analyzer.getMostFreqN(5, true);
                    Collections.reverse(top5);
                    String toShowTop5 = "";
                    for (Map.Entry<String, Integer> entry: top5){
                        toShowTop5 += entry.getKey() + " at " + entry.getValue() + " occurences\n\n";
                    }
                    top5Disp.setText(toShowTop5);

                    //CACHING DATA:
                    ((DataHolder) getApplication()).setBiGramTable(analyzer.getBiGramTable());
                    ((DataHolder) getApplication()).setTextReader(textReader);

                    //Displaying RandomParagraph and FindAndReplace buttons --> to new page
                    goToNext.setVisibility(View.VISIBLE);
                    getFindAndReplace.setVisibility(View.VISIBLE);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        goToNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, RandParaActivity.class);
                startActivity(intent);
            }

        });

        getFindAndReplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FindReplaceActivity.class);
                startActivity(intent);
            }
        });

        savePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = "";
                content += "Word Count: " + wordCountDisp.getText().toString() + "\n";
                content += "Sentence Count: " + sentenceCountDisp.getText().toString() + "\n";
                content += "Num Unique Words: " + numUniqueWordsDisp.getText().toString() + "\n";
                content += "Top 5 most frequent words:\n";
                content += top5Disp.getText().toString() + "\n";
                content += "Random Paragraph:\n";

                String randomParagraph = ((DataHolder) getApplication()).getRandomParagraph();

                PdfDocument pdfDoc = new PdfDocument();
                Paint paint = new Paint();
                paint.setTextSize(12);
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
                PdfDocument.Page page = pdfDoc.startPage(pageInfo);

                Canvas canvas = page.getCanvas();
                int yPos = 25;
                int xPos = 10;
                int pageWidth = pageInfo.getPageWidth() - 20;

                String[] lines = content.split("\n");
                for (String line: lines){
                    canvas.drawText(line, xPos, yPos, paint);
                    yPos += paint.descent() - paint.ascent();
                }

                String[] randParaWords = randomParagraph.split(" ");
                StringBuilder lineBuilder = new StringBuilder();
                for (String word: randParaWords){
                    float textWidth = paint.measureText(lineBuilder + word + " ");
                    if (textWidth > pageWidth){
                        canvas.drawText(lineBuilder.toString(), xPos, yPos, paint);
                        lineBuilder = new StringBuilder();
                        yPos += paint.descent() - paint.ascent();
                    }
                    lineBuilder.append(word).append(" ");
                }
                if (lineBuilder.length() > 0){
                    canvas.drawText(lineBuilder.toString(), xPos, yPos, paint);
                }

                pdfDoc.finishPage(page);

                String readingFileName = enterFilename.getText().toString();
                String writingFileName = readingFileName.substring(0, readingFileName.length()-4) + "_data.pdf";

                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), writingFileName);

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    pdfDoc.writeTo(fos);
                    Toast.makeText(MainActivity.this, "PDF saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public boolean fileExists(String f) throws IOException{
        AssetManager manager = getAssets();
        String[] files = manager.list("");
        for (String file: files){
            if (file.equals(f)) return true;
        }
        return false;
    }

    public void resetDisplays(){
        wordCountDisp.setText("...");
        sentenceCountDisp.setText("...");
        numUniqueWordsDisp.setText("...");
        top5Disp.setText("...");
    }

    public void setErrMsg(String msg){
        errMsg.setTextColor(Color.parseColor("#FF0000"));
        errMsg.setText(msg);
    }


}













