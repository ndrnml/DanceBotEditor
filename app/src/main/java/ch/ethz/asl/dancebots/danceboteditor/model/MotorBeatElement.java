package ch.ethz.asl.dancebots.danceboteditor.model;

import android.content.Context;

import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotConfiguration;

/**
 * Created by andrin on 04.09.15.
 */
public class MotorBeatElement extends BeatElement {

    private static final String LOG_TAG = "MOTOR_BEAT_ELEMENT";

    private MotorType mMotorType;

    private int mVelocityLeftIdx;
    private int mVelocityRightIdx;
    private int mLeftVelocityValue;
    private int mRightVelocityValue;

    /**
     * MotorBeatElement constructor
     * @param context application context is needed to resolve colors and strings
     * @param beatPos beat position index is needed for the choreography
     * @param samplePos absolute sample position of this element
     */
    public MotorBeatElement(Context context, int beatPos, int samplePos) {

        // Parent constructor call
        super(context, beatPos, samplePos);
    }

    /**
     * MotorBeatElement velocity byte computation
     * @param relativeBeat relative sample position within the current beat
     * @param isLeft boolean to specify whether the left or the right wheel spins
     * @return encoding of the velocity byte
     */
    private int computeVelocityValue(float relativeBeat, boolean isLeft) {

        int velocity = 0;

        switch (mMotorType) {

            case STRAIGHT:
                velocity = mLeftVelocityValue;
                break;

            case SPIN:
                if (isLeft) {
                    velocity = mLeftVelocityValue;
                } else {
                    velocity = -mLeftVelocityValue;
                }
                break;

            case TWIST:
                if (isLeft) {
                    velocity = (int) (mLeftVelocityValue * Math.sin(relativeBeat * mFrequencyVal * 2.0 * Math.PI));
                } else {
                    velocity = (int) (-mLeftVelocityValue * Math.sin(relativeBeat * mFrequencyVal * 2.0 * Math.PI));
                }
                break;

            case BACK_AND_FORTH:
                velocity = (int) (mLeftVelocityValue * Math.sin(relativeBeat * mFrequencyVal * 2.0 * Math.PI));
                break;

            case CONSTANT:
                if (isLeft) {
                    velocity = mLeftVelocityValue;
                } else {
                    velocity = mRightVelocityValue;
                }
                break;

            case WAIT:

            default:
                break;
        }

        return velocity;
    }

    /**
     * @param elem
     */
    public void setMotionType(BeatElement elem) {
        mMotorType = ((MotorBeatElement) elem).getMotionType();
    }

    /**
     * @param elem
     */
    public void setFrequencyVal(BeatElement elem) {
        mFrequencyVal = elem.getFrequencyVal();
    }

    /**
     * Set motor element properties based on specific input value
     * @param idx
     */
    public void setVelocityLeftIdx(int idx) {
        // Set motor element specific properties
        mVelocityLeftIdx = idx;
    }

    /**
     * Set motor element properties based on specific input value
     * @param idx
     */
    public void setVelocityRightIdx(int idx) {
        // Set motor element specific properties
        mVelocityRightIdx = idx;
    }

    /**
     * Set MotorBeatElement specific velocity indices
     * @param elem
     */
    public void setVelocityIndices(BeatElement elem) {
        setVelocityLeftIdx(((MotorBeatElement) elem).getVelocityLeftIdx());
        setVelocityRightIdx(((MotorBeatElement) elem).getVelocityRightIdx());
    }

    /**
     * Set MotorBeatElement specific left velocity value
     * @param velocityLeftValue
     */
    public void setVelocityLeftValue(int velocityLeftValue) {
        mLeftVelocityValue = velocityLeftValue;
    }

    /**
     * Set MotorBeatElement specific right velocity value
     * @param velocityRightValue
     */
    public void setVelocityRightValue(int velocityRightValue) {
        mRightVelocityValue = velocityRightValue;
    }

    /**
     * Set MotorBeatElement specific velocity values
     * @param elem
     */
    public void setVelocityValues(BeatElement elem) {
        setVelocityLeftValue(((MotorBeatElement) elem).getVelocityLeftValue());
        setVelocityRightValue(((MotorBeatElement) elem).getVelocityRightValue());
    }

    /**
     * Store MotorBeatElement specific menu data
     * @param motorType
     * @param frequencyVal
     * @param velocityLeftIdx
     * @param velocityRightIdx
     * @param leftVelocityVal
     * @param rightVelocityVal
     */
    public void pushSelectedMenuData(MotorType motorType, float frequencyVal, int velocityLeftIdx, int velocityRightIdx, int leftVelocityVal, int rightVelocityVal) {
        mMotorType = motorType;
        mFrequencyVal = frequencyVal;
        mVelocityLeftIdx = velocityLeftIdx;
        mVelocityRightIdx = velocityRightIdx;
        mLeftVelocityValue = leftVelocityVal;
        mRightVelocityValue = rightVelocityVal;
    }

    /**
     * Set default properties of MotorBeatElement
     */
    @Override
    protected void setDefaultSubProperties() {
        // Initialize beat element properties
        mMotorType = MotorType.DEFAULT;

        // Frequency
        mFrequencyVal = 0;

        // Initialize specific motor element default properties
        mVelocityLeftIdx = BeatElementContents.getDefaultVelocityIdx();
        mVelocityRightIdx = BeatElementContents.getDefaultVelocityIdx();

        // Initialize motor element specific default absolute values
        mLeftVelocityValue = 0;
        mRightVelocityValue = 0;
    }

    /**
     * Set MotorBeatElement specific properties
     * @param elem the BeatElement from which all MotorBeatElement specific properties get copied
     */
    @Override
    public void setSubProperties(BeatElement elem) {
        // Set MotorBeatElement type
        setMotionType(elem);

        // Frequency val
        setFrequencyVal(elem);

        // Set motor element specific properties
        setVelocityIndices(elem);

        // Set motor element specific absolute values
        setVelocityValues(elem);
    }

    @Override
    public MotorType getMotionType() {
        return mMotorType;
    }

    public int getVelocityLeftIdx() {
        return mVelocityLeftIdx;
    }

    public int getVelocityRightIdx() {
        return mVelocityRightIdx;
    }

    public int getVelocityLeft(float relativeBeat) {
        return computeVelocityValue(relativeBeat, true);
    }

    public int getVelocityRight(float relativeBeat) {
        return computeVelocityValue(relativeBeat, false);
    }

    public int getVelocityLeftValue() {
        return mLeftVelocityValue;
    }

    public int getVelocityRightValue() {
        return mRightVelocityValue;
    }
}
