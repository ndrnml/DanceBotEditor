package ch.ethz.asl.dancebots.danceboteditor.model;

import android.content.Context;

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

    public MotorBeatElement(Context context, int beatPos, int samplePos) {

        // Parent constructor call
        super(context, beatPos, samplePos);

        // Initialize beat element properties
        //mMotionType = MotorType.DEFAULT; // TODO: Obsolete. Remove!
        mMotorType = MotorType.DEFAULT;

        // Initialize specific motor element default properties
        mVelocityLeftIdx = 0;
        mVelocityRightIdx = 0;

        // Initialize motor element specific default absolute values
        mLeftVelocityValue = 0;
        mRightVelocityValue = 0;
    }

    /**
     *
     * @param relativeBeat
     * @param isLeft
     * @return
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
                    // TODO: Check this works with float and int
                    velocity = mLeftVelocityValue * (int) Math.sin(relativeBeat * mFrequencyVal * 2 * Math.PI);
                } else {
                    velocity = -mLeftVelocityValue * (int) Math.sin(relativeBeat * mFrequencyVal * 2 * Math.PI);
                }
                break;

            case BACK_AND_FORTH:
                // TODO: Check this works with float and int
                velocity = mLeftVelocityValue * (int) Math.sin(relativeBeat * mFrequencyVal * 2 * Math.PI);
                break;

            case CONSTANT:
                if (isLeft) {
                    velocity = mLeftVelocityValue;
                } else {
                    velocity = mRightVelocityValue;
                }
                break;

            case WAIT:
                break;

            default:
                break;
        }

        return velocity;
    }

    /**
     * @param motorType
     */
    public void setMotionType(MotorType motorType) {
        mMotorType = motorType;
    }

    /**
     * @param elem
     */
    public void setMotionType(BeatElement elem) {
        mMotorType = ((MotorBeatElement) elem).getMotionType();
    }

    /**
     * Set motor element properties based on specific input value
     * @param idx
     */
    public void setVelocityLeftIdx(int idx) {

        // Set motor element specific properties
        mVelocityLeftIdx = idx; // TODO Check that idx is a valid number?
    }

    /**
     * Set motor element properties based on specific input value
     * @param idx
     */
    public void setVelocityRightIdx(int idx) {

        // Set motor element specific properties
        mVelocityRightIdx = idx; // TODO Check that idx is a valid number?
    }

    /**
     * Set MotorBeatElement specific velocity indices
     * @param elem
     */
    public void setVelocityIndices(BeatElement elem) {
        // TODO is this cast type safe?
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
     * Set MotorBeatElement specific properties
     * @param elem the BeatElement from which all MotorBeatElement specific properties get copied
     */
    public void setMotorProperties(BeatElement elem) {
        // Set MotorBeatElement type
        setMotionType(elem);

        // Set motor element specific properties
        setVelocityIndices(elem);

        // Set motor element specific absolute values
        setVelocityValues(elem);
    }

    /**
     * Set general BeatElement properties
     * @param elem the BeatElement from which all properties get copied
     */
    @Override
    public void setProperties(BeatElement elem) {

        // Set general beat element properties
        super.setProperties(elem);

        // Set MotorBeatElement specific properties
        setMotorProperties(elem);
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
