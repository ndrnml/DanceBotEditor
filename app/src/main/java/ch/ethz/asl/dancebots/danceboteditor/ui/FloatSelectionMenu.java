package ch.ethz.asl.dancebots.danceboteditor.ui;

import android.util.Pair;

import java.util.ArrayList;

/**
 * Created by andrin on 21.10.15.
 */
public class FloatSelectionMenu {

    // The range selection is only assigned once
    private final ArrayList<Pair<Integer, Integer>> mSelection;

    public FloatSelectionMenu(ArrayList<Pair<Integer, Integer>> selection) {

        mSelection = selection;
    }

    public float getValAt(int idx) {
        if (mSelection.get(idx).second == 0) {
            throw new IllegalArgumentException("Argument 'divisor' is 0");
        }
        return (float) mSelection.get(idx).first / mSelection.get(idx).second;
    }

    public String getStringAt(int idx) {
        return mSelection.get(idx).first.toString() + "/" + mSelection.get(idx).second.toString();
    }

    public String[] getStrings() {

        String[] selection = new String[mSelection.size()];

        for (int i = 0; i < mSelection.size(); ++i) {
            selection[i] = getStringAt(i);
        }

        return selection;
    }
}

