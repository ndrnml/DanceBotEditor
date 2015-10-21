package ch.ethz.asl.dancebots.danceboteditor.utils;

import android.app.Activity;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.File;
import java.io.IOException;

import ch.ethz.asl.dancebots.danceboteditor.R;

/**
 * Created by andrin on 21.10.15.
 */
public class DanceBotMediaPlayer implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private Activity mActivity;
    private SeekBar mSeekBar;
    private Handler mSeekBarHandler;
    private MediaPlayer mMediaPlayer;
    private boolean isReady = false;
    private boolean mPlaying = false;
    private int mStartTime;

    public DanceBotMediaPlayer(Activity activity) {

        mActivity = activity;

        // Initialize media player
        mMediaPlayer = new MediaPlayer();

        // Attach on click listener to play/pause button
        Button btn = (Button) mActivity.findViewById(R.id.btn_play);
        btn.setOnClickListener(this);

        // Initialize seek bar handler
        mSeekBarHandler = new Handler();
    }

    public void openMusicFile(String songPath) {

        Uri songUri = Uri.fromFile(new File(songPath));
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mMediaPlayer.setDataSource(mActivity, songUri);
            mMediaPlayer.prepare();
            isReady = true;

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
    public void onClick(View v) {

        if (isReady) {
            mPlaying = !mPlaying;
            if (mPlaying) {

                mMediaPlayer.start();

                mStartTime = mMediaPlayer.getCurrentPosition();
                mSeekBar.setProgress((int) mStartTime);
                mSeekBarHandler.postDelayed(updateSongTime, 100);
                //m_seekbarSongHandler.postDelayed(updateCursor, 100);

            } else {
                mMediaPlayer.pause();
            }

            Button btn = (Button) v;
            btn.setText(mPlaying ? "Pause" : "Play");
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mMediaPlayer.seekTo(progress);
            mSeekBar.setProgress(progress);
            /*BeatInfoView biv = (BeatInfoView)findViewById(R.id.beat_info_view);
            biv.updateCursor(progress, m_seekbar.getMax());
            biv.invalidate();*/
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private Runnable updateSongTime = new Runnable() {
        public void run() {
            mStartTime = mMediaPlayer.getCurrentPosition();
            mSeekBar.setProgress((int) mStartTime);
            mSeekBarHandler.postDelayed(this, 100);
        }
    };
}
