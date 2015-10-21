package ch.ethz.asl.dancebots.danceboteditor.handlers;

import android.app.Activity;
import android.app.Notification;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorProjectFile;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotError;

/**
 * Created by andrin on 19.10.15.
 */
public class SaveMP3Handler extends AsyncTask<DanceBotEditorProjectFile, Void, Integer> {

    /**TODO
     * TODO AsyncTasks don't follow Activity instances' life cycle. If you start an AsyncTask inside an Activity and you rotate the device, the Activity will be destroyed and a new instance will be created. But the AsyncTask will not die. It will go on living until it completes.
     * TODO And when it completes, the AsyncTask won't update the UI of the new Activity. Indeed it updates the former instance of the activity that is not displayed anymore. This can lead to an Exception of the type java.lang.IllegalArgumentException: View not attached to window manager if you use, for instance, findViewById to retrieve a view inside the Activity.
     */
    private static final String LOG_TAG = "SOUND_FILE_HANDLER";

    private ProgressDialog mDialog;

    public SaveMP3Handler(Activity activity) {

        mDialog = new ProgressDialog(activity);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected Integer doInBackground(DanceBotEditorProjectFile... params) {
        return null;
    }

    /**
     * TODO comment
     * This is run on the main UI thread
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mDialog.setMessage("saving...");
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
        }

        Log.v(LOG_TAG, "PostExecute decoding extracting");
    }
}
