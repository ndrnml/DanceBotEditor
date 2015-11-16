package ch.ethz.asl.dancebots.danceboteditor.handlers;

import android.os.SystemClock;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import ch.ethz.asl.dancebots.danceboteditor.utils.BeatExtractor;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorProjectFile;

/**
 * Created by andrin on 14.11.15.
 */
public class SoundBeatExtractRunnable implements Runnable {

    // Sets the log tag
    private static final String LOG_TAG = "BEAT_EXTRACT_RUNNABLE";

    // Constants indicating the state of the beat extraction
    static final int BEAT_EXTRACTION_STATE_FAILED = -1;
    static final int BEAT_EXTRACTION_STATE_COMPLETED = 1;

    // Set maximum limit for the size of the beat buffer (IntBuffer)
    private final int MAX_EXPECTED_BEATS = 1000;

    // A pointer to the native sound file handle object
    private long mSoundFileHandle;

    // Range of samples to process
    private long mStartSample;
    private long mEndSample;

    // Beat extraction specific fields (data structure and number of beats detected)
    private IntBuffer mBeatBuffer;
    private int mNumBeatsDetected;

    // Thread specific fields
    private int mThreadId;
    private int mNumThreads;

    // Defines the field that contains the calling object of type SoundTask
    public final TaskRunnableBeatExtractionMethods mSoundTask;

    /**
     *
     * An interface that defines methods that PhotoTask implements. An instance of
     * PhotoTask passes itself to an PhotoDownloadRunnable instance through the
     * PhotoDownloadRunnable constructor, after which the two instances can access each other's
     * variables.
     */
    interface TaskRunnableBeatExtractionMethods {

        /**
         * Sets the filled beat buffer with the specific Thread ID
         * @param threadId
         * @param beatBuffer
         */
        void setBeatBuffer(int threadId, IntBuffer beatBuffer);

        /**
         * Sets the status of the current Thread with the specific Thread ID
         * @param threadId The current Thread ID assigned by SoundTask
         * @param status The status of the beat extraction
         */
        void setBeatExtractionRunnableStatus(int threadId, Boolean status);

        /**
         * Returns the pointer to the sound file handle
         * @return The file handle from the decoded sound file
         */
        long getSoundFileHandle();

        /**
         * Returns the number of samples of the sound file
         * @return The number of samples from the decoded sound file
         */
        long getNumSamples();
    }

    /**
     * This constructor creates an instance of SoundBeatExtractRunnable and stores in it a reference
     * to the SoundTask instance that instantiated it
     *
     * @param decodeTask
     * @param threadId
     * @param numThreads
     */
    public SoundBeatExtractRunnable(TaskRunnableBeatExtractionMethods decodeTask, int threadId, int numThreads) {

        // Store interface object
        mSoundTask = decodeTask;

        // Store current Thread ID and the total number of threads
        mThreadId = threadId;
        mNumThreads = numThreads;

        // Initialize the beat container IntBuffer, which stores the detected beats
        initBeatBuffer();
    }

    /*
     * This beat buffer data structure is needed for the communication between native and java code
     * All later computations will NOT be made on the IntBuffer data structure
     * IntBuffer contains 32-bit ints to store 2 channels of 16-bit
     */
    private void initBeatBuffer() {

        // Allocate max expected number of beats times Integer size bytes
        ByteBuffer bb = ByteBuffer.allocateDirect(MAX_EXPECTED_BEATS * Integer.SIZE);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        mBeatBuffer = bb.asIntBuffer();
    }

    @Override
    public void run() {

        // Get the file handle pointer to the native data structure
        mSoundFileHandle = mSoundTask.getSoundFileHandle();

        // Retrieve number of samples of decoded sound file
        long numSamples = mSoundTask.getNumSamples();

        try {
            // Before continuing, checks to see that the Thread hasn't been
            // interrupted
            if (Thread.interrupted()) {

                throw new InterruptedException();
            }

            // Calculate the onset detection function alignment (6 second frames)
            long alignment = 6 * 44100;

            // Compute the chunk of samples the current Thread has to process
            long chunk = numSamples / mNumThreads;

            // Get the factorial of the onset detection function alignment to this chunk
            long fac = chunk / alignment;

            // Compute aligned chunk of samples to process
            chunk = fac * alignment;

            // Set start and end sample
            mStartSample = mThreadId * chunk;
            mEndSample = (mThreadId + 1) * chunk;

            // Handle corner cases (first and last Thread)
            if (mThreadId == 0) {
                mStartSample = 0;
            }
            if (mThreadId == mNumThreads - 1) {
                mEndSample = numSamples;
            }

            // Moves the current Thread into the background
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

            Log.v(LOG_TAG, "start beat extraction...");

            /*
             * Call to native beat extraction algorithm
             * mBeatBuffer is filled with detected beats
             */
            mNumBeatsDetected = BeatExtractor.extract(mSoundFileHandle, mBeatBuffer, mStartSample, mEndSample);

            /*for (int b = 0; b < result; ++b) {
                Log.v(LOG_TAG, "IntBuffer at " + b + ": " + buf.get(b));
            }*/

            //mNumBeatsDetected = BeatExtractor.getNumberOfBeatsDetected(mSoundFileHandle);

            if (mNumBeatsDetected < 0) {

                Log.v(LOG_TAG, "Error while extracting beats");

            } else {

                Log.v(LOG_TAG, "Thread: " + mThreadId + " Successfully decoded and beats extracted: " + mNumBeatsDetected);

                // Set feedback to the SoundTask that this Thread finished execution
                mSoundTask.setBeatExtractionRunnableStatus(mThreadId, true);
                //mSoundTask.setBeatBuffer(mThreadId, mBeatBuffer);
            }

        } catch (InterruptedException e1) {

            // Does nothing

            // In all cases, handle the results
        } finally {

        }
    }
}
