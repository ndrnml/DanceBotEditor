package ch.ethz.asl.dancebots.danceboteditor.handlers;


import android.os.Handler;

/**
 * Created by andrin on 22.10.15.
 */
public class AutomaticScrollHandler implements Runnable {

    private Handler mScrollHandler = new Handler();

    public AutomaticScrollHandler() {

    }

    public void startListening() {
        mScrollHandler.postDelayed(this, 100);
    }

    public void stopListening() {
        mScrollHandler.removeCallbacks(this);
    }

    @Override
    public void run() {

    }

}
