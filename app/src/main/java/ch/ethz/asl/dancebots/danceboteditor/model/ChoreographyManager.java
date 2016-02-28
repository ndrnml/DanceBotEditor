package ch.ethz.asl.dancebots.danceboteditor.model;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import ch.ethz.asl.dancebots.danceboteditor.adapters.BeatElementAdapter;
import ch.ethz.asl.dancebots.danceboteditor.ui.FloatSelectionMenu;
import ch.ethz.asl.dancebots.danceboteditor.ui.IntegerSelectionMenu;
import ch.ethz.asl.dancebots.danceboteditor.ui.LedTypeSelectionMenu;
import ch.ethz.asl.dancebots.danceboteditor.ui.MotorTypeSelectionMenu;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotConfiguration;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotError;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotMusicFile;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotMusicStream;
import ch.ethz.asl.dancebots.danceboteditor.view.HorizontalRecyclerViews;

/**
 * Created by andrin on 31.08.15.
 */
public class ChoreographyManager implements DanceBotMusicStream.StreamPlayback {

    private static final String LOG_TAG = "CHOREOGRAPHY_MANAGER";

    private final DanceBotMusicFile mMusicFile;

    private Context mContext;

    private Choreography<LedBeatElement> mLedChoregraphy;
    private Choreography<MotorBeatElement> mMotorChoreography;

    private final ChoreographyViewManager mBeatViews;

    // Data source fields
    private ArrayList<MotorBeatElement> mMotorElements;
    private ArrayList<LedBeatElement> mLedElements;
    private short[] mDataBuffer;
    private short mLastSampleLevel;

    private int mNumSamplesReset;
    private int mNumSamplesOne;
    private int mNumSamplesZero;

    private int mSampleRate;


    /**
     * An interface that defines methods that SoundTask implements. An instance of
     * SoundTask passes itself to an SoundDecodeRunnable instance through the
     * SoundDecodeRunnable constructor, after which the two instances can access each other's
     * variables.
     */
    public interface ChoreographyViewManager {

        /**
         * Sets the current led element view adapter
         *
         * @param ledAdapter led light recycler view adapter
         */
        void setLedElementAdapter(BeatElementAdapter ledAdapter);

        /**
         * Sets the current motor element view adapter
         *
         * @param motorAdapter motor element recycler view adapter
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

        mBeatViews.setLedElementAdapter(new BeatElementAdapter<>(mContext, ledElements, mLedChoregraphy));

        ArrayList<MotorBeatElement> motorElements = initMotorBeatElements(musicFile);
        mMotorChoreography = new Choreography<>(motorElements);

        mBeatViews.setMotorElementAdapter(new BeatElementAdapter<>(mContext, motorElements, mMotorChoreography));

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

            if (selectedBeatElem.getDanceSequenceId() == null) {

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

            if (selectedBeatElem.getDanceSequenceId() == null) {

                mMotorChoreography.addNewDanceSequence((MotorBeatElement) selectedBeatElem, selectedChoreoLength.getValAt(selectedChoreoLengthIdx));

            } else {

                mMotorChoreography.updateDanceSequence((MotorBeatElement) selectedBeatElem, selectedChoreoLength.getValAt(selectedChoreoLengthIdx));

            }
        }
    }


    public void processNegativeClick(BeatElement selectedBeatElem) {

        // Check if dance sequence exists
        if (selectedBeatElem.getDanceSequenceId() != null) {

            if (selectedBeatElem.getClass() == LedBeatElement.class) { // LED_TYPE

                mLedChoregraphy.removeDanceSequence((LedBeatElement) selectedBeatElem);

            } else if (selectedBeatElem.getClass() == MotorBeatElement.class) { // MOTOR_TYPE

                mMotorChoreography.removeDanceSequence((MotorBeatElement) selectedBeatElem);
            }
        }
    }


    /**
     * Initialize led beat elements after successfully extracting all beats
     *
     * @param musicFile selected music file
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
            Log.d(LOG_TAG, "Error: initLedBeatElements, Number of beats: " + numBeats);
        }

        return elems;
    }

    /**
     * Initialize motor beat elements after successfully extracting all beats
     *
     * @param musicFile selected music file
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
            Log.d(LOG_TAG, "Error: initMotorBeatElements, Number of beats: " + numBeats);
        }

        return elems;
    }

    @Override
    public void prepareStreamPlayback() {

        // Get beat element lists for motor and led elements
        mMotorElements = mMotorChoreography.getBeatElements();
        mLedElements = mLedChoregraphy.getBeatElements();

        // Get the detected sample rate of decoding
        mSampleRate = mMusicFile.getSampleRate();

        // Compute the nominal sampling scale
        float sampleScale = mSampleRate / DanceBotConfiguration.SAMPLE_FREQUENCY_NOMINAL;

        // Round to the closest Integer
        mNumSamplesZero = Math.round(sampleScale * DanceBotConfiguration.BIT_LENGTH_ZERO_NOMINAL);
        mNumSamplesOne = Math.round(sampleScale * DanceBotConfiguration.BIT_LENGTH_ONE_NOMINAL);
        mNumSamplesReset = Math.round(sampleScale * DanceBotConfiguration.BIT_LENGTH_RESET_NOMINAL);

        // Define the maximum number of bits in a robot message
        int numBitsInMsg = 2 * (DanceBotConfiguration.NUM_BIT_MOTOR + 1) + 8;

        // Define the maximum number of samples in a robot message
        int maxNumSamplesInMsg = numBitsInMsg * mNumSamplesOne + mNumSamplesReset;

        // Initialize a new data buffer, which stores the current calculated message
        mDataBuffer = new short[maxNumSamplesInMsg];

        // Keep a state of the last sample written
        mLastSampleLevel = DanceBotConfiguration.DATA_LEVEL;

    }

    /**
     * Iterate over all whole music file (all beats) and gather dance sequence data. Transform it
     * to PCM and write it into the output buffer.
     *
     * @param outputBuffer output buffer that stores the PCM encoded dance sequence data
     * @return return number of samples written
     */
    public int readDataAll(short[] outputBuffer) {

        // Get beat element lists for motor and led elements
        ArrayList<MotorBeatElement> motorElements = getMotorBeatElements();
        ArrayList<LedBeatElement> ledElements = getLedBeatElements();

        // Get the total number of beats detected
        int numBeats = mMusicFile.getBeatCount();
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

        int samplePos = 0;

        // Iterate over all detected beats in the song (skipping the first)
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
            samplePos = 0;

            // Iterate while not all samples at the current beat are processed
            while (samplePos < samplesToProcess) {

                // Get relative beat in percent
                float relativeBeat = ((float) samplePos / (float) samplesToProcess);

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
                    writeMessage(outputBuffer, dataBuffer, currentSamplePosition, numSamplesInMsg);

                } else {

                    // If this happens, all messages have been written for the current beat
                    break;
                }

                samplePos += numSamplesInMsg;
            }
        }

        return samplePos;
    }


    /**
     * Read dance sequence data stream into outputDataBuffer
     *
     * @param outputDataBuffer output data buffer, containing dance sequence pcm encoding
     * @param currentMicroSecs number of shorts written so far
     * @return number of samples written to the output data buffer
     */
    @Override
    public int readDataStream(short[] outputDataBuffer, long currentMicroSecs) {

        int outputDataBufferSize = outputDataBuffer.length;
        int beatPos = mMusicFile.getBeatFromMicroSecs(currentMicroSecs);
        //Log.d(LOG_TAG, "beat: " + beatPos);

        // Get the start sample of the buffer
        long sampleStartBuffer = (long) (currentMicroSecs * 0.001 * 0.001 * mSampleRate);

        // Fill buffer with default value initially
        Arrays.fill(outputDataBuffer, (short) -DanceBotConfiguration.DATA_LEVEL);

        // Get the corresponding MotorBeatElement and LedBeatElement
        MotorBeatElement motorElement = mMotorElements.get(beatPos);
        LedBeatElement ledElement = mLedElements.get(beatPos);

        // Get beat based sample start and end positions
        long sampleStartBeat = mMotorElements.get(beatPos).getSamplePosition();
        long sampleEndBeat = 0;
        if (beatPos + 1 < mMotorElements.size()) {
            sampleEndBeat = mMotorElements.get(beatPos + 1).getSamplePosition();
        }

        // Compute the total number of samples to process for the current beat
        int samplesToProcess = outputDataBufferSize;
        // Check that samples to process does not exceed to next beat
        if (sampleStartBuffer + outputDataBufferSize >= sampleEndBeat) {
            samplesToProcess = outputDataBufferSize - (int) (sampleStartBuffer + outputDataBufferSize - sampleEndBeat);
        }

        // Initialize the (current) relative sample start position to zero
        int samplePos = 0;

        // Iterate while current buffer is not full AND while not all samples at the current beat
        // are processed
        while (samplePos < samplesToProcess) {

            // Relative beat must be calculated from real start and end position of beat
            float relativeBeat = ((float) (samplePos + sampleStartBuffer - sampleStartBeat) / (float) (sampleEndBeat - sampleStartBeat));

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

            int numSamplesInMsg = calculateMessage(mDataBuffer, vLeft, vRight, led, mLastSampleLevel);

            // Get last sample level
            mLastSampleLevel = mDataBuffer[numSamplesInMsg - 1];

            /*
             * If the end of samples to process is not reached, the data buffer is copied to
             * pcm buffer
             */
            if (samplePos + numSamplesInMsg < samplesToProcess) {

                // Write calculated data message to pcm buffer
                writeMessage(outputDataBuffer, mDataBuffer, samplePos, numSamplesInMsg);

                // Accumulate number of processed samples
                samplePos += numSamplesInMsg;

            } else {

                // If this happens, all messages have been written for the current beat
                break;
            }
        }

        return samplePos;
    }

    /**
     * Write next data message to the output buffer
     *
     * @param outputBuffer output buffer that contains all messages
     * @param dataMessage next message to write to the output buffer
     * @param msgStart start offset of the output buffer
     * @param msgLength length of the next message to write
     * @return error code
     */
    private int writeMessage(short[] outputBuffer, short[] dataMessage, int msgStart, int msgLength) {

        if (msgStart + msgLength <= outputBuffer.length) {

            for (int i = 0; i < msgLength; ++i) {
                outputBuffer[msgStart + i] = dataMessage[i];
            }

            return DanceBotError.NO_ERROR;

        } else {

            Log.d(LOG_TAG, "ERROR: writeMessage out of bounds");
            return DanceBotError.WRITE_ERROR;
        }
    }

    /**
     * This function calculates the needed samples for left and right velocities and for the led
     * light. The values of the current beat element will be parsed an put into the chunk container.
     * After the three properties are parsed and the buffer filled it returns the number of samples
     * written to the data chunk.
     *
     * @param dataChunk message into which will be written
     * @param vLeft velocity left of current element
     * @param vRight velocity right of current element
     * @param led led light indicator of current element
     * @param lastBitLevel last DATA_LEVEL that was written to the output buffer
     * @return the written samples
     */
    private int calculateMessage(short[] dataChunk, short vLeft, short vRight, byte led, short lastBitLevel) {

        int offsetSamples = 0;
        byte velByte = 0;

        // Init all dataBuffer elements to -DATA_LEVEL
        for (int i = 0; i < dataChunk.length; ++i) {
            dataChunk[i] = -DanceBotConfiguration.DATA_LEVEL;
        }

        // Invert last bit
        lastBitLevel *= -1;

        // Write reset message
        for (int i = 0; i < mNumSamplesReset; ++i) {
            dataChunk[i] = lastBitLevel;
        }

        // Bits written
        offsetSamples += mNumSamplesReset;

        // Parse left velocity
        if (vLeft < 0) {
            vLeft *= -1;
        } else {
            // Set sign bit in velByte
            velByte |= 0x80;
        }

        // Get bits for left velocity
        velByte |= 0x7F & vLeft;

        // Write left velocity message
        for (int i = 0; i < Byte.SIZE; ++i) {

            int numSamples = mNumSamplesZero;

            // Check if the i-th bit in velByte is set to 1
            if ((velByte & (0x01 << i)) != 0) {
                numSamples = mNumSamplesOne;
            }

            // Invert last bit level
            lastBitLevel *= -1;

            // Write the number of samples required for the specific velocity encoding
            for (int j = 0; j < numSamples; ++j) {
                dataChunk[j + offsetSamples] = lastBitLevel;
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
            velByte |= 0x80;
        }

        // Get bits for right velocity
        velByte |=  0x7F & vRight;

        // Write right velocity message
        for (int i = 0; i < Byte.SIZE; ++i) {

            int numSamples = mNumSamplesZero;

            // Check if the i-th bit in velByte is set to 1
            if ((velByte & (0x01 << i)) != 0) {
                numSamples = mNumSamplesOne;
            }

            // Invert last bit level
            lastBitLevel *= -1;

            // Write the number of samples required for the specific velocity encoding
            for (int j = 0; j < numSamples; ++j) {
                dataChunk[j + offsetSamples] = lastBitLevel;
            }

            // Count number of samples added to the buffer
            offsetSamples += numSamples;
        }

        // Parse led byte
        byte ledByte = (byte) (0xFF & led);

        // Write led message
        for (int i = 0; i < Byte.SIZE; ++i) {

            int numSamples = mNumSamplesZero;

            // Check if the i-th bit in ledByte is set to 1
            if ((ledByte & (0x01 << i)) != 0) {
                numSamples = mNumSamplesOne;
            }

            // Invert last bit level
            lastBitLevel *= -1;

            // Write the number of samples required for the specific led encoding
            for (int j = 0; j < numSamples; ++j) {
                dataChunk[j + offsetSamples] = lastBitLevel;
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