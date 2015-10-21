package ch.ethz.asl.dancebots.danceboteditor.model;

import android.content.Context;

import ch.ethz.asl.dancebots.danceboteditor.R;

/**
 * Created by andrin on 04.09.15.
 */
public class MotorBeatElement extends BeatElement {

    private static final String LOG_TAG = "MOTOR_BEAT_ELEMENT";

    private int mVelocityLeftIdx;
    private int mVelocityRightIdx;

    public MotorBeatElement(Context context, int beatPos, int samplePos) {

        // Parent constructor call
        super(context, beatPos, samplePos);

        // Initialize beat element properties
        mMotionType = MotorType.DEFAULT;

        // Initialize specific motor element default properties
        mVelocityLeftIdx = 0;
        mVelocityRightIdx = 0;
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

    public void setVelocityIndices(BeatElement elem) {

        // TODO is this cast type safe?
        setVelocityLeftIdx(((MotorBeatElement) elem).getVelocityLeftIdx());
        setVelocityRightIdx(((MotorBeatElement) elem).getVelocityRightIdx());
    }

    @Override
    public void setProperties(BeatElement elem) {

        // Set general beat element properties
        super.setProperties(elem);

        // Set motor element specific properties
        setVelocityIndices(elem);
    }

    @Override
    public boolean hasChoreography() {
        return mHasChoreography;
    }

    public int getVelocityLeftIdx() {
        return mVelocityLeftIdx;
    }
    public int getVelocityRightIdx() {
        return mVelocityRightIdx;
    }
}
