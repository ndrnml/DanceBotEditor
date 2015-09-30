package ch.ethz.asl.dancebots.danceboteditor.model;

/**
 * Created by andrin on 28.08.15.
 */
public abstract class BeatElement<T extends MotionType> {

    // Information about the beat element's choreography
    protected int mMotionStartIndex;
    protected int mMotionLength;

    // Song properties of a beat element
    protected int mBeatPosition;
    protected int mSamplePosition;

    // Choreography properties of a beat element
    protected int mColor;
    protected T[] mMotionTypes;

    protected int mMotionTypeIdx;
    protected int mFrequencyIdx;
    protected int mVelocityIdx;
    protected int mLedLightsIdx;
    protected int mChoreoLengthIdx;

    // TODO
    public BeatElement() {

        // Default values
        mMotionTypeIdx = 0;
        mFrequencyIdx = 0;
        mVelocityIdx = 0;
        mLedLightsIdx = 0; // TODO
        mChoreoLengthIdx = 0;
    }

    /**
     * Update beat element properties according to changes of motion
     */
    public abstract void updateProperties();

    public abstract void setProperties(BeatElement elem);

    // This method is just for testing purposes and might be removed in future time
    public abstract String getTypeAsString();

    ///////////
    // SETTERS
    ///////////

    public void setMotionStartIndex(int idx) {
        mMotionStartIndex = idx;
    }
    public void setMotionLength(int length) {
        mMotionLength = length;
    }
    public void setMotionTypes(T[] types) {
        mMotionTypes = types;
    }
    public void setMotionTypeIdx(int idx) {
        mMotionTypeIdx = idx; // TODO Check that idx is a valid number?
    }
    public void setFrequencyIdx(int idx) {
        mFrequencyIdx = idx; // TODO Check that idx is a valid number?
    }
    public void setVelocityIdx(int idx) {
        mVelocityIdx = idx; // TODO Check that idx is a valid number?
    }
    public void setChoreoLengthIdx(int idx) {
        mChoreoLengthIdx = idx; // TODO Check that idx is a valid number?
    }



    public int getMotionStartIndex() {
        return mMotionStartIndex;
    }
    public int getMotionLength() {
        return mMotionLength;
    }

    public int getBeatPosition() {
        return mBeatPosition;
    }
    public String getBeatPositionAsString() {
        return Integer.toString(mBeatPosition);
    }
    public int getColor() {
        return mColor;
    }
    public T getMotionType() {
        return mMotionTypes[mMotionTypeIdx];
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
