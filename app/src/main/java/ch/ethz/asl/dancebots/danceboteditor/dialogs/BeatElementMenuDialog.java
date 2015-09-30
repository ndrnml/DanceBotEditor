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

    public enum MENU_TYPE {MOTION, FREQUENCY, VELOCITY, LIGHTS, CHOREO_LENGTH}

    private View mBeatElementMenuView;
    private BeatElement mBeatElement;
    private DanceBotEditorProjectFile mDanceBotEditorProjectFile;

    private String[] mMenuListMotionTypes;
    private String[] mMenuListFrequencies;
    private String[] mMenuListVelocities;
    private String[] mMenuListLightSelection;
    private String[] mMenuChoreoLengths;

    private int mMenuMotionIdx;
    private int mMenuFrequencyIdx;
    private int mMenuVelocityIdx;
    private int mMenuChoreoLengthIdx;

    /**
     * Ensure that this method is called directly after instantiation of the menu
     * It loads all relevant information (menu lists) for setting up the menu
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
    public void doPositiveClick(MENU_TYPE type, int newVal) {

        switch (type) {

            case MOTION:
                // Update motion text view
                mMenuMotionIdx = newVal;
                TextView motionTypeTextView = (TextView) mBeatElementMenuView.findViewById(R.id.txt_motion_type_default);
                motionTypeTextView.setText(mMenuListMotionTypes[newVal]); // TODO change default value
                break;

            case FREQUENCY:
                // Update frequency text view
                mMenuFrequencyIdx = newVal;
                TextView frequencyTextView = (TextView) mBeatElementMenuView.findViewById(R.id.txt_frequency_default);
                frequencyTextView.setText(mMenuListFrequencies[newVal]); // TODO change default value
                break;

            case VELOCITY:
                // Update velocity text view
                mMenuVelocityIdx = newVal;
                TextView velocityTextView = (TextView) mBeatElementMenuView.findViewById(R.id.txt_velocity_default);
                velocityTextView.setText(mMenuListVelocities[newVal]); // TODO change default value
                break;

            case LIGHTS:
                break;

            case CHOREO_LENGTH:
                // Update length text view
                mMenuChoreoLengthIdx = newVal;
                TextView choreoLengthTextView = (TextView) mBeatElementMenuView.findViewById(R.id.txt_length_default);
                choreoLengthTextView.setText(mMenuChoreoLengths[newVal]); // TODO change default value
                break;

            default:
                break;
        }

        Log.v(LOG_TAG, "doPositiveClick()");
    }

    /**
     *
     * @param id
     * @param menuList
     */
    public void buildSubMenu(int id, int elemIdx, final String[] menuList, final MENU_TYPE menuType, final String menuTag) {

        // Create submenu for specific TextView
        TextView textView = (TextView) mBeatElementMenuView.findViewById(id);
        textView.setText(menuList[elemIdx]);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SubMenuDialog submenu = new SubMenuDialog();
                // Initialize the sub menu (AlertDialog) with the corresponding view and menu list
                submenu.initializeSubMenu(BeatElementMenuDialog.this, menuType, menuList);
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
        buildSubMenu(R.id.txt_motion_type_default, mBeatElement.getMotionTypeIdx(), mMenuListMotionTypes, MENU_TYPE.MOTION, "motion_menu");

        // Add frequency submenu
        buildSubMenu(R.id.txt_frequency_default, mBeatElement.getFrequencyIdx(), mMenuListFrequencies, MENU_TYPE.FREQUENCY, "frequency_menu");

        // Add velocity submenu
        buildSubMenu(R.id.txt_velocity_default, mBeatElement.getVelocityIdx(), mMenuListVelocities, MENU_TYPE.VELOCITY, "velocity_menu");

        // Add choreography length submenu
        buildSubMenu(R.id.txt_length_default, mBeatElement.getChoreoLengthIdx(), mMenuChoreoLengths, MENU_TYPE.CHOREO_LENGTH, "choreo_length_menu");

        // Compose AlertDialog with custom elements
        builder.setTitle("Element: " + mBeatElement.getBeatPositionAsString());

        // Build final view with positive and negative buttons
        builder.setView(mBeatElementMenuView)

                // Add action buttons
                .setPositiveButton(R.string.txt_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Save BeatElement properties
                        mBeatElement.setMotionTypeIdx(mMenuMotionIdx);
                        mBeatElement.setFrequencyIdx(mMenuFrequencyIdx);
                        mBeatElement.setVelocityIdx(mMenuVelocityIdx);
                        mBeatElement.setChoreoLengthIdx(mMenuChoreoLengthIdx);
                    }
                })
                .setNegativeButton(R.string.txt_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Discard BeatElement properties
                    }
                });

        return builder.create();
    }
}
