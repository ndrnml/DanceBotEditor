package ch.ethz.asl.dancebots.danceboteditor.model;

import android.content.Context;

import ch.ethz.asl.dancebots.danceboteditor.R;

/**
 * Created by andrin on 28.08.15.
 */
public abstract class BeatElement {

    // 'Static' enum types are instantiated with the object
    public enum Type {
        L_DEFAULT(mContext.getResources().getColor(R.color.led_list_default_color), ""),
        L_KNIGHT_RIDER(mContext.getResources().getColor(R.color.led_elem_color1), "K"),
        L_RANDOM(mContext.getResources().getColor(R.color.led_elem_color2), "R"),
        L_BLINK(mContext.getResources().getColor(R.color.led_elem_color3), "B"),
        L_SAME_BLINK(mContext.getResources().getColor(R.color.led_elem_color4), "S"),
        L_CONSTANT(mContext.getResources().getColor(R.color.led_elem_color5), "C"),

        M_DEFAULT(mContext.getResources().getColor(R.color.motor_list_default_color), ""),
        M_STRAIGHT(mContext.getResources().getColor(R.color.motor_elem_color1), "S"),
        M_SPIN(mContext.getResources().getColor(R.color.motor_elem_color2), "P"),
        M_TWIST(mContext.getResources().getColor(R.color.motor_elem_color3), "T"),
        M_BACK_AND_FORTH(mContext.getResources().getColor(R.color.motor_elem_color4), "B"),
        M_CONSTANT(mContext.getResources().getColor(R.color.motor_elem_color5), "C"),
        M_WAIT(mContext.getResources().getColor(R.color.motor_elem_color6), "W");

        private int mColor;
        private String mTag;

        Type(int color, String tag) {
            mColor = color;
            mTag = tag;
        }

        public int getColor() {
            return mColor;
        }

        public String getTag() {
            return mTag;
        }
    }

    // Context properties
    protected static Context mContext;

    // Choreography properties of a beat element
    protected boolean mHasChoreography;
    protected int mChoreoStartIdx;
    protected int mChoreoLength;
    protected Type mMotionType;

    // Song properties of a beat element
    protected int mBeatPosition;
    protected long mSamplePosition;

    // Menu property index of a beat element
    protected int mMotionTypeIdx;
    protected int mFrequencyIdx;
    protected int mChoreoLengthIdx;

    public BeatElement(Context context, int beatPosition, long samplePosition) {

        mContext = context;

        // Song properties
        mBeatPosition = beatPosition;
        mSamplePosition = samplePosition;

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
    public void setProperties(int choreoStartIdx, int choreoLength, int motionTypeIdx, int frequencyIdx, int choreoLengthIdx) {

        // TODO reduce redundancy
        // Set all choreography properties
        mChoreoStartIdx = choreoStartIdx;
        mChoreoLength = choreoLength;
        mMotionTypeIdx = motionTypeIdx;
        mFrequencyIdx = frequencyIdx;
        mChoreoLengthIdx = choreoLengthIdx;
        mHasChoreography = true;
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
    public Type getMotionType() {return mMotionType;}
}
