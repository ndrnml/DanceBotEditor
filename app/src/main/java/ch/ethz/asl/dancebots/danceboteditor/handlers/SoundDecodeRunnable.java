package ch.ethz.asl.dancebots.danceboteditor.handlers;

import android.os.SystemClock;
import android.util.Log;

import ch.ethz.asl.dancebots.danceboteditor.utils.BeatExtractor;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorProjectFile;
import ch.ethz.asl.dancebots.danceboteditor.utils.Decoder;

/**
 * Created by andrin on 14.11.15.
 */
public class SoundDecodeRunnable implements Runnable {

    // Sets the log tag
    private static final String LOG_TAG = "SoundDecodeRunnable";

    // Constants for indicating the state of the download
    static final int DECODE_STATE_FAILED = -1;
    static final int DECODE_STATE_COMPLETED = 1;

    // Defines a field that contains the calling object of type PhotoTask.
    final DanceBotEditorProjectFile mProjectFile;

    // Defines a field that contains the calling object of type PhotoTask.
    final TaskRunnableDecodeMethods mSoundTask;

    /**
     *
     * An interface that defines methods that PhotoTask implements. An instance of
     * PhotoTask passes itself to an PhotoDownloadRunnable instance through the
     * PhotoDownloadRunnable constructor, after which the two instances can access each other's
     * variables.
     */
    interface TaskRunnableDecodeMethods {

        /**
         * Sets the current decoding Thread
         * @param currentThread
         */
        void setDecodeThread(Thread currentThread);

        /**
         * Sets the pointer to the native sound file object
         * @param soundFileHandler
         */
        void setSoundFileHandler(long soundFileHandler);

        /**
         * Sets the number of samples of the decoded sound file
         * @param samples
         */
        void setNumSamples(long samples);

        /**
         * Handle the state of the decoding process
         * @param state
         */
        void handleDecodeState(int state);
    }


    /**
     * This constructor creates an instance of SoundDecodeRunnable and stores in it a reference
     * to the SoundTask instance that instantiated it
     *
     * @param soundTask The SoundTask which implements TaskRunnableDecodeMethods
     */
    public SoundDecodeRunnable(TaskRunnableDecodeMethods soundTask) {
        mProjectFile = DanceBotEditorProjectFile.getInstance();
        mSoundTask = soundTask;
    }

    @Override
    public void run() {

        /*
         * Stores the current Thread in the the SoundTask instance, so that the instance
         * can interrupt the Thread.
         */
        mSoundTask.setDecodeThread(Thread.currentThread());

        // Moves the current Thread into the background
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        try {
            // Before continuing, checks to see that the Thread hasn't been
            // interrupted
            if (Thread.interrupted()) {

                throw new InterruptedException();
            }

            // Create decoder object
            Decoder mp3Decoder = new Decoder();

            Log.v(LOG_TAG, "opening mp3 file...");
            mp3Decoder.openFile(mProjectFile.getDanceBotMusicFile().getSongPath());

            Log.v(LOG_TAG, "start decoding...");
            int result = mp3Decoder.decode();

            if (result <= 0) {

                Log.v(LOG_TAG, "Error: Decoding failed.");
            }

            // Get native sound file handle pointer to the decoded sound file
            long soundFileHandle = mp3Decoder.getHandle();

            // Set the handle in the SoundTask instance, such that other Threads can access it
            mSoundTask.setSoundFileHandler(soundFileHandle);

            // Extract sample rate from decoded file
            mProjectFile.getDanceBotMusicFile().setSampleRate(mp3Decoder.getSampleRate());
            Log.v(LOG_TAG, "sample rate: " + mp3Decoder.getSampleRate());

            // Extract total number of samples from decoded file
            mProjectFile.getDanceBotMusicFile().setTotalNumberOfSamples(mp3Decoder.getNumberOfSamples());
            Log.v(LOG_TAG, "total number of samples: " + mp3Decoder.getNumberOfSamples());

            // Get the total number of samples, which were decoded
            mSoundTask.setNumSamples(mp3Decoder.getNumberOfSamples());

            // Handle the state of the decoding Thread
            mSoundTask.handleDecodeState(DECODE_STATE_COMPLETED);

        } catch (InterruptedException e1) {

            // Does nothing

            // In all cases, handle the results
        } finally {

        }
    }
}