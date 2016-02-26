package ch.ethz.asl.dancebots.danceboteditor.model;

import android.content.Context;

import java.util.UUID;

/**
 * Created by andrin on 28.08.15.
 */
public abstract class BeatElement {

    // Context properties
    protected static Context mContext;

    // Song properties of a beat element
    protected final int mBeatPosition;
    protected final long mSamplePosition;

    // Choreography properties of a beat element
    protected UUID mChoreographyID;
    protected DanceSequence mDanceSequence;

    // Menu property index of a beat element
    //protected MotionType mMotionType;
    protected int mMotionTypeIdx;
    protected int mFrequencyIdx;
    protected int mChoreoLengthIdx;

    // Absolute values for beat element properties
    protected float mFrequencyVal;

    public BeatElement(Context context, int beatPosition, long samplePosition) {

        // Set context to access application resources
        mContext = context;

        // Initialize choreography id
        mChoreographyID = null;

        // Song properties
        mBeatPosition = beatPosition;
        mSamplePosition = samplePosition;

        // Set default BeatElement properties
        setDefaultProperties();
    }

    /**
     * Default properties for BeatElement
     */
    public void setDefaultProperties() {
        // Default choreography values
        mChoreographyID = null;

        // Default menu indices
        mMotionTypeIdx = 0;
        mFrequencyIdx = 0;
        mChoreoLengthIdx = BeatElementContents.getDefaultLengthIdx();

        // Call the more specific sub type setDefaultSubProperties() of sub class
        setDefaultSubProperties();
    }

    /**
     * This function has to be implemented with specific sub type properties
     */
    protected abstract void setDefaultSubProperties();

    /**
     * Set beat element properties
     * @param motionTypeIdx
     * @param frequencyIdx
     * @param choreoLengthIdx
     */
    public void setProperties(
            int motionTypeIdx,
            int frequencyIdx,
            int choreoLengthIdx) {

        // Set all choreography properties
        mMotionTypeIdx = motionTypeIdx;
        mFrequencyIdx = frequencyIdx;
        mChoreoLengthIdx = choreoLengthIdx;
    }

    /**
     * Copy beat element properties from the argument element
     * @param elem the BeatElement from which all properties get copied
     */
    public void setProperties(BeatElement elem) {

        setProperties(
                elem.getMotionTypeIdx(),
                elem.getFrequencyIdx(),
                elem.getChoreoLengthIdx());

        // Call the more specific sub type setSubProperties() of sub class
        setSubProperties(elem);
    }

    /**
     * This function has to be implemented with specific sub type properties
     * @param elem the specific sub class element
     */
    public abstract void setSubProperties(BeatElement elem);

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
        return (mChoreographyID == elem.getDanceSequenceId());
    }

    /**
     * @param choreoID
     */
    public void setChoreographyID(UUID choreoID) {
        mChoreographyID = choreoID;
    }

    /**********
     * GETTERS
     **********/
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
    public long getSamplePosition() {
        return mSamplePosition;
    }
    public float getFrequencyVal() {
        return mFrequencyVal;
    }
    public UUID getDanceSequenceId() {
        return mChoreographyID;
    }

}
