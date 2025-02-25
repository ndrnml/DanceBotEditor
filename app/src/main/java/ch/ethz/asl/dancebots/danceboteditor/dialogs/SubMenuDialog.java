package ch.ethz.asl.dancebots.danceboteditor.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import ch.ethz.asl.dancebots.danceboteditor.R;

/**
 * Created by andrin on 25.09.15.
 */
public class SubMenuDialog extends DialogFragment {

    private static final String LOG_TAG = "SUBMENU_DIALOG";

    private View mSubmenuView;
    private BeatElementMenuDialog mCallerDialog;
    private BeatElementMenuDialog.MENU_TYPE mMenuType;
    private String[] mMenuList;
    private int mMenuListIdx;
    private int mMenuTitleResource;
    private boolean mMotionTypeChanged = false;

    public void initializeSubMenu(BeatElementMenuDialog dialog,
                                  BeatElementMenuDialog.MENU_TYPE menuType,
                                  String[] menuList,
                                  int menuListIdx,
                                  int menuTitleResource) {
        mCallerDialog = dialog;
        mMenuType = menuType;
        mMenuList = menuList;
        mMenuListIdx = menuListIdx;
        mMenuTitleResource = menuTitleResource;

        // Make sub menu context aware (not a pretty solution)
        switch (menuType) {
            case MOTION:
                mMenuListIdx = mCallerDialog.getTmpMotionIdx();
                break;
            case FREQUENCY:
                mMenuListIdx = mCallerDialog.getTmpFrequencyIdx();
                break;
            case VELOCITY_LEFT:
                mMenuListIdx = mCallerDialog.getTmpVelLeftIdx();
                break;
            case VELOCITY_RIGHT:
                mMenuListIdx = mCallerDialog.getTmpVelRightIdx();
                break;
            case CHOREO_LENGTH:
                mMenuListIdx = mCallerDialog.getTmpLengthIdx();
                break;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        mSubmenuView = inflater.inflate(R.layout.dialog_menu_item, null);

        // Title of submenu view
        final TextView subMenuTitleView = (TextView) mSubmenuView.findViewById(R.id.id_submenu_title);
        subMenuTitleView.setText(mMenuTitleResource);

        // Get number picker view
        final NumberPicker numberPicker = (NumberPicker) mSubmenuView.findViewById(R.id.submenu_element_picker);

        // Configure number picker
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(mMenuList.length - 1);
        numberPicker.setDisplayedValues(mMenuList);
        numberPicker.setValue(mMenuListIdx);
        numberPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        numberPicker.setOrientation(LinearLayout.VERTICAL);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                // If changed motion style, adapt menu view
                if (mMenuType == BeatElementMenuDialog.MENU_TYPE.MOTION) {
                    mMotionTypeChanged = true;
                    //Log.d(LOG_TAG, "motion type: onValueChange");
                }
            }
        });

        // Setup dialog view and buttons
        builder.setView(mSubmenuView)
                // Add action buttons
                .setPositiveButton(R.string.txt_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        // Callback of parent menu
                        mCallerDialog.doPositiveClick(mMenuType, numberPicker.getValue());

                        // Check if motion type changed, and adapt menu element visibility
                        if (mMotionTypeChanged) {
                            mCallerDialog.changeVisibility(numberPicker.getValue());
                            Log.d(LOG_TAG, "motion type changed");
                        }

                        Log.d(LOG_TAG, mMenuList[numberPicker.getValue()]);
                    }
                })
                .setNegativeButton(R.string.txt_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Nothing will be changed
                    }
                });

        return builder.create();
    }
}
