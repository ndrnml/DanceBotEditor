package ch.ethz.asl.dancebots.danceboteditor.handlers;

import android.os.*;
import android.os.Process;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import ch.ethz.asl.dancebots.danceboteditor.model.LedBeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.LedType;
import ch.ethz.asl.dancebots.danceboteditor.model.MotorBeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.MotorType;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotError;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotMusicFile;
import ch.ethz.asl.dancebots.danceboteditor.utils.Decoder;
import ch.ethz.asl.dancebots.danceboteditor.utils.Encoder;
import ch.ethz.asl.dancebots.danceboteditor.utils.Helper;

/**
 * Created by andrin on 29.11.15.
 */
public class SoundEncodeRunnable implements Runnable {

    private static final String LOG_TAG = SoundEncodeRunnable.class.getSimpleName();

    private static final int BIT_RATE = 128;
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_COUNT = 2;


    // TODO: MOVE THIS CONSTS
    private static final int SAMPLE_FREQUENCY_NOMINAL = 44100;
    private static final int BIT_LENGTH_ONE_NOMINAL = 24;
    private static final int BIT_LENGTH_ZERO_NOMINAL = 8;
    private static final int BIT_LENGTH_RESET_NOMINAL = 40;
    private static final int NUM_BIT_MOTOR = 7;
    private static final short DATA_LEVEL = 26214;

    // Constants for indicating the state of the encoding
    public static final int ENCODE_STATE_FAILED = -1;
    public static final int ENCODE_STATE_STARTED = 0;
    public static final int ENCODE_STATE_COMPLETED = 1;

    // Defines a field that contains the calling object of type SoundTask.
    private final TaskRunnableEncodeMethods mSoundTask;
    private Encoder mEncoder;
    private int mNumSamplesReset;
    private int mNumSamplesOne;
    private int mNumSamplesZero;

    private long t1,t2;
    private short[] mPcmData;
    private short[] mPcmMusic;

    /**
     * An interface that defines methods that SoundTask implements. An instance of
     * SoundTask passes itself to an SoundEncodeRunnable instance through the
     * SoundEncodeRunnable constructor, after which the two instances can access each other's
     * variables.
     */
    interface TaskRunnableEncodeMethods {

        /**
         * Sets the current encoding Thread
         * @param currentThread
         */
        void setEncodeThread(Thread currentThread);

        /**
         * Handle the state of the encoding process
         * @param state
         */
        void handleEncodeState(int state);

        ArrayList<MotorBeatElement> getMotorElements();

        ArrayList<LedBeatElement> getLedElements();

        int getSamplingRate();

        int getNumBeats();

        long getNumSamples();

        DanceBotMusicFile getMusicFile();
    }

    public SoundEncodeRunnable(SoundTask soundTask) {

        mSoundTask = soundTask;
    }

    @Override
    public void run() {

        t1 = System.currentTimeMillis();

        /*
         * Stores the current Thread in the the SoundTask instance, so that the instance
         * can interrupt the Thread.
         */
        mSoundTask.setEncodeThread(Thread.currentThread());

        // Moves the current Thread into the background
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        try {
            // Before continuing, checks to see that the Thread hasn't been
            // interrupted
            if (Thread.interrupted()) {

                throw new InterruptedException();
            }

            /*
            * Calls the SoundTask implementation of {@link #handleEncodeState} to
            * set the state of the download
            */
            mSoundTask.handleEncodeState(ENCODE_STATE_STARTED);

            // Sound file to encode and save
            DanceBotMusicFile musicFile = mSoundTask.getMusicFile();

            long numSamples = mSoundTask.getNumSamples();

            mPcmMusic = new short[(int)numSamples];
            mPcmData = new short[(int)numSamples];

            Log.d(LOG_TAG, "pcm data size: " + 2 * numSamples + " bytes");

            // Initialize
            Arrays.fill(mPcmData, (short) -DATA_LEVEL);

            // Prepare data channel and music channel
            /*int result = generateDataChannel(mPcmData);
            //int result = Decoder.transfer(pcmData);
            result = Decoder.transfer(mPcmMusic);

            // Create new mp3 buffer and specify size in bytes
            // Calculate buffer size in bytes
            */int mp3bufSize = (int) numSamples / 3;
            byte[] mp3buf = new byte[mp3bufSize];
/*
            mEncoder = new Encoder.Builder(SAMPLE_RATE, CHANNEL_COUNT, SAMPLE_RATE, BIT_RATE).create();
            result = mEncoder.encode(mPcmMusic, mPcmData, (int) numSamples, mp3buf);
            result = mEncoder.flush(mp3buf);*/

            boolean success = Helper.saveToMusicFolder(musicFile.getSongTitle(), mp3buf);

            if (success) {

                // TODO show success toast

            } else {

                // TODO show fail toast

            }

            // Handle the state of the decoding Thread
            mSoundTask.handleEncodeState(ENCODE_STATE_COMPLETED);

        } catch (InterruptedException e1) {

            // Does nothing

            // In all cases, handle the results
        } finally {

            /*
            if (mEncoder != null) {
                mEncoder.close();
            }
            mPcmData = null;
            mPcmMusic = null;*/

            t2 = System.currentTimeMillis();
            Log.v(LOG_TAG, "Elapsed time for encoding: " + (t2 - t1) / 1000 + "s");

            Log.v(LOG_TAG, "EncodeThread finished.");
        }

    }



    /**
     *
     * @param pcmData
     * @return
     */
    private int generateDataChannel(short[] pcmData) {

        // Get beat element lists for motor and led elements
        ArrayList<MotorBeatElement> motorElements = mSoundTask.getMotorElements();
        ArrayList<LedBeatElement> ledElements = mSoundTask.getLedElements();

        // Get the total number of beats detected
        int numBeats = mSoundTask.getNumBeats();
        // Get the total number of samples decoded
        long numSamples = mSoundTask.getNumSamples();
        // Get the detected sample rate of decoding
        int samplingRate = mSoundTask.getSamplingRate();

        // Compute the nominal sampling scale
        float sampleScale = samplingRate / SAMPLE_FREQUENCY_NOMINAL;

        // Round to the closest Integer
        mNumSamplesZero = Math.round(sampleScale * BIT_LENGTH_ZERO_NOMINAL);
        mNumSamplesOne = Math.round(sampleScale * BIT_LENGTH_ONE_NOMINAL);
        mNumSamplesReset = Math.round(sampleScale * BIT_LENGTH_RESET_NOMINAL);

        // Define the maximum number of bits in a robot message
        int numBitsInMsg = 2 * (NUM_BIT_MOTOR + 1) + 8;

        // Define the maximum number of samples in a robot message
        int maxNumSamplesInMsg = numBitsInMsg * mNumSamplesOne + mNumSamplesReset;

        // Initialize a new data buffer, which stores the current calculated message
        short dataBuffer[] = new short[maxNumSamplesInMsg];

        // Keep a state of the last sample written
        short lastSampleLevel = DATA_LEVEL;

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

    /**
     * TODO
     * @param pcmData
     * @param dataBuffer
     * @param msgStart
     * @param msgLength
     * @return
     */
    private int writeMessage(short[] pcmData, short[] dataBuffer, int msgStart, int msgLength) {

        if (msgStart > 0 && (msgStart + msgLength < mSoundTask.getNumSamples())) {

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
            dataBuffer[i] = -DATA_LEVEL;
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
}
