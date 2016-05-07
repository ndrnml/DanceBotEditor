package ch.ethz.asl.dancebots.danceboteditor.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import ch.ethz.asl.dancebots.danceboteditor.R;

public class SplashScreenActivity extends Activity {

    private static final String LOG_TAG = SplashScreenActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        /** set time to splash out */
        final int welcomeScreenDisplay = 3000;
        final int stepSize = 500;
        /** create a thread to show splash up to splash time */

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(welcomeScreenDisplay);

        TextView textView = (TextView) findViewById(R.id.splash_intro);
        textView.startAnimation(fadeIn);

        Thread welcomeThread = new Thread() {

            int wait = 0;

            @Override
            public void run() {
                try {
                    super.run();
                    /**
                     * use while to get the splash time. Use sleep() to increase
                     * the wait variable for every 100L.
                     */
                    while (wait < welcomeScreenDisplay) {
                        sleep(stepSize);
                        wait += stepSize;
                    }
                } catch (Exception e) {

                    Log.i(LOG_TAG, e.toString());

                } finally {
                    /**
                     * Called after splash times up. Do some action after splash
                     * times up. Here we moved to another main activity class
                     */
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    finish();
                }
            }
        };

        welcomeThread.start();
    }
}
