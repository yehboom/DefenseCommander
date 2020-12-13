package com.example.defensecommander;


import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import static com.example.defensecommander.MainActivity.screenHeight;
import static com.example.defensecommander.MainActivity.screenWidth;

public class CloudScroll implements Runnable{  private Context context;
    private ViewGroup layout;
    private ImageView backImageA;
    private ImageView backImageB;
    private long duration;
    private int resId;
    private static final String TAG = "ParallaxBackground";


    CloudScroll(Context context, ViewGroup layout, int resId, long duration) {

        this.context = context;
        this.layout = layout;
        this.resId = resId;
        this.duration = duration;

        setupBackground();

    }

    private void setupBackground() {
        backImageA = new ImageView(context);
        backImageB = new ImageView(context);

        LinearLayout.LayoutParams params = new LinearLayout
                .LayoutParams(screenWidth + getBarHeight(), screenHeight);
        backImageA.setLayoutParams(params);
        backImageB.setLayoutParams(params);

        layout.addView(backImageA);
        layout.addView(backImageB);

        Bitmap backBitmapA = BitmapFactory.decodeResource(context.getResources(), resId);
        Bitmap backBitmapB = BitmapFactory.decodeResource(context.getResources(), resId);

        backImageA.setImageBitmap(backBitmapA);
        backImageB.setImageBitmap(backBitmapB);

        backImageA.setScaleType(ImageView.ScaleType.FIT_XY);
        backImageB.setScaleType(ImageView.ScaleType.FIT_XY);

        backImageA.setZ(-1);
        backImageB.setZ(-1);

        AlphaAnimation alpha = new AlphaAnimation(0.9F, 0.25F);
        alpha.setDuration(2000);
        alpha.setFillAfter(true);
        backImageA.startAnimation(alpha);
        backImageB.startAnimation(alpha);
        alpha.setRepeatMode(AlphaAnimation.REVERSE);
        alpha.setRepeatCount(AlphaAnimation.INFINITE);
        alpha.setInterpolator(new ReverseInterpolator());

        animateBack();



    }


    public class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float paramFloat) {
            return Math.abs(paramFloat -1f);
        }
    }
    @Override
    public void run() {

        backImageA.setX(0);
        backImageB.setX(-(screenWidth + getBarHeight()));
        double cycleTime = 25.0;

        double cycles = duration / cycleTime;
        double distance = (screenWidth + getBarHeight()) / cycles;

        boolean isRunning = true;
        while (isRunning) {

            long start = System.currentTimeMillis();

            double aX = backImageA.getX() - distance;
            backImageA.setX((float) aX);
            double bX = backImageB.getX() - distance;
            backImageB.setX((float) bX);

            long workTime = System.currentTimeMillis() - start;

            if (backImageA.getX() < -(screenWidth + getBarHeight()))
                backImageA.setX((screenWidth + getBarHeight()));

            if (backImageB.getX() < -(screenWidth + getBarHeight()))
                backImageB.setX((screenWidth + getBarHeight()));

            long sleepTime = (long) (cycleTime - workTime);

            if (sleepTime <= 0) {
                Log.d(TAG, "run: NOT KEEPING UP! " + sleepTime);
                continue;
            }

            try {
                Thread.sleep((long) (cycleTime - workTime));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

    }

    private void animateBack() {

        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(duration);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                float width = screenWidth + getBarHeight();

                float a_translationX = width * progress;
                float b_translationX = width * progress - width;

                backImageA.setTranslationX(a_translationX);
                backImageB.setTranslationX(b_translationX);


            }
        });
        animator.start();




    }


    private int getBarHeight() {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

}
