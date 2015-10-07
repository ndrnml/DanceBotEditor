package ch.ethz.asl.dancebots.danceboteditor.model;

import android.content.Context;

import ch.ethz.asl.dancebots.danceboteditor.R;

/**
 * Created by andrin on 18.09.15.
 */
public class LedBeatElement extends BeatElement<LedType> {

    private static final int NUM_LED_LIGHTS = 8;

    private int[] mLedLightSwitches; // Its values should be 0 or 1

    public LedBeatElement(Context context, int beatPos, int samplePos, LedType[] types) {

        // Parent constructor call
        super(context, beatPos, samplePos);

        // Initialize beat element properties
        mMotionTypes = types;

        // Initialize specific led element default properties
        mColor = mContext.getResources().getColor(R.color.led_list_default_color);
        mLedLightSwitches = new int[NUM_LED_LIGHTS];

        // Initialize led light indices to 0
        for (int i = 0; i < NUM_LED_LIGHTS; ++i) {
            mLedLightSwitches[i] = 0;
        }

    }

    public static int getNumLedLights() {
        return NUM_LED_LIGHTS;
    }

    /**
     * Set properties of led element based on specific input values
     * @param ledLightSwitches
     */
    public void setLedLightSwitches(int[] ledLightSwitches) {

        // Set led element specific values
        mLedLightSwitches = ledLightSwitches;
    }

    @Override
    public void setProperties(BeatElement elem) {

        super.setProperties(elem);

        mLedLightSwitches = ((LedBeatElement) elem).getLedLightSwitches();
    }

    @Override
    public String getTypeAsString() {
        return this.getClass().toString();
    }

    public int[] getLedLightSwitches() {
        return mLedLightSwitches;
    }
}
