package ch.ethz.asl.dancebots.danceboteditor.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.adapters.BeatElementAdapter;
import ch.ethz.asl.dancebots.danceboteditor.model.BeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.LedBeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.LedType;
import ch.ethz.asl.dancebots.danceboteditor.model.MotorBeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.MotorType;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorProjectFile;

/**
 * Created by andrin on 23.09.15.
 */
public class BeatElementMenuDialog<T extends BeatElement> extends DialogFragment {

    private static final String LOG_TAG = "BEAT_ELEM_MENU_DIALOG";

    public enum MENU_TYPE {MOTION, MOTOR_FREQUENCY, VELOCITY_LEFT, LIGHTS, CHOREO_LENGTH}

    private View mBeatElementMenuView;
    private T mBeatElement;
    private DanceBotEditorProjectFile mDanceBotEditorProjectFile;
    private BeatElementAdapter mBeatElementAdapter;

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
    public void initializeMenu(BeatElementAdapter adapter, T elem) {

        mBeatElement = elem;
        mDanceBotEditorProjectFile = DanceBotEditorProjectFile.getInstance();
        mBeatElementAdapter = adapter;

        // Menu list initialization
        mMenuListVelocities = mDanceBotEditorProjectFile.getVelocitiesStrings();
        mMenuChoreoLengths = mDanceBotEditorProjectFile.getChoreoLengthsStrings();
        mMenuListLightSelection = mDanceBotEditorProjectFile.getLedLightsStrings();

        // Further menu lists based on motion type
        if (mBeatElement.getMotionType().getClass() == LedType.class) { // LED_TYPE

            mMenuListMotionTypes = mDanceBotEditorProjectFile.getLedStatesStrings();
            mMenuListFrequencies = mDanceBotEditorProjectFile.getLedFrequenciesStrings();

        } else if (mBeatElement.getMotionType().getClass() == MotorType.class) { // MOVE_TYPE

            mMenuListMotionTypes = mDanceBotEditorProjectFile.getMotorStatesStrings();
            mMenuListFrequencies = mDanceBotEditorProjectFile.getMotorFrequenciesStrings();

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

            case MOTOR_FREQUENCY:
                // Update frequency text view
                mMenuFrequencyIdx = newVal;
                TextView frequencyTextView = (TextView) mBeatElementMenuView.findViewById(R.id.txt_frequency_default);
                frequencyTextView.setText(mMenuListFrequencies[newVal]); // TODO change default value
                break;

            case VELOCITY_LEFT:
                // Update velocity text view
                mMenuVelocityIdx = newVal;
                TextView velocityTextView = (TextView) mBeatElementMenuView.findViewById(R.id.txt_velocity_left_default);
                velocityTextView.setText(mMenuListVelocities[newVal]); // TODO change default value
                break;

            case LIGHTS:
                // TODO
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
    private void buildSubMenu(int id, int elemIdx, final String[] menuList, final MENU_TYPE menuType, final String menuTag) {

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


    private void createLedElementMenu(LedBeatElement elem) {

        // Add motion submenu
        buildSubMenu(R.id.txt_motion_type_default, elem.getMotionTypeIdx(), mMenuListMotionTypes, MENU_TYPE.MOTION, "motion_menu");

        // Add frequency submenu
        buildSubMenu(R.id.txt_frequency_default, elem.getFrequencyIdx(), mMenuListFrequencies, MENU_TYPE.MOTOR_FREQUENCY, "frequency_menu");

        // Add velocity submenu TODO
        //buildSubMenu(R.id.txt_lights, elem.getVelocityIdx(), mMenuListVelocities, MENU_TYPE.VELOCITY_LEFT, "velocity_menu");

        // Add choreography length submenu
        buildSubMenu(R.id.txt_length_default, elem.getChoreoLengthIdx(), mMenuChoreoLengths, MENU_TYPE.CHOREO_LENGTH, "choreo_length_menu");

        // Hide not relevant menu items
        mBeatElementMenuView.findViewById(R.id.menu_item_velocity_left).setVisibility(View.GONE);
        mBeatElementMenuView.findViewById(R.id.menu_item_velocity_right).setVisibility(View.GONE);
    }

    private void createMotorElementMenu(MotorBeatElement elem) {

        // Add motion submenu
        buildSubMenu(R.id.txt_motion_type_default, elem.getMotionTypeIdx(), mMenuListMotionTypes, MENU_TYPE.MOTION, "motion_menu");

        // Add frequency submenu
        buildSubMenu(R.id.txt_frequency_default, elem.getFrequencyIdx(), mMenuListFrequencies, MENU_TYPE.MOTOR_FREQUENCY, "frequency_menu");

        // Add velocity submenu
        buildSubMenu(R.id.txt_velocity_left_default, elem.getVelocityIdx(), mMenuListVelocities, MENU_TYPE.VELOCITY_LEFT, "velocity_menu");

        // Add choreography length submenu
        buildSubMenu(R.id.txt_length_default, elem.getChoreoLengthIdx(), mMenuChoreoLengths, MENU_TYPE.CHOREO_LENGTH, "choreo_length_menu");

        // Hide irrelevant menu items
        mBeatElementMenuView.findViewById(R.id.menu_item_lights).setVisibility(View.GONE);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        mBeatElementMenuView = inflater.inflate(R.layout.menu_beat_element, null);

        // Create submenu based on the element type
        if (mBeatElement.getMotionType().getClass() == LedType.class) { // LED_TYPE

            createLedElementMenu((LedBeatElement) mBeatElement);

        } else if (mBeatElement.getMotionType().getClass() == MotorType.class) { // MOVE_TYPE

            createMotorElementMenu((MotorBeatElement) mBeatElement);

        }

        // Compose AlertDialog with custom elements
        builder.setTitle("Element: " + mBeatElement.getBeatPositionAsString());

        // Build final view with positive and negative buttons
        builder.setView(mBeatElementMenuView)

                // Add action buttons
                .setPositiveButton(R.string.txt_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        // Save BeatElement properties
                        int choreoStartIdx = mBeatElement.getBeatPosition();
                        int choreoLength = mDanceBotEditorProjectFile.getChoreoLengthAtIdx(mMenuChoreoLengthIdx);

                        mBeatElement.setProperties(choreoStartIdx, choreoLength, Color.RED, mMenuMotionIdx, mMenuFrequencyIdx, mMenuChoreoLengthIdx);

                        // Save Led/Motor element properties
                        if (mBeatElement.getMotionType().getClass() == LedType.class) { // LED_TYPE

                            // TODO
                            ((LedBeatElement) mBeatElement).setLedLightSwitch(new int[]{1,2});

                        } else if (mBeatElement.getMotionType().getClass() == MotorType.class) { // MOVE_TYPE

                            // TODO
                            ((MotorBeatElement) mBeatElement).setVelocityLeftIdx(1);
                        }

                        // Notify all corresponding beat elements that belong to this choreography
                        mDanceBotEditorProjectFile.getChoreoManager().updateChoreography(mBeatElement);

                        // Notify the list adapter to update the modified list elements
                        mBeatElementAdapter.notifyDataSetChanged();
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
