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

    protected String mMotionTypeString;
    protected String mFrequencyString;
    protected String mVelocityString;
    protected String mChoreoLengthString;
    /**
     * Update beat element properties according to changes of motion
     */
    abstract void updateProperties();

    public void setMotionTypeString(String s) {
        mMotionTypeString = s;
    }
    public void setFrequencyString(String s) {
        mFrequencyString = s;
    }
    public void setVelocityString(String s) {
        mVelocityString = s;
    }
    public void setChoreoLengthString(String s) {
        mChoreoLengthString = s;
    }

    /**
     * TODO for testing. to remove!
     */
    public abstract String getTypeAsString();

    public MotionType getMotionType() {
        return mMotionType;
    }

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

    public String getMotionTypeString() {
        return mMotionTypeString;
    }
    public String getFrequencyString() {
        return mFrequencyString;
    }
    public String getVelocityString() {
        return mVelocityString;
    }
    public String getChoreoLengthString() {
        return mChoreoLengthString;
    }
}
