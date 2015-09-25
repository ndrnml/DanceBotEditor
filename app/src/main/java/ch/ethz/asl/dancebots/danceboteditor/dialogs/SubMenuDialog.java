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
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.model.BeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.LedType;
import ch.ethz.asl.dancebots.danceboteditor.model.MotionType;

/**
 * Created by andrin on 25.09.15.
 */
public class SubMenuDialog extends DialogFragment {

    private static final String LOG_TAG = "SUBMENU_DIALOG";

    private View mSubmenuView;
    private BeatElementMenuDialog mCallerDialog;
    private BeatElementMenuDialog.MENU_TYPE mMenuType;
    private BeatElement mBeatElement;
    private String[] mMenuList;

    public void initializeSubMenu(BeatElementMenuDialog dialog,
                                  BeatElementMenuDialog.MENU_TYPE menuType,
                                  BeatElement beatElem,
                                  String[] menuList) {
        mCallerDialog = dialog;
        mMenuType = menuType;
        mBeatElement = beatElem;
        mMenuList = menuList;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        mSubmenuView = inflater.inflate(R.layout.beat_element_submenu, null);

        // TODO Get title view
        //final TextView subMenuTitleView = (TextView) mSubmenuView.findViewById(R.id.id_submenu_title);

        // Get number picker view
        final NumberPicker numberPicker = (NumberPicker) mSubmenuView.findViewById(R.id.submenu_element_picker);

        // Configure number picker
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(mMenuList.length - 1);
        numberPicker.setDisplayedValues(mMenuList);
        numberPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        numberPicker.setOrientation(LinearLayout.VERTICAL);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // TODO: change title
                //subMenuTitleView.setText(newVal);
            }
        });

        // Compose submenu dialog with custom elements
        //builder.setTitle("MenuName: "); TODO. Either setTitle or customize TextView

        // Setup dialog view and buttons
        builder.setView(mSubmenuView)
                // Add action buttons
                .setPositiveButton(R.string.txt_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        switch(mMenuType) {

                            case MOTION:
                                // Save motion property
                                mBeatElement.setMotionTypeString(mMenuList[numberPicker.getValue()]);
                                break;

                            case FREQUENCY:
                                // Save frequency property
                                mBeatElement.setFrequencyString(mMenuList[numberPicker.getValue()]);
                                break;

                            case VELOCITY:
                                // Save velocity property
                                mBeatElement.setVelocityString(mMenuList[numberPicker.getValue()]);
                                break;

                            case LIGHTS:
                                // Save lights property
                                //mBeatElement.(mMenuList[numberPicker.getValue()]); TODO
                                break;

                            case LENGTH:
                                // Save choreography length property
                                mBeatElement.setChoreoLengthString(mMenuList[numberPicker.getValue()]);
                                break;

                            default:
                                break;
                        }

                        // Callback of parent menu
                        mCallerDialog.doPositiveClick();

                        Log.v(LOG_TAG, mMenuList[numberPicker.getValue()]);
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
