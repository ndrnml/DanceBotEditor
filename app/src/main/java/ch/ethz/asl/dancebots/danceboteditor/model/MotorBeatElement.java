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

        // TODO set default
        mMotionTypeIdx = 0;
        mFrequencyIdx = 0;
        mVelocityIdx = 0;
        mChoreoLengthIdx = 0;

        updateProperties();
    }

    @Override
    public void updateProperties() {

        switch (mMotionType) {

            case STRAIGHT:
                mColor = Color.BLACK;
                break;

            case SPIN:
                mColor = Color.GRAY;
                break;

            case TWIST:
                mColor = Color.GREEN;
                break;

            case BACK_AND_FORTH:
                mColor = Color.CYAN;
                break;

            case CONSTANT:
                mColor = Color.YELLOW;
                break;

            case WAIT:
                mColor = Color.RED;
                break;

            default:
                break;
        }
    }

    @Override
    public String getTypeAsString() {
        return "MoveType";
    }
}
