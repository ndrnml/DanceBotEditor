package ch.ethz.asl.dancebots.danceboteditor.model;

import android.content.Context;
import android.util.Log;

import ch.ethz.asl.dancebots.danceboteditor.R;

/**
 * Created by andrin on 18.09.15.
 */
public class LedBeatElement extends BeatElement<LedType> {

    private static final String LOG_TAG = "LED_BEAT_ELEMENT";
    private static final int NUM_LED_LIGHTS = 8;

    private boolean[] mLedLightSwitches;

    public LedBeatElement(Context context, int beatPos, int samplePos, LedType[] types) {

        // Parent constructor call
        super(context, beatPos, samplePos);

        // Initialize beat element properties
        mMotionTypes = types;

        // Initialize specific led element default properties
        mColor = mContext.getResources().getColor(R.color.led_list_default_color);
        mLedLightSwitches = new boolean[NUM_LED_LIGHTS];

        // Initialize led light indices to 0
        for (int i = 0; i < NUM_LED_LIGHTS; ++i) {
            mLedLightSwitches[i] = false;
        }

    }

    /**
     * Set properties of led element based on specific input values
     * @param ledLightSwitches
     */
    public void setLedLightSwitches(boolean[] ledLightSwitches) {

        // Set led element specific values
        mLedLightSwitches = ledLightSwitches;
    }

    /**
     * Set properties of led element based on specific input element
     * @param elem
     */
    public void setLedLightSwitches(BeatElement elem) {

        setLedLightSwitches(((LedBeatElement) elem).getLedLightSwitches());
    }

    @Override
    protected void setColorAndTag() {

        switch (mMotionTypes[mMotionTypeIdx]) {

            case KNIGHT_RIDER:
                mColor = mContext.getResources().getColor(R.color.led_elem_color1);
                mChoreoTag = "K";
                break;

            case RANDOM:
                mColor = mContext.getResources().getColor(R.color.led_elem_color2);
                mChoreoTag = "R";
                break;

            case BLINK:
                mColor = mContext.getResources().getColor(R.color.led_elem_color3);
                mChoreoTag = "B";
                break;

            case SAME_BLINK:
                mColor = mContext.getResources().getColor(R.color.led_elem_color4);
                mChoreoTag = "S";
                break;

            case CONSTANT:
                mColor = mContext.getResources().getColor(R.color.led_elem_color5);
                mChoreoTag = "C";
                break;

            default:
                Log.e(LOG_TAG, "Error switch: " + mMotionTypes[mMotionTypeIdx]);
                break;
        }
    }

    @Override
    public void setProperties(BeatElement elem) {

        // Set general beat element properties
        super.setProperties(elem);

        // Set led element specific properties
        setLedLightSwitches(elem);
    }

    @Override
    public boolean isSameChoreography(BeatElement elem) {

        // Check if all beat element choreography properties and led element choreo properties are the same
        if (super.isSameChoreography(elem) && hasSameSwitches((LedBeatElement) elem))
        {
            return true;

        } else {

            return false;
        }
    }

    private boolean hasSameSwitches(LedBeatElement elem) {

        boolean[] elemLightSwitches = elem.getLedLightSwitches();

        for (int i = 0; i < elemLightSwitches.length; ++i) {

            if (mLedLightSwitches[i] != elemLightSwitches[i]) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean hasChoreography() {
        return mHasChoreography;
    }

    public boolean[] getLedLightSwitches() {
        return mLedLightSwitches;
    }

    public static int getNumLedLights() {
        return NUM_LED_LIGHTS;
    }
}
