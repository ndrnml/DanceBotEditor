package ch.ethz.asl.dancebots.danceboteditor.model;

import android.content.Context;

/**
 * Created by andrin on 28.08.15.
 */
public abstract class BeatElement {

    // Context properties
    protected static Context mContext;

    // Choreography properties of a beat element
    protected boolean mHasChoreography;
    protected int mChoreoStartIdx;
    protected int mChoreoLength;

    // Song properties of a beat element
    protected int mBeatPosition;
    protected long mSamplePosition;
    protected boolean mIsFocused;

    // Menu property index of a beat element
    protected MotionType mMotionType;
    protected int mMotionTypeIdx;
    protected int mFrequencyIdx;
    protected int mChoreoLengthIdx;

    public BeatElement(Context context, int beatPosition, long samplePosition) {

        mContext = context;

        // Song properties
        mBeatPosition = beatPosition;
        mSamplePosition = samplePosition;
        mIsFocused = false;

        // Default choreogrpahy values
        mHasChoreography = false;
        mChoreoStartIdx = -1;
        mChoreoLength = -1;

        // Default menu indices
        mMotionTypeIdx = 0;
        mFrequencyIdx = 0;
        mChoreoLengthIdx = 0;
    }

    public void setDefaultProperties() {
        // TODO;
    }

    /**
     * Set beat element properties
     * @param choreoStartIdx
     * @param choreoLength
     * @param motionTypeIdx
     * @param frequencyIdx
     * @param choreoLengthIdx
     */
    public void setProperties(int choreoStartIdx, int choreoLength, int motionTypeIdx, int frequencyIdx, int choreoLengthIdx, MotionType type) {

        // Set all choreography properties
        mChoreoStartIdx = choreoStartIdx;
        mChoreoLength = choreoLength;
        mMotionTypeIdx = motionTypeIdx;
        mFrequencyIdx = frequencyIdx;
        mChoreoLengthIdx = choreoLengthIdx;
        mHasChoreography = true;
        mMotionType = type;
    }

    /**
     * Copy beat element properties from the argument element
     * @param elem
     */
    public void setProperties(BeatElement elem) {

        setProperties(elem.getChoreoStartIdx(), elem.getChoreoLength(), elem.getMotionTypeIdx(), elem.getFrequencyIdx(), elem.getChoreoLengthIdx(), elem.getMotionType());
    }

    public boolean isSameChoreography(BeatElement elem) {

        if (mChoreoStartIdx != elem.getChoreoStartIdx() ||
            mChoreoLength != elem.getChoreoLength() ||
            mMotionTypeIdx != elem.getMotionTypeIdx() ||
            mFrequencyIdx != elem.getFrequencyIdx() ||
            mChoreoLengthIdx != elem.getChoreoLengthIdx()) {

            return false;

        } else {

            return true;
        }
    }

    public abstract boolean hasChoreography();

    /*********
     * SETTERS
     *********/
    public void setFocus(boolean isFocused) {
        mIsFocused = isFocused;
    }
    public void setMotionType(MotionType t) {
        mMotionType = t;
    }
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

    public int getChoreoStartIdx() {
        return mChoreoStartIdx;
    }
    public int getChoreoLength() {
        return mChoreoLength;
    }
    public int getBeatPosition() {
        return mBeatPosition;
    }
    public String getBeatPositionAsString() {
        return Integer.toString(mBeatPosition);
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
    public MotionType getMotionType() {
        return mMotionType;
    }
    public boolean getFocus() {
        return mIsFocused;
    }
    public long getSamplePosition() {
        return mSamplePosition;
    }
}
