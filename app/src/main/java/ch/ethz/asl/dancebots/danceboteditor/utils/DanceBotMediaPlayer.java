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

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.handlers.AutomaticScrollHandler;

/**
 * Created by andrin on 21.10.15.
 */
public class DanceBotMediaPlayer implements View.OnClickListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener, AutomaticScrollHandler.ScrollMediaPlayerMethods {

    private static final String LOG_TAG = "DANCE_BOT_MEDIA_PLAYER";

    private final Activity mActivity;
    private final TextView mSeekBarTotalTimeView;
    private final TextView mSeekBarCurrentTimeView;
    private SeekBar mSeekBar;
    private final MediaPlayer mMediaPlayer;
    private boolean mIsReady = false;
    private boolean mIsPlaying = false;
    private int mTotalTime;
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

        // Init seek bar labels
        mSeekBarCurrentTimeView = (TextView) mActivity.findViewById(R.id.seekbar_current_time);
        mSeekBarTotalTimeView = (TextView) mActivity.findViewById(R.id.seekbar_total_time);

        mSeekBarCurrentTimeView.setText(songTimeFormat(0));
        mSeekBarTotalTimeView.setText(songTimeFormat(0));
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

        // Store other important music file properties
        mTotalTime = mMusicFile.getDurationInMiliSecs();

        // Update total time view
        mSeekBarTotalTimeView.setText(songTimeFormat(mTotalTime));
    }

    /**
     * Get song time hh:ss format from milliseconds
     *
     * @param timeInMilliseconds time in milliseconds
     *
     * @return string format mm:ss
     */
    private String songTimeFormat(int timeInMilliseconds) {
        return String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds),
                TimeUnit.MILLISECONDS.toSeconds(timeInMilliseconds) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds)));
    }

    /************************************
     * ScrollMediaPlayerMethods Interface
     ************************************/

    @Override
    public boolean isPlaying() {
        return mIsPlaying;
    }

    /**
     * Get the current playback position in milliseconds
     *
     * @return position in milliseconds
     */
    @Override
    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public void setSeekBarProgress(int progress) {
        // Update seek bar
        mSeekBar.setProgress(progress);

        // Update seek bar text view current time
        mSeekBarCurrentTimeView.setText(songTimeFormat(progress));
    }

    @Override
    public int getSeekBarProgress() {
        return mSeekBar.getProgress();
    }

    @Override
    public int getTotalTime() {
        return mTotalTime;
    }

    @Override
    public int getSampleRate() {
        return mMusicFile.getSampleRate();
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

        Log.d(LOG_TAG, "seekBar: on progress changed");

        // Notify automatic scroll listener when media player progressed
        if (DanceBotEditorManager.getInstance().getAutomaticScrollHandler() != null) {
            DanceBotEditorManager.getInstance().notifyAutomaticScrollHandler();
        }

        // If user interaction, set media player progress
        if (fromUser) {
            mMediaPlayer.seekTo(progress);
            Log.d(LOG_TAG, "fromUser: on progress changed");
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
}
