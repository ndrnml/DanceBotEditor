package ch.ethz.asl.dancebots.danceboteditor.model;

import android.content.Context;

import ch.ethz.asl.dancebots.danceboteditor.R;

/**
 * Created by andrin on 04.09.15.
 */
public class MotorBeatElement extends BeatElement<MotorType> {

    private int mVelocityLeftIdx;
    private int mVelocityRightIdx;

    public MotorBeatElement(Context context, int beatPos, int samplePos, MotorType[] types) {

        // Parent constructor call
        super(context, beatPos, samplePos);

        // Initialize beat element properties
        mMotionTypes = types;

        // Initialize specific motor element default properties
        mVelocityLeftIdx = 0;
        mVelocityRightIdx = 0;
        mColor = mContext.getResources().getColor(R.color.motor_list_default_color);

    }

    /**
     * Set motor element properties based on specific input values
     * @param idx
     */
    public void setVelocityLeftIdx(int idx) {

        // Set motor element specific properties
        mVelocityLeftIdx = idx; // TODO Check that idx is a valid number?
    }

    @Override
    public void setProperties(BeatElement elem) {
        super.setProperties(elem);

        // TODO is this cast type safe?
        mVelocityLeftIdx = ((MotorBeatElement) elem).getVelocityLeftIdx();
        mVelocityRightIdx = ((MotorBeatElement) elem).getVelocityRightIdx();
    }

    @Override
    public String getTypeAsString() {
        return this.getClass().toString();
    }

    public int getVelocityLeftIdx() {
        return mVelocityLeftIdx;
    }

    public int getVelocityRightIdx() {
        return mVelocityRightIdx;
    }
}
