package ch.ethz.asl.dancebots.danceboteditor.ui;

import android.view.View;

import java.util.ArrayList;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.model.MotorType;

/**
 * Created by andrin on 21.10.15.
 */
public class MotorTypeSelectionMenu {

    private ArrayList<MotorType> mSelection;

    public MotorTypeSelectionMenu(ArrayList<MotorType> selection) {

        mSelection = selection;
    }

    public MotorType getValAt(int idx) {
        return mSelection.get(idx);
    }

    public String getStringAt(int idx) {
        return mSelection.get(idx).getReadableName();
    }

    public String[] getStrings() {

        String[] selection = new String[mSelection.size()];

        for (int i = 0; i < mSelection.size(); ++i) {
            selection[i] = getStringAt(i);
        }

        return selection;
    }

    /**
     * Change visibility of specific menu elements based on the selected value
     * @param menuView beat element view
     * @param val value based on which elements will be made invisible
     */
    public void setVisibility(View menuView, int val) {

        MotorType type = mSelection.get(val);

        // Make all non-motor elements invisible
        menuView.findViewById(R.id.menu_item_lights).setVisibility(View.GONE);

        // Make all motor menu elements visible
        menuView.findViewById(R.id.menu_item_frequency).setVisibility(View.VISIBLE);
        menuView.findViewById(R.id.menu_item_velocity_left).setVisibility(View.VISIBLE);
        menuView.findViewById(R.id.menu_item_velocity_right).setVisibility(View.VISIBLE);

        // Now individually make the desired menu element invisible
        switch (type) {

            case DEFAULT:
                break;

            case STRAIGHT:
                menuView.findViewById(R.id.menu_item_frequency).setVisibility(View.GONE);
                break;

            case SPIN:
                menuView.findViewById(R.id.menu_item_frequency).setVisibility(View.GONE);
                break;

            case TWIST:
                break;

            case BACK_AND_FORTH:
                break;

            case CONSTANT:
                menuView.findViewById(R.id.menu_item_frequency).setVisibility(View.GONE);
                break;

            case WAIT:
                menuView.findViewById(R.id.menu_item_frequency).setVisibility(View.GONE);
                menuView.findViewById(R.id.menu_item_velocity_left).setVisibility(View.GONE);
                menuView.findViewById(R.id.menu_item_velocity_right).setVisibility(View.GONE);
                break;
        }
    }

}