package ch.ethz.asl.dancebots.danceboteditor;

import android.graphics.Color;

/**
 * Created by andrin on 28.08.15.
 */
abstract class BeatElement<T extends MotionType> {

    // Information about the beat element's motion
    protected int mMotionStartIndex;
    protected int mMotionLength;

    // Properties of a beat element
    protected int mBeatPosition;
    protected int mSamplePosition;
    protected int mColor;
    protected T mMotionType;

    /**
     * Update beat element properties according to changes of motion
     */
    abstract void updateProperties();

    // TODO for testing. to remove!
    abstract String getType();

    /**
     * TODO to test. remove later
     */
    String getBeatPositionAsString() {
        return Integer.toString(mBeatPosition);
    }

    /**
     *
     * @return
     */
    int getColor() {
        return mColor;
    }
}
