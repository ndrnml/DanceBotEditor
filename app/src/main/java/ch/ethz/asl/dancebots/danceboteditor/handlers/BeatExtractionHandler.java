package ch.ethz.asl.dancebots.danceboteditor.handlers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import ch.ethz.asl.dancebots.danceboteditor.adapters.BeatElementAdapter;
import ch.ethz.asl.dancebots.danceboteditor.utils.BeatExtractor;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorProjectFile;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotError;
import ch.ethz.asl.dancebots.danceboteditor.utils.Decoder;
import ch.ethz.asl.dancebots.danceboteditor.view.HorizontalRecyclerViews;

/**
 * Created by andrin on 09.07.15.
 */
public class BeatExtractionHandler extends AsyncTask<Integer, Void, Integer> {

    /**TODO
     * TODO AsyncTasks don't follow Activity instances' life cycle. If you start an AsyncTask inside an Activity and you rotate the device, the Activity will be destroyed and a new instance will be created. But the AsyncTask will not die. It will go on living until it completes.
     * TODO And when it completes, the AsyncTask won't update the UI of the new Activity. Indeed it updates the former instance of the activity that is not displayed anymore. This can lead to an Exception of the type java.lang.IllegalArgumentException: View not attached to window manager if you use, for instance, findViewById to retrieve a view inside the Activity.
     */
    private static final String LOG_TAG = "BEAT_EXTRACTION_HANDLER";

    private Activity mActivity;
    private DanceBotEditorProjectFile mProjectFile;
    private HorizontalRecyclerViews mBeatElementViews;
    private ProgressDialog mDialog;

    /**
     * TODO COmment
     * @param activity
     */
    public BeatExtractionHandler(Activity activity, HorizontalRecyclerViews beatViews) {

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

        // Create decoder object
        Decoder mp3Decoder = new Decoder();

        mp3Decoder.openFile(mProjectFile.getDanceBotMusicFile().getSongPath());

        int result = mp3Decoder.decode();

        if (result <= 0) {
            // Error while decoding
            return DanceBotError.DECODING_ERR;
        }

        long soundFileHandle = mp3Decoder.getHandle();

        // Extract beats
        result = BeatExtractor.extract(soundFileHandle, mProjectFile.getBeatGrid().getBeatBuffer(), mProjectFile.getBeatGrid().getBeatBuffer().capacity());

        // Extract sample rate from decoded file
        mProjectFile.getDanceBotMusicFile().setSampleRate(mp3Decoder.getSampleRate());
        Log.v(LOG_TAG, "sample rate: " + mp3Decoder.getSampleRate());

        // Extract total number of samples from decoded file
        mProjectFile.getDanceBotMusicFile().setTotalNumberOfSamples(mp3Decoder.getNumberOfSamples());
        Log.v(LOG_TAG, "total number of samples: " + mp3Decoder.getNumberOfSamples());

        // Extract total number of beats detected from selected file
        mProjectFile.getBeatGrid().setNumOfBeats(mp3Decoder.getNumerOfBeatsDetected());
        Log.v(LOG_TAG, "total number of beats detected: " + mp3Decoder.getNumerOfBeatsDetected());

        // Save number of beats detected to dance bot music file
        mProjectFile.getDanceBotMusicFile().setNumberOfBeatsDected(mp3Decoder.getNumerOfBeatsDetected());
        Log.v(LOG_TAG, "store in music file: total number of beats detected: " + mp3Decoder.getNumerOfBeatsDetected());

        if (result <= 0) {

            Log.v(LOG_TAG, "Error while extracting beats");

            // Error while extracting beats
            return DanceBotError.BEAT_DETECTION_ERR;

        } else {

            // Prepare music player
            mProjectFile.getMediaPlayer().preparePlayback();

            Log.v(LOG_TAG, "Successfully decoded and beats extracted");

            // Successfully decoded and beats extracted
            mProjectFile.beatExtractionDone = true;

            // Populate beats
            mProjectFile.initChoreography();

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

}
