package ch.ethz.asl.dancebots.danceboteditor.handlers;

import android.app.Activity;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import net.lame.LameEncoder;

import java.io.File;
import java.util.Arrays;

import ch.ethz.asl.dancebots.danceboteditor.dialogs.StickyOkDialog;
import ch.ethz.asl.dancebots.danceboteditor.model.ChoreographyManager;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotConfiguration;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorManager;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotMusicFile;
import ch.ethz.asl.dancebots.danceboteditor.utils.Decoder;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotHelper;
import de.mpg123.MPG123Decoder;

/**
 * Created by andrin on 29.11.15.
 */
public class SoundEncodeRunnable implements Runnable {

    private static final String LOG_TAG = SoundEncodeRunnable.class.getSimpleName();

    private static final int BIT_RATE = 128;
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_COUNT = 2;

    // Constants for indicating the state of the encoding
    public static final int ENCODE_STATE_FAILED = -1;
    public static final int ENCODE_STATE_STARTED = 0;
    public static final int ENCODE_STATE_COMPLETED = 1;

    // Defines a field that contains the calling object of type SoundTask.
    private final TaskRunnableEncodeMethods mSoundTask;
    private LameEncoder mEncoder;

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
         * @param currentThread this worker Thread
         */
        void setEncodeThread(Thread currentThread);

        /**
         * Handle the state of the encoding process
         * @param state progress state
         */
        void handleEncodeState(int state);

        DanceBotMusicFile getMusicFile();

        ChoreographyManager getChoreographyManager();
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

            long numSamples = mSoundTask.getMusicFile().getSampleCount();

            // Initialize music and data buffers
            mPcmMusic = new short[(int)numSamples];
            mPcmData = new short[(int)numSamples];

            Log.d(LOG_TAG, "pcm data size: " + 2 * numSamples + " bytes");

            // Initialize
            Arrays.fill(mPcmData, (short) -DanceBotConfiguration.DATA_LEVEL);

            // Prepare data channel and music channel
            mSoundTask.getChoreographyManager().readDataAll(mPcmData);

            Decoder mp3Decoder = new MPG123Decoder();
            mp3Decoder.openFile(musicFile.getSongPath());
            mp3Decoder.decode();
            mp3Decoder.transfer(mPcmMusic);

            // Create new mp3 buffer and specify size in bytes
            // Calculate buffer size in bytes
            int mp3bufSize = (int) numSamples / 3;
            byte[] mp3buf = new byte[mp3bufSize];

            // Encode the audio and data buffer to a new mp3 file
            mEncoder = new LameEncoder.Builder(SAMPLE_RATE, CHANNEL_COUNT, SAMPLE_RATE, BIT_RATE).create();
            mEncoder.encode(mPcmMusic, mPcmData, (int) numSamples, mp3buf);
            mEncoder.flush(mp3buf);

            // Save raw audio buffer to a new file
            File file = DanceBotHelper.saveToMusicFolder(musicFile.getSongTitle(), mp3buf);

            if (file != null) {
                Context context = DanceBotEditorManager.getInstance().getContext();
                new StickyOkDialog()
                        .setTitle("File successfully written")
                        .setMessage("Path: " + file.getAbsolutePath())
                        .show(((Activity) context).getFragmentManager(), "dialog_ok");
            } else {
                Context context = DanceBotEditorManager.getInstance().getContext();
                new StickyOkDialog()
                        .setTitle("Error: Could not write file!")
                        .setMessage(null)
                        .show(((Activity) context).getFragmentManager(), "dialog_ok_negative");

                Log.d(LOG_TAG, "Error writing file.");
            }

            // Handle the state of the decoding Thread
            mSoundTask.handleEncodeState(ENCODE_STATE_COMPLETED);

        } catch (InterruptedException e1) {

            // Does nothing

            // In all cases, handle the results
        } finally {

            if (mEncoder != null) {
                mEncoder.close();
            }
            mPcmData = null;
            mPcmMusic = null;

            t2 = System.currentTimeMillis();
            Log.v(LOG_TAG, "Elapsed time for encoding: " + (t2 - t1) / 1000 + "s");

            Log.v(LOG_TAG, "EncodeThread finished.");
        }
    }


}
