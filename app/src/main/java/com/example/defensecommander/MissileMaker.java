package com.example.defensecommander;

import android.animation.AnimatorSet;
import android.util.Log;

import java.util.ArrayList;

import static com.example.defensecommander.Interceptor.INTERCEPTOR_BLAST;
import static com.example.defensecommander.MainActivity.countInterecptor;


import static com.example.defensecommander.MainActivity.base01_x;
import static com.example.defensecommander.MainActivity.base02_x;
import static com.example.defensecommander.MainActivity.base03_x;
import static com.example.defensecommander.MainActivity.base01_y;
import static com.example.defensecommander.MainActivity.base02_y;
import static com.example.defensecommander.MainActivity.base03_y;
import static com.example.defensecommander.MainActivity.base01;
import static com.example.defensecommander.MainActivity.base02;
import static com.example.defensecommander.MainActivity.base03;

public class MissileMaker implements Runnable{
    private static final String TAG = "MissleMaker";

    private MainActivity mainActivity;
    private boolean isRunning;
    private ArrayList<Missile> activeMissile = new ArrayList<>();
    private int screenWidth, screenHeight;
    //private static final int NUM_LEVELS = 5;
    private static final int LEVEL_COUNT = 5;
    private int count = 0;
    private int level = 1;
    long missileTime;


    MissileMaker(MainActivity mainActivity, int screenWidth, int screenHeight) {
        this.mainActivity = mainActivity;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    void setRunning(boolean running) {
        isRunning = running;
        ArrayList<Missile> temp = new ArrayList<>(activeMissile);
        for (Missile p : temp) {
            p.stop();
        }
    }

    @Override
    public void run() {

        setRunning(true);
        long delay = LEVEL_COUNT * 1000;
        missileTime = (long) ((delay * 0.5) + (Math.random() * delay));
        try {
            Thread.sleep((long) (missileTime));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        while (isRunning) {

            count++;
            final Missile missile = new Missile(screenWidth, screenHeight, missileTime, mainActivity);
            activeMissile.add(missile);
            final AnimatorSet as = missile.setData(R.drawable.missile);

           //making missle
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    as.start();
                }
            });


            if (count > LEVEL_COUNT) {
                increaseLevel();
                mainActivity.setLevel(level);
                Log.d(TAG, "run: LEVEL NOW " + level);
                delay -= 250;

                if (delay < 250)
                    delay = 250;

                Log.d(TAG, "run: DELAY NOW " + delay);
                count = 0;
            }
            try {
                Thread.sleep((long) (getSleepTime()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



        }
    }

    private double getSleepTime(){
        double sleepTime =  Math.random();
        if (sleepTime<0.1){
            return 1.0;
        }else if (sleepTime<0.2){
            return (0.5* missileTime);
        }else{
            return missileTime;
        }
    }

    private void increaseLevel(){
        level++;
        if((missileTime-500)<0){
            missileTime=(long) 0.001;
        }
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.setLevel(level);
            }
        });
        try {
            Thread.sleep((long) (2));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    void removeMissle(Missile p) {
        activeMissile.remove(p);
    }

    void applyInterceptorBlast(Interceptor interceptor, int id) {

        countInterecptor--;
        Log.d(TAG, "applyInterceptorBlast: -------------------------- " + id);

        float x1 = interceptor.getX();
        float y1 = interceptor.getY();


        float dwithbase01 = (float) Math.sqrt((base01_y - y1) * (base01_y - y1) + (base01_x - x1) * (base01_x - x1));
        float dwithbase02 = (float) Math.sqrt((base02_y - y1) * (base02_y - y1) + (base02_x - x1) * (base02_x - x1));
        float dwithbase03 = (float) Math.sqrt((base03_y - y1) * (base03_y - y1) + (base03_x - x1) * (base03_x - x1));


        if(dwithbase01<100){
            Base base_d=new Base(mainActivity,screenWidth,screenHeight,base01);
            base01=null;
            mainActivity.stop();
            SoundPlayer.getInstance().start("base_blast");
        }else if (dwithbase02<100){
            mainActivity.stop();
            Base base_d=new Base(mainActivity,screenWidth,screenHeight,base02);
            base02=null;
            mainActivity.stop();
            SoundPlayer.getInstance().start("base_blast");
        }else if (dwithbase03<100){
            Base base_d=new Base(mainActivity,screenWidth,screenHeight,base03);
            base03=null;
            mainActivity.stop();
            SoundPlayer.getInstance().start("base_blast");
        }


        Log.d(TAG, "applyInterceptorBlast: INTERCEPTOR: " + x1 + ", " + y1);

        ArrayList<Missile> nowGone = new ArrayList<>();
        ArrayList<Missile> temp = new ArrayList<>(activeMissile);

        for (Missile m : temp) {

            float x2 = (int) (m.getX() + (0.5 * m.getWidth()));
            float y2 = (int) (m.getY() + (0.5 * m.getHeight()));

            Log.d(TAG, "applyInterceptorBlast:    Missile: " + x2 + ", " + y2);


            float f = (float) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
            Log.d(TAG, "applyInterceptorBlast:    DIST: " + f);

            if (f < INTERCEPTOR_BLAST) {

                SoundPlayer.getInstance().start("interceptor_blast");
                mainActivity.incrementScore();
                Log.d(TAG, "applyInterceptorBlast:    Hit: " + f);
                m.interceptorBlast(x2, y2);
                nowGone.add(m);
            }

            Log.d(TAG, "applyInterceptorBlast: --------------------------");


        }

        for (Missile m : nowGone) {
            activeMissile.remove(m);
        }
    }
}
