package ch.ethz.asl.dancebots.danceboteditor.handlers;


import android.os.Handler;
import android.util.Log;

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

        long getSampleAt(int position);

        int getFirstVisibleItem();

        int getLastVisibleItem();

        void setFocus(int position);
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

        int getSampleRate();
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

    /**
     * This is the main loop of the scroll handler. It is executed repetitively until time to live
     * is reached.
     */
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
        int currentTimeInMilliseconds = mDanceBotMediaPlayer.getCurrentPosition();
        mDanceBotMediaPlayer.setSeekBarProgress(currentTimeInMilliseconds);

        // Update the HorizontalRecyclerViews when the player is playing or the seek bar has changed
        if (mDanceBotMediaPlayer.isPlaying() || seekBarChanged()) {

            // Estimate beat position
            int estimatedBeatElement = (int) (((float) mScrollViews.getNumElements() / (float) mDanceBotMediaPlayer.getTotalTime()) * (float) currentTimeInMilliseconds);

            // Compute current sample
            long currentSample = (long) ((float) currentTimeInMilliseconds * 0.001 * (float) mDanceBotMediaPlayer.getSampleRate());

            int exactBeatElement = estimatedBeatElement;

            // Check exact beat position for previous, current and next beat
            for (int i = -1; i <= 1; ++i) {
                
                // Check boundaries
                if (estimatedBeatElement + i > 0 && estimatedBeatElement + i < mScrollViews.getNumElements() - 1) {

                    // Get start and end beat sample positions
                    long estimatedBeatStartSample = mScrollViews.getSampleAt(estimatedBeatElement + i);
                    long estimatedBeatEndSample = mScrollViews.getSampleAt(estimatedBeatElement + i + 1);

                    // Check if current sample is in range
                    if (isInRange(estimatedBeatStartSample, estimatedBeatEndSample, currentSample)) {
                        exactBeatElement = estimatedBeatElement + i;
                        if (i == 0) {
                            Log.d(LOG_TAG, "i == 0");
                        }
                        break;
                    }
                }
            }

            //int currentBeatElement = (int) (((float) mScrollViews.getNumElements() / (float) mDanceBotMediaPlayer.getTotalTime()) * (float) currentTimeInMilliseconds);

            int firstVisibleItem = mScrollViews.getFirstVisibleItem();
            int lastVisibleItem = mScrollViews.getLastVisibleItem();

            if (exactBeatElement <= firstVisibleItem || exactBeatElement >= lastVisibleItem) {
                mScrollViews.scrollToPosition(exactBeatElement);
            } else {
                mScrollViews.setFocus(exactBeatElement);
            }

            Log.d(LOG_TAG, "update scroll to element: " + exactBeatElement);
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

        int currentSeekBarProgress = mDanceBotMediaPlayer.getSeekBarProgress();

        if (mSeekBarProgress == currentSeekBarProgress) {
            return false;
        } else {
            mSeekBarProgress = currentSeekBarProgress;
            return true;
        }
    }
}
