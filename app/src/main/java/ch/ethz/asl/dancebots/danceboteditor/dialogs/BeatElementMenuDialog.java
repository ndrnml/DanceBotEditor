package ch.ethz.asl.dancebots.danceboteditor.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.model.BeatElement;

/**
 * Created by andrin on 23.09.15.
 */
public class BeatElementMenuDialog extends DialogFragment {

    private static final String LOG_TAG = "BEAT_ELEM_MENU_DIALOG";
    private View mBeatElementMenuView;
    private BeatElement mBeatElement;

    /**
     * Ensure that this method is called directly after instantiation of the menu
     * @param elem
     */
    public void initializeMenu(BeatElement elem) {

        mBeatElement = elem;
    }

    public void updateMenuView(View v, int val) {


    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        mBeatElementMenuView = inflater.inflate(R.layout.beat_element_menu, null);

        // Set event listeners on menu items
        // Choose motion type
        final TextView tv = (TextView) mBeatElementMenuView.findViewById(R.id.txt_motion_type_default);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create submenu where clicked item can be selected
                SubMenuDialog submenu = new SubMenuDialog();
                submenu.initializeSubMenu(mBeatElement, new ArrayList());
                submenu.show(getFragmentManager(), "blubb2");
            }
        });


        // Compose AlertDialog with custom elements
        builder.setTitle("Element: " + mBeatElement.getBeatPositionAsString());

        builder.setView(mBeatElementMenuView)

                // Add action buttons
                .setPositiveButton(R.string.txt_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // save properties ...
                        // TODO
                    }
                })
                .setNegativeButton(R.string.txt_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO
                    }
                });

        return builder.create();
    }
}
