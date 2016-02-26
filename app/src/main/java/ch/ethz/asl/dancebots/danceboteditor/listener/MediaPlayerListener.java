package ch.ethz.asl.dancebots.danceboteditor.listener;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorManager;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotMusicFile;
import ch.ethz.asl.dancebots.danceboteditor.utils.Helper;
import ch.ethz.asl.dancebots.danceboteditor.view.HorizontalRecyclerViews;

/**
 * Created by andrin on 23.02.16.
 */
public class MediaPlayerListener implements Runnable, View.OnClickListener {

    private static final String LOG_TAG = MediaPlayerListener.class.getSimpleName();

    public static final int DEFAULT_FLAG = 0;
    public static final int STOP_PLAYING_FLAG = 1;

    private final Activity mActivity;
    private final float mTotalDurationInMilliSecs;
    private final float mSampleRate;
    private final int mNumElements;

    private List<OnMediaPlayerChangeListener> registeredListeners = new ArrayList<>();

    private Handler mHandler = new Handler();
    private HorizontalRecyclerViews mRecyclerView;
    private final SeekBar mSeekBar;
    private OnMediaPlayerChangeListener mActiveMediaPlayer;
    private int mSeekBarProgress = 0;
    private boolean mIsRunning = false;
    private TextView mCurrentTimeView;

    /**
     * Any media playing instance that wants its changes being observed by the MediaPlayerListener
     * must implement this interface.
     */
    public interface OnMediaPlayerChangeListener {

        void play();

        void pause();

        boolean isReady();

        boolean isPlaying();

        void setPlayButtonPlay();

        void setPlayButtonPause();

        Button getPlayButton();

        int getCurrentPosition();
    }

    /**
     * MediaPlayerListener changes the HorizontalRecyclerViews and the SeekBar progress, based
     * on media player playback changes, or any SeekBar changes
     *
     * @param recyclerView HorizontalRecyclerViews that will be updated on changes
     * @param seekBar SeekBar that will be updated on changes
     * @param musicFile corresponding music file, that passes information about the song
     */
    public MediaPlayerListener(HorizontalRecyclerViews recyclerView, SeekBar seekBar, DanceBotMusicFile musicFile) {

        mActivity = ((Activity) DanceBotEditorManager.getInstance().getContext());
        mCurrentTimeView = (TextView) mActivity.findViewById(R.id.seekbar_current_time);

        mRecyclerView = recyclerView;
        mSeekBar = seekBar;

        // To speed up media player listener store variables
        mTotalDurationInMilliSecs = (float) musicFile.getDurationInMilliSecs();
        mSampleRate = (float) musicFile.getSampleRate();

        mNumElements = recyclerView.getNumElements();
    }

    /**
     * Register media playing instance to this change listener
     *
     * @param mediaPlayer media player instance, that wants its changes to be notified
     */
    public void registerMediaPlayer(OnMediaPlayerChangeListener mediaPlayer) {

        // Register media player
        registeredListeners.add(mediaPlayer);

        // Add onClickListener for media player play button
        mediaPlayer.getPlayButton().setOnClickListener(this);
    }

    /**
     * Start background listening to any media player playback changes or seek bar changes
     */
    public void startListening() {
        if (!mIsRunning) {
            mHandler.postDelayed(this, 100);
            mIsRunning = true;
        }
    }

    /**
     * Stop listening, if no media player is currently playing
     */
    public void stopListening(int flag) {

        boolean isAnyPlaying = false;

        // Check global state, whether any playback is playing
        for (OnMediaPlayerChangeListener mediaPlayer : registeredListeners) {
            isAnyPlaying = isAnyPlaying || mediaPlayer.isPlaying();
        }

        // Only stop listening, when no media player is playing
        if (!isAnyPlaying) {
            mHandler.removeCallbacks(this);
            mIsRunning = false;
        }

        // This is an absolute stopping flag, it stops listening even when media player are playing
        if (flag == STOP_PLAYING_FLAG) {

            // Setting back the button text can either be implemented here or directly in the media
            // player where the stop request comes from

            // Set all buttons to pause
            for (OnMediaPlayerChangeListener mediaPlayer : registeredListeners) {
                final OnMediaPlayerChangeListener mp = mediaPlayer;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mp.setPlayButtonPlay();
                    }
                });
            }

            // Remove all callbacks and stop listening
            mHandler.removeCallbacks(this);
            mIsRunning = false;
        }
    }

    @Override
    public void run() {

        Log.d(LOG_TAG, "run listener");

        // Check if currently song is playing
        if (mActiveMediaPlayer != null) {

            int currentDuration = mActiveMediaPlayer.getCurrentPosition();
            mSeekBar.setProgress(currentDuration);

            mCurrentTimeView.setText(Helper.songTimeFormat(currentDuration));
        }

        // Check if seek bar progress changed
        if (seekBarChanged()) {

            //Log.d(LOG_TAG, "seek bar changed");

            // Get the seek bar progress
            int currentTimeInMilliSecs = mSeekBar.getProgress();

            // Update view
            mCurrentTimeView.setText(Helper.songTimeFormat(currentTimeInMilliSecs));

            // Estimate beat position
            int estimatedBeatElement = (int) (((float) mRecyclerView.getNumElements() / mTotalDurationInMilliSecs * (float) currentTimeInMilliSecs));

            // Compute current sample
            long currentSample = (long) ((float) currentTimeInMilliSecs * 0.001 * mSampleRate);

            int exactBeatElement = estimatedBeatElement;

            // Check exact beat position for previous, current and next beat
            for (int i = -1; i <= 1; ++i) {

                // Check boundaries
                if (estimatedBeatElement + i > 0 && estimatedBeatElement + i < mNumElements - 1) {

                    // Get start and end beat sample positions
                    long estimatedBeatStartSample = mRecyclerView.getSampleAt(estimatedBeatElement + i);
                    long estimatedBeatEndSample = mRecyclerView.getSampleAt(estimatedBeatElement + i + 1);

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
            int firstVisibleItem = mRecyclerView.getFirstVisibleItem();
            int lastVisibleItem = mRecyclerView.getLastVisibleItem();

            if (exactBeatElement <= firstVisibleItem || exactBeatElement >= lastVisibleItem) {
                mRecyclerView.scrollToPosition(exactBeatElement);
            } else {
                mRecyclerView.setFocus(exactBeatElement);
            }

            //Log.d(LOG_TAG, "update scroll to element: " + exactBeatElement);
        }

        mHandler.postDelayed(this, 100);
    }

    /**
     * On top of HorizontalRecyclerViews and SeekBar progress changes, the MediaPlayerListener
     * also registers and handles clicks on media player instances, such that only one media player
     * can start playback at the time
     */
    @Override
    public void onClick(View v) {

        boolean isAnyPlaying = false;
        int id = v.getId();

        // Check global state, whether any playback is playing
        for (OnMediaPlayerChangeListener mediaPlayer : registeredListeners) {
            isAnyPlaying = isAnyPlaying || mediaPlayer.isPlaying();
        }

        if (isAnyPlaying) {

            for (OnMediaPlayerChangeListener mediaPlayer : registeredListeners) {

                if (mediaPlayer.getPlayButton().getId() == id) {
                    if (mediaPlayer.isPlaying()) {
                        mActiveMediaPlayer = null;
                        mediaPlayer.pause();
                        mHandler.removeCallbacks(this);
                        mIsRunning = false;

                        mediaPlayer.setPlayButtonPlay();
                    }
                }
                /*
                 * else, ignore any clicks on the buttons that do not correspond to the
                 * media player that is playing
                 */
            }

        } else {

            // If no media player is playing, then start the one which was clicked
            for (OnMediaPlayerChangeListener mediaPlayer : registeredListeners) {

                if (mediaPlayer.getPlayButton().getId() == id) {

                    if (mediaPlayer.isReady()) {
                        mActiveMediaPlayer = mediaPlayer;
                        mediaPlayer.play();
                        mediaPlayer.setPlayButtonPause();

                        if (!mIsRunning) {
                            mIsRunning = true;
                            mHandler.postDelayed(this, 100);
                        }
                    }

                }
            }
        }
    }

    /**
     * Check if seek bar changed
     *
     * @return state of seek bar state
     */
    private boolean seekBarChanged() {

        int currentSeekBarProgress = mSeekBar.getProgress();

        if (mSeekBarProgress == currentSeekBarProgress) {
            return false;
        } else {
            mSeekBarProgress = currentSeekBarProgress;
            return true;
        }
    }

    private boolean isInRange(long estimatedBeatStartSample, long estimatedBeatEndSample, long currentSample) {
        return (currentSample <= estimatedBeatEndSample && currentSample >= estimatedBeatStartSample);
    }

}
