package ch.ethz.asl.dancebots.danceboteditor.model;

import android.graphics.Color;

/**
 * Created by andrin on 18.09.15.
 */
public class LedBeatElement extends BeatElement<LedType> {

    public LedBeatElement(int beatPos, int samplePos, LedType[] types) {

        // Parent constructor call
        super();

        // Initialize motion element properties$
        mMotionStartIndex = -1;
        mMotionLength = -1;

        // Initialize beat element properties
        mBeatPosition = beatPos;
        mSamplePosition = samplePos;
        mMotionTypes = types;

        // Initial update of element properties
        updateProperties();

    }

    @Override
    public void updateProperties() {

        switch (mMotionTypes[mMotionTypeIdx]) {

            case KNIGHT_RIDER:
                mColor = Color.BLUE;
                break;

            case CONSTANT:
                mColor = Color.BLACK;
                break;


            default:
                break;
        }
    }

    @Override
    public void setProperties(BeatElement elem) {

    }

    @Override
    public String getTypeAsString() {
        return this.getClass().toString();
    }

}
