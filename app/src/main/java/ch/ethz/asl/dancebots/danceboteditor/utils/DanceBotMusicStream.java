package ch.ethz.asl.dancebots.danceboteditor.utils;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.projection.MediaProjectionManager;
import android.os.*;
import android.os.Process;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.dialogs.StickyOkDialog;
import ch.ethz.asl.dancebots.danceboteditor.listener.MediaPlayerListener;

/**
 * Created by andrin on 28.01.16.
 */
public class DanceBotMusicStream implements Runnable, SeekBar.OnSeekBarChangeListener, MediaPlayerListener.OnMediaPlayerChangeListener {

    private String LOG_TAG = this.getClass().getSimpleName();

    private MediaPlayerListener mEventListener = null;
    private Handler handler = new Handler();

    private DanceBotMusicFile mMusicFile;
    private MusicStreamStates mStreamStates;

    private SeekBar mSeekBar;

    private MediaExtractor mMediaExtractor;
    private String mSourcePath;
    private boolean mStop = true;

    private String mime = null;
    private int sampleRate = 0, channels = 0, bitrate = 0;
    private long presentationTimeUs = 0, duration = 0;

    private StreamPlayback mDataSource;
    private boolean mDataSourceSet = false;
    private long mSampleCountMicroSecs = 0;
    private Button mPlayButton;

    /**
     * Interface for any instance that
     */
    /*public interface StreamPlayerEvents {
        void onStart(String mime, int sampleRate, int channels, long durationInMs);
        void onPlay();
        void onPlayUpdate(int percentage, long currentMs, long totalMs);
        void onStop();
    }*/

    /**
     * Interface that must be implemented by any instance that wants to stream media data
     */
    public interface StreamPlayback {

        void prepareStreamPlayback();

        int readDataStream(short[] outBuffer, long shortCount);
    }


    /**
     * Create new DanceBotMusicStream instance. This should only happen when a valid
     * DanceBotMusicFile is present.
     *
     * @param musicFile the music file which is the data source of the stream player
     */
    public DanceBotMusicStream(DanceBotMusicFile musicFile) {

        mStreamStates = new MusicStreamStates();

        setDataSource(musicFile);
    }

    public void setEventListener(MediaPlayerListener eventListener) {
        mEventListener = eventListener;
    }

    public void setDataSource(DanceBotMusicFile musicFile) {
        mMusicFile = musicFile;
        mSourcePath = mMusicFile.getSongPath();
    }

    public void setStreamSource(StreamPlayback dataSource) {
        mDataSource = dataSource;
        mDataSource.prepareStreamPlayback();
        mDataSourceSet = true;
    }

    public void setPlayButton(Button playButton) {
        mPlayButton = playButton;
    }

    public void setMediaPlayerSeekBar(SeekBar seekBar) {

        // Prepare seek bar for the selected song
        mSeekBar = seekBar;
        // Register stream player to seek bar
        CompositeSeekBarListener.registerListener(this);
    }

    // Stop streaming playback
    public void onStop() {

        if (isPlaying() && mEventListener != null) {
            mEventListener.stopListening(MediaPlayerListener.STOP_PLAYING_FLAG);
        }

        stop();
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

        mMediaExtractor.getTrackFormat(0); // TODO: is this necessary? Redundant

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

                // Create new short buffer, info.size is the amount of data (in bytes) in the buffer
                short[] chunk = new short[info.size / 2];
                buf.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(chunk);
                buf.clear();

                if (chunk.length > 0) {

                    if (mDataSourceSet) {
                        mSampleCountMicroSecs = mMediaExtractor.getSampleTime();
                        interleaveChannels(chunk, mDataSource, mSampleCountMicroSecs);
                        //Log.d(LOG_TAG, "microsecs count: " + mSampleCountMicroSecs);
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

        onCompletion();
    }

    /**
     * Start stream playback.
     */
    private void startPlay() {

        if (mStreamStates.getState() == MusicStreamStates.STOPPED) {
            mStop = false;
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
    private synchronized void syncNotify() {
        notify();
    }

    /**
     * Synchronized wait if the player is on pause. Pause the media stream player.
     * This causes the Thread to spin in a wait loop.
     */
    private synchronized void waitPlay() {

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
    private void stop() {
        mStop = true;
    }

    /**
     * Seek to closest position
     *
     * @param positionInMilliSeconds position in milliseconds
     */
    private void seekTo(long positionInMilliSeconds) {
        if (mMediaExtractor != null) {
            // MediaExtractor expects microseconds
            mMediaExtractor.seekTo(positionInMilliSeconds * 1000, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
        }
    }

    private void onCompletion() {

        mStreamStates.setState(MusicStreamStates.STOPPED);
        mStop = true;

        if (mEventListener != null) {
            mEventListener.stopListening(MediaPlayerListener.DEFAULT_FLAG);
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mPlayButton != null) {
                    mPlayButton.setText(R.string.txt_stream);
                }
            }
        });
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
     * @param sampleCountMicroSecs samples streamed so far
     * @return number of samples interleaved
     */
    private int interleaveChannels(short[] chunk, StreamPlayback dataSource, long sampleCountMicroSecs) {

        // Create data buffer, which will be filled with dance sequence pcm data
        short[] tmpDataBuffer = new short[chunk.length / 2];

        // Fill dance sequence pcm data into output buffer tmpDataBuffer
        int shortCount = dataSource.readDataStream(tmpDataBuffer, sampleCountMicroSecs);
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

        return idx;
    }

    @Override
    public void play() {
        startPlay();
    }

    @Override
    public void pause() {
        mStreamStates.setState(MusicStreamStates.READY_TO_PLAY);
    }

    @Override
    public boolean isReady() {
        // Check that streaming is only possible with attached head set (cable)
        if (MusicIntentReceiver.isHeadSetPlugged()) {
            return true;
        } else {
            Context context = DanceBotEditorManager.getInstance().getContext();
            new StickyOkDialog()
                    .setTitle("Streaming not possible!")
                    .setMessage("Please connect the audio cable to the Dance Bot")
                    .show(((Activity) context).getFragmentManager(), "ok_dialog");
            return false;
        }
    }

    @Override
    public boolean isPlaying() {
        return mStreamStates.isPlaying();
    }

    @Override
    public void setPlayButtonPlay() {
        mPlayButton.setText(R.string.txt_stream);
    }

    @Override
    public void setPlayButtonPause() {
        mPlayButton.setText(R.string.txt_pause);
    }

    @Override
    public Button getPlayButton() {
        return mPlayButton;
    }

    @Override
    public int getCurrentPosition() {
        if (mMediaExtractor != null) {
            return (int) mMediaExtractor.getSampleTime() / 1000;
        }
        return 0;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (mSeekBar != null) {

            // If user interaction, set media player progress
            if (fromUser) {
                seekTo(progress);
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

}
