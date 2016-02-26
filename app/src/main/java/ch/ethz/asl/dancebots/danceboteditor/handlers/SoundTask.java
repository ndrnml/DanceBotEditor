package ch.ethz.asl.dancebots.danceboteditor.handlers;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.nio.IntBuffer;
import java.util.ArrayList;

import ch.ethz.asl.dancebots.danceboteditor.utils.BeatExtractor;
import ch.ethz.asl.dancebots.danceboteditor.model.ChoreographyManager;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorManager;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotMusicFile;
import ch.ethz.asl.dancebots.danceboteditor.view.HorizontalRecyclerViews;

/**
 * Created by andrin on 14.11.15.
 */

/**
 * This class is the task factory for sound related tasks, like decoding, encoding and beat
 * extraction.
 */
public class SoundTask implements
        SoundDecodeRunnable.TaskRunnableDecodeMethods,
        SoundBeatExtractRunnable.TaskRunnableBeatExtractionMethods,
        SoundEncodeRunnable.TaskRunnableEncodeMethods {

    private static final String LOG_TAG = "SOUND_TASK";

    // UI Thread dialog
    private ProgressDialog mSoundTaskProgressDialog;

    // UI Thread info toast
    private Toast mInfoToast;

    // The DanceBotMusicFile, which keeps all relevant information about the selected song
    private DanceBotMusicFile mMusicFile;

    // The ChoreographyManager, which keeps all relevant information about the selected choreography
    private ChoreographyManager mChoreoManager;

    // The width and height of the decoded image
    private long mSoundFileHandler;

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
    private ArrayList<Integer> mBeatExtractionRunnablesStatus;
    private ArrayList<IntBuffer> mBeatBuffers;

    // Reference to runnable object that encodes raw song data
    private Runnable mEncodeRunnable;

    // An object that contains the ThreadPool singleton.
    private final SoundManager sSoundManager;

    // Time measurement intrinsics, this can be removed eventually
    private long t1,t2;

    /**
     * Creates a SoundTask containing a decoder object and a beat extractor object.
     */
    public SoundTask(int numThreads) {

        t1 = System.currentTimeMillis();

        // Create the decode Runnable
        mDecodeRunnable = new SoundDecodeRunnable(this);

        // Create the encode Runnable
        mEncodeRunnable = new SoundEncodeRunnable(this);

        /*
         * Creates the initial beat extraction runnables
         */
        mBeatExtractionRunnables = new ArrayList<>();

        /*
         * Sets the initial beat extraction runnable status
         */
        mBeatExtractionRunnablesStatus = new ArrayList<>();

        // Sets the initial beat buffer array to numThreads size
        mBeatBuffers = new ArrayList<>();

        for (int i = 0; i < numThreads; ++i) {
            mBeatExtractionRunnables.add(new SoundBeatExtractRunnable(this, i, numThreads));
            mBeatExtractionRunnablesStatus.add(i, -1);
            mBeatBuffers.add(null);
        }

        // Get the SoundManager instance and attach it to this task
        sSoundManager = SoundManager.getInstance();

    }

    /**
     * Initialize the decoder task.
     *
     * @param context pass the caller context
     * @param musicFile the music file which will be decoded
     */
    public void initializeDecoderTask(Context context, DanceBotMusicFile musicFile) {

        // Sets the selected dance bot editor music file
        mMusicFile = musicFile;

        // Initialize progress dialog on UI thread
        initializeProgressDialog(context);

        // Initialize error toast
        mInfoToast = Toast.makeText(context, "", Toast.LENGTH_LONG);
    }

    /**
     * Initialize the encoder task
     *
     * @param context context which the Encoder task belongs to
     * @param musicFile the music file whose audio will be encoded
     * @param choreoManager the choreography manager which is responsible for handling the dance
     *                      sequence transformation
     */
    public void initializeEncoderTask(Context context, DanceBotMusicFile musicFile, ChoreographyManager choreoManager) {

        // Sets the selected dance bot editor music file
        mMusicFile = musicFile;

        // Sets the selected choreography manager
        mChoreoManager = choreoManager;

        // Initialize progress dialog on UI thread
        initializeProgressDialog(context);

        // Initialize error toast
        mInfoToast = Toast.makeText(context, "", Toast.LENGTH_LONG);
    }


    /**
     * Initialize progress dialog for the SoundTasks
     * @param context pass context to ProgressDialog
     */
    private void initializeProgressDialog(Context context) {
        // Initialize progress dialog on UI thread
        mSoundTaskProgressDialog = new ProgressDialog(context);
        mSoundTaskProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mSoundTaskProgressDialog.setCancelable(false);
        mSoundTaskProgressDialog.setCanceledOnTouchOutside(false);
        mSoundTaskProgressDialog.setIndeterminate(true);
        mSoundTaskProgressDialog.setProgress(0);
    }

    public ArrayList<Runnable> getSoundBeatExtractionRunnables() {
        return mBeatExtractionRunnables;
    }

    private void handleState(int state) {
        sSoundManager.handleState(this, state);
    }

    /**
     * Collect all detected beats from the different worker Threads and put them into the same
     * data structure.
     */
    private void postProcessExtractedBeats() {

        // Main decoder Thread actively waits for worker (BeatExtraction) Threads to finish
        while (!allBeatExtractionRunnablesDone()) {

            if (Thread.interrupted()) {
                try {
                    throw new InterruptedException();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.v(LOG_TAG, "SoundTask Thread interrupted...");
                    return;
                }
            }

            try {
                //Log.v(LOG_TAG, "Beat extraction not yet done............");
                Thread.sleep(200);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            /*
             * Pass to SoundManager and handle global state.
             * In this case update the beat extraction progress bar.
             */
            handleState(SoundManager.UPDATE_PROGRESS);
            //Log.v(LOG_TAG, "Progress: " + BeatExtractor.getNumberOfProcessedSamples(mSoundFileHandler));
        }

        t2 = System.currentTimeMillis();
        Log.v(LOG_TAG, "Elapsed time for decoding and extracting: " + (t2 - t1) / 1000 + "s");

        /*
         * After all beat extraction Threads terminated, the current thread is collecting
         * all extracted beats from the ArrayList<IntBuffer>.
         */
        collectExtractedBeats();
        Log.v(LOG_TAG, "Collecting extracted beats done.");

        /*
         * Notify SoundManager that beat extraction is complete now.
         * Only the SoundManager can modify views, thus all view related initializations have to be
         * performed in the main loop thread
         */
        handleState(SoundManager.BEAT_EXTRACTION_COMPLETE);
    }

    /**
     * @return state of all worker Runnables
     */
    private boolean allBeatExtractionRunnablesDone() {
        for (int status : mBeatExtractionRunnablesStatus) {
            if (status < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method provides functionality to collect the extracted beats
     */
    private void collectExtractedBeats() {

        // Compute the total number of beats extracted
        int totalNumBeats = 0;
        for (int beats : mBeatExtractionRunnablesStatus) {
            totalNumBeats += beats;
        }

        /*
         * Instantiate a new beatBuffer int array with a fixed size of totalNumBeats
         * The reason why a int[] array is chosen is, that JDK does not yet offer a convenient
         * way to convert int to Integer types.
         */
        int[] beatBuffer = new int[totalNumBeats];

        // Keep a beat index to fill the final array consecutively
        int beatIdx = 0;

        // To collect all extracted beats, iterate over all ArrayList<IntBuffer> mBeatBuffers
        for (int bufferIdx = 0; bufferIdx < mBeatBuffers.size(); ++bufferIdx) {

            // Get the number of extracted beats for the current IntBuffer
            int currentNumBeats = mBeatExtractionRunnablesStatus.get(bufferIdx);

            // Get the IntBuffer
            IntBuffer currentBeatBuffer = mBeatBuffers.get(bufferIdx);

            // Initialize a buffer container
            int[] buffer = new int[currentNumBeats];

            // Fetch the bytes from the IntBuffer and put it into the buffer container
            currentBeatBuffer.get(buffer);

            // Iterate over all extracted beats and copy them into the final beatBuffer
            for (int i = 0; i < buffer.length; ++i) {

                beatBuffer[beatIdx] = buffer[i];
                beatIdx += 1;
            }
        }

        // Update the music file with the total number of beats extracted
        mMusicFile.setNumberOfBeatsDected(totalNumBeats);
        // Update the music file with the final collection of extracted beat positions
        mMusicFile.setBeatBuffer(beatBuffer);
    }

    public Runnable getDecodeRunnable() {
        return mDecodeRunnable;
    }

    public ProgressDialog getProgressDialog() {
        return mSoundTaskProgressDialog;
    }

    public Runnable getEncodeRunnable() {
        return mEncodeRunnable;
    }

    /**
     * Compute the progress percentage for beat extraction
     */
    public int getProgress() {
        float fraction = (float) BeatExtractor.getNumberOfProcessedSamples(mSoundFileHandler) / (float) getNumSamples();
        return (int) (fraction * 100);
    }

    /*******************************
     * SoundDecodeRunnable Interface
     *******************************/

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

    /**
     * Handle the local state of the decoder Thread and map it to a global SoundManager state
     * @param state The local decoder state which is handled
     */
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

        // If the decoding Thread terminated successfully, continue with beat collection
        if (state == SoundDecodeRunnable.DECODE_STATE_COMPLETED) {

            /*
             * When decoding is completed, the multi-threaded beat extraction starts.
             * The current (decoder Thread) waits spinning for the beat extraction to complete.
             */
            postProcessExtractedBeats();

            // Handle global SoundManager state
            handleState(SoundManager.TASK_COMPLETE);
        }
    }

    @Override
    public DanceBotMusicFile getDanceBotMusicFile() {
        return mMusicFile;
    }

    /************************************
     * SoundBeatExtractRunnable Interface
     ************************************/

    /**
     * For each Thread assign a beat buffer with the corresponding Thread ID
     * @param threadId The Thread ID which belongs to the beat buffer
     * @param beatBuffer The buffer which contains the extracted beat sample position
     */
    @Override
    public void setBeatBuffer(int threadId, IntBuffer beatBuffer) {
        mBeatBuffers.set(threadId, beatBuffer);
    }

    @Override
    public void setBeatExtractionRunnableStatus(int threadId, int beats) {
        mBeatExtractionRunnablesStatus.set(threadId, beats);
    }

    @Override
    public long getSoundFileHandle() {
        return mSoundFileHandler;
    }

    @Override
    public long getNumSamples() {
        return mMusicFile.getSampleCount();
    }

    @Override
    public int getSampleRate() {
        return mMusicFile.getSampleRate();
    }

    /*******************************
     * SoundEncodeRunnable Interface
     *******************************/

    @Override
    public void setEncodeThread(Thread thread) {
        synchronized (sSoundManager) {
            mThreadThis = thread;
        }
    }

    /**
     * Handle SoundTask worker Thread state.
     *
     * @param state pass the current SoundTask state. The state will be processed by the SoundManager
     */
    @Override
    public void handleEncodeState(int state) {

        int soundTaskState;

        // Converts the encode state to the overall state
        switch(state) {

            case SoundEncodeRunnable.ENCODE_STATE_COMPLETED:
                soundTaskState = SoundManager.TASK_COMPLETE;
                break;

            case SoundEncodeRunnable.ENCODE_STATE_FAILED:
                soundTaskState = SoundManager.ENCODING_FAILED;
                break;

            default:
                soundTaskState = SoundManager.ENCODING_STARTED;
                break;
        }

        // Passes the state to the ThreadPool object.
        handleState(soundTaskState);
    }

    @Override
    public ChoreographyManager getChoreographyManager() {
        return DanceBotEditorManager.getInstance().getChoreoManager();
    }

    @Override
    public DanceBotMusicFile getMusicFile() {
        return mMusicFile;
    }

    public Toast getInfoToast() {
        return mInfoToast;
    }
}
