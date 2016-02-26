package ch.ethz.asl.dancebots.danceboteditor.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ch.ethz.asl.dancebots.danceboteditor.R;

/**
 * Created by andrin on 26.02.16.
 */
public class StickyOkDialog extends DialogFragment {

    private View mOkDialogView;
    private String mMessageText;
    private String mTitleText;

    public StickyOkDialog setMessage(String message) {
        mMessageText = message;
        return this;
    }

    public StickyOkDialog setTitle(String title) {
        mTitleText = title;
        return this;
    }

    /**
     * This implementation would be much easier!
     AlertDialog.Builder builder = new AlertDialog.Builder(this);
     builder.setMessage("Look at this dialog!")
     .setCancelable(false)
     .setPositiveButton("OK", new DialogInterface.OnClickListener() {
     public void onClick(DialogInterface dialog, int id) {
     //do things
     }
     });
     AlertDialog alert = builder.create();
     alert.show();
     */

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        mOkDialogView = inflater.inflate(R.layout.dialog_ok, null);

        // Set a title to the dialog
        final TextView titleView = (TextView) mOkDialogView.findViewById(R.id.tv_ok_dialog_title);
        titleView.setText(mTitleText);

        // Set a text to the dialog
        final TextView messageView = (TextView) mOkDialogView.findViewById(R.id.tv_ok_dialog_message);
        if (mMessageText == null) {
            messageView.setVisibility(View.GONE);
        } else {
            messageView.setText(mMessageText);
        }

        // Get beat element menu OK button
        Button btnOk = (Button) mOkDialogView.findViewById(R.id.btn_ok_dialog);

        // Add OK button on click listener
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Dismiss dialog after OK button is pressed and data is passed to choreography manager
                dismiss();
            }
        });

        // Ensure only OK button closes the dialog
        setCancelable(false);

        // Build final view with positive button
        builder.setView(mOkDialogView);

        return builder.create();
    }
}
