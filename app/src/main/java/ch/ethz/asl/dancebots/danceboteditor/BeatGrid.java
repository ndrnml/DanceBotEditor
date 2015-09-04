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
     * TODO Comment
     */
    public BeatGrid() {

        // Initialize int buffer which is used to store the beat grid information
        initBeatBuffer();
    }

    /**
     * TODO comment
     */
    private void initBeatBuffer() {

        // Allocate max expected number of beats times Integer size bytes
        ByteBuffer bb = ByteBuffer.allocateDirect(MAX_EXPECTED_BEATS * Integer.SIZE);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        mBeatBuffer = bb.asIntBuffer();

    }

    /**
     * TODO
     * @param numberOfBeats
     */
    private void setNumOfBeats(int numberOfBeats) {
        mNumberOfBeats = numberOfBeats;
    }

    /**
     * TODO comment
     * @return
     */
    public IntBuffer getBeatBuffer() {

        return mBeatBuffer;
    }
}
