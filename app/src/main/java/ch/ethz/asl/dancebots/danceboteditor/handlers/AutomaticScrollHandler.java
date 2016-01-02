package ch.ethz.asl.dancebots.danceboteditor.handlers;


import android.os.Handler;
import android.util.Log;
import android.view.View;

/**
 * Created by andrin on 22.10.15.
 */
public class AutomaticScrollHandler implements Runnable {

    // TODO FINISH THIS CLASS, make use of stopListening
    private static final String LOG_TAG = "SCROLL_HANDLER";

    private final int TIME_TO_LIVE = 10000;

    private long mLastInteraction;

    private final Handler mScrollHandler;

    private ScrollViewMethods mScrollViews;

    private ScrollMediaPlayerMethods mDanceBotMediaPlayer;

    private boolean mIsRunning;

    private int mSeekBarProgress;

    /**
     * INTERFACE
     */
    public interface ScrollViewMethods {

        void scrollToPosition(int position);

        int getNumElements();
    }

    /**
     * INTERFACE
     */
    public interface ScrollMediaPlayerMethods {

        boolean isPlaying();

        void setSeekBarProgress(int progress);

        int getSeekBarProgress();

        int getCurrentPosition();

        View getSeekBarView();

        int getTotalTime();
    }

    /**
     * AutomaticScrollHandler creates a self removing scroll handler that updates the media
     * player, the seek bar state and the scroll state
     * Handler removes itself, when user is inactive
     * @param scrollViews scrollable view interface
     * @param mediaPlayer media player interface
     */
    public AutomaticScrollHandler(ScrollViewMethods scrollViews, ScrollMediaPlayerMethods mediaPlayer) {

        mScrollViews = scrollViews;
        mDanceBotMediaPlayer = mediaPlayer;

        mScrollHandler = new Handler();

        // Keep track of user interaction to register user inactivity
        mLastInteraction = System.currentTimeMillis();

        // Initialize seek bar progress
        mSeekBarProgress = 0;
    }

    /**
     *
     */
    public void startListening() {

        // Check if handler not already running to prevent postDelayed flood
        if (!mIsRunning) {

            // Post this handler runnable for later execution
            mScrollHandler.postDelayed(this, 0);

            // Update last interaction time
            mLastInteraction = System.currentTimeMillis();

            // Set running flag
            mIsRunning = true;
        }
    }

    /**
     *
     */
    private void stopListening() {
        mScrollHandler.removeCallbacks(this);
    }

    @Override
    public void run() {

        Log.d(LOG_TAG, "run automatic scroll handler");

        // Check if media player is playing the song
        if (!mDanceBotMediaPlayer.isPlaying()) {

            Log.d(LOG_TAG, "song is not playing");

            // Check for user inactivity only when not playing a song
            if (System.currentTimeMillis() - mLastInteraction > TIME_TO_LIVE) {

                Log.d(LOG_TAG, "time to live is reached, stop automatic scroll handler");

                // If user has been inactive for a certain time, stop the scroll handler
                stopListening();
                mIsRunning = false;
                return;
            }
        }

        // Update seek bar
        int currentTime = mDanceBotMediaPlayer.getCurrentPosition();
        mDanceBotMediaPlayer.setSeekBarProgress(currentTime);

        if (mDanceBotMediaPlayer.isPlaying() || seekBarChanged()) {

            int currentBeatElement = (int) (((float) mScrollViews.getNumElements() / (float) mDanceBotMediaPlayer.getTotalTime()) * (float) currentTime);

            mScrollViews.scrollToPosition(currentBeatElement);

            Log.d(LOG_TAG, "update scroll to element: " + currentBeatElement);
        }

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

    /**
     *
     * @return
     */
    private boolean seekBarChanged() {

        int currentSeekBarProgress = mDanceBotMediaPlayer.getSeekBarProgress();

        if (mSeekBarProgress == currentSeekBarProgress) {
            return false;
        } else {
            mSeekBarProgress = currentSeekBarProgress;
            return true;
        }
    }
}
