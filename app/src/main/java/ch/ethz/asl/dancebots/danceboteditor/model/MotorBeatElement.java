package ch.ethz.asl.dancebots.danceboteditor.model;

import android.graphics.Color;

/**
 * Created by andrin on 04.09.15.
 */
public class MotorBeatElement extends BeatElement<MoveType> {

    public MotorBeatElement(int beatPos, int samplePos, MoveType[] types) {

        // Parent constructor call
        super();

        // Initialize motion element properties$
        mMotionStartIndex = -1;
        mMotionLength = -1;

        // Initialize beat element properties
        mBeatPosition = beatPos;
        mSamplePosition = samplePos;
        mMotionTypes = types;

        updateProperties();
    }

    @Override
    public void updateProperties() {

        switch (mMotionTypes[mMotionTypeIdx]) {

            case STRAIGHT:
                mColor = Color.RED;
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
                mColor = Color.WHITE;
                break;

            default:
                break;
        }
    }

    @Override
    public void setProperties(BeatElement elem) {

        // TODO comment
        mMotionTypeIdx = elem.getMotionTypeIdx();
        mMotionStartIndex = elem.getMotionStartIndex();
        mMotionLength = elem.getMotionLength();
    }

    @Override
    public String getTypeAsString() {
        return this.getClass().toString();
    }
}
