package ch.ethz.asl.dancebots.danceboteditor.utils;

import java.nio.IntBuffer;

import ch.ethz.asl.dancebots.danceboteditor.model.BeatElement;

/**
 * Created by andrin on 25.10.15.
 */
public class BeatExtractor {

    protected BeatExtractor() {};

    /**
     *
     * @param buffer
     * @param bufferSize
     * @return number of beats detected
     */
    public static int extract(long handle, IntBuffer buffer, int bufferSize) {
        return extractBeats(handle, buffer, bufferSize);
    }

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

    private native static int extract(long handle, IntBuffer intBuffer, int intBufferSize, long startSample, long endSample);

    // Get total number of beats detected from selected song
    private native static int getNumBeatsDetected(long soundFileHandle);

    private native static int getNumSamplesProcessed(long soundFileHandle);
}
