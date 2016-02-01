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

import ch.ethz.asl.dancebots.danceboteditor.handlers.AutomaticScrollHandler;

/**
 * Created by andrin on 28.01.16.
 */
public class DanceBotMusicStream implements Runnable {

    private String LOG_TAG = this.getClass().getSimpleName();

    private AutomaticScrollHandler streamPlayerEvents = null;
    private Handler handler = new Handler();

    private final DanceBotMusicFile mMusicFile;

    private TextView mSeekBarTotalTimeView;
    private TextView mSeekBarCurrentTimeView;
    private SeekBar mSeekBar;

    private MediaExtractor mMediaExtractor;
    private String mSourcePath;
    boolean stop = true;
    Thread thread = null;

    String mime = null;
    int sampleRate = 0, channels = 0, bitrate = 0;
    long presentationTimeUs = 0, duration = 0;

    /**
     * Constructor
     * @param musicFile
     */
    public DanceBotMusicStream(DanceBotMusicFile musicFile) {

        mMusicFile = musicFile;
        mSourcePath = mMusicFile.getSongPath();
    }

    public void setEventListener(AutomaticScrollHandler eventListener) {
        streamPlayerEvents = eventListener;
    }

    public void attachMediaPlayerSeekBar(SeekBar seekBar, TextView currentTime, TextView totalTime) {
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

        if (stop) {
            stop = false;
            thread = new Thread(this);
            thread.start();
        } else {
            stop = false;
            thread.stop();
        }
    }

    public void stop() {
        stop = true;
    }

    public void pause() {
        //state.set(PlayerStates.READY_TO_PLAY);
    }

    public void seek(long position) {
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
        boolean isAudioMime = false;

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

        try {
            format = mMediaExtractor.getTrackFormat(0);
            mime = format.getString(MediaFormat.KEY_MIME);
            sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
            channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
            // if duration is 0, we are probably playing a live stream
            duration = format.getLong(MediaFormat.KEY_DURATION);
            bitrate = format.getInteger(MediaFormat.KEY_BIT_RATE);
            isAudioMime = mime.startsWith("audio/");
        } catch (Exception e) {
            Log.e(LOG_TAG, "Reading format parameters exception:" + e.getMessage());
            // don't exit, tolerate this error, we'll fail later if this is critical
        }
        Log.d(LOG_TAG, "Track info: mime:" + mime + " sampleRate:" + sampleRate + " channels:" + channels + " bitrate:" + bitrate + " duration:" + duration);

        // check we have audio content we know
        if (format == null || !isAudioMime) {
            //if (events != null) handler.post(new Runnable() { @Override public void run() { events.onError();  } });
            Log.d(LOG_TAG, "Error: Format or MIME incorrect");
            return;
        }

        // create the actual decoder, using the mime to select
        try {
            codec = MediaCodec.createDecoderByType(mime);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // check we have a valid codec instance
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

        // configure AudioTrack
        int channelConfiguration = channels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;
        int minSize = AudioTrack.getMinBufferSize(sampleRate, channelConfiguration, AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, channelConfiguration,
                AudioFormat.ENCODING_PCM_16BIT, minSize, AudioTrack.MODE_STREAM);

        // start playing, we will feed the AudioTrack later
        audioTrack.play();
        mMediaExtractor.selectTrack(0);

        // start decoding
        final long kTimeOutUs = 1000;
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        boolean sawInputEOS = false;
        boolean sawOutputEOS = false;
        int noOutputCounter = 0;
        int noOutputCounterLimit = 10;

        //state.set(PlayerStates.PLAYING);
        while (!sawOutputEOS && noOutputCounter < noOutputCounterLimit && !stop) {

            // pause implementation
            //waitPlay();

            noOutputCounter++;
            // read a buffer before feeding it to the decoder
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

            // decode to PCM and push it to the AudioTrack player
            int res = codec.dequeueOutputBuffer(info, kTimeOutUs);

            if (res >= 0) {
                if (info.size > 0) noOutputCounter = 0;

                int outputBufIndex = res;
                ByteBuffer buf = codecOutputBuffers[outputBufIndex];

                final byte[] chunk = new byte[info.size];
                buf.get(chunk);
                buf.clear();
                if (chunk.length > 0) {
                    audioTrack.write(chunk, 0, chunk.length);
                	/*if(this.state.get() != PlayerStates.PLAYING) {
                		if (events != null) handler.post(new Runnable() { @Override public void run() { events.onPlay();  } });
            			state.set(PlayerStates.PLAYING);
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


        //state.set(PlayerStates.STOPPED);
        stop = true;

        /*if(noOutputCounter >= noOutputCounterLimit) {
            if (events != null) handler.post(new Runnable() { @Override public void run() { events.onError();  } });
        } else {
            if (events != null) handler.post(new Runnable() { @Override public void run() { events.onStop();  } });
        }*/
    }
}
