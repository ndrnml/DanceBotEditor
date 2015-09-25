package ch.ethz.asl.dancebots.danceboteditor.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.activities.EditorActivity;

/**
 * Created by andrin on 28.08.15.
 */
public final class DanceBotEditorDialogs {

    // TODO
    // TODO Clear code and move to "dialogs" package
    // TODO

    /**
     * Exit app only if user select yes
     */
    public static void showAlertDialogExit(final Activity activity, final EditorActivity.State state) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

        alertDialog.setPositiveButton(R.string.txt_yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // TODO
                //state = EditorActivity.State.NEW;
                activity.finish();
            }
        });

        alertDialog.setNegativeButton(R.string.txt_no, null);

        alertDialog.setMessage(R.string.alert_ask_exit_txt);
        alertDialog.setTitle(R.string.app_name);
        alertDialog.show();
    }
}
