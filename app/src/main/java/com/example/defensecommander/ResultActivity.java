package com.example.defensecommander;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class ResultActivity  extends AppCompatActivity {

    public static int screenHeight;
    public static int screenWidth;
    private static final String TAG = "ResultActivity";
    public TextView result;
    private String level;
    private String scoreValue;
    private String name="";


    @Override
    public void onResume(){
        super.onResume();
        setupFullScreen();
    }


    @Override
    public void onPause() {
        super.onPause();
        setupFullScreen();
    }

    @Override
    public void onStop() {
        super.onStop();
        setupFullScreen();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setupFullScreen();
        Intent i= this.getIntent();
        scoreValue = i.getStringExtra("scoreValue");
        level=i.getStringExtra("level");


        SoundPlayer.getInstance().setupSound(this, "background", R.raw.background);
        Button exitClick = (Button) findViewById(R.id.exit);


        exitClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClick((Button) view);
            }

        });
        executeSql();

        result = findViewById(R.id.ExitTitle);
        result.setTypeface(Typeface.MONOSPACE);


    }

    public void executeSql(){
        DatabaseTopScoresAsync dbh = new DatabaseTopScoresAsync(this);
        dbh.execute(scoreValue,level,name);
    }

    public void setResult2(String s1){
        name=s1;
        executeSql();
    }


    public void setResults(String s) {

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%3s %7s %7s %7s %14s%n","\u0023","Init", "Level","Score", "Data/Time"));
        sb.append(s);

        result.setText(sb);
    }

    public void onButtonClick(Button view) {
        finish();
        System.exit(0);
    }

    private void setupFullScreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }


}