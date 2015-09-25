package ch.ethz.asl.dancebots.danceboteditor.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
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

/**
 * Created by andrin on 25.09.15.
 */
public class SubMenuDialog extends DialogFragment {

    private View mSubmenuView;
    private BeatElement mBeatElement;
    private ArrayList mMenuList;

    public void initializeSubMenu(BeatElement beatElem, ArrayList menuList) {

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

        // Get title view
        final TextView subMenuTitleView = (TextView) mSubmenuView.findViewById(R.id.id_submenu_title);

        // Get number picker view
        NumberPicker np = (NumberPicker) mSubmenuView.findViewById(R.id.submenu_element_picker);

        // Configure number picker
        np.setMinValue(0);
        np.setMaxValue(2);
        np.setDisplayedValues(new String[]{"Belgium", "France", "United Kingdom"});
        np.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        np.setOrientation(LinearLayout.VERTICAL);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Toast.makeText(
                        getActivity(),
                        "You Chose : " + newVal,
                        Toast.LENGTH_SHORT
                ).show();

                //subMenuTitleView.setText(newVal);
            }
        });

        // Compose submenu dialog with custom elements
        //builder.setTitle("MenuName: ");

        builder.setView(mSubmenuView)

                // Add action buttons
                .setPositiveButton(R.string.txt_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Save selected property

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
