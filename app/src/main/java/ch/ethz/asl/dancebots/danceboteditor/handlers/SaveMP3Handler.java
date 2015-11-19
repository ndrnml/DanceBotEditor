package ch.ethz.asl.dancebots.danceboteditor.handlers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorManager;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotError;
import ch.ethz.asl.dancebots.danceboteditor.utils.Decoder;
import ch.ethz.asl.dancebots.danceboteditor.utils.Encoder;

/**
 * Created by andrin on 19.10.15.
 */
public class SaveMP3Handler extends AsyncTask<DanceBotEditorManager, Void, Integer> {

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
    protected Integer doInBackground(DanceBotEditorManager... params) {

        long numSamples = DanceBotEditorManager.getInstance().getDanceBotMusicFile().getNumberOfSamples();
        short[] pcm_l = new short[(int)numSamples];
        short[] pcm_r = new short[(int)numSamples];

        int result = Decoder.transfer(pcm_l);
        result = Decoder.transfer(pcm_r);

        byte[] mp3buf = new byte[(int)(1.25 * numSamples + 7200)];

        Encoder encoder = new Encoder.Builder(44100, 2, 44100, 128).create();
        result = encoder.encode(pcm_l, pcm_r, (int)numSamples, mp3buf);
        result = encoder.flush(mp3buf);

        File mp3File = new File(Environment.getExternalStorageDirectory(), "FOO.mp3");

        Log.v(LOG_TAG, "Store mp3 file: " + mp3File.getAbsolutePath());

        if (mp3File.exists()) {
            mp3File.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream(mp3File.getPath());
            fos.write(mp3buf);
            fos.close();
        }
        catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }

        return DanceBotError.NO_ERROR;
    }

    /**
     * TODO comment
     * This is run on the main UI thread
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mDialog.setMessage("Converting saving and a bunch of other methods...");
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
