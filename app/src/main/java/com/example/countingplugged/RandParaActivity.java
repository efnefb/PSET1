package com.example.countingplugged;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;

public class RandParaActivity extends AppCompatActivity {
    private Button getRandPara;
    private TextView randPara;
    private EditText selectTemp;
    private EditText chooseNumWords;
    private TextView errMsg;
    private TextView errMsg2;
    private TextView errMsg3;
    private Button back;

    HashMap<String, HashMap<String, Integer>> biGramTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rand_para);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getRandPara = (Button) findViewById(R.id.getRandpara);
        randPara = (TextView) findViewById(R.id.randPara);
        selectTemp = (EditText) findViewById(R.id.selectTemp);
        errMsg = (TextView) findViewById(R.id.errMsg);
        errMsg2 = (TextView) findViewById(R.id.errMsg2);
        errMsg3 = (TextView) findViewById(R.id.errMsg3);
        chooseNumWords = (EditText) findViewById(R.id.chooseNumWords);
        back = (Button) findViewById(R.id.BackFromRandPara);

        Intent intent = getIntent();

        getRandPara.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                resetDisplays();
                try {
                    //Getting temp, numWords logic
                    setErrMsg("", errMsg3);
                    int temp = Integer.parseInt(selectTemp.getText().toString());
                    if (temp < 1 || temp > 5) {
                        setErrMsg("Must be from 1-5!", errMsg);
                        return;
                    }
                    setErrMsg("", errMsg);
                    int numWords = Integer.parseInt(chooseNumWords.getText().toString());
                    if (numWords > 100) {
                        setErrMsg("At most 100 words!", errMsg2);
                        return;
                    }
                    setErrMsg("", errMsg2);

                    //Getting randomParagraph
                    HashMap<String, HashMap<String, Integer>> biGramTable =
                            ((DataHolder) getApplication()).getBiGramTable();
                    String randomParagraph = Generator.getRandomParagraph(temp, biGramTable, numWords);
                    randPara.setText(randomParagraph);

                    //CACHING DATA
                    ((DataHolder) getApplication()).setRandomParagraph(randomParagraph);

                } catch (NumberFormatException e){
                    setErrMsg("Enter a valid number!", errMsg3);
                    Log.d("fuckfuck", e.toString());
                } catch (StringIndexOutOfBoundsException e){
                    randPara.setTextColor(Color.parseColor("#FF0000"));
                    randPara.setText("Unknown issue was encountered. Please click again.");
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });

    }

    public void setErrMsg(String msg, TextView err){
        err.setTextColor(Color.parseColor("#FF0000"));
        err.setText(msg);
    }

    public void resetDisplays(){
        setErrMsg("", errMsg);
        setErrMsg("", errMsg2);
        setErrMsg("", errMsg3);
        randPara.setText("");
        randPara.setTextColor(Color.parseColor("#000000"));
    }






}