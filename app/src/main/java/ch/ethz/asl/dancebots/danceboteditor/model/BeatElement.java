package ch.ethz.asl.dancebots.danceboteditor.model;

import android.content.Context;

/**
 * Created by andrin on 28.08.15.
 */
public abstract class BeatElement<T extends MotionType> {

    // Context properties
    protected Context mContext;

    // Choreography properties of a beat element
    protected boolean mHasChoreography;
    protected int mChoreoStartIdx;
    protected int mChoreoLength;
    protected int mColor;
    protected T[] mMotionTypes;
    protected String mChoreoTag;

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

        // Default choreogrpahy values
        mHasChoreography = false;
        mChoreoStartIdx = -1;
        mChoreoLength = -1;
        mChoreoTag = "";

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
    public void setProperties(int choreoStartIdx, int choreoLength, int motionTypeIdx, int frequencyIdx, int choreoLengthIdx) {

        // TODO reduce redundancy
        // Set all choreography properties
        mChoreoStartIdx = choreoStartIdx;
        mChoreoLength = choreoLength;
        mMotionTypeIdx = motionTypeIdx;
        mFrequencyIdx = frequencyIdx;
        mChoreoLengthIdx = choreoLengthIdx;
        mHasChoreography = true;

        // Set beat element color and tag
        setColorAndTag();
    }

    /**
     * Copy beat element properties from the argument element
     * @param elem
     */
    public void setProperties(BeatElement elem) {

        setProperties(elem.getChoreoStartIdx(), elem.getChoreoLength(), elem.getMotionTypeIdx(), elem.getFrequencyIdx(), elem.getChoreoLengthIdx());
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

    ///////////
    // SETTERS
    ///////////

    protected abstract void setColorAndTag();

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
    public int getColor() {
        return mColor;
    }
    public T getMotionType() {
        return mMotionTypes[mMotionTypeIdx];
    }
    public String getChoreoTag() {
        return mChoreoTag;
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
}
