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

    // Native call to vamp plugin: queen marry beat detection
    private native static int extractBeats(long handle, IntBuffer intBuffer, int intBufferSize);
}
