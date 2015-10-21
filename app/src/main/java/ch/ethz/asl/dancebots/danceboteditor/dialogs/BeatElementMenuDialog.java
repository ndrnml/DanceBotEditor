package ch.ethz.asl.dancebots.danceboteditor.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.adapters.BeatElementAdapter;
import ch.ethz.asl.dancebots.danceboteditor.model.BeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.LedBeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.MotorBeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.MotorType;
import ch.ethz.asl.dancebots.danceboteditor.ui.FloatSelectionMenu;
import ch.ethz.asl.dancebots.danceboteditor.ui.LedTypeSelectionMenu;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorProjectFile;

/**
 * Created by andrin on 23.09.15.
 */
public class BeatElementMenuDialog extends DialogFragment {

    private static final String LOG_TAG = "BEAT_ELEM_MENU_DIALOG";

    public enum MENU_TYPE {MOTION, FREQUENCY, VELOCITY_LEFT, VELOCITY_RIGHT, CHOREO_LENGTH}

    // General fields
    private View mBeatElementMenuView;
    private BeatElement mBeatElement;
    private DanceBotEditorProjectFile mDanceBotEditorProjectFile;
    private BeatElementAdapter mBeatElementAdapter;

    // Menus
    private LedTypeSelectionMenu mLedTypeSelectionMenu;
    private FloatSelectionMenu mLedFrequencySelectionMenu;

    private String[] mMenuListMotionTypes;
    private String[] mMenuListFrequencies;
    private String[] mMenuListVelocities;
    private String[] mMenuChoreoLengths;

    private ArrayList<CheckBox> mCheckBoxes;
    private boolean[] mLedLightSwitches;
    private int mNumCheckBoxes;
    
    private int mMenuMotionTypeIdx;
    private int mMenuFrequencyIdx;
    private int mMenuVelocityLeftIdx;
    private int mMenuVelocityRightIdx;
    private int mMenuChoreoLengthIdx;

    /**
     * Ensure that this method is called directly after instantiation of the menu
     * It loads all relevant information (menu lists) for setting up the menu
     * @param elem
     */
    public void initializeMenu(BeatElementAdapter adapter, BeatElement elem) {

        mBeatElementAdapter = adapter;
        mBeatElement = elem;
        mDanceBotEditorProjectFile = DanceBotEditorProjectFile.getInstance();

        // Menu list initialization
        mMenuListVelocities = mDanceBotEditorProjectFile.getVelocitiesStrings();
        mMenuChoreoLengths = mDanceBotEditorProjectFile.getChoreoLengthsStrings();

        // Further menu lists based on motion type
        if (mBeatElement.getClass() == LedBeatElement.class) { // LED_TYPE

            // Load led type menu
            mLedTypeSelectionMenu = mDanceBotEditorProjectFile.getLedTypeMenu();

            mMenuListMotionTypes = mLedTypeSelectionMenu.getStrings();
            mMenuListFrequencies = mDanceBotEditorProjectFile.getLedFrequenciesStrings();

            // Check box initialization
            mNumCheckBoxes = LedBeatElement.getNumLedLights();
            mCheckBoxes = new ArrayList<>(mNumCheckBoxes);
            mLedLightSwitches = new boolean[mNumCheckBoxes];

        } else if (mBeatElement.getClass() == MotorBeatElement.class) { // MOVE_TYPE

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
                mMenuMotionTypeIdx = newVal;
                TextView motionTypeTextView = (TextView) mBeatElementMenuView.findViewById(R.id.txt_motion_type_default);
                motionTypeTextView.setText(mMenuListMotionTypes[newVal]);
                break;

            case FREQUENCY:
                // Update frequency text view
                mMenuFrequencyIdx = newVal;
                TextView frequencyTextView = (TextView) mBeatElementMenuView.findViewById(R.id.txt_frequency_default);
                frequencyTextView.setText(mMenuListFrequencies[newVal]);
                break;

            case VELOCITY_LEFT:
                // Update velocity text view
                mMenuVelocityLeftIdx = newVal;
                TextView velocityLeftTextView = (TextView) mBeatElementMenuView.findViewById(R.id.txt_velocity_left_default);
                velocityLeftTextView.setText(mMenuListVelocities[newVal]);
                break;

            case VELOCITY_RIGHT:
                // Update velocity text view
                mMenuVelocityRightIdx = newVal;
                TextView velocityRightTextView = (TextView) mBeatElementMenuView.findViewById(R.id.txt_velocity_right_default);
                velocityRightTextView.setText(mMenuListVelocities[newVal]);
                break;

            case CHOREO_LENGTH:
                // Update length text view
                mMenuChoreoLengthIdx = newVal;
                TextView choreoLengthTextView = (TextView) mBeatElementMenuView.findViewById(R.id.txt_length_default);
                choreoLengthTextView.setText(mMenuChoreoLengths[newVal]);
                break;

            default:
                break;
        }

        Log.v(LOG_TAG, "doPositiveClick()");
    }

    private void processCheckBoxes() {

        for (int i = 0; i < mNumCheckBoxes; ++i) {

            if (mCheckBoxes.get(i).isChecked()) {
                // Set light switch on
                mLedLightSwitches[i] = true;
                //Log.v(LOG_TAG, "checkbox: " + i + " is " + mCheckBoxes.get(i).isChecked());
            } else {
                // Set light switch off
                mLedLightSwitches[i] = false;
                //Log.v(LOG_TAG, "checkbox: " + i + " is " + mCheckBoxes.get(i).isChecked());
            }
        }
    }

    /**
     *
     * @param id
     * @param menuList
     */
    private void buildSubMenu(int id, final int elemIdx, final String[] menuList, final MENU_TYPE menuType, final String menuTag) {

        // Create submenu for specific TextView
        TextView textView = (TextView) mBeatElementMenuView.findViewById(id);
        textView.setText(menuList[elemIdx]);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SubMenuDialog submenu = new SubMenuDialog();
                // Initialize the sub menu (AlertDialog) with the corresponding view and menu list
                submenu.initializeSubMenu(BeatElementMenuDialog.this, menuType, menuList, elemIdx);
                submenu.show(getFragmentManager(), menuTag);
            }
        });
    }

    private void buildCheckBoxMenu(boolean[] ledLightSwitches) {

        // Register check boxes
        mCheckBoxes.add((CheckBox) mBeatElementMenuView.findViewById(R.id.light_check_box1));
        mCheckBoxes.add((CheckBox) mBeatElementMenuView.findViewById(R.id.light_check_box2));
        mCheckBoxes.add((CheckBox) mBeatElementMenuView.findViewById(R.id.light_check_box3));
        mCheckBoxes.add((CheckBox) mBeatElementMenuView.findViewById(R.id.light_check_box4));
        mCheckBoxes.add((CheckBox) mBeatElementMenuView.findViewById(R.id.light_check_box5));
        mCheckBoxes.add((CheckBox) mBeatElementMenuView.findViewById(R.id.light_check_box6));
        mCheckBoxes.add((CheckBox) mBeatElementMenuView.findViewById(R.id.light_check_box7));
        mCheckBoxes.add((CheckBox) mBeatElementMenuView.findViewById(R.id.light_check_box8));

        for (int i = 0; i < mNumCheckBoxes; ++i) {

            if (ledLightSwitches[i]) {
                mCheckBoxes.get(i).setChecked(true);
            } else {
                mCheckBoxes.get(i).setChecked(false);
            }
        }
    }

    private void createLedElementMenu(LedBeatElement elem) {

        // Add motion submenu
        buildSubMenu(R.id.txt_motion_type_default, elem.getMotionTypeIdx(), mMenuListMotionTypes, MENU_TYPE.MOTION, "motion_menu");

        // Add frequency submenu
        buildSubMenu(R.id.txt_frequency_default, elem.getFrequencyIdx(), mMenuListFrequencies, MENU_TYPE.FREQUENCY, "frequency_menu");

        // Add choreography length submenu
        buildSubMenu(R.id.txt_length_default, elem.getChoreoLengthIdx(), mMenuChoreoLengths, MENU_TYPE.CHOREO_LENGTH, "choreo_length_menu");

        // Add a check box menu
        buildCheckBoxMenu(elem.getLedLightSwitches());

        // Hide not relevant menu items
        mBeatElementMenuView.findViewById(R.id.menu_item_velocity_left).setVisibility(View.GONE);
        mBeatElementMenuView.findViewById(R.id.menu_item_velocity_right).setVisibility(View.GONE);
    }

    private void createMotorElementMenu(MotorBeatElement elem) {

        // Add motion submenu
        buildSubMenu(R.id.txt_motion_type_default, elem.getMotionTypeIdx(), mMenuListMotionTypes, MENU_TYPE.MOTION, "motion_menu");

        // Add frequency submenu
        buildSubMenu(R.id.txt_frequency_default, elem.getFrequencyIdx(), mMenuListFrequencies, MENU_TYPE.FREQUENCY, "frequency_menu");

        // Add velocity left submenu
        buildSubMenu(R.id.txt_velocity_left_default, elem.getVelocityLeftIdx(), mMenuListVelocities, MENU_TYPE.VELOCITY_LEFT, "velocity_left_menu");

        // Add velocity right submenu
        buildSubMenu(R.id.txt_velocity_right_default, elem.getVelocityRightIdx(), mMenuListVelocities, MENU_TYPE.VELOCITY_RIGHT, "velocity_right_menu");

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
        if (mBeatElement.getClass() == LedBeatElement.class) { // LED_TYPE

            createLedElementMenu((LedBeatElement) mBeatElement);

        } else if (mBeatElement.getClass() == MotorBeatElement.class) { // MOVE_TYPE

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

                        if (mBeatElement.hasChoreography()) {

                            // Remove existing choreography
                            /*TODO
                            mDanceBotEditorProjectFile.getChoreoManager().removeSequence(mBeatElement);

                            // Store new choreography properties
                            storeCollectedMenuData();

                            // Remove existing choregraphy
                            mDanceBotEditorProjectFile.getChoreoManager().addSequence(mBeatElement);
                            */
                        } else {

                            // Add a new choreography
                            storeCollectedMenuData();

                            // Notify all corresponding beat elements that belong to this choreography
                            mDanceBotEditorProjectFile.getChoreoManager().addSequence(mBeatElement);
                        }

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

    private void storeCollectedMenuData() {

        // Save properties of the selected beat element

        // Get position of the currently selected beat element
        int choreoStartIdx = mBeatElement.getBeatPosition();

        // Get the selected length of the choreography
        int choreoLength = mDanceBotEditorProjectFile.getChoreoLengthAtIdx(mMenuChoreoLengthIdx);

        // Set general beat element properties according to menu choices
        mBeatElement.setProperties(choreoStartIdx, choreoLength, mMenuMotionTypeIdx, mMenuFrequencyIdx, mMenuChoreoLengthIdx);

        // Save Led/Motor element properties
        if (mBeatElement.getClass() == LedBeatElement.class) { // LED_TYPE

            // Process all check boxes
            processCheckBoxes();
            // Set led light switches according to selected check boxes
            ((LedBeatElement) mBeatElement).setLedLightSwitches(mLedLightSwitches);

            // Set motion type of the led beat element
            mBeatElement.setMotionType(mLedTypeSelectionMenu.getValAt(mMenuMotionTypeIdx));

        } else if (mBeatElement.getClass() == MotorBeatElement.class) { // MOVE_TYPE

            // Set velocities of motor beat element
            ((MotorBeatElement) mBeatElement).setVelocityLeftIdx(mMenuVelocityLeftIdx);
            ((MotorBeatElement) mBeatElement).setVelocityRightIdx(mMenuVelocityRightIdx);
        }
    }

}
