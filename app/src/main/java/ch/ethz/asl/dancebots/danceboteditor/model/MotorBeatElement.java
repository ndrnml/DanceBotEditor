package ch.ethz.asl.dancebots.danceboteditor.model;

import android.graphics.Color;

/**
 * Created by andrin on 04.09.15.
 */
public class MotorBeatElement extends BeatElement<MoveType> {

    public MotorBeatElement(int beatPos, int samplePos, MoveType motion) {

        // Initialize motion element properties$
        mMotionStartIndex = -1;
        mMotionLength = -1;

        // Initialize beat element properties
        mBeatPosition = beatPos;
        mSamplePosition = samplePos;
        mMotionType = motion;

        updateProperties();
    }

    @Override
    void updateProperties() {

        switch (mMotionType) {

            case WAIT:
                mColor = Color.RED;
                break;

            default:
                break;
        }
    }

    @Override
    public String getType() {
        return "MoveType";
    }
}
