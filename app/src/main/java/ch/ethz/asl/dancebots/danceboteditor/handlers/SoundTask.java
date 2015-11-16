package ch.ethz.asl.dancebots.danceboteditor.handlers;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import ch.ethz.asl.dancebots.danceboteditor.view.HorizontalRecyclerViews;

/**
 * Created by andrin on 14.11.15.
 */
public class SoundTask implements
        SoundDecodeRunnable.TaskRunnableDecodeMethods,
        SoundBeatExtractRunnable.TaskRunnableBeatExtractionMethods {

    private static final String LOG_TAG = "SOUND_TASK";

    // The image's URL
    //private URL mImageURL;

    // The width and height of the decoded image
    private long mSoundFileHandler;
    private long mNumSamples;

    /*
     * Field containing the Thread this task is running on.
     */
    private Thread mThreadThis;

    /*
     * Fields containing references to the two runnable objects that handle downloading and
     * decoding of the image.
     */
    private Runnable mDecodeRunnable;
    private ArrayList<Runnable> mBeatExtractionRunnables;
    private ArrayList<Boolean> mBeatExtractionRunnablesStatus;
    private ArrayList<IntBuffer> mBeatBuffers;

    // A buffer for containing the bytes that make up the image
    private byte[] mBeatBuffer;

    // An object that contains the ThreadPool singleton.
    private SoundManager sSoundManager;

    private long t1,t2;
    /**
     * Creates an PhotoTask containing a download object and a decoder object.
     */
    public SoundTask(int numThreads) {

        t1 = SystemClock.currentThreadTimeMillis();

        // Create the runnables
        mDecodeRunnable = new SoundDecodeRunnable(this);

        /*
         * Creates the initial beat extraction runnables
         */
        mBeatExtractionRunnables = new ArrayList<>();

        /*
         * Sets the initial beat extraction runnable status
         */
        mBeatExtractionRunnablesStatus = new ArrayList<>();

        for (int i = 0; i < numThreads; ++i) {
            mBeatExtractionRunnables.add(new SoundBeatExtractRunnable(this, i, numThreads));
            mBeatExtractionRunnablesStatus.add(i, false);
        }

        sSoundManager = SoundManager.getInstance();

    }

    public void initializeDecoderTask(SoundManager soundManager, View toastView) {

        // Sets this object's ThreadPool field to be the input argument
        sSoundManager = soundManager;

        // Gets the URL for the View
        //mImageURL = beatView.getLocation();

        // Gets the width and height of the provided ImageView
        //mTargetWidth = beatView.getWidth();
        //mTargetHeight = beatView.getHeight();
    }

    public void initializeBeatExtractionRunnablesk() {
        // TODO, based on the length of the song decide how many threads should be used
    }

    public ArrayList<Runnable> getSoundBeatExtractionRunnables() {
        return mBeatExtractionRunnables;
    }

    private void handleState(int state) {
        sSoundManager.handleState(this, state);
    }

    private boolean allBeatExtractionRunnablesDone() {
        for (boolean status : mBeatExtractionRunnablesStatus) {
            if (!status) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setDecodeThread(Thread thread) {
        synchronized (sSoundManager) {
            mThreadThis = thread;
        }
    }

    @Override
    public void setSoundFileHandler(long soundFileHandler) {
        mSoundFileHandler = soundFileHandler;
    }

    @Override
    public void setNumSamples(long samples) {
        mNumSamples = samples;
    }

    @Override
    public void handleDecodeState(int state) {

        int soundTaskState;

        // Converts the download state to the overall state
        switch(state) {
            case SoundDecodeRunnable.DECODE_STATE_COMPLETED:
                soundTaskState = SoundManager.DECODING_COMPLETE;
                break;
            case SoundDecodeRunnable.DECODE_STATE_FAILED:
                soundTaskState = SoundManager.DECODING_FAILED;
                break;
            default:
                soundTaskState = SoundManager.DECODING_STARTED;
                break;
        }

        // Passes the state to the ThreadPool object.
        handleState(soundTaskState);

        while (!allBeatExtractionRunnablesDone()) {

            try {
                Log.v(LOG_TAG, "Beat extraction not yet done............");
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        t2 = SystemClock.currentThreadTimeMillis();
        Log.v(LOG_TAG, "Elapsed time for decoding and extracting: " + Long.toString(t2 - t1));

        Log.v(LOG_TAG, "Now we can continue....................");
    }

    @Override
    public void setBeatBuffer(int threadId, IntBuffer beatBuffer) {
        mBeatBuffers.set(threadId, beatBuffer);
    }

    @Override
    public void setBeatExtractionRunnableStatus(int threadId, Boolean status) {
        mBeatExtractionRunnablesStatus.set(threadId, status);
    }

    @Override
    public long getSoundFileHandle() {
        return mSoundFileHandler;
    }

    @Override
    public long getNumSamples() {
        return mNumSamples;
    }

    public Runnable getDecodeRunnable() {
        return mDecodeRunnable;
    }
}
