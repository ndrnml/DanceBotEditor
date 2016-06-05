package ch.ethz.asl.dancebots.danceboteditor.utils;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;

import java.io.File;
import java.io.IOException;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.listener.CompositeSeekBarListener;
import ch.ethz.asl.dancebots.danceboteditor.listener.MediaPlayerListener;

/**
 * Author: Andrin Jenal
 * Copyright: ETH Zürich
 */
public class DanceBotMediaPlayer implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener, MediaPlayerListener.OnMediaPlayerChangeListener {

    private static final String LOG_TAG = DanceBotMediaPlayer.class.getSimpleName();

    private final Activity mActivity;
    private SeekBar mSeekBar;
    private MediaPlayer mMediaPlayer;
    private boolean mIsReady = false;
    private boolean mIsPlaying = false;
    private DanceBotMusicFile mMusicFile;
    private ImageButton mPlayPauseButton;
    private MediaPlayerListener mEventListener;

    public DanceBotMediaPlayer(Activity activity) {

        mActivity = activity;

        // Initialize media player
        mMediaPlayer = new MediaPlayer();
        // Attach on completion listener
        mMediaPlayer.setOnCompletionListener(this);
    }

    public void setEventListener(MediaPlayerListener eventListener) {
        mEventListener = eventListener;
    }

    public void setMediaPlayerSeekBar(SeekBar seekBar) {

        // Prepare seek bar for the selected song
        mSeekBar = seekBar;
        // Register media player to seek bar
        CompositeSeekBarListener.registerListener(this);
    }

    public void setPlayButton(ImageButton playButton) {
        mPlayPauseButton = playButton;
    }

    public void setDataSource(DanceBotMusicFile musicFile) {

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
    public void setPlayButtonPlay() {
        mPlayPauseButton.setImageResource(R.drawable.play_music_play_icon);
    }

    @Override
    public void setPlayButtonPause() {
        mPlayPauseButton.setImageResource(R.drawable.play_music_pause_icon);
    }

    @Override
    public ImageButton getPlayButton() {
        return mPlayPauseButton;
    }

    @Override
    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

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
            mEventListener.stopListening(MediaPlayerListener.DEFAULT_FLAG);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        // Set playing flag
        mIsPlaying = false;

        // Rewind media player to the start
        mMediaPlayer.seekTo(0);

        if (mPlayPauseButton != null) {
            mPlayPauseButton.setImageResource(R.drawable.play_music_play_icon);
        }

        if (mEventListener != null) {
            mEventListener.stopListening(MediaPlayerListener.DEFAULT_FLAG);
        }
    }

    /**
     * Stop media player playback and release resource
     */
    public void cleanUp() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mIsReady = false;
                mIsPlaying = false;
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void onStart() {
        try {
            if (!mIsReady) {
                mMediaPlayer.prepare();
                mIsReady = true;
                Log.d(LOG_TAG, "onStart: media player prepared");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isReady() {
        return mIsReady;
    }

    public void onStop() {

        if (mIsPlaying && mEventListener != null) {
            mEventListener.stopListening(MediaPlayerListener.STOP_PLAYING_FLAG);
            Log.d(LOG_TAG, "onStop: media player running, but stopping now");
        }

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mIsReady = false;
            mIsPlaying = false;
            Log.d(LOG_TAG, "onStop: media player stopped");
        }
    }

}