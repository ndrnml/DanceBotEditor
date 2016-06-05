package ch.ethz.asl.dancebots.danceboteditor.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by andrin on 26.02.16.
 */
public class MusicIntentReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "MainActivity";

    private static boolean mHeadSetPlugged = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {

            int state = intent.getIntExtra("state", -1);

            switch (state) {
                case 0:
                    mHeadSetPlugged = false;
                    Log.d(LOG_TAG, "Headset is unplugged");
                    break;
                case 1:
                    mHeadSetPlugged = true;
                    Log.d(LOG_TAG, "Headset is plugged");
                    break;
                default:
                    mHeadSetPlugged = false;
                    Log.d(LOG_TAG, "I have no idea what the headset state is");
            }
        }
    }

    public static boolean isHeadSetPlugged() {
        return mHeadSetPlugged;
    }

}
