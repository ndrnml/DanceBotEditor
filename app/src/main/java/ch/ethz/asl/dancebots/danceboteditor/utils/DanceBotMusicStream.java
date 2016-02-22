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
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.asl.dancebots.danceboteditor.handlers.AutomaticScrollHandler;
import ch.ethz.asl.dancebots.danceboteditor.model.ChoreographyManager;

/**
 * Created by andrin on 28.01.16.
 */
public class DanceBotMusicStream implements Runnable {

    private String LOG_TAG = this.getClass().getSimpleName();

    private AutomaticScrollHandler streamPlayerEvents = null;
    private Handler handler = new Handler();

    private final DanceBotMusicFile mMusicFile;
    private MusicStreamStates mStreamStates;

    private TextView mSeekBarTotalTimeView;
    private TextView mSeekBarCurrentTimeView;
    private SeekBar mSeekBar;

    private MediaExtractor mMediaExtractor;
    private String mSourcePath;
    boolean stop = true;
    private Thread mThread = null;

    private String mime = null;
    private int sampleRate = 0, channels = 0, bitrate = 0;
    private long presentationTimeUs = 0, duration = 0;

    private ChoreographyManager mDataSource;
    private boolean mDataSourceSet = false;
    private int mByteOffset;

    /**
     * Constructor
     * @param musicFile
     */
    public DanceBotMusicStream(DanceBotMusicFile musicFile) {

        mStreamStates = new MusicStreamStates();
        mMusicFile = musicFile;
        mSourcePath = mMusicFile.getSongPath();
    }

    public void setEventListener(AutomaticScrollHandler eventListener) {
        streamPlayerEvents = eventListener;
    }

    public void setDataSource(final ChoreographyManager dataSource) {
        mDataSource = dataSource;
        mDataSourceSet = true;
    }

    public void setMediaPlayerSeekBar(SeekBar seekBar, TextView currentTime, TextView totalTime) {
/*
        // Prepare seek bar for the selected song
        mSeekBar = seekBar;
        mSeekBar.setClickable(true);
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setMax(mMediaPlayer.getDuration());

        // Init seek bar labels
        mSeekBarCurrentTimeView = currentTime;
        mSeekBarTotalTimeView = totalTime;

        mSeekBarCurrentTimeView.setText(songTimeFormat(0));
        mSeekBarTotalTimeView.setText(songTimeFormat(0));*/
    }

    public void play() {

        if (mStreamStates.getState() == MusicStreamStates.STOPPED) {
            stop = false;
            // Set number of bytes written initially to zero
            mByteOffset = 0;
            mThread = new Thread(this);
            mThread.start();
        }

        if (mStreamStates.getState() == MusicStreamStates.READY_TO_PLAY) {
            mStreamStates.setState(MusicStreamStates.PLAYING);
            syncNotify();
        }
    }

    public synchronized void syncNotify() {
        notify();
    }

    public synchronized void waitPlay() {

        while (mStreamStates.getState() == MusicStreamStates.READY_TO_PLAY) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        stop = true;
    }

    public void pause() {
        mStreamStates.setState(MusicStreamStates.READY_TO_PLAY);
    }

    public void seekTo(long position) {
        if (mMediaExtractor != null) {
            mMediaExtractor.seekTo(position, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
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

        while (!sawOutputEOS && noOutputCounter < noOutputCounterLimit && !stop) {

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
                        final int percent = (duration == 0) ? 0 : (int) (100 * presentationTimeUs / duration);
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

                    // TODO: Check channels
                    /*if (mDataSourceSet) {
                        chunk = interleaveChannels(chunk, mDataSource, mByteOffset);
                        mByteOffset += info.size;
                    }*/

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
        mSourcePath = null;
        duration = 0;
        mime = null;
        sampleRate = 0; channels = 0; bitrate = 0;
        presentationTimeUs = 0; duration = 0;

        mStreamStates.setState(MusicStreamStates.STOPPED);
        stop = true;

        /*if(noOutputCounter >= noOutputCounterLimit) {
            if (events != null) handler.post(new Runnable() { @Override public void run() { events.onError();  } });
        } else {
            if (events != null) handler.post(new Runnable() { @Override public void run() { events.onStop();  } });
        }*/
    }

    private byte[] interleaveChannels(byte[] chunk, ChoreographyManager dataSource, int byteOffset) {

        byte[] tmpBuffer = new byte[2 * chunk.length];
        short[] tmpDataBuffer = new short[(chunk.length + 1) / 2];
        byte[] tmpDataBufferByte = new byte[chunk.length];

        dataSource.readDataChannel(tmpDataBuffer, byteOffset);

        int byteIdx = 0;

        for (int shortIdx = 0; shortIdx < tmpDataBuffer.length; ++shortIdx) {

            tmpDataBufferByte[byteIdx] = (byte) (tmpDataBuffer[shortIdx] & 0x00FF);
            tmpDataBufferByte[byteIdx + 1] = (byte) ((tmpDataBuffer[shortIdx] & 0xFF00) >> 8);

            byteIdx += 2;
        }

        for (int i = 0; i < chunk.length; ++i) {

            tmpBuffer[(2 * i)] = chunk[i];
            tmpBuffer[(2 * i) + 1] = tmpDataBufferByte[i];
        }

        return tmpBuffer;
    }

    public boolean isPlaying() {
        return mStreamStates.isPlaying();
    }
}
