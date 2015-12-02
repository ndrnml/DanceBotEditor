package ch.ethz.asl.dancebots.danceboteditor.handlers;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import ch.ethz.asl.dancebots.danceboteditor.model.LedBeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.MotorBeatElement;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotError;
import ch.ethz.asl.dancebots.danceboteditor.utils.Decoder;
import ch.ethz.asl.dancebots.danceboteditor.utils.Encoder;

/**
 * Created by andrin on 29.11.15.
 */
public class SoundEncodeRunnable implements Runnable {

    private static final String LOG_TAG = "ENCODE_RUNNABLE";

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
    private int mNumTicksReset;
    private int mNumTicksOne;
    private int mNumTicksZero;

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
    }

    public SoundEncodeRunnable(SoundTask soundTask) {

        mSoundTask = soundTask;
    }

    @Override
    public void run() {

        /*
         * Stores the current Thread in the the SoundTask instance, so that the instance
         * can interrupt the Thread.
         */
        mSoundTask.setEncodeThread(Thread.currentThread());

        // Moves the current Thread into the background
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

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

            long numSamples = mSoundTask.getNumSamples();

            short[] pcmMusic = new short[(int)numSamples];
            short[] pcmData = new short[(int)numSamples];

            // Prepare data channel and music channel
            //int result = fillRawDataChannel(pcmData);
            int result = Decoder.transfer(pcmData);
            result = Decoder.transfer(pcmMusic);

            byte[] mp3buf = new byte[(int)(1.25 * numSamples + 7200)];

            Encoder encoder = new Encoder.Builder(44100, 2, 44100, 128).create();
            result = encoder.encode(pcmMusic, pcmData, (int)numSamples, mp3buf);
            result = encoder.flush(mp3buf);

            File mp3File = new File(Environment.getExternalStorageDirectory(), "FOO.mp3");

            Log.v(LOG_TAG, "Store mp3 file: " + mp3File.getAbsolutePath());

            if (mp3File.exists()) {
                mp3File.delete();
            }
            try {
                FileOutputStream fos = new FileOutputStream(mp3File.getPath());
                fos.write(mp3buf);
                fos.close();
            } catch (java.io.IOException e) {
                Log.d(LOG_TAG, "Exception in file writing", e);
            }

            // Handle the state of the decoding Thread
            mSoundTask.handleEncodeState(ENCODE_STATE_COMPLETED);

        } catch (InterruptedException e1) {

            // Does nothing

            // In all cases, handle the results
        } finally {

            Log.v(LOG_TAG, "EncodeThread finished.");
        }

    }

    private int fillRawDataChannel(short[] pcmData) {

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
        int tickScale = samplingRate / SAMPLE_FREQUENCY_NOMINAL;

        // TODO: round up or down?
        mNumTicksZero = tickScale * BIT_LENGTH_ZERO_NOMINAL;
        mNumTicksOne = tickScale * BIT_LENGTH_ONE_NOMINAL;
        mNumTicksReset = tickScale * BIT_LENGTH_RESET_NOMINAL;
        int numBitsInMsg = 2 * (NUM_BIT_MOTOR + 1) + 8;

        int maxNumTicks = numBitsInMsg * mNumTicksOne + mNumTicksReset;

        short dataBuffer[] = new short[maxNumTicks];

        for (int i = 0; i < numBeats - 1; ++i) {

            MotorBeatElement motorElement = motorElements.get(i);
            LedBeatElement ledElement = ledElements.get(i);

            // Get the start and end sample for the current selected beat
            long startSamplePosition = motorElements.get(i).getSamplePosition();
            long endSamplePosition = motorElements.get(i + 1).getSamplePosition();

            //int processSamples = (int) (endSamplePosition - startSamplePosition);

            short lastBit = DATA_LEVEL;
            int sample = (int) startSamplePosition;

            while (sample < endSamplePosition) {

                float relativeBeat = sample / numSamples;

                short vLeft = motorElement.getVelocityLeft(relativeBeat);
                short vRight = motorElement.getVelocityRight(relativeBeat);
                short led = ledElement.getLedBytes(relativeBeat);

                int numTicks = calculateMessage(dataBuffer, vLeft, vRight, led, lastBit);

                lastBit = dataBuffer[numTicks - 1];

                int error = writeMessage(pcmData, dataBuffer, sample, numTicks);

                sample += numTicks;
            }
        }

        return -1;
    }

    private int writeMessage(short[] pcmData, short[] dataBuffer, int msgStart, int msgLength) {

        if (msgStart > 0 && (msgStart + msgLength < mSoundTask.getNumSamples())) {

            for (int i = 0; i < msgLength; ++i) {
                pcmData[msgStart + i] = dataBuffer[i];
            }
            return DanceBotError.NO_ERROR;

        } else {

            Log.v(LOG_TAG, "ERROR: writeMessage out of bounds");
            return DanceBotError.WRITE_ERROR;
        }
    }

    private int calculateMessage(short[] dataBuffer, short vLeft, short vRight, short led, short lastBitLevel) {

        int offsetBits = 0;
        short velByte = 0;

        // Invert last bit
        lastBitLevel *= -1;

        // Write reset message
        for (int i = 0; i < mNumTicksReset; ++i) {
            dataBuffer[i] = lastBitLevel;
        }

        // Bits written
        offsetBits += mNumTicksReset;

        // Parse left velocity
        if (vLeft < 0) {
            vLeft *= -1;
        } else {
            velByte = 0x80;
        }

        velByte |= 0x7F & vLeft;

        // Write left velocity message
        for (int i = 0; i < Byte.SIZE; ++i) {

            int numTicks = mNumTicksZero;

            if (true /*velByte & (0x01 << i)*/) {
                numTicks = mNumTicksOne;
            }

            lastBitLevel *= -1;

            for (int j = 0; j < numTicks; ++j) {
                dataBuffer[j + offsetBits] = lastBitLevel;
            }

            offsetBits += numTicks;
        }

        // Parse right velocity
        velByte = 0;

        if (vRight < 0) {
            vRight *= -1;
        } else {
            velByte = 0x80;
        }

        velByte |=  0x7F & vRight;

        // Write right velocity message
        for (int i = 0; i < Byte.SIZE; ++i) {

            int numTicks = mNumTicksZero;

            if (true /*velByte & (0x01 << i)*/) {
                numTicks = mNumTicksOne;
            }

            lastBitLevel *= -1;

            for (int j = 0; j < numTicks; ++j) {
                dataBuffer[j + offsetBits] = lastBitLevel;
            }

            offsetBits += numTicks;
        }

        // Parse led byte
        short ledByte = 0xFF;

        ledByte &= led;

        // Write led message
        for (int i = 0; i < Byte.SIZE; ++i) {

            int numTicks = mNumTicksZero;

            if (true /*ledByte & (0x01 << i)*/) {
                numTicks = mNumTicksOne;
            }

            lastBitLevel *= -1;

            for (int j = 0; j < numTicks; ++j) {
                dataBuffer[j + offsetBits] = lastBitLevel;
            }

            offsetBits += numTicks;
        }

        return offsetBits;
    }
}
