package ch.ethz.asl.dancebots.danceboteditor.utils;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by andrin on 24.02.16.
 */
public class AACEncoder {

    private String LOG_TAG;

    public void todo() {


        MediaCodec encoder = null;
        MediaCodec decoder = null;

        // MediaExtractor gets information about the input file
        MediaExtractor mediaExtractor = new MediaExtractor();

        // Set the MediaExtractor source to the selected music file
        try {

            //if (mSoundTask.getMusicFile().getSongPath() != null) mediaExtractor.setDataSource(mSoundTask.getMusicFile().getSongPath());

        } catch (Exception e) {
            Log.e(LOG_TAG, "could not set data source to media extractor.");
            return;
        }

        // Create media format from selected source
        MediaFormat format;

        String mime;
        int sampleRate = 0;
        int channels;
        int bitrate = 0;
        long duration;

        // Read media codec information
        try {

            format = mediaExtractor.getTrackFormat(0);
            mime = format.getString(MediaFormat.KEY_MIME);
            sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
            channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
            duration = format.getLong(MediaFormat.KEY_DURATION);
            //bitrate = format.getInteger(MediaFormat.KEY_BIT_RATE);

            Log.d(LOG_TAG, "Track info: mime:" + mime + " sampleRate:" + sampleRate + " channels:" + channels + " bitrate:" + bitrate + " duration:" + duration);

            // check we have audio content we know
            if (!mime.startsWith("audio/")) {
                Log.d(LOG_TAG, "Error: Format or MIME incorrect");
                return;
            }

            // Create a MediaCodec for the decoder, just based on the MIME type. The various
            // format details will be passed through the csd-0 meta-data later on.
            decoder = MediaCodec.createDecoderByType(mime);
            //decoderFormat = MediaFormat.createAudioFormat(mime, SAMPLE_RATE, CHANNEL_COUNT); TODO: is this necessary?
            decoder.configure(format, null, null, 0);
            decoder.start();

            // Create a MediaCodec for the desired codec, then configure it as an encoder with
            // our desired properties.
            MediaCodecInfo codecInfo = selectCodec(mime);
            encoder = MediaCodec.createByCodecName(codecInfo.getName());
            encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            encoder.start();

            final int TIMEOUT_USEC = 10000;
            ByteBuffer[] encoderInputBuffers = encoder.getInputBuffers();
            ByteBuffer[] decoderInputBuffers = decoder.getInputBuffers();
            ByteBuffer[] decoderOutputBuffers = decoder.getOutputBuffers();

            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

            // Save a copy to disk.  Useful for debugging the test.  Note this is a raw elementary
            // stream, not a .mp4 file, so not all players will know what to do with it.
                /*FileOutputStream outputStream = null;
                if (DEBUG_SAVE_FILE) {
                    String fileName = DEBUG_FILE_NAME_BASE + mWidth + "x" + mHeight + ".mp4";
                    try {
                        outputStream = new FileOutputStream(fileName);
                        Log.d(TAG, "encoded output will be saved as " + fileName);
                    } catch (IOException ioe) {
                        Log.w(TAG, "Unable to create debug output file " + fileName);
                        throw new RuntimeException(ioe);
                    }
                }*/

            // Loop until the output side is done.
            boolean inputDone = false;
            boolean decoderDone = false;
            boolean outputDone = false;

            while (!outputDone) {

                if (!inputDone) {

                    int inputBufIndex = decoder.dequeueInputBuffer(TIMEOUT_USEC);

                    if (inputBufIndex >= 0) {

                        ByteBuffer dstBuf = decoderInputBuffers[inputBufIndex];
                        int sampleSize = mediaExtractor.readSampleData(dstBuf, 0);

                        if (sampleSize < 0) {
                            // Send an empty frame with the end-of-stream flag set.
                            Log.d(LOG_TAG, "signaling input EOS");
                            decoder.signalEndOfInputStream();
                            //decoder.queueInputBuffer(inputBufIndex, 0, 0, info.presentationTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                            inputDone = true;

                        } else {

                            decoder.queueInputBuffer(inputBufIndex, 0, info.size, info.presentationTimeUs, info.flags);
                            mediaExtractor.advance();
                        }
                    }
                }

                // Assume output is available. Loop until both assumptions are false.
                boolean decoderOutputAvailable = !decoderDone;
                boolean encoderOutputAvailable = true;
                while (decoderOutputAvailable || encoderOutputAvailable) {
                    // Start by draining any pending output from the decoder. It's important to
                    // do this before we try to stuff any more data in.
                    //int decoderStatus = decoder.dequeueOutputBuffer(info, TIMEOUT_USEC);
                    int encoderStatus = encoder.dequeueOutputBuffer(info, TIMEOUT_USEC);
                    if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        // no output available yet
                        Log.d(LOG_TAG, "no output from encoder available");
                        //decoderOutputAvailable = false;
                        encoderOutputAvailable = false;
                    } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                        Log.d(LOG_TAG, "encoder output buffers changed (but we don't care)");
                    } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        // this happens before the first frame is returned
                        //MediaFormat decoderOutputFormat = decoder.getOutputFormat();
                        MediaFormat encoderOutputFormat = encoder.getOutputFormat();
                        Log.d(LOG_TAG, "encoder output format changed: " + encoderOutputFormat);
                    } else if (encoderStatus < 0) {
                        Log.d(LOG_TAG, "unexpected result from encoder.dequeueOutputBuffer: " + encoderStatus);
                    } else {  // encoderStatus >= 0
                        Log.d(LOG_TAG, "surface decoder given buffer " + encoderStatus + " (size=" + info.size + ")");
                        if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            Log.d(LOG_TAG, "output EOS");
                            outputDone = true;
                        }

                        // The ByteBuffers are null references, but we still get a nonzero size for the decoded data.
                        boolean doWrite = (info.size != 0);

                        // As soon as we call releaseOutputBuffer, the buffer will be forwarded
                        // to TODO.
                        encoder.releaseOutputBuffer(encoderStatus, doWrite);

                        if (doWrite) {
                            // TODO
                            Log.d(LOG_TAG, "write: " + info.size);
                        }
                    }

                    if (encoderStatus != MediaCodec.INFO_TRY_AGAIN_LATER) {
                        // Continue attempts to drain output.
                        continue;
                    }

                    // Encoder is drained, check to see if we've got a new buffer of output from the decoder
                    if (!decoderDone) {
                        int decoderStatus = decoder.dequeueOutputBuffer(info, TIMEOUT_USEC);
                        if (decoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                            // no output available yet
                            Log.d(LOG_TAG, "no output from decoder available");
                            decoderOutputAvailable = false;
                        } else if (decoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                            // not expected for an encoder
                            decoderOutputBuffers = decoder.getOutputBuffers();
                            Log.d(LOG_TAG, "decoder output buffers changed");
                        } else if (decoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                            // not expected for an encoder
                            MediaFormat newFormat = decoder.getOutputFormat();
                            Log.d(LOG_TAG, "decoder output format changed: " + newFormat);
                        } else if (decoderStatus < 0) {
                            Log.d(LOG_TAG, "unexpected result from encoder.dequeueOutputBuffer: " + decoderStatus);
                        } else { // encoderStatus >= 0
                            //ByteBuffer encodedData = decoderOutputBuffers[decoderStatus];
                            ByteBuffer decodedData = decoderOutputBuffers[decoderStatus];
                            if (decodedData == null) {
                                Log.d(LOG_TAG, "decoderOutputBuffer " + decoderStatus + " was null");
                            }

                            // It's usually necessary to adjust the ByteBuffer values to match BufferInfo.
                            decodedData.position(info.offset);
                            decodedData.limit(info.offset + info.size);

                            // Get a encoder input buffer, blocking until it's available. We just
                            // drained the decoder output, so we expect there to be a free input
                            // buffer now or in the near future (i.e. this should never deadlock
                            // if the codec is meeting requirements).
                            //
                            // The first buffer of data we get will have the BUFFER_FLAG_CODEC_CONFIG
                            // flag set; the encoder will see this and finish configuring itself.
                            int inputBufIndex = encoder.dequeueInputBuffer(-1);
                            ByteBuffer inputBuf = encoderInputBuffers[inputBufIndex];
                            inputBuf.clear();
                            inputBuf.put(decodedData);
                            encoder.queueInputBuffer(inputBufIndex, 0, info.size, info.presentationTimeUs, info.flags);

                            // If everything from the decoder has been passed to the encoder, we
                            // can stop polling the decoder output. (This just an optimization.)
                            if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                                decoderDone = true;
                                decoderOutputAvailable = false;
                            }
                            Log.d(LOG_TAG, "passed " + info.size + " bytes to decoder" + (decoderDone ? " (EOS)" : ""));
                            decoder.releaseOutputBuffer(decoderStatus, false);
                        }
                    }
                }
            }

        } catch (IOException e) {

            Log.d(LOG_TAG, "Failded to decode and encode mp3");

        } finally {

            if (encoder != null) {
                encoder.stop();
                encoder.release();
            }

            if (decoder != null) {
                decoder.stop();
                decoder.release();
            }
        }
    }

    private static MediaCodecInfo selectCodec(String mimeType) {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);

            if (!codecInfo.isEncoder()) {
                continue;
            }

            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(mimeType)) {
                    return codecInfo;
                }
            }
        }
        return null;
    }


}
