package ch.ethz.asl.dancebots.danceboteditor.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.model.BeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.LedType;
import ch.ethz.asl.dancebots.danceboteditor.model.MoveType;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorProjectFile;

/**
 * Created by andrin on 23.09.15.
 */
public class BeatElementMenuDialog extends DialogFragment {

    private static final String LOG_TAG = "BEAT_ELEM_MENU_DIALOG";

    public enum MENU_TYPE {MOTION, FREQUENCY, VELOCITY, LIGHTS, LENGTH}

    private View mBeatElementMenuView;
    private BeatElement mBeatElement;
    private DanceBotEditorProjectFile mDanceBotEditorProjectFile;

    private String[] mMenuListMotionTypes;
    private String[] mMenuListFrequencies;
    private String[] mMenuListVelocities;
    private String[] mMenuListLightSelection;
    private String[] mMenuChoreoLengths;

    /**
     * Ensure that this method is called directly after instantiation of the menu
     * @param elem
     */
    public void initializeMenu(BeatElement elem) {

        mBeatElement = elem;
        mDanceBotEditorProjectFile = DanceBotEditorProjectFile.getInstance();

        // Menu list initialization
        mMenuListVelocities = mDanceBotEditorProjectFile.getVelocities();
        mMenuChoreoLengths = mDanceBotEditorProjectFile.getChoreoLengths();
        mMenuListLightSelection = mDanceBotEditorProjectFile.getLights();

        // Further menu lists based on motion type
        if (mBeatElement.getMotionType().getClass() == LedType.class) { // LED_TYPE

            mMenuListMotionTypes = mDanceBotEditorProjectFile.getLedStates();
            mMenuListFrequencies = mDanceBotEditorProjectFile.getLedFrequencies();

        } else if (mBeatElement.getMotionType().getClass() == MoveType.class) { // MOVE_TYPE

            mMenuListMotionTypes = mDanceBotEditorProjectFile.getMoveStates();
            mMenuListFrequencies = mDanceBotEditorProjectFile.getMoveFrequencies();

        } else {
            Log.v(LOG_TAG, "Error doing menu initialization based on beat element type");
        }
    }

    /**
     * Callback method, which will be called on positive click within submenu
     */
    public void doPositiveClick() {

        // Update motion text view
        TextView motionTypeTextView = (TextView) mBeatElementMenuView.findViewById(R.id.txt_motion_type_default);
        motionTypeTextView.setText(mBeatElement.getMotionTypeString()); // TODO change default value

        // Update frequency text view
        TextView frequencyTextView = (TextView) mBeatElementMenuView.findViewById(R.id.txt_frequency_default);
        frequencyTextView.setText(mBeatElement.getFrequencyString()); // TODO change default value

        // Update velocity text view
        TextView velocityTextView = (TextView) mBeatElementMenuView.findViewById(R.id.txt_velocity_default);
        velocityTextView.setText(mBeatElement.getVelocityString()); // TODO change default value

        // Update length text view
        TextView choreoLengthTextView = (TextView) mBeatElementMenuView.findViewById(R.id.txt_length_default);
        choreoLengthTextView.setText(mBeatElement.getChoreoLengthString()); // TODO change default value

        Log.v(LOG_TAG, "doPositiveClick()");
    }

    /**
     *
     * @param id
     * @param menuList
     */
    public void buildSubMenu(int id, String defaultValue, final String[] menuList, final MENU_TYPE menuType, final String menuTag) {

        // Create submenu for specific TextView
        TextView textView = (TextView) mBeatElementMenuView.findViewById(id);
        textView.setText(defaultValue);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SubMenuDialog submenu = new SubMenuDialog();
                // Initialize the sub menu (AlertDialog) with the corresponding view and menu list
                submenu.initializeSubMenu(BeatElementMenuDialog.this, menuType, mBeatElement, menuList);
                submenu.show(getFragmentManager(), menuTag);
            }
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        mBeatElementMenuView = inflater.inflate(R.layout.beat_element_menu, null);

        // Add motion submenu
        buildSubMenu(R.id.txt_motion_type_default, mBeatElement.getMotionTypeString(), mMenuListMotionTypes, MENU_TYPE.MOTION, "motion_menu");

        // Add frequency submenu
        buildSubMenu(R.id.txt_frequency_default, mBeatElement.getFrequencyString(), mMenuListFrequencies, MENU_TYPE.FREQUENCY, "frequency_menu");

        // Add velocity submenu
        buildSubMenu(R.id.txt_velocity_default, mBeatElement.getVelocityString(), mMenuListVelocities, MENU_TYPE.VELOCITY, "velocity_menu");

        // Add choreography length submenu
        buildSubMenu(R.id.txt_length_default, mBeatElement.getChoreoLengthString(), mMenuChoreoLengths, MENU_TYPE.LENGTH, "choreo_length_menu");

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
