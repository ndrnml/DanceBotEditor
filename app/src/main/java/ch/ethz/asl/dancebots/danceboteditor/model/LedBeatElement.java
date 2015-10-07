package ch.ethz.asl.dancebots.danceboteditor.model;

import android.content.Context;
import android.graphics.Color;

import ch.ethz.asl.dancebots.danceboteditor.R;

/**
 * Created by andrin on 18.09.15.
 */
public class LedBeatElement extends BeatElement<LedType> {

    private final int NUM_LED_LIGHTS = 8;
    private int[] mLedLightSwitch; // Its values should be 0 or 1

    public LedBeatElement(Context context, int beatPos, int samplePos, LedType[] types) {

        // Parent constructor call
        super(context, beatPos, samplePos);

        // Initialize beat element properties
        mMotionTypes = types;

        // Initialize specific led element default properties
        mColor = mContext.getResources().getColor(R.color.led_list_default_color);
        mLedLightSwitch = new int[NUM_LED_LIGHTS];

        // Initialize led light indices to 0
        for (int i = 0; i < NUM_LED_LIGHTS; ++i) {
            mLedLightSwitch[i] = 0;
        }

    }

    /**
     * Set properties of led element based on specific input values
     * @param ledLightSwitch
     */
    public void setLedLightSwitch(int[] ledLightSwitch) {

        // Set led element specific values
        mLedLightSwitch = ledLightSwitch;
    }

    @Override
    public void setProperties(BeatElement elem) {

        super.setProperties(elem);

        mLedLightSwitch = ((LedBeatElement) elem).getLedLightSwitch();
    }

    @Override
    public String getTypeAsString() {
        return this.getClass().toString();
    }

    public int[] getLedLightSwitch() {
        return mLedLightSwitch;
    }
}
