package ch.ethz.asl.dancebots.danceboteditor.ui;

import java.util.ArrayList;

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
}
