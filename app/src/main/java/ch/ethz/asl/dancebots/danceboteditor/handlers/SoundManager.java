package ch.ethz.asl.dancebots.danceboteditor.handlers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.activities.EditorActivity;
import ch.ethz.asl.dancebots.danceboteditor.model.ChoreographyManager;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorManager;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotMusicFile;
import ch.ethz.asl.dancebots.danceboteditor.view.HorizontalRecyclerViews;

/**
 * Created by andrin on 15.11.15.
 */

/**
 * This class is the sound task manager for SoundTask thread factory.
 */
public class SoundManager {

    private static final String LOG_TAG = SoundManager.class.getSimpleName();

    /*
     * Status indicators
     */
    static final int DECODING_FAILED = -1;
    static final int DECODING_STARTED = 1;
    static final int DECODING_COMPLETE = 2;
    static final int UPDATE_PROGRESS = 3;
    static final int BEAT_EXTRACTION_COMPLETE = 4;
    static final int TASK_COMPLETE = 5;
    static final int ENCODING_STARTED = 6;
    static final int ENCODING_FAILED = -2;

    // Sets the amount of time an idle thread will wait for a task before terminating
    private static final int KEEP_ALIVE_TIME = 1;

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    // Sets the initial threadpool size to 8
    private static final int CORE_POOL_SIZE = 8;

    // Sets the maximum threadpool size to 8
    private static final int MAXIMUM_POOL_SIZE = 8;

    /**
     * NOTE: This is the number of total available cores. On current versions of
     * Android, with devices that use plug-and-play cores, this will return less
     * than the total number of cores. The total number of cores is not
     * available in current Android implementations.
     */
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    // A queue of Runnables for the sound beat extraction pool
    private final BlockingQueue<Runnable> mBeatExtractionWorkQueue;

    // A managed pool of background beat extraction threads
    private final ThreadPoolExecutor mBeatExtractionThreadPool;

    // An object that manages Messages in a Thread
    private Handler mHandler;

    // A single instance of PhotoManager, used to implement the singleton pattern
    private static SoundManager sInstance = null;

    // A static block that sets class fields
    static {

        // The time unit for "keep alive" is in seconds
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

        // Creates a single static instance of PhotoManager
        sInstance = new SoundManager();
    }

    /**
     * Constructs the work queues and thread pools used to download and decode images.
     */
    @SuppressLint("HandlerLeak")
    private SoundManager() {

        /*
         * Creates a work queue for the pool of Thread objects used for beat extraction,
         * using a linked list queue that blocks when the queue is empty.
         */
        mBeatExtractionWorkQueue = new LinkedBlockingQueue<>();

        /*
         * Creates a new pool of Thread objects for the beat extraction work queue
         */
        mBeatExtractionThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mBeatExtractionWorkQueue);

        /*
         * Instantiates a new anonymous Handler object and defines its
         * handleMessage() method. The Handler *must* run on the UI thread, because it moves photo
         * Bitmaps from the PhotoTask object to the View object.
         * To force the Handler to run on the UI thread, it's defined as part of the PhotoManager
         * constructor. The constructor is invoked when the class is first referenced, and that
         * happens when the View invokes startDownload. Since the View runs on the UI Thread, so
         * does the constructor and the Handler.
         */
        mHandler = new Handler(Looper.getMainLooper()) {

            /*
             * handleMessage() defines the operations to perform when the
             * Handler receives a new Message to process.
             */
            @Override
            public void handleMessage(Message inputMessage) {

                // Get the SoundTask that invoked the message
                SoundTask soundTask = (SoundTask) inputMessage.obj;

                // Get the progress dialog
                ProgressDialog dialog = soundTask.getProgressDialog();

                // Get the info toast
                Toast infoToast = soundTask.getInfoToast();

                // Get the current state of the SoundTask
                int state = inputMessage.what;

                switch (state) {

                    case DECODING_STARTED:

                        // Update progress dialog
                        dialog.setMessage("PREPARING YOUR ABSOLUTE FAVORITE SONG ;)");
                        dialog.show();

                        Log.v(LOG_TAG, "handleMessage: " + "DECODING_STARTED");
                        break;

                    case DECODING_COMPLETE:

                        dialog.setIndeterminate(false);
                        Log.v(LOG_TAG, "handleMessage: " + "DECODING_COMPLETE");
                        break;

                    case DECODING_FAILED:

                        // Update progress dialog
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        // Inform user
                        infoToast.setText("Decoding failed :(");
                        infoToast.show();

                        Log.v(LOG_TAG, "handleMessage: DECODING_FAILED");
                        break;

                    case UPDATE_PROGRESS:

                        int progress = soundTask.getProgress();
                        dialog.setProgress(progress);

                        Log.v(LOG_TAG, "handleMessage: " + "UPDATE_PROGRESS: " + progress);
                        break;

                    case BEAT_EXTRACTION_COMPLETE:

                        // Start new EditorActivity
                        Context context = DanceBotEditorManager.getInstance().getContext();
                        Intent editorIntent = new Intent(context, EditorActivity.class);
                        context.startActivity(editorIntent);

                        Log.v(LOG_TAG, "handleMessage: BEAT_EXTRACTION_COMPLETE");
                        break;

                    case TASK_COMPLETE:

                        // Discard progress dialog if task completed successfully
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        Log.v(LOG_TAG, "handleMessage: TASK_COMPLETE");
                        break;

                    case ENCODING_STARTED:

                        // Not so nice way of removing disturbing percentage numbers
                        dialog.setProgressPercentFormat(null);

                        dialog.setMessage(DanceBotEditorManager.getInstance().getContext().getString(R.string.string_encoding_started_message));
                        dialog.show();

                        Log.v(LOG_TAG, "handleMessage: ENCODING_STARTED");
                        break;

                    case ENCODING_FAILED:
                        Log.v(LOG_TAG, "handleMessage: ENCODING_FAILED");
                        break;

                    default:
                        Log.v(LOG_TAG, "handleMessage: default");
                        break;
                }
            }
        };
    }

    /**
     * Returns the PhotoManager object
     * @return The global PhotoManager object
     */
    public static SoundManager getInstance() {

        return sInstance;
    }

    /**
     * Handle the SoundTask state and pass it on to the MainLooper (UI Thread)
     * @param soundTask current worker task
     * @param state worker task state
     */
    public void handleState(SoundTask soundTask, int state) {

        switch (state) {

            // The task finished decoding and sound beat extraction
            case TASK_COMPLETE:

                // Gets a Message object, stores the state in it, and sends it to the Handler
                Message completeMessage = mHandler.obtainMessage(state, soundTask);
                completeMessage.sendToTarget();
                break;

            // The task finished downloading the image
            case DECODING_COMPLETE:

                /*
                 * Extract beats of sound file using multi-threaded beat extraction
                 */

                // Fetch runnable list and hand it to the thread pool
                ArrayList<Runnable> beatExtractionRunnables = soundTask.getSoundBeatExtractionRunnables();

                /*
                 * Execute all beat extraction runnables in parallel
                 */
                for (int i = 0; i < beatExtractionRunnables.size(); ++i) {
                    mBeatExtractionThreadPool.execute(beatExtractionRunnables.get(i));
                }

                // In all other cases, pass along the message without any other action.

            default:
                mHandler.obtainMessage(state, soundTask).sendToTarget();
                break;
        }

    }

    /**
     * Decoding worker initialization. Successful decoding automatically triggers the beat
     * extraction process.
     *
     * @param musicFile the music file, which will be decoded
     * @param numThreads number of threads that will be used for the decoding task
     * @return the executing worker Thread
     */
    static public SoundTask startDecoding(Context context, DanceBotMusicFile musicFile, int numThreads) {

        // Ensure that at least one Thread is invoked
        if (numThreads <= 0) {
            numThreads = 1;
        }

        // Create a new sound task
        SoundTask decodeTask = new SoundTask(numThreads);

        // Initializes the decoding task
        decodeTask.initializeDecoderTask(context, musicFile);

        /*
         * "Executes" the tasks' download Runnable in order to download the image. If no
         * Threads are available in the thread pool, the Runnable waits in the queue.
         */
        Thread decodeThread = new Thread(decodeTask.getDecodeRunnable());
        decodeThread.start();

        // Sets the display to show that the image is queued for downloading and decoding.
        //imageView.setStatusResource(R.drawable.imagequeued);

        // Returns a task object, either newly-created or one from the task pool
        return decodeTask;
    }

    /**
     * Start the encoding and saving process of a music file in combination with dance sequence data
     *
     * @param context Context for all the dialogs
     * @param musicFile music file which will be encoded in one channel
     * @param choreoManager choreography manager provides the dance sequence data for the other channel
     * @return worker Thread task
     */
    static public SoundTask startEncoding(Context context, DanceBotMusicFile musicFile, ChoreographyManager choreoManager) {

        // Ensure that at least one Thread is invoked
        int numThreads = 1;

        // Create new sound task
        SoundTask encodingTask = new SoundTask(numThreads);

        // Initialize the encoding task
        encodingTask.initializeEncoderTask(context, musicFile, choreoManager);

        /*
         * Executes the tasks' encode Runnable in order to encode raw music data and choreography
         * data.
         */
        Thread encodeThread = new Thread(encodingTask.getEncodeRunnable());
        encodeThread.start();

        // Returns the newly created task object
        return encodingTask;
    }

    /**
     * Cancels all Threads in the ThreadPool
     */
    public static void cancelAll() {

        /*
         * Creates an array of tasks that's the same size as the task work queue
         */
        Thread[] taskArray = new Thread[sInstance.mBeatExtractionWorkQueue.size()];

        // Populates the array with the task objects in the queue
        sInstance.mBeatExtractionWorkQueue.toArray(taskArray);

        // Stores the array length in order to iterate over the array
        int taskArraylen = taskArray.length;

        /*
         * Locks on the singleton to ensure that other processes aren't mutating Threads, then
         * iterates over the array of tasks and interrupts the task's current Thread.
         */
        synchronized (sInstance) {

            // Iterates over the array of tasks
            for (int taskArrayIndex = 0; taskArrayIndex < taskArraylen; taskArrayIndex++) {

                // Gets the task's current thread
                Thread thread = taskArray[taskArrayIndex];

                // if the Thread exists, post an interrupt to it
                if (null != thread) {
                    thread.interrupt();
                }
            }
        }
    }

}