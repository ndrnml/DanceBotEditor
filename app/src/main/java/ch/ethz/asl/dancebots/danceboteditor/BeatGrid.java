package ch.ethz.asl.dancebots.danceboteditor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * Created by andrin on 09.07.15.
 */
public class BeatGrid {

    private final int MAX_EXPECTED_BEATS = 1000;

    private IntBuffer mBeatBuffer;
    private int mNumberOfBeats;

    /**
     * The BeatGrid class is only used as an auxiliary class to pass the detected beats between native
     * and java code
     */
    public BeatGrid() {

        // Initialize int buffer which is used to store the beat grid information
        initBeatBuffer();
    }

    /**
     * This beat buffer data structure is needed for the communication between native and java code
     * All later computations will NOT be made on the IntBuffer data structure
     */
    private void initBeatBuffer() {

        // Allocate max expected number of beats times Integer size bytes
        ByteBuffer bb = ByteBuffer.allocateDirect(MAX_EXPECTED_BEATS * Integer.SIZE);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        mBeatBuffer = bb.asIntBuffer();
    }

    /**
     * Set the true number of detected beats
     * @param numberOfBeats
     */
    public void setNumOfBeats(int numberOfBeats) {
        mNumberOfBeats = numberOfBeats;
    }

    /**
     * Return the IntBuffer address. This is passed to the C++ code
     * @return IntBuffer
     */
    public IntBuffer getBeatBuffer() {

        return mBeatBuffer;
    }
}
