package ch.ethz.asl.dancebots.danceboteditor.handlers;


import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

/**
 * Created by andrin on 22.10.15.
 */
public class AutomaticScrollHandler implements Runnable {

    // TODO FINISH THIS CLASS, make use of stopListening
    private static final String LOG_TAG = "SCROLL_HANDLER";

    private final int TIME_TO_LIVE = 2000;

    private long mLastInteraction;

    private final Handler mScrollHandler;

    private ScrollViewMethods mBeatViews;

    private ScrollMediaPlayerMethods mDanceBotMediaPlayer;

    /**
     * INTERFACE
     */
    public interface ScrollViewMethods {

    }

    /**
     * INTERFACE
     */
    public interface ScrollMediaPlayerMethods {

        void preparePlayback();

        boolean isPlaying();

        int getCurrentPosition();

        void setSeekBarProgress(int progress);

        View getSeekBarView();
    }

    public AutomaticScrollHandler(ScrollViewMethods beatViews, ScrollMediaPlayerMethods mediaPlayer) {

        mBeatViews = beatViews;
        mDanceBotMediaPlayer = mediaPlayer;

        mScrollHandler = new Handler();

        mLastInteraction = System.currentTimeMillis();

        startListening();
    }

    public void startListening() {
        mScrollHandler.postDelayed(this, 0);
    }

    private void stopListening() {
        mScrollHandler.removeCallbacks(this);
    }

    @Override
    public void run() {

        Log.d(LOG_TAG, "run automatic scroll handler");

        if (System.currentTimeMillis() - mLastInteraction > TIME_TO_LIVE) {
            stopListening();
            return;
        }

        // Update seek bar
        int currentTime = mDanceBotMediaPlayer.getCurrentPosition();
        mDanceBotMediaPlayer.setSeekBarProgress(currentTime);

/*
        if (mIsPlaying || mSeekbarChanged) {

            mSeekbarChanged = false;

            // Automatic scrolling of beat element views
            int currentBeatElement = (int) (((float) mNumBeats / (float) mTotalTime) * (float) currentTime);

            LinearLayoutManager llm = (LinearLayoutManager) mMotorView.getLayoutManager();
            llm.scrollToPositionWithOffset(currentBeatElement, 20);
            //mMotorView.scrollToPosition(currentBeatElement);

            LinearLayoutManager llm2 = (LinearLayoutManager) mLedView.getLayoutManager();
            llm2.smoothScrollToPosition(mLedView, null, currentBeatElement);
            //mLedView.scrollToPosition(currentBeatElement);

            // TODO

                BeatElementAdapter adapter = (BeatElementAdapter) mMotorView.getAdapter();

                // TODO
                if (currentBeatElement > 0 && currentBeatElement < adapter.getItemCount()) {
                    adapter.getItem(currentBeatElement).setFocus(true);
                }
                if (currentBeatElement - 1 > 0 && currentBeatElement - 1 < adapter.getItemCount()) {
                    adapter.getItem(currentBeatElement - 1).setFocus(false);
                }

            //Log.d(LOG_TAG, "update scroll to element: " + currentBeatElement);

        }
*/
        mScrollHandler.postDelayed(this, 200);
    }
}
