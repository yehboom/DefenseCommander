package com.example.defensecommander;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;



public class Missile{
    private MainActivity mainActivity;
    private ImageView imageView;
    private AnimatorSet aSet = new AnimatorSet();
    private int screenHeight;
    private int screenWidth;
    private long screenTime;
    private static final String TAG = "Plane";
    private boolean hit = false;
    private int startX;
    private int endX;
    private int endY;
    private int startY;

    private float image_x;
    private float image_y;


    Missile(int screenWidth, int screenHeight, long screenTime, final MainActivity mainActivity) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.screenTime = screenTime;
        this.mainActivity = mainActivity;

        imageView = new ImageView(mainActivity);
        imageView.setImageResource(R.drawable.missile);

        imageView.setY(-100);
        endY=(int) (Math.random() * screenHeight);

        startX = (int) (Math.random() * screenWidth);
        endX = (int) (Math.random() * screenWidth);
        startY=(-100);

        startX= startX-(imageView.getDrawable().getIntrinsicWidth()/2);
        startY= startY-(imageView.getDrawable().getIntrinsicWidth()/2);

        imageView.setX(startX);
        imageView.setY(startY);
        imageView.setZ(-10);

        float a = calculateAngle(startX, startY, endX, endY);
        imageView.setRotation(a-180);


    }

    AnimatorSet setData(final int drawId) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.getLayout().addView(imageView);
            }
        });



        final ObjectAnimator xAnim = ObjectAnimator.ofFloat(imageView, "x",  startX, endX);
        xAnim.setInterpolator(new LinearInterpolator());
        xAnim.setDuration(screenTime);

        final ObjectAnimator yAnim = ObjectAnimator.ofFloat(imageView, "y", -100, (screenHeight+100));

        yAnim.setInterpolator(new LinearInterpolator());
        yAnim.setDuration(screenTime);

        xAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                image_x=(Float) xAnim.getAnimatedValue();
                image_y=(Float) yAnim.getAnimatedValue();

                Float tempy=(Float) yAnim.getAnimatedValue();

                if (tempy >(screenHeight * 0.85)){

                    Log.d(TAG, "run: !!");
                    image_x=image_x+(float)(imageView.getDrawable().getIntrinsicWidth()*0.5);
                    image_y=image_y+(float)(imageView.getDrawable().getIntrinsicWidth()*0.5);

                    xAnim.cancel();
                    yAnim.cancel();
                    stop();
                    makeGroundBlast(image_x,image_y);
                    mainActivity.removeMissle(Missile.this);

                }

            }
        });

        xAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!hit) {
                            mainActivity.getLayout().removeView(imageView);
                        }
                        Log.d(TAG, "run: NUM VIEWS " +
                                mainActivity.getLayout().getChildCount());
                    }
                });

            }
        });

        aSet.playTogether(xAnim, yAnim);
        return aSet;

    }



    public float calculateAngle(double x1, double y1, double x2, double y2) {
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        // Keep angle between 0 and 360
        angle = angle + Math.ceil(-angle / 360) * 360;

        return (float) ( 370.0f-angle);
    }



    void stop() {
        Log.d(TAG, "stop: !");
        aSet.cancel();

    }

    float getX() {
        return imageView.getX();
    }

    float getY() {
        return imageView.getY();
    }

    float getWidth() {
        return imageView.getWidth();
    }

    float getHeight() {
        return imageView.getHeight();
    }

    void makeGroundBlast(float x, float y){
        final ImageView iv = new ImageView(mainActivity);
        iv.setImageResource(R.drawable.explode);

        SoundPlayer.getInstance().start("interceptor_blast");
        Log.d(TAG, "makeGroundBlast: !!!");
        iv.setTransitionName("Missile ground Blast");

        int w = imageView.getDrawable().getIntrinsicWidth();
        int offset = (int) (w * 0.5);

        iv.setX(x - offset);
        iv.setY(y - offset);
        iv.setRotation((float) (360.0 * Math.random()));

        mainActivity.getLayout().removeView(imageView);
        mainActivity.getLayout().addView(iv);
        stop();
        final ObjectAnimator alpha = ObjectAnimator.ofFloat(iv, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(imageView);
            }
        });
        alpha.start();

            mainActivity.applyMissileBlast(x,y);



    }


    void interceptorBlast(float x, float y) {
        mainActivity.removeMissle(this);

        final ImageView iv = new ImageView(mainActivity);
        iv.setImageResource(R.drawable.explode);

        iv.setTransitionName("Missile Intercepted Blast");

        int w = imageView.getDrawable().getIntrinsicWidth();
        int offset = (int) (w * 0.5);

        stop();
        aSet.cancel();


        iv.setX(x - offset);
        iv.setY(y - offset);
        iv.setRotation((float) (360.0 * Math.random()));
        //
        iv.setZ(-15);

        mainActivity.getLayout().removeView(imageView);
        mainActivity.getLayout().addView(iv);

        final ObjectAnimator alpha = ObjectAnimator.ofFloat(iv, "alpha", 0.0f);
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
