package ch.ethz.asl.dancebots.danceboteditor.handlers;

import android.util.Log;

import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotMusicFile;
import ch.ethz.asl.dancebots.danceboteditor.utils.Decoder;

/**
 * Created by andrin on 14.11.15.
 */
public class SoundDecodeRunnable implements Runnable {

    // Sets the log tag
    private static final String LOG_TAG = "DECODE_RUNNABLE";

    // Constants for indicating the state of the decoding
    public static final int DECODE_STATE_FAILED = -1;
    public static final int DECODE_STATE_STARTED = 0;
    public static final int DECODE_STATE_COMPLETED = 1;

    // Defines a field that contains the calling object of type SoundTask.
    private final TaskRunnableDecodeMethods mSoundTask;

    /**
     * An interface that defines methods that SoundTask implements. An instance of
     * SoundTask passes itself to an SoundDecodeRunnable instance through the
     * SoundDecodeRunnable constructor, after which the two instances can access each other's
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
         * Handle the state of the decoding process
         * @param state
         */
        void handleDecodeState(int state);

        /**
         * Returns the music file, which contains all relevant information about the selected music file
         * @return The DanceBotMusicFile
         */
        DanceBotMusicFile getDanceBotMusicFile();
    }


    /**
     * This constructor creates an instance of SoundDecodeRunnable and stores in it a reference
     * to the SoundTask instance that instantiated it
     *
     * @param soundTask The SoundTask which implements TaskRunnableDecodeMethods
     */
    public SoundDecodeRunnable(TaskRunnableDecodeMethods soundTask) {
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

            /*
             * Calls the PhotoTask implementation of {@link #handleDecodeState} to
             * set the state of the download
             */
            mSoundTask.handleDecodeState(DECODE_STATE_STARTED);

            // Get the current dance bot editor music file
            DanceBotMusicFile musicFile = mSoundTask.getDanceBotMusicFile();

            // Create and initialize decoder object
            Decoder mp3Decoder = new Decoder();

            // Open a music file path
            Log.v(LOG_TAG, "opening mp3 file...");
            mp3Decoder.openFile(musicFile.getSongPath());

            // Decode the opened music file, if no error occured
            Log.v(LOG_TAG, "start decoding...");
            int result = mp3Decoder.decode();

            // Check result of the decoded music file
            if (result <= 0) {
                Log.v(LOG_TAG, "Error: Decoding failed.");
            }

            // Get native sound file handle pointer to the decoded sound file
            long soundFileHandle = mp3Decoder.getHandle();

            // Set the handle in the SoundTask instance, such that other Threads can access it
            mSoundTask.setSoundFileHandler(soundFileHandle);

            // Extract sample rate from decoded file
            musicFile.setSampleRate(mp3Decoder.getSampleRate());
            Log.v(LOG_TAG, "sample rate: " + musicFile.getSampleRate());

            // Get the total number of samples, which were decoded
            musicFile.setTotalNumberOfSamples(mp3Decoder.getNumberOfSamples());
            Log.v(LOG_TAG, "total number of samples: " + musicFile.getNumberOfSamples());

            // Handle the state of the decoding Thread
            mSoundTask.handleDecodeState(DECODE_STATE_COMPLETED);

        } catch (InterruptedException e1) {

            // Does nothing

            // In all cases, handle the results
        } finally {

            Log.v(LOG_TAG, "DecodeThread finished.");
        }
    }
}
