package ch.ethz.asl.dancebots.danceboteditor.utils;

import java.nio.IntBuffer;

/**
 * Created by andrin on 25.10.15.
 */
public class BeatExtractor {

    public int extract(IntBuffer buffer, int bufferSize) {
        return extractBeats(buffer, bufferSize);
    }

    // Native call to vamp plugin: queen marry beat detection
    private native static int extractBeats(IntBuffer intBuffer, int intBufferSize);
}
