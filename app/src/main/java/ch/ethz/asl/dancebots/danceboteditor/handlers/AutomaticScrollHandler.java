package ch.ethz.asl.dancebots.danceboteditor.handlers;


import android.os.Handler;
import android.util.Log;

import ch.ethz.asl.dancebots.danceboteditor.listener.MediaPlayerScrollListener;
import ch.ethz.asl.dancebots.danceboteditor.listener.RecyclerViewScrollListener;

/**
 * Created by andrin on 22.10.15.
 */
public class AutomaticScrollHandler implements Runnable {

    private static final String LOG_TAG = "SCROLL_HANDLER";

    private final int TIME_TO_LIVE = 10000;

    private long mLastInteraction;

    private final Handler mScrollHandler;

    private RecyclerViewScrollListener mRecyclerViewScrollListener;
    private MediaPlayerScrollListener mMediaPlayerListener;

    private boolean mIsRunning;

    private int mSeekBarProgress;

    /**
     * AutomaticScrollHandler creates a self removing scroll handler that updates the media
     * player, the seek bar state and the scroll state
     * Handler removes itself, when user is inactive
     * @param scrollViewListener scrollable view interface
     * @param mediaPlayerListener media player interface
     */
    public AutomaticScrollHandler(RecyclerViewScrollListener scrollViewListener, MediaPlayerScrollListener mediaPlayerListener) {

        mRecyclerViewScrollListener = scrollViewListener;
        mMediaPlayerListener = mediaPlayerListener;

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

            Log.d(LOG_TAG, "start listening, add callbacks.");

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
        Log.d(LOG_TAG, "stop listening, remove callbacks.");
    }

    /**
     * This is the main loop of the scroll handler. It is executed repetitively until time to live
     * is reached.
     */
    @Override
    public void run() {

        //Log.d(LOG_TAG, "run automatic scroll handler");

        // Check if media player is playing the song
        if (!mMediaPlayerListener.isPlaying()) {

            //Log.d(LOG_TAG, "song is not playing");

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
        int currentTimeInMilliseconds = mMediaPlayerListener.getCurrentPosition();
        mMediaPlayerListener.setSeekBarProgress(currentTimeInMilliseconds);

        // Update the HorizontalRecyclerViews when the player is playing or the seek bar has changed
        if (mMediaPlayerListener.isPlaying() || seekBarChanged()) {

            // Estimate beat position
            int estimatedBeatElement = (int) (((float) mRecyclerViewScrollListener.getNumElements() / (float) mMediaPlayerListener.getTotalTime()) * (float) currentTimeInMilliseconds);

            // Compute current sample
            long currentSample = (long) ((float) currentTimeInMilliseconds * 0.001 * (float) mMediaPlayerListener.getSampleRate());

            int exactBeatElement = estimatedBeatElement;

            // Check exact beat position for previous, current and next beat
            for (int i = -1; i <= 1; ++i) {
                
                // Check boundaries
                if (estimatedBeatElement + i > 0 && estimatedBeatElement + i < mRecyclerViewScrollListener.getNumElements() - 1) {

                    // Get start and end beat sample positions
                    long estimatedBeatStartSample = mRecyclerViewScrollListener.getSampleAt(estimatedBeatElement + i);
                    long estimatedBeatEndSample = mRecyclerViewScrollListener.getSampleAt(estimatedBeatElement + i + 1);

                    // Check if current sample is in range
                    if (isInRange(estimatedBeatStartSample, estimatedBeatEndSample, currentSample)) {
                        exactBeatElement = estimatedBeatElement + i;
                        /*if (i == 0) {
                            Log.d(LOG_TAG, "i == 0");
                        }*/
                        break;
                    }
                }
            }

            //int currentBeatElement = (int) (((float) mRecyclerViewScrollListener.getNumElements() / (float) mMediaPlayerListener.getTotalTime()) * (float) currentTimeInMilliseconds);
            int firstVisibleItem = mRecyclerViewScrollListener.getFirstVisibleItem();
            int lastVisibleItem = mRecyclerViewScrollListener.getLastVisibleItem();

            if (exactBeatElement <= firstVisibleItem || exactBeatElement >= lastVisibleItem) {
                mRecyclerViewScrollListener.scrollToPosition(exactBeatElement);
            } else {
                mRecyclerViewScrollListener.setFocus(exactBeatElement);
            }

            //Log.d(LOG_TAG, "update scroll to element: " + exactBeatElement);
        }

        mScrollHandler.postDelayed(this, 100);
    }

    private boolean isInRange(long estimatedBeatStartSample, long estimatedBeatEndSample, long currentSample) {
        return (currentSample <= estimatedBeatEndSample && currentSample >= estimatedBeatStartSample);
    }

    /**
     * Check if seek bar changed
     * @return state of seek bar state
     */
    private boolean seekBarChanged() {

        int currentSeekBarProgress = mMediaPlayerListener.getSeekBarProgress();

        if (mSeekBarProgress == currentSeekBarProgress) {
            return false;
        } else {
            mSeekBarProgress = currentSeekBarProgress;
            return true;
        }
    }

    public void cleanUp() {
        stopListening();
    }
}
