package ch.ethz.asl.dancebots.danceboteditor.model;

import android.content.Context;

import java.util.Random;

/**
 * Created by andrin on 18.09.15.
 */
public class LedBeatElement extends BeatElement {

    private static final String LOG_TAG = "LED_BEAT_ELEMENT";

    private static final int NUM_LED_LIGHTS = 8;

    private LedType mLedType;

    private boolean[] mLedLightSwitches;

    public LedBeatElement(Context context, int beatPos, int samplePos) {

        // Parent constructor call
        super(context, beatPos, samplePos);

        // TODO this whole chunk is obsolete
        /*// Initialize beat element properties
        //mMotionType = LedType.DEFAULT; // TODO: Obsolete. REMOVE!
        mLedType = LedType.DEFAULT;

        // Initialize specific led element default properties
        mLedLightSwitches = new boolean[NUM_LED_LIGHTS];

        // Initialize led light indices to 0
        for (int i = 0; i < NUM_LED_LIGHTS; ++i) {
            mLedLightSwitches[i] = false;
        }*/
    }

    /**
     *
     * @param relativeBeat
     * @return
     */
    private byte computeLedBytes(float relativeBeat) {

        byte ledByte = 0;

        switch (mLedType) {

            case KNIGHT_RIDER:

                byte pos = (byte) (3.5 + 3.49 * Math.sin(relativeBeat * mFrequencyVal * 2 * Math.PI));
                ledByte = (byte) (0x0003 << pos);
                break;

            case RANDOM:

                // TODO: Where comes frequency and relative beat into place?
                byte[] bs = new byte[50];
                new Random().nextBytes(bs);
                ledByte = bs[new Random().nextInt(50)];

                break;

            case BLINK:

                byte b = computeByteFromSwitches();

                if ((relativeBeat * mFrequencyVal) % 2 == 0) {
                    ledByte = b;
                } else {
                    ledByte = (byte) ~b;
                }

                break;

            case SAME_BLINK:

                if ((relativeBeat * mFrequencyVal) % 2 == 0) {

                    ledByte = 0;

                } else {

                    ledByte = computeByteFromSwitches();
                }

                break;

            case CONSTANT:

                ledByte = computeByteFromSwitches();
                break;
        }

        return ledByte;
    }

    /**
     * Transform boolean[] into single 8-bit byte with the corresponding bits set to 1
     * @return single byte that represents boolean[] mLedLightSwitches
     */
    private byte computeByteFromSwitches() {

        // Init new string
        String s = "";

        // Concatenate light switch booleans to string
        for (boolean b : mLedLightSwitches) {
            s = s.concat(b ? "1" : "0");
        }
        // Parse string as (byte) integer with radix 2
        return (byte) Integer.parseInt(s, 2);
    }

    /**
     *
     * @param elem
     * @return
     */
    private boolean hasSameSwitches(LedBeatElement elem) {

        boolean[] elemLightSwitches = elem.getLedLightSwitches();
        for (int i = 0; i < elemLightSwitches.length; ++i) {

            if (mLedLightSwitches[i] != elemLightSwitches[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param ledType
     */
    public void setMotionType(LedType ledType) {
        mLedType = ledType;
    }

    public void setMotionType(BeatElement elem) {
        mLedType = ((LedBeatElement) elem).getMotionType();
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

    /**
     * Set default properties of LedBeatElement
     */
    @Override
    protected void setDefaultSubProperties() {
        // Initialize beat element properties
        //mMotionType = LedType.DEFAULT; // TODO: Obsolete. REMOVE!
        mLedType = LedType.DEFAULT;

        // Initialize specific led element default properties
        mLedLightSwitches = new boolean[NUM_LED_LIGHTS];

        // Initialize led light indices to 0
        for (int i = 0; i < NUM_LED_LIGHTS; ++i) {
            mLedLightSwitches[i] = false;
        }
    }

    /**
     * Set LedBeatElement specific properties
     * @param elem the BeatElement from which all LedBeatElement specific properties get copied
     */
    @Override
    public void setSubProperties(BeatElement elem) {
        // Set LedBeatElement type
        setMotionType(elem);

        // Set led element specific properties
        setLedLightSwitches(elem);
    }

    @Override
    public boolean isSameDanceSequence(BeatElement elem) {
        // Check if all BeatElement choreography properties and LedBeatElement choreography properties are the same
        return super.isSameDanceSequence(elem) && hasSameSwitches((LedBeatElement) elem);
    }

    @Override
    public LedType getMotionType() {
        return mLedType;
    }

    public boolean[] getLedLightSwitches () {
        return mLedLightSwitches;
    }

    public static int getNumLedLights() {
        return NUM_LED_LIGHTS;
    }

    public byte getLedBytes(float relativeBeat) {
        return computeLedBytes(relativeBeat);
    }
}
