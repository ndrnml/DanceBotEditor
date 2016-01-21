package ch.ethz.asl.dancebots.danceboteditor.utils;

import java.nio.IntBuffer;

import ch.ethz.asl.dancebots.danceboteditor.model.BeatElement;

/**
 * Created by andrin on 25.10.15.
 */
public class BeatExtractor {

    protected BeatExtractor() {};

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
    public static int extract(long handle, IntBuffer buffer, long startSample, long endSample) {
        return extract(handle, buffer, buffer.capacity(), startSample, endSample);
    }

    public static int getNumberOfBeatsDetected(long handle) {
        return getNumBeatsDetected(handle);
    }

    public static int getNumberOfProcessedSamples(long handle) {
        return getNumSamplesProcessed(handle);
    }

    // Native call to vamp plugin: queen marry beat detection
    private native static int extractBeats(long handle, IntBuffer intBuffer, int intBufferSize);

    // Extract beats from startSample to endSample
    private native static int extract(long handle, IntBuffer intBuffer, int intBufferSize, long startSample, long endSample);

    // Delete native sound file
    private native static int cleanUp(long mSoundFileHandle);

    // Get total number of beats detected from selected song
    private native static int getNumBeatsDetected(long soundFileHandle);

    private native static int getNumSamplesProcessed(long soundFileHandle);
}
