package ch.ethz.asl.dancebots.danceboteditor.handlers;


import android.os.Handler;
import android.util.Log;
import android.view.View;

/**
 * Created by andrin on 22.10.15.
 */
public class AutomaticScrollHandler implements Runnable {

    private static final String LOG_TAG = "SCROLL_HANDLER";

    private final int TIME_TO_LIVE = 10000;

    private long mLastInteraction;

    private final Handler mScrollHandler;

    private ScrollViewMethods mScrollViews;

    private ScrollMediaPlayerMethods mDanceBotMediaPlayer;

    private boolean mIsRunning;

    private int mSeekBarProgress;

    /**
     * An interface that defines methods that the scroll views implement. An instance of
     * HorizontalRecyclerViews passes itself to the AutomaticScrollHandler. This is needed
     * to handle scroll events correctly and communicate between scroll views and seek bars.
     */
    public interface ScrollViewMethods {

        void scrollToPosition(int position);

        int getNumElements();
    }

    /**
     * An interface that defines methods that the media player implements. An instance of
     * DanceBotMediaPlayer passes itself to the AutomaticScrollHandler. This is needed to handle
     * communication between scroll views and seek bars correctly.
     */
    public interface ScrollMediaPlayerMethods {

        boolean isPlaying();

        void setSeekBarProgress(int progress);

        int getSeekBarProgress();

        int getCurrentPosition();

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
     * Start handler on user activity
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
     * Stop handler while user is inactive
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

        mScrollHandler.postDelayed(this, 200);
    }

    /**
     * Check if seek bar changed
     * @return state of seek bar state
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
