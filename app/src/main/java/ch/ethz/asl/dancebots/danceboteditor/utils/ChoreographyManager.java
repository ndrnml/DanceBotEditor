package ch.ethz.asl.dancebots.danceboteditor.utils;

import android.util.Log;

import java.nio.IntBuffer;
import java.util.ArrayList;

import ch.ethz.asl.dancebots.danceboteditor.model.BeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.LedBeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.MotorBeatElement;

/**
 * Created by andrin on 31.08.15.
 */
public class ChoreographyManager {

    private static final String LOG_TAG = "CHOREOGRAPHY_MANAGER";

    //TODO -> change to private
    public ArrayList<BeatElement> mMotorBeatElements;
    public ArrayList<BeatElement> mLedBeatElements;

    public ChoreographyManager(BeatGrid beatGrid) {

        mMotorBeatElements = new ArrayList<>();
        mLedBeatElements = new ArrayList<>();

        initBeatElements(beatGrid);
    }

    public void updateChoreography(BeatElement startElem) {

        if (startElem.getClass().equals(LedBeatElement.class)) {

            updateElements(mLedBeatElements, startElem);

        } else if (startElem.getClass().equals(MotorBeatElement.class)) {

            updateElements(mMotorBeatElements, startElem);
        }
    }

    private void updateElements(ArrayList<BeatElement> elemList, BeatElement startElem) {

        int startIdx = startElem.getChoreoStartIdx();
        int choreoLength = startElem.getChoreoLength();

        int length = 1;
        int nextElemIdx = startIdx + 1;

        BeatElement nextElem = elemList.get(nextElemIdx);

        // Update element if it does not belong to any choreography and if the current length is
        // less than the total choreography length
        while (isNotAssigned(nextElem) && (length < choreoLength)) {

            // Copy the element properties
            nextElem.setProperties(startElem);

            // Increment the current length
            length += 1;

            // Increment element
            nextElemIdx += 1;
            nextElem = elemList.get(nextElemIdx);
        }
    }

    private boolean isNotAssigned(BeatElement elem) {
        return (elem.getChoreoStartIdx() == -1);
    }

    /**
     * Initialize beat elements after successfully extracting all beats
     * beatGrid.getBeatBuffer() must be NOT null
     * @param beatGrid
     */
    private void initBeatElements(BeatGrid beatGrid) {

        IntBuffer beatBuffer = beatGrid.getBeatBuffer();
        int numBeats = beatGrid.getNumOfBeats();

        if (beatBuffer != null && numBeats > 0) {
            for (int i = 0; i < numBeats; ++i) {

                // TODO CORRECT INITIALIZATION
                //mMotorBeatElements.add(new MotorBeatElement(i, beatBuffer.get(i), ));
                //mLedBeatElements.add(new LedBeatElement(i, beatBuffer.get(i)));
            }
        } else {
            // TODO some error?
            Log.v(LOG_TAG, "Error: " + beatBuffer.toString() + ", Number of beats: " + numBeats);
        }
    }

}
