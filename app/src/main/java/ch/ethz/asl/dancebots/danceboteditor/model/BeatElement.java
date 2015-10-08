package ch.ethz.asl.dancebots.danceboteditor.model;

import android.content.Context;

/**
 * Created by andrin on 28.08.15.
 */
public abstract class BeatElement<T extends MotionType> {

    // Context properties
    protected Context mContext;

    // Choreography properties of a beat element
    protected int mChoreoStartIdx;
    protected int mChoreoLength;
    protected int mColor;
    protected T[] mMotionTypes;

    // Song properties of a beat element
    protected int mBeatPosition;
    protected long mSamplePosition;

    // Menu properties of a beat element
    protected int mMotionTypeIdx;
    protected int mFrequencyIdx;
    protected int mChoreoLengthIdx;

    // TODO
    public BeatElement(Context context, int beatPosition, long samplePosition) {

        // Context properties
        mContext = context;

        // Song properties
        mBeatPosition = beatPosition;
        mSamplePosition = samplePosition;

        // Default values
        mChoreoStartIdx = -1;
        mChoreoLength = -1;

        // Default menu indices
        mMotionTypeIdx = 0;
        mFrequencyIdx = 0;
        mChoreoLengthIdx = 0;
    }

    /**
     * Set beat element properties
     * @param choreoStartIdx
     * @param choreoLength
     * @param motionTypeIdx
     * @param frequencyIdx
     * @param choreoLengthIdx
     */
    public void setProperties(int choreoStartIdx, int choreoLength, int motionTypeIdx, int frequencyIdx, int choreoLengthIdx) {

        // Set all general beat element properties
        mChoreoStartIdx = choreoStartIdx;
        mChoreoLength = choreoLength;
        mMotionTypeIdx = motionTypeIdx;
        mFrequencyIdx = frequencyIdx;
        mChoreoLengthIdx = choreoLengthIdx;

        // Update color
        setColor();
    }

    /**
     * Copy beat element properties from the argument element
     * @param elem
     */
    public void setProperties(BeatElement elem) {

        mChoreoStartIdx = elem.getChoreoStartIdx();
        mChoreoLength = elem.getChoreoLength();
        mColor = elem.getColor();
        mMotionTypeIdx = elem.getMotionTypeIdx();
        mFrequencyIdx = elem.getFrequencyIdx();
        mChoreoLengthIdx = elem.getChoreoLengthIdx();
    }

    protected abstract void setColor();

    // This method is just for testing purposes and might be removed in future time
    public abstract String getTypeAsString();

    ///////////
    // SETTERS
    ///////////

    public void setMotionTypeIdx(int idx) {
        mMotionTypeIdx = idx; // TODO Check that idx is a valid number?
    }
    public void setFrequencyIdx(int idx) {
        mFrequencyIdx = idx; // TODO Check that idx is a valid number?
    }
    public void setChoreoLengthIdx(int idx) {
        mChoreoLengthIdx = idx; // TODO Check that idx is a valid number?
    }


    ///////////
    // GETTERS
    ///////////

    public int getMotionStartIndex() {
        return mChoreoStartIdx;
    }
    public int getMotionLength() {
        return mChoreoLength;
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
    public int getChoreoLengthIdx() {
        return mChoreoLengthIdx;
    }
    public int getChoreoStartIdx() {
        return mChoreoStartIdx;
    }
    public int getChoreoLength() {
        return mChoreoLength;
    }
}
