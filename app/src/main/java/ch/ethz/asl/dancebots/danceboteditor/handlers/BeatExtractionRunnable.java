package ch.ethz.asl.dancebots.danceboteditor.handlers;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import ch.ethz.asl.dancebots.danceboteditor.utils.BeatExtractor;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotError;

/**
 * Created by andrin on 15.11.15.
 */
public class BeatExtractionRunnable implements Runnable {

    private static final String LOG_TAG = "BEAT_EXTRACT_RUNNABLE";

    private final int MAX_EXPECTED_BEATS = 1000;

    private int mThreadId;
    private TaskSoundProcessing mSoundProcessingTask;
    private IntBuffer mBeatBuffer;
    private int mNumberOfBeatsDetected;

    interface TaskSoundProcessing {

        void setBeatBuffer(int threadId, IntBuffer beatBuffer);

        void setBeatExtractionRunnableStatus(int threadId, boolean status);

        long getSoundFileHandle();
    }

    public BeatExtractionRunnable(TaskSoundProcessing soundProcessingTask, int threadId, long startSample, long endSample) {

        mSoundProcessingTask = soundProcessingTask;
        mThreadId = threadId;

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

        long soundFileHandle = mSoundProcessingTask.getSoundFileHandle();

        // Moves the current Thread into the background
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        // Extract beats
        Log.v(LOG_TAG, "start beat extraction...");

        int result = BeatExtractor.extract(soundFileHandle, mBeatBuffer, mBeatBuffer.capacity());

        mNumberOfBeatsDetected = BeatExtractor.getNumberOfBeatsDetected(soundFileHandle);

        //for (int b = 0; b < result; ++b) {
        //    Log.v(LOG_TAG, "IntBuffer at " + b + ": " + mBeatBuffer.get(b));
        //}

        // Save number of beats detected to dance bot music file
        //mProjectFile.getDanceBotMusicFile().setNumberOfBeatsDected(BeatExtractor.getNumberOfBeatsDetected(soundFileHandle));
        //Log.v(LOG_TAG, "store in music file: total number of beats detected: " + BeatExtractor.getNumberOfBeatsDetected(soundFileHandle));

        if (result <= 0) {

            Log.v(LOG_TAG, "Error while extracting beats");

            // Error while extracting beats
            //return DanceBotError.BEAT_EXTRACTION_ERR;

        } else {

            // Prepare music player
            //mProjectFile.getMediaPlayer().preparePlayback();

            mSoundProcessingTask.setBeatBuffer(mThreadId, mBeatBuffer);
            mSoundProcessingTask.setBeatExtractionRunnableStatus(mThreadId, true);
            Log.v(LOG_TAG, "Successfully decoded and beats extracted");

            // Successfully decoded and beats extracted
            //mProjectFile.beatExtractionDone = true;

            // Populate beats
            //mProjectFile.initChoreography();

            //return DanceBotError.NO_ERROR;
        }
    }
}
