package ch.ethz.asl.dancebots.danceboteditor.ui;

import android.view.View;

import java.util.ArrayList;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.model.LedType;

/**
 * Created by andrin on 21.10.15.
 */
public class LedTypeSelectionMenu {

    private ArrayList<LedType> mSelection;

    public LedTypeSelectionMenu(ArrayList<LedType> selection) {

        mSelection = selection;
    }

    public LedType getValAt(int idx) {
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

        LedType type = mSelection.get(val);

        // Make all menu elements visible
        menuView.findViewById(R.id.menu_item_frequency).setVisibility(View.VISIBLE);
        menuView.findViewById(R.id.menu_item_lights).setVisibility(View.VISIBLE);

        // Now individually make the desired menu element invisible
        switch (type) {

            case DEFAULT:
                break;

            case KNIGHT_RIDER:
                menuView.findViewById(R.id.menu_item_lights).setVisibility(View.GONE);
                break;

            case RANDOM:
                break;

            case BLINK:
                break;

            case SAME_BLINK:
                break;

            case CONSTANT:
                menuView.findViewById(R.id.menu_item_frequency).setVisibility(View.GONE);
                break;

            default:
                break;
        }
    }

}
