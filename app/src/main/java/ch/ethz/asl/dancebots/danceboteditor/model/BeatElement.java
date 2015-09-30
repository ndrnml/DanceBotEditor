package ch.ethz.asl.dancebots.danceboteditor.model;

/**
 * Created by andrin on 28.08.15.
 */
public abstract class BeatElement<T extends MotionType> {

    // Information about the beat element's motion
    protected int mMotionStartIndex;
    protected int mMotionLength;

    // Song properties of a beat element
    protected int mBeatPosition;
    protected int mSamplePosition;

    // Choreography properties of a beat element
    protected int mColor;
    protected T mMotionType;

    protected int mMotionTypeIdx;
    protected int mFrequencyIdx;
    protected int mVelocityIdx;
    protected int mLedLightsIdx;
    protected int mChoreoLengthIdx;

    // TODO
    public BeatElement() {
        // TODO
    }

    /**
     * Update beat element properties according to changes of motion
     */
    public abstract void updateProperties();

    /**
     * This method is just for testing purposes and might be removed in future time
     */
    public abstract String getTypeAsString();

    public void setMotionStartIndex(int idx) {
        mMotionStartIndex = idx;
    }
    public void setMotionLength(int length) {
        mMotionLength = length;
    }
    public void setMotionTypeIdx(int idx) {
        // TODO Check that idx is a valid number?
        mMotionTypeIdx = idx;
    }
    public void setFrequencyIdx(int idx) {
        // TODO Check that idx is a valid number?
        mFrequencyIdx = idx;
    }
    public void setVelocityIdx(int idx) {
        // TODO Check that idx is a valid number?
        mVelocityIdx = idx;
    }
    public void setChoreoLengthIdx(int idx) {
        // TODO Check that idx is a valid number?
        mChoreoLengthIdx = idx;
    }

    public MotionType getMotionType() {
        return mMotionType;
    }

    /**
     * TODO to test. remove later
     */
    public String getBeatPositionAsString() {
        return Integer.toString(mBeatPosition);
    }


    public int getMotionStartIndex() {
        return mMotionStartIndex;
    }
    public int getMotionLength() {
        return mMotionLength;
    }
    public int getColor() {
        return mColor;
    }

    public int getMotionTypeIdx() {
        return mMotionTypeIdx;
    }
    public int getFrequencyIdx() {
        return mFrequencyIdx;
    }
    public int getVelocityIdx() {
        return mVelocityIdx;
    }
    public int getChoreoLengthIdx() {
        return mChoreoLengthIdx;
    }
}
