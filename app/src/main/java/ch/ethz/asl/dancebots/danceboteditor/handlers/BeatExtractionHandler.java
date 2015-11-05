package ch.ethz.asl.dancebots.danceboteditor.handlers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.nio.IntBuffer;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.activities.EditorActivity;
import ch.ethz.asl.dancebots.danceboteditor.adapters.BeatElementAdapter;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorProjectFile;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotError;
import ch.ethz.asl.dancebots.danceboteditor.utils.Decoder;
import ch.ethz.asl.dancebots.danceboteditor.utils.NativeSoundHandler;

/**
 * Created by andrin on 09.07.15.
 */
public class BeatExtractionHandler extends AsyncTask<DanceBotEditorProjectFile, Void, Integer> {

    /**TODO
     * TODO AsyncTasks don't follow Activity instances' life cycle. If you start an AsyncTask inside an Activity and you rotate the device, the Activity will be destroyed and a new instance will be created. But the AsyncTask will not die. It will go on living until it completes.
     * TODO And when it completes, the AsyncTask won't update the UI of the new Activity. Indeed it updates the former instance of the activity that is not displayed anymore. This can lead to an Exception of the type java.lang.IllegalArgumentException: View not attached to window manager if you use, for instance, findViewById to retrieve a view inside the Activity.
     */
    private static final String LOG_TAG = "SOUND_FILE_HANDLER";

    private Activity mActivity;
    private RecyclerView mMotorView;
    private RecyclerView mLedView;

    private ProgressDialog mDialog;

    /**
     * TODO COmment
     * @param activity
     */
    public BeatExtractionHandler(Activity activity, RecyclerView motorView, RecyclerView ledView) {

        mActivity = activity;

        mMotorView = motorView;
        mLedView = ledView;

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
    protected Integer doInBackground(DanceBotEditorProjectFile... params) {

        // First open sound file and decode it
        //int err = NativeSoundHandler.getInstance().init(params[0].getDanceBotMusicFile().getSongPath());
        //Log.v(LOG_TAG, "error code NativeSoundHandlerInit: " + err);

        Decoder mp3Decoder = new Decoder();

        mp3Decoder.openFile(params[0].getDanceBotMusicFile().getSongPath());

        int result = mp3Decoder.decode();

        if (result < 0) {

            // Error while decoding
            return DanceBotError.DECODING_ERR;
        }

        /*
        // Extract sample rate from decoded file
        params[0].getDanceBotMusicFile().setSampleRate(NativeGetSampleRate());
        Log.v(LOG_TAG, "sample rate: " + NativeGetSampleRate());

        // Extract total number of samples from decoded file
        params[0].getDanceBotMusicFile().setTotalNumberOfSamples(NativeGetNumberOfSamples());
        Log.v(LOG_TAG, "total number of samples: " + NativeGetNumberOfSamples());

        // Then extract beats and return to previous activity
        err = NativeExtractBeats(params[0].getBeatGrid().getBeatBuffer(), params[0].getBeatGrid().getBeatBuffer().capacity());
        Log.v(LOG_TAG, "error code NativeExtractBeats: " + err);

        // Extract total number of beats detected from selected file
        params[0].getBeatGrid().setNumOfBeats(NativeGetNumBeatsDetected());
        Log.v(LOG_TAG, "total number of beats detected: " + NativeGetNumBeatsDetected());

        // Save number of beats detected to dance bot music file
        params[0].getDanceBotMusicFile().setNumberOfBeatsDected(NativeGetNumBeatsDetected());
        Log.v(LOG_TAG, "store in music file: total number of beats detected: " + NativeGetNumBeatsDetected());

        */

        if (result < 0) {

            Log.v(LOG_TAG, "Error while extracting beats");

            // Error while extracting beats
            return DanceBotError.BEAT_DETECTION_ERR;

        } else {

            Log.v(LOG_TAG, "Successfully decoded and beats extracted");

            // Successfully decoded and beats extracted
            params[0].beatExtractionDone = true;

            // Populate beats
            params[0].initChoreography();

            return DanceBotError.NO_ERROR;
        }
    }


    /**
     * TODO comment
     * This is run on the main UI thread
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mDialog.setMessage("doing all the important C++ stuff... please wait, it only takes a few seconds");
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
            BeatElementAdapter motorAdapter = new BeatElementAdapter(DanceBotEditorProjectFile.getInstance().getChoreoManager().mMotorChoreography.mBeatElements);
            BeatElementAdapter ledAdapter = new BeatElementAdapter(DanceBotEditorProjectFile.getInstance().getChoreoManager().mLedChoregraphy.mBeatElements);

            // Attach apapters
            mMotorView.setAdapter(motorAdapter);
            mLedView.setAdapter(ledAdapter);
        }

        Log.v(LOG_TAG, "PostExecute decoding extracting");
    }

}
