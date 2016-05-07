package org.vamp.beatextraction;

import java.nio.IntBuffer;

import ch.ethz.asl.dancebots.danceboteditor.utils.BeatExtractor;

/**
 * Created by andrin on 25.10.15.
 */
public class VampBeatExtractor implements BeatExtractor {

    public VampBeatExtractor() {};

    /**
     * Extract beats for a selected song file
     *
     * @param handle song file handle (this is a pointer)
     * @param buffer buffer to store all detected beats
     * @param bufferSize buffer capacity
     * @return number of beats detected
     */
    public static int extract(long handle, IntBuffer buffer, int bufferSize) {

        return extractBeats(handle, buffer, bufferSize);
    }

    /**
     * Extract beats for a selected range: startSample to endSample
     * @param handle songFileHandle (this is a pointer)
     * @param buffer buffer to store all detected beats
     * @param startSample the sample from where the beat detections should start
     * @param endSample the sample where the beat detection should finish
     * @return number of extracted beats (can be zero or less, in case of error)
     */
    @Override
    public int extract(long handle, IntBuffer buffer, long startSample, long endSample) {
        return extract(handle, buffer, buffer.capacity(), startSample, endSample);
    }

    @Override
    public int getNumberOfProcessedSamples(long handle) {
        return getProcessedSamples();
    }

    @Override
    public int close(long handle) {
        return cleanUp(handle);
    }

    // Native call to vamp plugin: queen marry beat detection
    private native static int extractBeats(long handle, IntBuffer intBuffer, int intBufferSize);

    // Extract beats from startSample to endSample
    private native static int extract(long handle, IntBuffer intBuffer, int intBufferSize, long startSample, long endSample);

    // Delete native sound file
    private native static int cleanUp(long mSoundFileHandle);

    private native static int getProcessedSamples();
}
