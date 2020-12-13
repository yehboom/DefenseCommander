package com.example.defensecommander;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import android.content.pm.ActivityInfo;

import android.util.DisplayMetrics;

import android.view.WindowManager;


import java.util.Locale;




public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ConstraintLayout layout;
//    private int screenHeight;
//    private int screenWidth;
    private static final int SPLASH_TIME_OUT = 3000;

    public static int screenHeight;
    public static int screenWidth;
   // private static final int setToUse = 1;
    private MissileMaker missileMaker;
    private ImageView launcher;
    private int scoreValue;
    private TextView score, level;
    static ImageView base01;
    static ImageView base02;
    static ImageView base03;

    static Double base01_x;
    static Double base01_y;
    static Double base02_x;
    static Double base02_y;
    static Double base03_x;
    static Double base03_y;
    private Double base01D;
    private Double base02D;
    private Double base03D;
    private ImageView gameover;
    private boolean allgone;
    static int countInterecptor=0;
    private int levelnum;

    @Override
    public void onStart(){
        super.onStart();
        setupFullScreen();
    }

    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
        setupFullScreen();

    }


    @Override
    public void onPause() {
        super.onPause();
        setupFullScreen();

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setupFullScreen();
        getScreenDimensions();

        layout = findViewById(R.id.layout);
        score = findViewById(R.id.score);
        level = findViewById(R.id.levelText);
        base01=findViewById(R.id.base01);
        base02=findViewById(R.id.base02);
        base03=findViewById(R.id.base03);
        gameover=findViewById(R.id.gameover);

        gameover.setVisibility(View.INVISIBLE);


            layout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        handleTouch(motionEvent.getX(), motionEvent.getY());
                    }
                    return false;
                }
            });


        SoundPlayer.getInstance().setupSound(this, "interceptor_blast", R.raw.interceptor_blast);
        SoundPlayer.getInstance().setupSound(this, "base_blast", R.raw.base_blast);
        SoundPlayer.getInstance().setupSound(this, "launch_interceptor", R.raw.launch_interceptor);



        new CloudScroll(this, layout, R.drawable.clouds, 30000);

        missileMaker = new MissileMaker(this, screenWidth, screenHeight);
        new Thread(missileMaker).start();
    }


    private void fadeInAndHideImage(final ImageView img)
    {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(1000);

        fadeIn.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
                img.setVisibility(View.VISIBLE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });

        img.startAnimation(fadeIn);
    }


    public ConstraintLayout getLayout() {
        return layout;
    }

    public void incrementScore() {
        scoreValue++;
        score.setText(String.format(Locale.getDefault(), "%d", scoreValue));
    }

    public void setLevel(final int value) {
        levelnum=value;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                level.setText(String.format(Locale.getDefault(), "Level: %d", value));
            }
        });
    }


    public void removeMissle(Missile m) {
        missileMaker.removeMissle(m);
    }


    public void handleTouch(float x1, float y1) {

        if (allgone == false && countInterecptor<=2) {

            findBase(x1,y1);
            SoundPlayer.getInstance().start("launch_interceptor");
            Interceptor i;
            if (base01D <= base02D && base01D <= base03D) {
                i = new Interceptor(this, (float) (base01_x - 20), (float) (screenHeight - 50), x1, y1);
                i.launch();
                countInterecptor++;
            } else if (base02D <= base01D && base02D <= base03D) {
                i = new Interceptor(this, (float) (base02_x - 20), (float) (screenHeight - 50), x1, y1);
                i.launch();
                countInterecptor++;
            } else if (base03D <= base02D && base03D <= base01D) {
                i = new Interceptor(this, (float) (base03_x - 20), (float) (screenHeight - 50), x1, y1);
                i.launch();
                countInterecptor++;
            }
        }

    }

    private void findBase(Float x1, Float y1){

        if (base01 ==null && base02 ==null && base03 == null){
            stop();
            return;
        }
        if (base01 == null){
            base01_x =6000.0;
            base01_y =6000.0;
        }else{
            base01_x= base01.getX()+(0.5 * base01.getWidth());
            base01_y= base01.getY()+(0.5 * base01.getHeight());
        }

        if (base02 == null){
            base02_x =6000.0;
            base02_y =6000.0;
        }else{
            base02_x= base02.getX()+(0.5 * base02.getWidth());
            base02_y= base02.getY()+(0.5 * base02.getHeight());
        }

        if (base03 == null){
            base03_x =6000.0;
            base03_y =6000.0;
        }else{
            base03_x= base03.getX()+(0.5 * base03.getWidth());
            base03_y= base03.getY()+(0.5 * base03.getHeight());
        }


        base01D=Math.sqrt((base01_x-x1)*(base01_x-x1)+(base01_y-y1)*(base01_y-y1));
        base02D=Math.sqrt((base02_x-x1)*(base02_x-x1)+(base02_y-y1)*(base02_y-y1));
        base03D=Math.sqrt((base03_x-x1)*(base03_x-x1)+(base03_y-y1)*(base03_y-y1));


    }

    private void getScreenDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
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


    public void applyInterceptorBlast(Interceptor interceptor, int id) {
        missileMaker.applyInterceptorBlast(interceptor, id);
    }




    public void applyMissileBlast(Float x1,Float y1){
        findBase(x1,y1);

        if(allgone==false){

        //ORIGINAL NEED SET TO 250, BUT I THINK 250 IS TOO LARGE, SO I SET TO 160
        if (base01D <= base02D && base01D <= base03D){
            if (base01D <160){
                Base base_d=new Base(this,screenWidth,screenHeight,base01);
                SoundPlayer.getInstance().start("base_blast");
                base01=null;
            }
        }else if (base02D <= base01D && base02D <= base03D){
            if (base02D <160){
                Base base_d=new Base(this,screenWidth,screenHeight,base02);
                SoundPlayer.getInstance().start("base_blast");
                base02=null;
            }

        }else if (base03D <= base02D && base03D <= base01D){
            if (base03D <160){
                Base base_d=new Base(this,screenWidth,screenHeight,base03);
                SoundPlayer.getInstance().start("base_blast");
                base03=null;
            }

        }
        }

        if (base01==null && base02==null && base03==null){
            stop();
        }

    }


    public void stop(){
        if (base01==null && base02==null && base03==null){
            missileMaker.setRunning(false);
            fadeInAndHideImage(gameover);
            allgone=true;


            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    Intent i = new Intent(MainActivity.this, ResultActivity.class);
                    i.putExtra("scoreValue", String.valueOf(scoreValue));
                    i.putExtra("level",String.valueOf(levelnum));
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    // close this activity
                    finish();
                }
            }, SPLASH_TIME_OUT);

        }
    }
}
