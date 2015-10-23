package ch.ethz.asl.dancebots.danceboteditor.utils;

import android.app.Activity;
import android.content.pm.LabeledIntent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.File;
import java.io.IOException;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.adapters.BeatElementAdapter;
import ch.ethz.asl.dancebots.danceboteditor.handlers.AutomaticScrollHandler;

/**
 * Created by andrin on 21.10.15.
 */
public class DanceBotMediaPlayer implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private static final String LOG_TAG = "DANCE_BOT_MEDIA_PLAYER";

    private Activity mActivity;
    private SeekBar mSeekBar;
    private Handler mSeekBarHandler;
    private AutomaticScrollHandler mScrollHandler;
    private MediaPlayer mMediaPlayer;
    private boolean mIsReady = false;
    private boolean mIsPlaying = false; // TODO change to mMediaPlayer.isPlaying(); ?
    private boolean mSeekbarChanged = false;
    private int mStartTime;
    private int mTotalTime;
    private int mNumBeats;
    private DanceBotMusicFile mMusicFile;

    private RecyclerView mMotorView;
    private RecyclerView mLedView;

    public DanceBotMediaPlayer(Activity activity) {

        mActivity = activity;

        // Initialize media player
        mMediaPlayer = new MediaPlayer();

        // Attach on click listener to play/pause button
        Button btn = (Button) mActivity.findViewById(R.id.btn_play);
        btn.setOnClickListener(this);

        // Bind the beat element views to the media player
        mMotorView = (RecyclerView) mActivity.findViewById(R.id.motor_element_list);
        mLedView = (RecyclerView) mActivity.findViewById(R.id.led_element_list);

        // Initialize seek bar handler
        mSeekBarHandler = new Handler();
        mScrollHandler = new AutomaticScrollHandler();
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

    public void preparePlayback() {
        // Store other important music file properties
        mTotalTime = mMusicFile.getDurationInMiliSecs();
        mNumBeats = mMusicFile.getNumberOfBeatsDetected();
        mSeekBarHandler.postDelayed(updateSongTime, 100);
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

                // Start seek bar handler
                int currentTime = mMediaPlayer.getCurrentPosition();
                mSeekBar.setProgress(currentTime);

            } else {

                mMediaPlayer.pause();
            }

            Button btn = (Button) v;
            btn.setText(mIsPlaying ? "Pause" : "Play");
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        Log.d(LOG_TAG, "seekbar: on progress changed");

        if (fromUser) {
            mMediaPlayer.seekTo(progress);
            mSeekbarChanged = true;
            //mSeekBar.setProgress(progress); // TODO: do I need this?
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

    public void setState(boolean isPlaying) {
        mIsPlaying = isPlaying;
    }

    public boolean getState() {
        return mIsPlaying;
    }

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
                /*
                BeatElementAdapter adapter = (BeatElementAdapter) mMotorView.getAdapter();

                // TODO
                if (currentBeatElement > 0 && currentBeatElement < adapter.getItemCount()) {
                    adapter.getItem(currentBeatElement).setFocus(true);
                }
                if (currentBeatElement - 1 > 0 && currentBeatElement - 1 < adapter.getItemCount()) {
                    adapter.getItem(currentBeatElement - 1).setFocus(false);
                }*/

                Log.d(LOG_TAG, "update scroll to element: " + currentBeatElement);

            }

            mSeekBarHandler.postDelayed(this, 100);
        }
    };
}
