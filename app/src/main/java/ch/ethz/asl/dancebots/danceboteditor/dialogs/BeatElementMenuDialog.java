package ch.ethz.asl.dancebots.danceboteditor.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.adapters.BeatElementAdapter;
import ch.ethz.asl.dancebots.danceboteditor.model.BeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.LedBeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.MotorBeatElement;
import ch.ethz.asl.dancebots.danceboteditor.ui.FloatSelectionMenu;
import ch.ethz.asl.dancebots.danceboteditor.ui.IntegerSelectionMenu;
import ch.ethz.asl.dancebots.danceboteditor.ui.LedTypeSelectionMenu;
import ch.ethz.asl.dancebots.danceboteditor.ui.MotorTypeSelectionMenu;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorManager;

/**
 * Created by andrin on 23.09.15.
 */
public class BeatElementMenuDialog extends DialogFragment {

    private static final String LOG_TAG = "BEAT_ELEM_MENU_DIALOG";

    public enum MENU_TYPE {MOTION, FREQUENCY, VELOCITY_LEFT, VELOCITY_RIGHT, CHOREO_LENGTH}

    // General fields
    private View mBeatElementMenuView;
    private BeatElement mBeatElement;
    private DanceBotEditorManager mProjectFile;
    private BeatElementAdapter mBeatElementAdapter;

    // Menus
    private LedTypeSelectionMenu mLedTypeMenu;
    private MotorTypeSelectionMenu mMotorTypeMenu;
    private FloatSelectionMenu mLedFrequencyMenu;
    private FloatSelectionMenu mMotorFrequencyMenu;
    private IntegerSelectionMenu mVelocityMenu;
    private IntegerSelectionMenu mChoreoLengthMenu;

    // Menu list strings
    private String[] mMenuListMotionTypes;
    private String[] mMenuListFrequencies;
    private String[] mMenuListVelocities;
    private String[] mMenuChoreoLengths;

    // Special checkbox menu elements
    private ArrayList<CheckBox> mCheckBoxes;
    private boolean[] mLedLightSwitches;
    private int mNumCheckBoxes;

    // Temporarily store selected menu indices
    private int mSelectedMotionTypeIdx;
    private int mSelectedFrequencyIdx;
    private int mSelectedVelocityLeftIdx;
    private int mSelectedVelocityRightIdx;
    private int mSelectedChoreoLengthIdx;

    /**
     * Ensure that this method is called directly after instantiation of the menu
     * It loads all relevant information (menu lists) for setting up the menu
     * @param elem
     */
    public void initializeMenuFromElement(BeatElementAdapter adapter, BeatElement elem) {

        // Contextual information
        mBeatElementAdapter = adapter;
        mBeatElement = elem;
        mProjectFile = DanceBotEditorManager.getInstance();

        // Load different menus
        mLedTypeMenu = mProjectFile.getLedTypeMenu();
        mMotorTypeMenu = mProjectFile.getMotorTypeMneu();
        mLedFrequencyMenu = mProjectFile.getLedFrequencyMenu();
        mMotorFrequencyMenu = mProjectFile.getMotorFrequencyMenu();
        mVelocityMenu = mProjectFile.getVelocityMenu();
        mChoreoLengthMenu = mProjectFile.getChoreoLengthMenu();

        // Menu list initialization
        mMenuListVelocities = mVelocityMenu.getStrings();
        mMenuChoreoLengths = mChoreoLengthMenu.getStrings();

        // TODO:  ...
        // TODO: USE INSTANCEOF???
        // TODO
        // Further menu lists based on motion type
        if (mBeatElement.getClass() == LedBeatElement.class) { // LED_TYPE

            // Load LedElement type specific menu strings
            mMenuListMotionTypes = mLedTypeMenu.getStrings();
            mMenuListFrequencies = mLedFrequencyMenu.getStrings();

            // Check box initialization
            mNumCheckBoxes = LedBeatElement.getNumLedLights();
            mCheckBoxes = new ArrayList<>(mNumCheckBoxes);
            mLedLightSwitches = new boolean[mNumCheckBoxes];

            // Load LedElement type specific property indices
            mSelectedMotionTypeIdx = elem.getMotionTypeIdx();
            mSelectedFrequencyIdx = elem.getFrequencyIdx();
            mSelectedChoreoLengthIdx = elem.getChoreoLengthIdx();

        } else if (mBeatElement.getClass() == MotorBeatElement.class) { // MOVE_TYPE

            // Load MotorElement type specific menu strings
            mMenuListMotionTypes = mMotorTypeMenu.getStrings();
            mMenuListFrequencies = mMotorFrequencyMenu.getStrings();

            // Load MotorElement type specific property indices
            mSelectedMotionTypeIdx = elem.getMotionTypeIdx();
            mSelectedFrequencyIdx = elem.getFrequencyIdx();
            mSelectedVelocityLeftIdx = ((MotorBeatElement) elem).getVelocityLeftIdx();
            mSelectedVelocityRightIdx = ((MotorBeatElement) elem).getVelocityRightIdx();
            mSelectedChoreoLengthIdx = elem.getChoreoLengthIdx();

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
                mSelectedMotionTypeIdx = newVal;
                TextView motionTypeTextView = (TextView) mBeatElementMenuView.findViewById(R.id.txt_motion_type_default);
                motionTypeTextView.setText(mMenuListMotionTypes[newVal]);
                break;

            case FREQUENCY:
                // Update frequency text view
                mSelectedFrequencyIdx = newVal;
                TextView frequencyTextView = (TextView) mBeatElementMenuView.findViewById(R.id.txt_frequency_default);
                frequencyTextView.setText(mMenuListFrequencies[newVal]);
                break;

            case VELOCITY_LEFT:
                // Update velocity text view
                mSelectedVelocityLeftIdx = newVal;
                TextView velocityLeftTextView = (TextView) mBeatElementMenuView.findViewById(R.id.txt_velocity_left_default);
                velocityLeftTextView.setText(mMenuListVelocities[newVal]);
                break;

            case VELOCITY_RIGHT:
                // Update velocity text view
                mSelectedVelocityRightIdx = newVal;
                TextView velocityRightTextView = (TextView) mBeatElementMenuView.findViewById(R.id.txt_velocity_right_default);
                velocityRightTextView.setText(mMenuListVelocities[newVal]);
                break;

            case CHOREO_LENGTH:
                // Update length text view
                mSelectedChoreoLengthIdx = newVal;
                TextView choreoLengthTextView = (TextView) mBeatElementMenuView.findViewById(R.id.txt_length_default);
                choreoLengthTextView.setText(mMenuChoreoLengths[newVal]);
                break;

            default:
                break;
        }

        Log.v(LOG_TAG, "doPositiveClick()");
    }

    /**
     * Get selected led light switches
     */
    private void processCheckBoxes() {
        for (int i = 0; i < mNumCheckBoxes; ++i) {
            // Set light switch on/off
            mLedLightSwitches[i] = mCheckBoxes.get(i).isChecked();
        }
    }

    /**
     * Build the sub menu for value selection
     * @param id layout id of the menu that is inflated
     * @param menuList menu string values needed for this sub menu
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

        // Create new alert dialog builder
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

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

        // Get beat element menu OK button
        Button btnOk = (Button) mBeatElementMenuView.findViewById(R.id.beatmenu_btn_ok);

        // Add OK button on click listener
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Process all check boxes
                processCheckBoxes();

                mProjectFile.getChoreoManager().processPositiveClick(
                        mBeatElement,
                        mSelectedChoreoLengthIdx,
                        mChoreoLengthMenu/*.getValAt(mSelectedChoreoLengthIdx)*/,
                        mSelectedMotionTypeIdx,
                        mSelectedFrequencyIdx,
                        mLedTypeMenu/*.getValAt(mSelectedMotionTypeIdx)*/,
                        mLedFrequencyMenu/*.getValAt(mSelectedFrequencyIdx)*/,
                        mLedLightSwitches,
                        mMotorTypeMenu/*.getValAt(mSelectedMotionTypeIdx)*/,
                        mMotorFrequencyMenu/*.getValAt(mSelectedFrequencyIdx)*/,
                        mSelectedVelocityLeftIdx,
                        mSelectedVelocityRightIdx,
                        mVelocityMenu/*.getValAt(mSelectedVelocityLeftIdx)*/,
                        mVelocityMenu);/*.getValAt(mSelectedVelocityRightIdx)*/

                // Notify the list adapter to update the modified list elements
                mBeatElementAdapter.notifyDataSetChanged();

                // Dismiss dialog after OK button is pressed and data is passed to choreography manager
                dismiss();
            }
        });

        // Get beat element menu CANCEL button
        Button btnCancel = (Button) mBeatElementMenuView.findViewById(R.id.beatmenu_btn_cancel);

        // Add CANCEL button on click listener
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do nothing
                dismiss();
            }
        });

        // Get beat element menu DISCARD button
        Button btnDiscard = (Button) mBeatElementMenuView.findViewById(R.id.beatmenu_btn_discard);

        // Add DISCARD button on click listener
        btnDiscard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Discard BeatElement properties
                mProjectFile.getChoreoManager().processNegativeClick(mBeatElement);

                // Notify the list adapter to update the modified list elements
                mBeatElementAdapter.notifyDataSetChanged();

                // After DISCARD button is pressed dismiss dialog
                dismiss();
            }
        });

        // Only show discard button if selected beat element already belongs to a dance sequence
        if (mBeatElement.getChoreographyID() == null) {
            mBeatElementMenuView.findViewById(R.id.beatmenu_btn_discard).setVisibility(View.GONE);
        }

        // Build final view with positive and negative buttons
        builder.setView(mBeatElementMenuView);

        return builder.create();
    }

}
