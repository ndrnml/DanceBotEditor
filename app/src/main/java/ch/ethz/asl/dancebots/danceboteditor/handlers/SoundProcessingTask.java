package ch.ethz.asl.dancebots.danceboteditor.handlers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import java.nio.IntBuffer;
import java.util.ArrayList;

import ch.ethz.asl.dancebots.danceboteditor.adapters.BeatElementAdapter;
import ch.ethz.asl.dancebots.danceboteditor.utils.BeatExtractor;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorProjectFile;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotError;
import ch.ethz.asl.dancebots.danceboteditor.utils.Decoder;
import ch.ethz.asl.dancebots.danceboteditor.view.HorizontalRecyclerViews;

/**
 * Created by andrin on 09.07.15.
 */
public class SoundProcessingTask extends AsyncTask<Integer, Void, Integer> implements
        BeatExtractionRunnable.TaskSoundProcessing {

    /**TODO
     * TODO AsyncTasks don't follow Activity instances' life cycle. If you start an AsyncTask inside an Activity and you rotate the device, the Activity will be destroyed and a new instance will be created. But the AsyncTask will not die. It will go on living until it completes.
     * TODO And when it completes, the AsyncTask won't update the UI of the new Activity. Indeed it updates the former instance of the activity that is not displayed anymore. This can lead to an Exception of the type java.lang.IllegalArgumentException: View not attached to window manager if you use, for instance, findViewById to retrieve a view inside the Activity.
     */
    private static final String LOG_TAG = "BEAT_EXTRACTION_HANDLER";

    private Activity mActivity;
    private DanceBotEditorProjectFile mProjectFile;
    private HorizontalRecyclerViews mBeatElementViews;
    private ProgressDialog mDialog;

    private ArrayList<Runnable> mBeatExtractionRunnables;
    private ArrayList<Boolean> mBeatExtractionRunnablesStatus;
    private ArrayList<IntBuffer> mBeatBuffers;
    private long mSoundFileHandle;

    /**
     * TODO COmment
     * @param activity
     */
    public SoundProcessingTask(Activity activity, HorizontalRecyclerViews beatViews) {

        mActivity = activity;
        mBeatElementViews = beatViews;

        mProjectFile = DanceBotEditorProjectFile.getInstance();

        mDialog = new ProgressDialog(activity);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * TODO comment
     * @param params
     * @return
     */
    @Override
    protected Integer doInBackground(Integer... params) {

        long t1 = SystemClock.currentThreadTimeMillis();

        mSoundFileHandle = runDecode();

        if (mSoundFileHandle == DanceBotError.DECODING_ERR) {
            return DanceBotError.DECODING_ERR;
        }

        // Based on length of decoded file calculate number of threads
        int numThreads = 1;

        int result = runBeatExtraction(numThreads);

        long t2 = SystemClock.currentThreadTimeMillis();
        Log.v(LOG_TAG, "Elapsed time for decoding and extracting: " + Long.toString(t2 - t1));

        if (result == DanceBotError.BEAT_EXTRACTION_ERR) {
            return DanceBotError.BEAT_EXTRACTION_ERR;
        }

        return result;
    }

    private int runBeatExtraction(int numThreads) {

        while (!allBeatExtractionRunnablesDone()) {

            try {
                Log.v(LOG_TAG, "Beat extraction not yet done............");
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.v(LOG_TAG, "Now we can continue....................");

        return 0;
    }

    private long runDecode() {

        // Create decoder object
        Decoder mp3Decoder = new Decoder();

        Log.v(LOG_TAG, "opening mp3 file...");
        mp3Decoder.openFile(mProjectFile.getDanceBotMusicFile().getSongPath());

        Log.v(LOG_TAG, "start decoding...");
        int result = mp3Decoder.decode();

        if (result <= 0) {
            // Error while decoding
            return DanceBotError.DECODING_ERR;
        }

        // Extract sample rate from decoded file
        mProjectFile.getDanceBotMusicFile().setSampleRate(mp3Decoder.getSampleRate());
        Log.v(LOG_TAG, "sample rate: " + mp3Decoder.getSampleRate());

        // Extract total number of samples from decoded file
        mProjectFile.getDanceBotMusicFile().setTotalNumberOfSamples(mp3Decoder.getNumberOfSamples());
        Log.v(LOG_TAG, "total number of samples: " + mp3Decoder.getNumberOfSamples());

        return mp3Decoder.getHandle();
    }

    private boolean allBeatExtractionRunnablesDone() {
        for (boolean status : mBeatExtractionRunnablesStatus) {
            if (!status) {
                return false;
            }
        }
        return true;
    }

    /**
     * TODO comment
     * This is run on the main UI thread
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mDialog.setMessage("doing all the important C++ stuff... please wait, it only takes a few seconds/minutes/hours");
        mDialog.show();

        Log.v(LOG_TAG, "PreExecute decoding extracting");

    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);

        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }

        if (result == DanceBotError.NO_ERROR)
        {
            // TODO: successfully executed async task
            // TODO set adapters for editor activity
            // Create the beat adapters
            BeatElementAdapter motorAdapter = new BeatElementAdapter<>(DanceBotEditorProjectFile.getInstance().getChoreoManager().mMotorChoreography.mBeatElements);
            BeatElementAdapter ledAdapter = new BeatElementAdapter<>(DanceBotEditorProjectFile.getInstance().getChoreoManager().mLedChoregraphy.mBeatElements);

            // Attach apapters
            mBeatElementViews.setAdapters(motorAdapter, ledAdapter);
        }

        Log.v(LOG_TAG, "PostExecute decoding extracting");
    }

    @Override
    public void setBeatBuffer(int threadId, IntBuffer beatBuffer) {
        mBeatBuffers.set(threadId, beatBuffer);
    }

    @Override
    public void setBeatExtractionRunnableStatus(int threadId, boolean status) {
        mBeatExtractionRunnablesStatus.set(threadId, status);
    }

    @Override
    public long getSoundFileHandle() {
        return mSoundFileHandle;
    }
}
