package ch.ethz.asl.dancebots.danceboteditor.handlers;


import android.os.Handler;
import android.util.Log;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.asl.dancebots.danceboteditor.listener.MediaPlayerScrollListener;
import ch.ethz.asl.dancebots.danceboteditor.listener.RecyclerViewScrollListener;

/**
 * Created by andrin on 22.10.15.
 */
public class AutomaticScrollHandler implements Runnable {

    private static final String LOG_TAG = "SCROLL_HANDLER";

    private static AutomaticScrollHandler sInstance = null;

    private final int TIME_TO_LIVE = 10000;
    private long mLastInteraction = System.currentTimeMillis();
    private final Handler mScrollHandler = new Handler();

    private static List<RecyclerViewScrollListener> mRegisteredRecyclerViewScrollListeners = new ArrayList<>();
    private static List<MediaPlayerScrollListener> mRegisteredMediaPlayerListeners = new ArrayList<>();
    private static List<Integer> mSeekBarProgress = new ArrayList<>();

    private boolean mIsRunning = false;

    // A static block that sets class fields
    static {
        // Creates a single static instance of PhotoManager
        sInstance = new AutomaticScrollHandler();
    }
    /**
     * AutomaticScrollHandler creates a self removing scroll handler that updates the media
     * player, the seek bar state and the scroll state
     * Handler removes itself, when user is inactive
     */
    private AutomaticScrollHandler() {}

    public static void registerScrollListeners(RecyclerViewScrollListener recyclerListener, MediaPlayerScrollListener playerListener) {
        mRegisteredRecyclerViewScrollListeners.add(recyclerListener);
        mRegisteredMediaPlayerListeners.add(playerListener);
        mSeekBarProgress.add(0);
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

        for (int i = 0; i < mRegisteredMediaPlayerListeners.size(); ++i) {

            // Check if media player is playing the song
            if (!mRegisteredMediaPlayerListeners.get(i).isPlaying()) {

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

            updateSeekBar(mRegisteredMediaPlayerListeners.get(i));
            updateRecyclerView(mRegisteredMediaPlayerListeners.get(i), mRegisteredRecyclerViewScrollListeners.get(i), i);

            mScrollHandler.postDelayed(this, 100);
        }

        //Log.d(LOG_TAG, "run automatic scroll handler");


    }

    private void updateSeekBar(MediaPlayerScrollListener mediaPlayerScrollListener) {
        // Update seek bar
        int currentTimeInMilliseconds = mediaPlayerScrollListener.getCurrentPosition();
        mediaPlayerScrollListener.setSeekBarProgress(currentTimeInMilliseconds);
    }

    private void updateRecyclerView(MediaPlayerScrollListener mediaPlayerScrollListener, RecyclerViewScrollListener recyclerViewScrollListener, int idx) {

        int currentTimeInMilliseconds = mediaPlayerScrollListener.getCurrentPosition();

        // Update the HorizontalRecyclerViews when the player is playing or the seek bar has changed
        if (mediaPlayerScrollListener.isPlaying() || seekBarChanged(mediaPlayerScrollListener, idx)) {

            // Estimate beat position
            int estimatedBeatElement = (int) (((float) recyclerViewScrollListener.getNumElements() / (float) mediaPlayerScrollListener.getTotalTime()) * (float) currentTimeInMilliseconds);

            // Compute current sample
            long currentSample = (long) ((float) currentTimeInMilliseconds * 0.001 * (float) mediaPlayerScrollListener.getSampleRate());

            int exactBeatElement = estimatedBeatElement;

            // Check exact beat position for previous, current and next beat
            for (int i = -1; i <= 1; ++i) {

                // Check boundaries
                if (estimatedBeatElement + i > 0 && estimatedBeatElement + i < recyclerViewScrollListener.getNumElements() - 1) {

                    // Get start and end beat sample positions
                    long estimatedBeatStartSample = recyclerViewScrollListener.getSampleAt(estimatedBeatElement + i);
                    long estimatedBeatEndSample = recyclerViewScrollListener.getSampleAt(estimatedBeatElement + i + 1);

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
            int firstVisibleItem = recyclerViewScrollListener.getFirstVisibleItem();
            int lastVisibleItem = recyclerViewScrollListener.getLastVisibleItem();

            if (exactBeatElement <= firstVisibleItem || exactBeatElement >= lastVisibleItem) {
                recyclerViewScrollListener.scrollToPosition(exactBeatElement);
            } else {
                recyclerViewScrollListener.setFocus(exactBeatElement);
            }

            //Log.d(LOG_TAG, "update scroll to element: " + exactBeatElement);
        }

    }

    private boolean isInRange(long estimatedBeatStartSample, long estimatedBeatEndSample, long currentSample) {
        return (currentSample <= estimatedBeatEndSample && currentSample >= estimatedBeatStartSample);
    }

    /**
     * Check if seek bar changed
     *
     * @param mediaPlayerScrollListener
     * @param idx
     * @return state of seek bar state
     */
    private boolean seekBarChanged(MediaPlayerScrollListener mediaPlayerScrollListener, int idx) {

        int currentSeekBarProgress = mediaPlayerScrollListener.getSeekBarProgress();

        if (mSeekBarProgress.get(idx) == currentSeekBarProgress) {
            return false;
        } else {
            mSeekBarProgress.set(idx, currentSeekBarProgress);
            return true;
        }
    }

    public static AutomaticScrollHandler getInstance() {
        return sInstance;
    }
}
