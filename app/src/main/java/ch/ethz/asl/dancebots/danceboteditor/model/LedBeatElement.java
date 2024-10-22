package ch.ethz.asl.dancebots.danceboteditor.model;

import android.content.Context;

import java.util.Random;

/**
 * Created by andrin on 18.09.15.
 */
public class LedBeatElement extends BeatElement {

    private static final String LOG_TAG = "LED_BEAT_ELEMENT";

    // Number of led lights attached to the robot
    private static final int NUM_LED_LIGHTS = 8;
    // Specific motion type
    private LedType mLedType;
    // Led light array
    private boolean[] mLedLightSwitches;

    /**
     * LedBeatElement constructor
     * @param context application context needed to resolve colors and strings
     * @param beatPos beat position index is needed for the choreography
     * @param samplePos absolute sample position of this element
     */
    public LedBeatElement(Context context, int beatPos, int samplePos) {

        // Parent constructor call
        super(context, beatPos, samplePos);
    }

    /**
     * LedBeatElement light motion computation
     * @param relativeBeat relative sample position within the current beat
     * @return encoding of led light byte
     */
    private byte computeLedBytes(float relativeBeat) {

        byte ledByte = 0;

        switch (mLedType) {

            case KNIGHT_RIDER:

                int pos = (int) (3.5 + 3.49 * Math.sin(relativeBeat * mFrequencyVal * 2.0 * Math.PI));
                ledByte |= (0x03 << pos);
                break;

            case RANDOM:

                int NUM = 100;
                int numRands = NUM;
                byte[] bs = new byte[numRands];
                new Random().nextBytes(bs);
                ledByte = bs[new Random().nextInt(numRands)];

                break;

            case BLINK:

                byte b = computeByteFromSwitches();

                if (Math.round(relativeBeat * mFrequencyVal) % 2 == 0) {
                    ledByte = b;
                } else {
                    ledByte = (byte) ~b;
                }

                break;

            case SAME_BLINK:

                if (Math.round(relativeBeat * mFrequencyVal) % 2 == 0) {
                    ledByte = computeByteFromSwitches();
                } else {
                    ledByte = 0;
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
     * Check if selected switches of the current and the LedBeatElement elem are the same
     * @param elem the reference LedBeatElement
     * @return a match of the led switch
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
     * @param elem
     */
    public void setMotionType(BeatElement elem) {
        mLedType = ((LedBeatElement) elem).getMotionType();
    }

    /**
     * @param elem
     */
    public void setFrequencyVal(BeatElement elem) {
        mFrequencyVal = elem.getFrequencyVal();
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
     * Store LedBeatElement specific selected menu data
     * @param ledType
     * @param frequencyVal
     * @param ledLightSwitches
     */
    public void pushSelectedManuData(LedType ledType, float frequencyVal, boolean[] ledLightSwitches) {
        // Store type of motion, frequency value and led light switch selection
        mLedType = ledType;
        mFrequencyVal = frequencyVal;
        mLedLightSwitches = ledLightSwitches;
    }

    /**
     * Set default properties of LedBeatElement
     */
    @Override
    protected void setDefaultSubProperties() {
        // Initialize beat element properties
        mLedType = LedType.DEFAULT;

        // Frequency
        mFrequencyVal = 0;

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

        // Set led element specific frequency val
        setFrequencyVal(elem);
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
