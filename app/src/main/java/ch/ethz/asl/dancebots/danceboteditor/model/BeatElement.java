package ch.ethz.asl.dancebots.danceboteditor.model;

/**
 * Created by andrin on 28.08.15.
 */
public abstract class BeatElement<T extends MotionType> {

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
    public abstract String getType();

    /**
     * TODO to test. remove later
     */
    public String getBeatPositionAsString() {
        return Integer.toString(mBeatPosition);
    }

    /**
     *
     * @return
     */
    public int getColor() {
        return mColor;
    }
}
