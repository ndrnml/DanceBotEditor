package ch.ethz.asl.dancebots.danceboteditor.handlers;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by andrin on 15.11.15.
 */
public class SoundManager {

    /*
     * Status indicators
     */
    static final int DECODING_FAILED = -1;
    static final int DECODING_STARTED = 1;
    static final int DECODING_COMPLETE = 2;
    static final int BEAT_EXTRACTION_STARTED = 3;
    static final int TASK_COMPLETE = 4;

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
    private SoundManager() {

        /*
         * Creates a work queue for the pool of Thread objects used for beat extraction,
         * using a linked list queue that blocks when the queue is empty.
         */
        mBeatExtractionWorkQueue = new LinkedBlockingQueue<Runnable>();

        /*
         * Creates a new pool of Thread objects for the beat extraction work queue
         */
        mBeatExtractionThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mBeatExtractionWorkQueue);

        /*
         * Creates a single Thread object for decoding
         */
        //mDecodeThread = new Thread();

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

    public void handleState(SoundTask soundTask, int state) {

        switch (state) {

            // The task finished decoding and sound beat extraction
            case TASK_COMPLETE:

                // TODO

                // Gets a Message object, stores the state in it, and sends it to the Handler
                Message completeMessage = mHandler.obtainMessage(state, soundTask);
                completeMessage.sendToTarget();
                break;

            // The task finished downloading the image
            case DECODING_COMPLETE:

                /*
                 * Extract beats of sound file using multi-threaded beat extraction
                 */

                // Initialize all runnables based on the length of the song
                soundTask.initializeBeatExtractionRunnablesk();

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

    static public SoundTask startDecoding(View toastView, int numThreads) {

        // If the queue was empty, create a new task instead.
        SoundTask decodeTask = new SoundTask(numThreads);

        // Initializes the task
        //decodeTask.initializeDecoderTask(sInstance, toastView);

        /*
         * Provides the download task with the cache buffer corresponding to the URL to be
         * downloaded.
         */
        //decodeTask.setByteBuffer(sInstance.mPhotoCache.get(decodeTask.getImageURL()));

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