package ch.ethz.asl.dancebots.danceboteditor.model;

import android.content.Context;

/**
 * Created by andrin on 28.08.15.
 */
public abstract class BeatElement {

    // Context properties
    protected static Context mContext;

    // Choreography properties of a beat element
    protected boolean mHasDanceSequence;
    protected int mChoreoStartIdx;
    protected int mChoreoLength;

    // Song properties of a beat element
    protected final int mBeatPosition;
    protected final long mSamplePosition;

    // Menu property index of a beat element
    //protected MotionType mMotionType;
    protected int mMotionTypeIdx;
    protected int mFrequencyIdx;
    protected int mChoreoLengthIdx;

    // Absolute values for beat element properties
    protected float mFrequencyVal;

    public BeatElement(Context context, int beatPosition, long samplePosition) {

        mContext = context;

        // Song properties
        mBeatPosition = beatPosition;
        mSamplePosition = samplePosition;

        // Default choreogrpahy values
        mHasDanceSequence = false;
        mChoreoStartIdx = -1;
        mChoreoLength = -1;

        // Default menu indices
        mMotionTypeIdx = 0;
        mFrequencyIdx = 0;
        mChoreoLengthIdx = 0;
    }

    // TODO
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
    public void setProperties(
            int choreoStartIdx,
            int choreoLength,
            int motionTypeIdx,
            int frequencyIdx,
            float frequencyVal,
            int choreoLengthIdx/*,
            MotionType type*/) {

        // Set all choreography properties
        mChoreoStartIdx = choreoStartIdx;
        mChoreoLength = choreoLength;
        mMotionTypeIdx = motionTypeIdx;
        mFrequencyIdx = frequencyIdx;
        mFrequencyVal = frequencyVal;
        mChoreoLengthIdx = choreoLengthIdx;
        //mMotionType = type;
        mHasDanceSequence = true;
    }

    /**
     * Copy beat element properties from the argument element
     * @param elem the BeatElement from which all properties get copied
     */
    public void setProperties(BeatElement elem) {

        setProperties(elem.getChoreoStartIdx(),
                elem.getChoreoLength(),
                elem.getMotionTypeIdx(),
                elem.getFrequencyIdx(),
                elem.getFrequencyVal(),
                elem.getChoreoLengthIdx()/*,
                elem.getMotionType()*/);
    }

    /**
     * This function is implemented in the sub class
     * @return more specific type than MotionType
     */
    public abstract MotionType getMotionType();

    /**
     * Compare all property indices of this BeatElement to the elem BeatElement and check if the
     * two elements belong to the same dance sequence
     * @param elem
     * @return
     */
    public boolean isSameDanceSequence(BeatElement elem) {

        return !(mChoreoStartIdx != elem.getChoreoStartIdx() ||
                mChoreoLength != elem.getChoreoLength() ||
                mMotionTypeIdx != elem.getMotionTypeIdx() ||
                mFrequencyIdx != elem.getFrequencyIdx() ||
                mChoreoLengthIdx != elem.getChoreoLengthIdx());
    }

    /**
     * @return true if BeatElement belongs to a valid dance sequence
     */
    public boolean hasDanceSequence() {
        return mHasDanceSequence;
    }

    /**********
     * GETTERS
     **********/

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
    /*public MotionType getMotionType() {
        return mMotionType;
    }*/
    public long getSamplePosition() {
        return mSamplePosition;
    }
    public float getFrequencyVal() {
        return mFrequencyVal;
    }

}
