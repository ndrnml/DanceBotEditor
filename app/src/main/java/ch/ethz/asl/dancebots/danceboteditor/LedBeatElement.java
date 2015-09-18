package ch.ethz.asl.dancebots.danceboteditor;

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

        // Initial update of element properties
        updateProperties();

    }

    @Override
    String getType() {
        return "LedType";
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
}
