package ch.ethz.asl.dancebots.danceboteditor.utils;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.EventListener;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.listener.AutomaticScrollListener;
import ch.ethz.asl.dancebots.danceboteditor.listener.MediaPlayerListener;
import ch.ethz.asl.dancebots.danceboteditor.listener.MediaPlayerScrollListener;

/**
 * Created by andrin on 21.10.15.
 */
public class DanceBotMediaPlayer implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener, MediaPlayerListener.OnMediaPlayerChangeListener {

    private static final String LOG_TAG = DanceBotMediaPlayer.class.getSimpleName();

    private final Activity mActivity;
    private TextView mSeekBarTotalTimeView;
    private TextView mSeekBarCurrentTimeView;
    private SeekBar mSeekBar;
    private MediaPlayer mMediaPlayer;
    private boolean mIsReady = false;
    private boolean mIsPlaying = false;
    private int mTotalTime;
    private DanceBotMusicFile mMusicFile;
    private Button mPlayPauseButton;
    private MediaPlayerListener mEventListener;

    public DanceBotMediaPlayer(Activity activity, DanceBotMusicFile musicFile) {

        mActivity = activity;

        // Initialize media player
        mMediaPlayer = new MediaPlayer();
        // Attach on completion listener
        mMediaPlayer.setOnCompletionListener(this);

        // Set selected music file as data source
        setDataSource(musicFile);
    }

    public void setEventListener(MediaPlayerListener eventListener) {
        mEventListener = eventListener;
    }

    public void setMediaPlayerSeekBar(SeekBar seekBar, TextView currentTime, TextView totalTime) {

        // Prepare seek bar for the selected song
        mSeekBar = seekBar;
        // Register media player to seek bar
        CompositeSeekBarListener.registerListener(this);

        // Init seek bar labels
        mSeekBarCurrentTimeView = currentTime;
        mSeekBarTotalTimeView = totalTime;

        mSeekBarCurrentTimeView.setText(Helper.songTimeFormat(0));
        mSeekBarTotalTimeView.setText(Helper.songTimeFormat(0));
    }

    public void setPlayButton(Button playButton) {
        mPlayPauseButton = playButton;
    }

    private void setDataSource(DanceBotMusicFile musicFile) {

        // Bind music file as a lot information is needed later
        mMusicFile = musicFile;
        final String songPath = mMusicFile.getSongPath();

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

        // Store other important music file properties
        mTotalTime = mMusicFile.getDurationInMilliSecs();

        // Update max seek bar
        if (mSeekBar != null) {
            mSeekBar.setMax(mMediaPlayer.getDuration());
        }

        // Update total time view
        if (mSeekBarTotalTimeView != null) {
            mSeekBarTotalTimeView.setText(Helper.songTimeFormat(mTotalTime));
        }
    }


    @Override
    public void play() {

        if (mIsReady) {
            mMediaPlayer.start();
            mIsPlaying = true;
        }
    }

    @Override
    public void pause() {
        if (mIsReady) {
            mMediaPlayer.pause();
            mIsPlaying = false;
        }
    }

    @Override
    public boolean isPlaying() {
        return mIsPlaying;
    }

    @Override
    public Button getPlayButton() {
        return mPlayPauseButton;
    }

    @Override
    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /*
    @Override
    public void onClick(View v) {

        if (mPlayPauseButton != null) {

            if (mIsReady) {
                mIsPlaying = !mIsPlaying;
                if (mIsPlaying) {

                    mMediaPlayer.start();

                    // Set seek bar progress to current song position
                    int currentTime = mMediaPlayer.getCurrentPosition();
                    if (mSeekBar != null) {
                        mSeekBar.setProgress(currentTime);
                    }

                    // Notify automatic scroll listener when media player progressed
                    if (mEventListener != null) {
                        mEventListener.startListening();
                    }

                } else {

                    mMediaPlayer.pause();
                }

                // Update button text value
                if (mIsPlaying) {
                    mPlayPauseButton.setText(R.string.txt_pause);
                } else {
                    mPlayPauseButton.setText(R.string.txt_play);
                }
            }
        }
    }*/

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        //Log.d(LOG_TAG, "seekBar: on progress changed");

        if (mMediaPlayer != null) {

            // If user interaction, set media player progress
            if (fromUser) {
                mMediaPlayer.seekTo(progress);
                //Log.d(LOG_TAG, "fromUser: on progress changed");
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (mEventListener != null) {
            mEventListener.startListening();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mEventListener != null) {
            mEventListener.stopListening();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        // Set playing flag
        mIsPlaying = false;

        // Rewind media player to the start
        mMediaPlayer.seekTo(0);

        if (mPlayPauseButton != null) {
            mPlayPauseButton.setText(R.string.txt_play);
        }

        if (mEventListener != null) {
            mEventListener.stopListening();
        }
    }

    /**
     * Stop media player playback and release resource
     */
    public void cleanUp() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
