package com.example.defensecommander;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.util.ArrayList;

public class Base {

    private  MainActivity mainActivity;
    private ArrayList<Missile> activeMissile = new ArrayList<>();
    private int screenWidth, screenHeight;

    private ImageView base;
    private ImageView imageView;
    private static final String TAG = "Base";


    Base(MainActivity mainActivity, int screenWidth, int screenHeight, ImageView base) {
        if(base==null){
            return;
        }
        this.mainActivity = mainActivity;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.base=base;

        mainActivity.getLayout().removeView(base);
        imageView = new ImageView(mainActivity);
        imageView.setImageResource(R.drawable.blast);
        imageView.setX(base.getX());
        imageView.setY(base.getY());
        mainActivity.getLayout().addView(imageView);
        setData();

    }

    public void setData(){

        final ObjectAnimator alpha = ObjectAnimator.ofFloat(imageView, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(imageView);
            }
        });
        alpha.start();
    }

}
