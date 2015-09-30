package ch.ethz.asl.dancebots.danceboteditor.utils;

import java.nio.IntBuffer;
import java.util.ArrayList;

import ch.ethz.asl.dancebots.danceboteditor.model.BeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.LedBeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.LedType;
import ch.ethz.asl.dancebots.danceboteditor.model.MotorBeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.MoveType;

/**
 * Created by andrin on 31.08.15.
 */
public class ChoreographyManager {

    //TODO -> change to private
    public ArrayList<BeatElement> mMotorBeatElements;
    public ArrayList<BeatElement> mLedBeatElements;

    public ChoreographyManager(BeatGrid beatGrid) {

        mMotorBeatElements = new ArrayList<>();
        mLedBeatElements = new ArrayList<>();

        initBeatElements(beatGrid);
    }

    public void updateElements(Class elemClass, BeatElement startElem, int startIdx, int choreoLength) {

        int idx = startIdx;
        ArrayList<BeatElement> elemArray = mLedBeatElements;

        if (elemClass.equals(MotorBeatElement.class)) {

            elemArray = mMotorBeatElements;

        } else {
            // TODO: This should never happen
        }

        while (elemArray.get(idx).getMotionStartIndex() == -1 && choreoLength - 1 >= 0) {

            // Get element
            BeatElement elem = elemArray.get(idx);
            elem.updateProperties();
        }
    }

    /**
     * Initialize beat elements after successfully extracting all beats
     * beatGrid.getBeatBuffer() must be NOT null
     * @param beatGrid
     */
    private void initBeatElements(BeatGrid beatGrid) {

        IntBuffer beatBuffer = beatGrid.getBeatBuffer();
        int numBeats = beatGrid.getNumOfBeats();
        int i = 0;

        if (beatBuffer != null) {
            while (i < numBeats) {

                mMotorBeatElements.add(new MotorBeatElement(i, beatBuffer.get(i), MoveType.WAIT));
                mLedBeatElements.add(new LedBeatElement(i, beatBuffer.get(i), LedType.CONSTANT));
            }
        } else {
            // TODO some error?
        }
    }

}
