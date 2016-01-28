package ch.ethz.asl.dancebots.danceboteditor.utils;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by andrin on 28.01.16.
 */
public class DanceBotMusicStream implements Runnable {

    private String LOG_TAG = this.getClass().getSimpleName();

    private String sourcePath;

    public DanceBotMusicStream(String pathToSoundFile) {

        sourcePath = pathToSoundFile;
    }

    @Override
    public void run() {

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

        AudioTrack audioTrack;
        MediaCodec codec = null;
        String mime = null;
        int sampleRate = 0, channels = 0, bitrate = 0;
        long presentationTimeUs = 0, duration = 0;
        boolean stop = false;

        // extractor gets information about the stream
        MediaExtractor extractor = new MediaExtractor();

        // try to set the source, this might fail
        try {
            if (sourcePath != null) extractor.setDataSource(this.sourcePath);
        } catch (Exception e) {
            Log.e(LOG_TAG, "exception:" + e.getMessage());
            //if (events != null) handler.post(new Runnable() { @Override public void run() { events.onError();  } });
            return;
        }

        // Read track header
        MediaFormat format = null;

        try {
            format = extractor.getTrackFormat(0);
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
        extractor.selectTrack(0);

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
                    int sampleSize = extractor.readSampleData(dstBuf, 0);

                    if (sampleSize < 0) {
                        Log.d(LOG_TAG, "saw input EOS. Stopping playback");
                        sawInputEOS = true;
                        sampleSize = 0;
                    } else {
                        presentationTimeUs = extractor.getSampleTime();
                        final int percent = (duration == 0) ? 0 : (int) (100 * presentationTimeUs / duration);
                        //if (events != null) handler.post(new Runnable() { @Override public void run() { events.onPlayUpdate(percent, presentationTimeUs / 1000, duration / 1000);  } });
                    }

                    codec.queueInputBuffer(inputBufIndex, 0, sampleSize, presentationTimeUs, sawInputEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);

                    if (!sawInputEOS) extractor.advance();

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
        sourcePath = null;
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