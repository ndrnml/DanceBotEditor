package ch.ethz.asl.dancebots.danceboteditor.model;

import android.graphics.Color;

/**
 * Created by andrin on 18.09.15.
 */
public class LedBeatElement extends BeatElement<LedType> {

    public LedBeatElement(int beatPos, int samplePos, LedType motion) {

        // Initialize motion element properties$
        mMotionStartIndex = -1;
        mMotionLength = -1;

        // Initialize beat element properties
        mBeatPosition = beatPos;
        mSamplePosition = samplePos;
        mMotionType = motion;

        // TODO set default
        mMotionTypeString = "Knight Rider";
        mFrequencyString = "1/4";
        mVelocityString = "1";
        mChoreoLengthString = "1";

        // Initial update of element properties
        updateProperties();

    }

    @Override
    void updateProperties() {

        switch (mMotionType) {

            case CONSTANT:
                mColor = Color.BLUE;
                break;

            default:
                break;
        }
    }

    @Override
    public String getTypeAsString() {
        return "LedType";
    }

}
