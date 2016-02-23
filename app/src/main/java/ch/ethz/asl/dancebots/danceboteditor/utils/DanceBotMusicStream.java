package ch.ethz.asl.dancebots.danceboteditor.utils;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.*;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.listener.AutomaticScrollListener;
import ch.ethz.asl.dancebots.danceboteditor.listener.MediaPlayerScrollListener;
import ch.ethz.asl.dancebots.danceboteditor.model.ChoreographyManager;

/**
 * Created by andrin on 28.01.16.
 */
public class DanceBotMusicStream implements Runnable, View.OnClickListener, SeekBar.OnSeekBarChangeListener, MediaPlayerScrollListener {

    private String LOG_TAG = this.getClass().getSimpleName();

    private AutomaticScrollListener mEventListener = null;
    private Handler handler = new Handler();

    private final DanceBotMusicFile mMusicFile;
    private MusicStreamStates mStreamStates;

    private TextView mSeekBarTotalTimeView;
    private TextView mSeekBarCurrentTimeView;
    private SeekBar mSeekBar;

    private MediaExtractor mMediaExtractor;
    private String mSourcePath;
    private boolean mStop = true;

    private String mime = null;
    private int sampleRate = 0, channels = 0, bitrate = 0;
    private long presentationTimeUs = 0, duration = 0;

    private ChoreographyManager mDataSource;
    private boolean mDataSourceSet = false;
    private int mShortOffset;
    private Button mPlayButton;

    /**
     * Create new DanceBotMusicStream instance. This should only happen when a valid
     * DanceBotMusicFile is present.
     *
     * @param musicFile the music file which is the data source of the stream player
     */
    public DanceBotMusicStream(DanceBotMusicFile musicFile) {

        mStreamStates = new MusicStreamStates();
        mMusicFile = musicFile;
        mSourcePath = mMusicFile.getSongPath();
    }

    public void setEventListener(AutomaticScrollListener eventListener) {
        mEventListener = eventListener;
    }

    public void setDataSource(final ChoreographyManager dataSource) {
        mDataSource = dataSource;
        mDataSource.prepareStreamPlayback();
        mDataSourceSet = true;
    }

    public void setPlayButton(Button playButton) {

        mPlayButton = playButton;
        mPlayButton.setOnClickListener(this);
    }

    public void setMediaPlayerSeekBar(SeekBar seekBar, TextView currentTime, TextView totalTime) {

        // Prepare seek bar for the selected song
        mSeekBar = seekBar;
        // Register stream player to seek bar
        CompositeSeekBarListener.registerListener(this);

        mSeekBar.setMax(mMusicFile.getDurationInMilliSecs());

        // Init seek bar labels
        mSeekBarCurrentTimeView = currentTime;
        mSeekBarTotalTimeView = totalTime;

        mSeekBarCurrentTimeView.setText(Helper.songTimeFormat(0));
        mSeekBarTotalTimeView.setText(Helper.songTimeFormat(mMusicFile.getDurationInMilliSecs()));
    }

    /**
     * Start stream playback.
     */
    public void play() {

        if (mStreamStates.getState() == MusicStreamStates.STOPPED) {
            mStop = false;
            // Set number of bytes written initially to zero
            mShortOffset = 0;
            new Thread(this).start();
        }

        if (mStreamStates.getState() == MusicStreamStates.READY_TO_PLAY) {
            mStreamStates.setState(MusicStreamStates.PLAYING);
            syncNotify();
        }
    }

    /**
     * Notify background Thread if player state changed.
     */
    public synchronized void syncNotify() {
        notify();
    }

    /**
     * Synchronized wait if the player is on pause.
     */
    public synchronized void waitPlay() {

        while (mStreamStates.getState() == MusicStreamStates.READY_TO_PLAY) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Stop the media stream player.
     */
    public void stop() {
        mStop = true;
    }

    /**
     * Pause the media stream player. This causes the Thread to spin in a wait loop.
     */
    public void pause() {
        mStreamStates.setState(MusicStreamStates.READY_TO_PLAY);
    }

    /**
     * Seek to closest position
     *
     * @param positionInMilliSeconds position in milliseconds
     */
    public void seekTo(long positionInMilliSeconds) {
        if (mMediaExtractor != null) {
            // MediaExtractor expects microseconds
            mMediaExtractor.seekTo(positionInMilliSeconds * 1000, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
        }
    }

    @Override
    public void run() {

        // Set thread priority to audio
        Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);

        AudioTrack audioTrack;
        MediaCodec codec = null;

        // mMediaExtractor gets information about the stream
        mMediaExtractor = new MediaExtractor();

        // try to set the source, this might fail
        try {
            if (mSourcePath != null) mMediaExtractor.setDataSource(this.mSourcePath);
        } catch (Exception e) {
            Log.e(LOG_TAG, "exception:" + e.getMessage());
            //if (events != null) handler.post(new Runnable() { @Override public void run() { events.onError();  } });
            return;
        }

        // Read track header
        MediaFormat format = null;

        mMediaExtractor.getTrackFormat(0);

        // Read media codec information
        try {

            format = mMediaExtractor.getTrackFormat(0);
            mime = format.getString(MediaFormat.KEY_MIME);
            sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
            channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
            // if duration is 0, we are probably playing a live stream
            duration = format.getLong(MediaFormat.KEY_DURATION);
            bitrate = format.getInteger(MediaFormat.KEY_BIT_RATE);

        } catch (Exception e) {
            Log.e(LOG_TAG, "Reading format parameters exception:" + e.getMessage());
            // don't exit, tolerate this error, we'll fail later if this is critical
        }

        Log.d(LOG_TAG, "Track info: mime:" + mime + " sampleRate:" + sampleRate + " channels:" + channels + " bitrate:" + bitrate + " duration:" + duration);

        // check we have audio content we know
        if (format == null || !mime.startsWith("audio/")) {
            //if (events != null) handler.post(new Runnable() { @Override public void run() { events.onError();  } });
            Log.d(LOG_TAG, "Error: Format or MIME incorrect");
            return;
        }

        // Create the actual decoder, using the mime to select
        try {
            codec = MediaCodec.createDecoderByType(mime);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Check we have a valid codec instance
        if (codec == null) {
            //if (events != null) handler.post(new Runnable() { @Override public void run() { events.onError();  } });
            return;
        }

        //state.set(PlayerStates.READY_TO_PLAY);
        //if (events != null) handler.post(new Runnable() { @Override public void run() { events.onStart(mime, sampleRate, channels, duration);  } });

        codec.configure(format, null, null, 0);
        codec.start();

        ByteBuffer[] codecInputBuffers = codec.getInputBuffers();
        ByteBuffer[] codecOutputBuffers = codec.getOutputBuffers();

        // Configure AudioTrack
        int channelConfiguration = channels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;
        int minSize = AudioTrack.getMinBufferSize(sampleRate, channelConfiguration, AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC, sampleRate, channelConfiguration,
                AudioFormat.ENCODING_PCM_16BIT, minSize, AudioTrack.MODE_STREAM);

        // Start playing, we will feed the AudioTrack later
        audioTrack.play();
        mMediaExtractor.selectTrack(0);

        // Start decoding
        final long kTimeOutUs = 1000;
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        boolean sawInputEOS = false;
        boolean sawOutputEOS = false;
        int noOutputCounter = 0;
        int noOutputCounterLimit = 10;

        mStreamStates.setState(MusicStreamStates.PLAYING);

        while (!sawOutputEOS && noOutputCounter < noOutputCounterLimit && !mStop) {

            // Pause implementation
            waitPlay();

            noOutputCounter++;

            // Read a buffer before feeding it to the decoder
            if (!sawInputEOS) {

                int inputBufIndex = codec.dequeueInputBuffer(kTimeOutUs);

                if (inputBufIndex >= 0) {

                    ByteBuffer dstBuf = codecInputBuffers[inputBufIndex];
                    int sampleSize = mMediaExtractor.readSampleData(dstBuf, 0);

                    if (sampleSize < 0) {

                        Log.d(LOG_TAG, "saw input EOS. Stopping playback");
                        sawInputEOS = true;
                        sampleSize = 0;

                    } else {
                        presentationTimeUs = mMediaExtractor.getSampleTime();
                        //Log.d(LOG_TAG, "current sample time: " + presentationTimeUs / 1000);

                        //final int percent = (duration == 0) ? 0 : (int) (100 * presentationTimeUs / duration);
                        //if (streamPlayerEvents != null) handler.post(new Runnable() { @Override public void run() { streamPlayerEvents.onPlayUpdate(percent, presentationTimeUs / 1000, duration / 1000);  } });
                    }

                    codec.queueInputBuffer(inputBufIndex, 0, sampleSize, presentationTimeUs, sawInputEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);

                    if (!sawInputEOS) mMediaExtractor.advance();

                } else {
                    Log.e(LOG_TAG, "inputBufIndex " + inputBufIndex);
                }
            } // !sawInputEOS

            // Decode to PCM and push it to the AudioTrack player
            int res = codec.dequeueOutputBuffer(info, kTimeOutUs);

            if (res >= 0) {
                if (info.size > 0) noOutputCounter = 0;

                int outputBufIndex = res;
                ByteBuffer buf = codecOutputBuffers[outputBufIndex];


                short[] chunk = new short[info.size / 2];
                buf.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(chunk);
                buf.clear();

                if (chunk.length > 0) {

                    if (mDataSourceSet) {
                        int shortCount = interleaveChannels(chunk, mDataSource, mShortOffset);
                        mShortOffset += info.size / 2;
                    }

                    // Write decoded PCM to the AudioTrack
                    audioTrack.write(chunk, 0, chunk.length);

                    /*if (mStreamStates.getState() != MusicStreamStates.PLAYING) {
                        // if (events != null) handler.post(new Runnable() { @Override public void run() { events.onPlay();  } });
                        mStreamStates.setState(MusicStreamStates.PLAYING);
                    }*/
                }

                codec.releaseOutputBuffer(outputBufIndex, false);

                if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    Log.d(LOG_TAG, "saw output EOS.");
                    sawOutputEOS = true;
                }

            } else if (res == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {

                codecOutputBuffers = codec.getOutputBuffers();
                Log.d(LOG_TAG, "output buffers have changed.");

            } else if (res == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {

                MediaFormat oformat = codec.getOutputFormat();
                Log.d(LOG_TAG, "output format has changed to " + oformat);

            } else {

                Log.d(LOG_TAG, "dequeueOutputBuffer returned " + res);
            }
        }

        Log.d(LOG_TAG, "stopping...");

        if(codec != null) {
            codec.stop();
            codec.release();
            codec = null;
        }

        if(audioTrack != null) {
            audioTrack.flush();
            audioTrack.release();
            audioTrack = null;
        }

        // clear source and the other globals
        /*mSourcePath = null;
        duration = 0;
        mime = null;
        sampleRate = 0; channels = 0; bitrate = 0;
        presentationTimeUs = 0; duration = 0;*/

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mPlayButton != null) {
                    mPlayButton.setText(R.string.txt_stream);
                }
            }
        });

        mStreamStates.setState(MusicStreamStates.STOPPED);
        mStop = true;

        /*if(noOutputCounter >= noOutputCounterLimit) {
            if (events != null) handler.post(new Runnable() { @Override public void run() { events.onError();  } });
        } else {
            if (events != null) handler.post(new Runnable() { @Override public void run() { events.onStop();  } });
        }*/
    }

    /**
     * Combine music channel with dance sequence data channel. This will delete one channel of the
     * stereo music playback of AudioTrack. Instead the (mono) dance sequence data channel will be
     * used.
     *
     * Attention: This makes the song unpleasant to listen to.
     *
     * @param chunk data buffer with the original stereo signal
     * @param dataSource data buffer with the dance sequence data
     * @param shortOffset samples streamed so far
     * @return number of samples interleaved
     */
    private int interleaveChannels(short[] chunk, ChoreographyManager dataSource, int shortOffset) {

        // Create data buffer, which will be filled with dance sequence pcm data
        short[] tmpDataBuffer = new short[chunk.length / 2];

        // Fill dance sequence pcm data into output buffer tmpDataBuffer
        int shortCount = dataSource.readDataStream(tmpDataBuffer, shortOffset);
        // shortCount should be equal to tmpDataBuffer.length

        // Data buffer index
        int idx = 0;

        /*
         * Replace every second signal entry
         * AudioTrack stereo signals are build like this:
         * left channel: {24, 45, 9...}
         * right channel: {58, 28, 12...}
         * stereo channel: {24, 58, 45, 28, 9, 12...}
         */
        for (int i = 1; i < chunk.length; i+=2) {
            chunk[i] = tmpDataBuffer[idx];
            idx++;
        }

        return tmpDataBuffer.length;
    }

    public boolean isPlaying() {
        return mStreamStates.isPlaying();
    }

    public boolean isReady() {
        return mStreamStates.isReadyToPlay();
    }

    @Override
    public void setSeekBarProgress(int progress) {
        // Update seek bar
        if (mSeekBar != null) {
            mSeekBar.setProgress(progress);
        }

        // Update seek bar text view current time
        if (mSeekBarCurrentTimeView != null) {
            mSeekBarCurrentTimeView.setText(Helper.songTimeFormat(progress));
        }
    }

    @Override
    public int getSeekBarProgress() {
        if (mSeekBar != null) {
            return mSeekBar.getProgress();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (mMediaExtractor != null) {
            return (int) mMediaExtractor.getSampleTime() / 1000;
        }
        return 0;
    }

    @Override
    public int getTotalTime() {
        return mMusicFile.getDurationInMilliSecs();
    }

    @Override
    public int getSampleRate() {
        return mMusicFile.getSampleRate();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (mSeekBar != null) {

            //Log.d(LOG_TAG, "seekBar: on progress changed");

            // Notify automatic scroll listener when seek bar progressed
            if (mEventListener != null) {
                mEventListener.startListening();
            }

            // If user interaction, set media player progress
            if (fromUser) {
                seekTo(progress);
                //Log.d(LOG_TAG, "fromUser: on progress changed");
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onClick(View v) {

        if (mPlayButton != null) {

            if (isPlaying()) {

                pause();

            } else {

                play();

                // Set seek bar progress to current song position
                if (mSeekBar != null) {
                    if (mMediaExtractor != null) {
                        int currentTime = (int) mMediaExtractor.getSampleTime() / 1000;
                        mSeekBar.setProgress(currentTime);
                    }
                }

                // Notify automatic scroll listener when media player progressed
                if (mEventListener != null) {
                    mEventListener.startListening();
                }
            }

            // Update button text value
            if (isPlaying()) {
                mPlayButton.setText(R.string.txt_pause);
            } else {
                mPlayButton.setText(R.string.txt_stream);
            }

            mPlayButton.setText("HUGO");
        }
    }

}
