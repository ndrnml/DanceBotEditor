package ch.ethz.asl.dancebots.danceboteditor.ui;

import java.util.ArrayList;

import ch.ethz.asl.dancebots.danceboteditor.model.LedType;

/**
 * Created by andrin on 21.10.15.
 */
public class IntegerSelectionMenu {

    private ArrayList<Integer> mSelection;

    public IntegerSelectionMenu(ArrayList<Integer> selection) {

        mSelection = selection;
    }

    public int getValAt(int idx) {
        return mSelection.get(idx);
    }

    public String getStringAt(int idx) {
        return mSelection.get(idx).toString();
    }

    public String[] getStrings() {

        String[] selection = new String[mSelection.size()];

        for (int i = 0; i < mSelection.size(); ++i) {
            selection[i] = getStringAt(i);
        }

        return selection;
    }
}

