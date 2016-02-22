package ch.ethz.asl.dancebots.danceboteditor.model;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import ch.ethz.asl.dancebots.danceboteditor.adapters.BeatElementAdapter;
import ch.ethz.asl.dancebots.danceboteditor.ui.FloatSelectionMenu;
import ch.ethz.asl.dancebots.danceboteditor.ui.IntegerSelectionMenu;
import ch.ethz.asl.dancebots.danceboteditor.ui.LedTypeSelectionMenu;
import ch.ethz.asl.dancebots.danceboteditor.ui.MotorTypeSelectionMenu;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotConfiguration;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotError;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotMusicFile;
import ch.ethz.asl.dancebots.danceboteditor.view.HorizontalRecyclerViews;

/**
 * Created by andrin on 31.08.15.
 */
public class ChoreographyManager {

    private static final String LOG_TAG = "CHOREOGRAPHY_MANAGER";

    private final DanceBotMusicFile mMusicFile;

    private Context mContext;

    private Choreography<LedBeatElement> mLedChoregraphy;
    private Choreography<MotorBeatElement> mMotorChoreography;

    private final ChoreographyViewManager mBeatViews;

    // Data source fields
    private int mNumSamplesReset;
    private int mNumSamplesOne;
    private int mNumSamplesZero;


    /**
     * An interface that defines methods that SoundTask implements. An instance of
     * SoundTask passes itself to an SoundDecodeRunnable instance through the
     * SoundDecodeRunnable constructor, after which the two instances can access each other's
     * variables.
     */
    public interface ChoreographyViewManager {

        /**
         * Sets the current led element view adapter
         * @param ledAdapter
         */
        void setLedElementAdapter(BeatElementAdapter ledAdapter);

        /**
         * Sets the current motor element view adapter
         * @param motorAdapter
         */
        void setMotorElementAdapter(BeatElementAdapter motorAdapter);
    }

    public ChoreographyManager(Context context, HorizontalRecyclerViews beatViews, DanceBotMusicFile musicFile) {

        // Set application context, to load constant colors and strings
        mContext = context;

        // Store the beat view interface object
        mBeatViews = beatViews;

        ArrayList<LedBeatElement> ledElements = initLedBeatElements(musicFile);
        mLedChoregraphy = new Choreography<>(ledElements);

        mBeatViews.setLedElementAdapter(new BeatElementAdapter<>(mContext, ledElements));

        ArrayList<MotorBeatElement> motorElements = initMotorBeatElements(musicFile);
        mMotorChoreography = new Choreography<>(motorElements);

        mBeatViews.setMotorElementAdapter(new BeatElementAdapter<>(mContext, motorElements));

        mMusicFile = musicFile;
    }


    public void processPositiveClick(
            BeatElement selectedBeatElem,
            int selectedChoreoLengthIdx,
            IntegerSelectionMenu selectedChoreoLength,
            int selectedMotionTypeIdx,
            int selectedFrequencyIdx,
            LedTypeSelectionMenu ledType,
            FloatSelectionMenu ledFrequencyVal,
            boolean[] ledLightSwitches,
            MotorTypeSelectionMenu motorType,
            FloatSelectionMenu motorFrquencyVal,
            int selectedVelocityLeftIdx,
            int selectedVelocityRightIdx,
            IntegerSelectionMenu leftVelocityVal,
            IntegerSelectionMenu rightVelocityVal) {

        // Set general beat element properties according to menu choices
        selectedBeatElem.setProperties(
                selectedMotionTypeIdx,
                selectedFrequencyIdx,
                selectedChoreoLengthIdx);

        if (selectedBeatElem.getClass() == LedBeatElement.class) { // LED_TYPE

            // Fetch and store led specific menu values; motion, frequency, switches...
            ((LedBeatElement) selectedBeatElem).pushSelectedManuData(
                    ledType.getValAt(selectedMotionTypeIdx),
                    ledFrequencyVal.getValAt(selectedFrequencyIdx),
                    ledLightSwitches);

            if (selectedBeatElem.getChoreographyID() == null) {

                mLedChoregraphy.addNewDanceSequence((LedBeatElement) selectedBeatElem, selectedChoreoLength.getValAt(selectedChoreoLengthIdx));

            } else {

                mLedChoregraphy.updateDanceSequence((LedBeatElement) selectedBeatElem, selectedChoreoLength.getValAt(selectedChoreoLengthIdx));

            }

        } else if (selectedBeatElem.getClass() == MotorBeatElement.class) { // MOTOR_TYPE

            // Fetch and store motor specific menu values: motion, frequency, velocities...
            ((MotorBeatElement) selectedBeatElem).pushSelectedMenuData(
                    motorType.getValAt(selectedMotionTypeIdx),
                    motorFrquencyVal.getValAt(selectedFrequencyIdx),
                    selectedVelocityLeftIdx,
                    selectedVelocityRightIdx,
                    leftVelocityVal.getValAt(selectedVelocityLeftIdx),
                    rightVelocityVal.getValAt(selectedVelocityRightIdx));

            if (selectedBeatElem.getChoreographyID() == null) {

                mMotorChoreography.addNewDanceSequence((MotorBeatElement) selectedBeatElem, selectedChoreoLength.getValAt(selectedChoreoLengthIdx));

            } else {

                mMotorChoreography.updateDanceSequence((MotorBeatElement) selectedBeatElem, selectedChoreoLength.getValAt(selectedChoreoLengthIdx));

            }
        }
    }


    public void processNegativeClick(BeatElement selectedBeatElem) {

        // Check if dance sequence exists
        if (selectedBeatElem.getChoreographyID() != null) {

            if (selectedBeatElem.getClass() == LedBeatElement.class) { // LED_TYPE

                mLedChoregraphy.removeDanceSequence((LedBeatElement) selectedBeatElem);

            } else if (selectedBeatElem.getClass() == MotorBeatElement.class) { // MOTOR_TYPE

                mMotorChoreography.removeDanceSequence((MotorBeatElement) selectedBeatElem);
            }
        }
    }


    /**
     * Initialize led beat elements after successfully extracting all beats
     * @param musicFile
     */
    public ArrayList<LedBeatElement> initLedBeatElements(DanceBotMusicFile musicFile) {

        ArrayList<LedBeatElement> elems = new ArrayList<>();

        int[] beatBuffer = musicFile.getBeatBuffer();
        int numBeats = beatBuffer.length;

        if (numBeats > 0) {
            for (int i = 0; i < numBeats; ++i) {
                elems.add(new LedBeatElement(mContext, i, beatBuffer[i]));
            }
        } else {
            // TODO some error?
            Log.v(LOG_TAG, "Error: " + beatBuffer.toString() + ", Number of beats: " + numBeats);
        }

        return elems;
    }

    /**
     * Initialize motor beat elements after successfully extracting all beats
     * @param musicFile
     */
    public ArrayList<MotorBeatElement> initMotorBeatElements(DanceBotMusicFile musicFile) {

        ArrayList<MotorBeatElement> elems = new ArrayList<>();

        int[] beatBuffer = musicFile.getBeatBuffer();
        int numBeats = beatBuffer.length;

        if (numBeats > 0) {
            for (int i = 0; i < numBeats; ++i) {
                elems.add(new MotorBeatElement(mContext, i, beatBuffer[i]));
            }
        } else {
            // TODO some error?
            Log.v(LOG_TAG, "Error: " + beatBuffer.toString() + ", Number of beats: " + numBeats);
        }

        return elems;
    }

    private int readDataChannel(short[] pcmData, int byteOffset) {

        // Get beat element lists for motor and led elements
        ArrayList<MotorBeatElement> motorElements = mMotorChoreography.getBeatElements();
        ArrayList<LedBeatElement> ledElements = mLedChoregraphy.getBeatElements();

        // Get the total number of beats detected
        int numBeats = mMusicFile.getBeatCount();
        // Get the total number of samples decoded
        long numSamples = mMusicFile.getSampleCount();
        // Get the detected sample rate of decoding
        int samplingRate = mMusicFile.getSampleRate();

        // Compute the nominal sampling scale
        float sampleScale = samplingRate / DanceBotConfiguration.SAMPLE_FREQUENCY_NOMINAL;

        // Round to the closest Integer
        mNumSamplesZero = Math.round(sampleScale * DanceBotConfiguration.BIT_LENGTH_ZERO_NOMINAL);
        mNumSamplesOne = Math.round(sampleScale * DanceBotConfiguration.BIT_LENGTH_ONE_NOMINAL);
        mNumSamplesReset = Math.round(sampleScale * DanceBotConfiguration.BIT_LENGTH_RESET_NOMINAL);

        // Define the maximum number of bits in a robot message
        int numBitsInMsg = 2 * (DanceBotConfiguration.NUM_BIT_MOTOR + 1) + 8;

        // Define the maximum number of samples in a robot message
        int maxNumSamplesInMsg = numBitsInMsg * mNumSamplesOne + mNumSamplesReset;

        // Initialize a new data buffer, which stores the current calculated message
        short dataBuffer[] = new short[maxNumSamplesInMsg];

        // Keep a state of the last sample written
        short lastSampleLevel = DanceBotConfiguration.DATA_LEVEL;

        // Iterate over all detected beats in the song
        for (int i = 0; i < numBeats - 1; ++i) {

            // Get the corresponding MotorBeatElement and LedBeatElement
            MotorBeatElement motorElement = motorElements.get(i);
            LedBeatElement ledElement = ledElements.get(i);

            // Get the start and end sample for the current selected beat
            long startSamplePosition = motorElements.get(i).getSamplePosition();
            long endSamplePosition = motorElements.get(i + 1).getSamplePosition();

            // Compute the total number of samples to process for the current beat
            int samplesToProcess = (int) (endSamplePosition - startSamplePosition);

            // Initialize the (current) relative sample start position to zero
            int samplePos = 0;

            // Iterate while not all samples at the current beat are processed
            while (samplePos < samplesToProcess) {

                float relativeBeat = (float) samplePos / (float) samplesToProcess;

                // Initialize velocities and led
                short vLeft = 0;
                short vRight = 0;
                byte led = 0;

                // Check the current motor element is different from the DEFAULT state
                if (motorElement.getMotionType() != MotorType.DEFAULT) {
                    vLeft = (short) motorElement.getVelocityLeft(relativeBeat);
                    vRight = (short) motorElement.getVelocityRight(relativeBeat);
                }

                // Check the current led element is different from the DEFAULT state
                if (ledElement.getMotionType() != LedType.DEFAULT) {
                    led = ledElement.getLedBytes(relativeBeat);
                }

                int numSamplesInMsg = calculateMessage(dataBuffer, vLeft, vRight, led, lastSampleLevel);

                // Get last sample level
                lastSampleLevel = dataBuffer[numSamplesInMsg - 1];

                /*
                 * If the end of samples to process is not reached, the data buffer is copied to
                 * pcm buffer
                 */
                if (samplePos + numSamplesInMsg < samplesToProcess) {

                    // Compute the current absolute sample position and write data to the pcm buffer
                    int currentSamplePosition = (int) startSamplePosition + samplePos;
                    // Write calculated data message to pcm buffer
                    writeMessage(pcmData, dataBuffer, currentSamplePosition, numSamplesInMsg);

                } else {

                    // If this happens, all messages have been written for the current beat
                    break;
                }

                samplePos += numSamplesInMsg;
            }
        }

        return -1;
    }

    private int writeMessage(short[] pcmData, short[] dataBuffer, int msgStart, int msgLength) {

        if (msgStart > 0 && (msgStart + msgLength < mMusicFile.getSampleCount())) {

            for (int i = 0; i < msgLength; ++i) {
                pcmData[msgStart + i] = dataBuffer[i];
            }

            return DanceBotError.NO_ERROR;

        } else {

            Log.d(LOG_TAG, "ERROR: writeMessage out of bounds");
            return DanceBotError.WRITE_ERROR;
        }
    }

    /**
     * This function calculates the needed samples for left and right velocities and for the led
     * light. The values of the current beat element will be parsed an put into the dataBuffer.
     * After the three properties are parsed and the buffer filled it returns the number of samples
     * written to the dataBuffer.
     * @param dataBuffer
     * @param vLeft
     * @param vRight
     * @param led
     * @param lastBitLevel
     * @return the written samples
     */
    private int calculateMessage(short[] dataBuffer, short vLeft, short vRight, byte led, short lastBitLevel) {

        // TODO: WHERE IS CHECKED THAT dataBuffer IS NOT OUT OF BOUND?

        int offsetSamples = 0;
        byte velByte = 0;

        // Init all dataBuffer elements to -DATA_LEVEL
        for (int i = 0; i < dataBuffer.length; ++i) {
            dataBuffer[i] = -DanceBotConfiguration.DATA_LEVEL;
        }

        // Invert last bit
        lastBitLevel *= -1;

        // Write reset message
        for (int i = 0; i < mNumSamplesReset; ++i) {
            dataBuffer[i] = lastBitLevel;
        }

        // Bits written
        offsetSamples += mNumSamplesReset;

        // Parse left velocity
        if (vLeft < 0) {
            vLeft *= -1;
        } else {
            // Set sign bit in velByte
            velByte = (byte) 0x80;
        }

        // Get bits for left velocity
        velByte |= 0x7F & vLeft;

        // Write left velocity message
        for (int i = 0; i < 8; ++i) {

            int numSamples = mNumSamplesZero;

            // Check if the i-th bit in velByte is set to 1
            if ((velByte & (0x01 << i)) != 0) {
                numSamples = mNumSamplesOne;
            }

            // Invert last bit level
            lastBitLevel *= -1;

            // Write the number of samples required for the specific velocity encoding
            for (int j = 0; j < numSamples; ++j) {
                dataBuffer[j + offsetSamples] = lastBitLevel;
            }

            // Count number of samples added to the buffer
            offsetSamples += numSamples;
        }

        // Init right velocity
        velByte = 0;

        // Parse right velocity
        if (vRight < 0) {
            vRight *= -1;
        } else {
            velByte = (byte) 0x80;
        }

        // Get bits for right velocity
        velByte |=  0x7F & vRight;

        // Write right velocity message
        for (int i = 0; i < 8; ++i) {

            int numSamples = mNumSamplesZero;

            // Check if the i-th bit in velByte is set to 1
            if ((velByte & (0x01 << i)) != 0) {
                numSamples = mNumSamplesOne;
            }

            // Invert last bit level
            lastBitLevel *= -1;

            // Write the number of samples required for the specific velocity encoding
            for (int j = 0; j < numSamples; ++j) {
                dataBuffer[j + offsetSamples] = lastBitLevel;
            }

            // Count number of samples added to the buffer
            offsetSamples += numSamples;
        }

        // Parse led byte
        byte ledByte = led;

        // Write led message
        for (int i = 0; i < 8; ++i) {

            int numSamples = mNumSamplesZero;

            // Check if the i-th bit in ledByte is set to 1
            if ((ledByte & (0x01 << i)) != 0) {
                numSamples = mNumSamplesOne;
            }

            // Invert last bit level
            lastBitLevel *= -1;

            // Write the number of samples required for the specific led encoding
            for (int j = 0; j < numSamples; ++j) {
                dataBuffer[j + offsetSamples] = lastBitLevel;
            }

            // Count number of samples added to the buffer
            offsetSamples += numSamples;
        }

        return offsetSamples;
    }

    public ArrayList<MotorBeatElement> getMotorBeatElements() {
        return mMotorChoreography.getBeatElements();
    }

    public ArrayList<LedBeatElement> getLedBeatElements() {
        return mLedChoregraphy.getBeatElements();
    }
}