package ch.ethz.asl.dancebots.danceboteditor.utils;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.File;
import java.io.IOException;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.handlers.AutomaticScrollHandler;

/**
 * Created by andrin on 21.10.15.
 */
public class DanceBotMediaPlayer implements View.OnClickListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener, AutomaticScrollHandler.ScrollMediaPlayerMethods {

    private static final String LOG_TAG = "DANCE_BOT_MEDIA_PLAYER";

    private final Activity mActivity;
    private SeekBar mSeekBar;
    private final MediaPlayer mMediaPlayer;
    private boolean mIsReady = false;
    private boolean mIsPlaying = false; // TODO change to mMediaPlayer.isPlaying(); ?
    private boolean mSeekbarChanged = false;
    private int mStartTime;
    private int mTotalTime;
    private int mNumBeats;
    private DanceBotMusicFile mMusicFile;
    private Button mPlayPauseButton;

    public DanceBotMediaPlayer(Activity activity) {

        mActivity = activity;

        // Initialize media player
        mMediaPlayer = new MediaPlayer();
        // Attach on completion listener
        mMediaPlayer.setOnCompletionListener(this);

        // Attach on click listener to play/pause button
        Button btn = (Button) mActivity.findViewById(R.id.btn_play);
        btn.setOnClickListener(this);
    }

    public void openMusicFile(DanceBotMusicFile musicFile) {

        // Bind music file as a lot information is needed later
        mMusicFile = musicFile;
        String songPath = mMusicFile.getSongPath();

        // Retrieve song from song path and resolve to URI
        Uri songUri = Uri.fromFile(new File(songPath));
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mMediaPlayer.setDataSource(mActivity, songUri);
            mMediaPlayer.prepare();
            mIsReady = true;

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Prepare seek bar for the selected song
        mSeekBar = (SeekBar) mActivity.findViewById(R.id.seekbar_media_player);
        mSeekBar.setClickable(true);
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setMax(mMediaPlayer.getDuration());
    }

    @Override
    public void preparePlayback() {
        // Store other important music file properties
        mTotalTime = mMusicFile.getDurationInMiliSecs();
        mNumBeats = mMusicFile.getNumberOfBeatsDetected();
    }

    @Override
    public boolean isPlaying() {
        return mIsPlaying;
    }

    @Override
    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public void setSeekBarProgress(int progress) {
        mSeekBar.setProgress(progress);
    }

    @Override
    public SeekBar getSeekBarView() {
        return mSeekBar;
    }

    public void pause() {
        mMediaPlayer.pause();
    }

    @Override
    public void onClick(View v) {

        if (mIsReady) {
            mIsPlaying = !mIsPlaying;
            if (mIsPlaying) {

                mMediaPlayer.start();

                // Set seek bar progress to current song position
                int currentTime = mMediaPlayer.getCurrentPosition();
                mSeekBar.setProgress(currentTime);

                // Notify automatic scroll listener when media player progressed
                if (DanceBotEditorManager.getInstance().getAutomaticScrollHandler() != null) {
                    DanceBotEditorManager.getInstance().notifyAutomaticScrollHandler();
                }

            } else {

                mMediaPlayer.pause();
            }

            // Get media player play/pause button
            mPlayPauseButton = (Button) v;

            // Update button text value
            if (mIsPlaying) {
                mPlayPauseButton.setText(R.string.txt_pause);
            } else {
                mPlayPauseButton.setText(R.string.txt_play);
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        Log.d(LOG_TAG, "seekbar: on progress changed");

        // Notify automatic scroll listener when media player progressed
        if (DanceBotEditorManager.getInstance().getAutomaticScrollHandler() != null) {
            DanceBotEditorManager.getInstance().notifyAutomaticScrollHandler();
        }

        // If user interaction, set media player progress
        if (fromUser) {
            Log.d(LOG_TAG, "fromUser: on progress changed");
            mMediaPlayer.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        if (mPlayPauseButton != null) {
            // Set playing flag
            mIsPlaying = false;
            mPlayPauseButton.setText(R.string.txt_play);
            // Rewind media player to the start
            mMediaPlayer.seekTo(0);
        }
    }

/*
    private Runnable updateSongTime = new Runnable() {

        @Override
        public void run() {

            Log.d(LOG_TAG, "update song time");

            // Update seek bar
            // TODO change mStartTime to local variable?
            int currentTime = mMediaPlayer.getCurrentPosition();
            mSeekBar.setProgress(currentTime);

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

                Log.d(LOG_TAG, "update scroll to element: " + currentBeatElement);

            }

            mSeekBarHandler.postDelayed(this, 100);
        }
    };*/
}
