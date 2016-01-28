package ch.ethz.asl.dancebots.danceboteditor.utils;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
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
    private MediaPlayer mMediaPlayer;
    private boolean mIsReady = false;
    private boolean mIsPlaying = false;
    private int mTotalTime;
    private DanceBotMusicFile mMusicFile;
    private Button mPlayPauseButton;
    private AudioTrack audioTrack;

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

        // Prepare seek bar for the selected song
        mSeekBar = (SeekBar) mActivity.findViewById(R.id.seekbar_media_player);
        mSeekBar.setClickable(true);
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setMax(mMediaPlayer.getDuration());

        // Store other important music file properties
        mTotalTime = mMusicFile.getDurationInMiliSecs();

        // Update total time view
        mSeekBarTotalTimeView.setText(songTimeFormat(mTotalTime));

        /**
         * TODO
         */
        /*AssetFileDescriptor sampleFD = getResources().openRawResourceFd(R.raw.sample);

        MediaExtractor extractor;
        MediaCodec codec = null;
        ByteBuffer[] codecInputBuffers;
        ByteBuffer[] codecOutputBuffers;

        extractor = new MediaExtractor();
        extractor.setDataSource(sampleFD.getFileDescriptor(), sampleFD.getStartOffset(), sampleFD.getLength());

        Log.d(LOG_TAG, String.format("TRACKS #: %d", extractor.getTrackCount()));
        MediaFormat format = extractor.getTrackFormat(0);
        String mime = format.getString(MediaFormat.KEY_MIME);
        Log.d(LOG_TAG, String.format("MIME TYPE: %s", mime));

        try {
            codec = MediaCodec.createDecoderByType(mime);
        } catch (IOException e) {
            e.printStackTrace();
        }
        codec.configure(format, null, null, 0);
        codec.start();
        codecInputBuffers = codec.getInputBuffers();
        codecOutputBuffers = codec.getOutputBuffers();

        extractor.selectTrack(0); // <= You must select a track. You will read samples from the media from this track!

        int inputBufIndex = codec.dequeueInputBuffer(TIMEOUT_US);
        if (inputBufIndex >= 0) {
            ByteBuffer dstBuf = codecInputBuffers[inputBufIndex];

            int sampleSize = extractor.readSampleData(dstBuf, 0);
            long presentationTimeUs = 0;
            if (sampleSize < 0) {
                sawInputEOS = true;
                sampleSize = 0;
            } else {
                presentationTimeUs = extractor.getSampleTime();
            }

            codec.queueInputBuffer(inputBufIndex,
                    0, //offset
                    sampleSize,
                    presentationTimeUs,
                    sawInputEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);
            if (!sawInputEOS) {
                extractor.advance();
            }
        }

        Button btn = (Button) mActivity.findViewById(R.id.btn_stream);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int res = codec.dequeueOutputBuffer(info, TIMEOUT_US);
                if (res >= 0) {
                    int outputBufIndex = res;
                    ByteBuffer buf = codecOutputBuffers[outputBufIndex];

                    final byte[] chunk = new byte[info.size];
                    buf.get(chunk); // Read the buffer all at once
                    buf.clear(); // ** MUST DO!!! OTHERWISE THE NEXT TIME YOU GET THIS SAME BUFFER BAD THINGS WILL HAPPEN

                    if (chunk.length > 0) {
                        audioTrack.write(chunk, 0, chunk.length);
                    }
                    codec.releaseOutputBuffer(outputBufIndex, false);

                    if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        sawOutputEOS = true;
                    }
                } else if (res == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    codecOutputBuffers = codec.getOutputBuffers();
                } else if (res == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    final MediaFormat oformat = codec.getOutputFormat();
                    Log.d(LOG_TAG, "Output format has changed to " + oformat);
                    mAudioTrack.setPlaybackRate(oformat.getInteger(MediaFormat.KEY_SAMPLE_RATE));
                }
            }
        });
        */
        /**
         * TODO END
         */

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
