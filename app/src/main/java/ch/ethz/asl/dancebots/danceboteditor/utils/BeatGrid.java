package ch.ethz.asl.dancebots.danceboteditor.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * This class is exclusively for the data exchange between JNI (C++) and JAVA
 * It serves as a data container for the gained extracted beats
 */
public class BeatGrid {

    private final int MAX_EXPECTED_BEATS = 1000;

    // Chose int (32 bit) buffer because two channels (16 bit) are decoded
    private IntBuffer mBeatBuffer;
    private int mNumberOfBeats;

    /**
     * The BeatGrid class is only used as an auxiliary class to pass the detected beats between native
     * and java code
     */
    public BeatGrid() {

        // Initialize int buffer, which is later used to store the beat grid information
        initBeatBuffer();
    }

    /**
     * This beat buffer data structure is needed for the communication between native and java code
     * All later computations will NOT be made on the IntBuffer data structure
     * IntBuffer contains 32-bit ints to store 2 channels of 16-bit
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
     * Get the true number of detected beats
     * @return
     */
    public int getNumOfBeats() {
        return mNumberOfBeats;
    }

    /**
     * Return the IntBuffer address. This is passed to the C++ code
     * @return IntBuffer
     */
    public IntBuffer getBeatBuffer() {

        return mBeatBuffer;
    }
}
