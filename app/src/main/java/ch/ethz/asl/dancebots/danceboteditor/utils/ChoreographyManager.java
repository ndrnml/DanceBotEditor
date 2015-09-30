package ch.ethz.asl.dancebots.danceboteditor.utils;

import android.util.Log;

import java.nio.IntBuffer;
import java.util.ArrayList;

import ch.ethz.asl.dancebots.danceboteditor.model.BeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.LedBeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.LedType;
import ch.ethz.asl.dancebots.danceboteditor.model.MotionType;
import ch.ethz.asl.dancebots.danceboteditor.model.MotorBeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.MoveType;

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

    public void updateElements(ArrayList<BeatElement> elemList, BeatElement startElem) {

        int startIdx = startElem.getMotionStartIndex();
        int choreoLength = startElem.getMotionLength();

        for (int i = 1; i < choreoLength; ++i) {

            int nextElemIdx = startIdx + i;

            // Check that beat element i is not yet assigned to another choreography
            if (elemList.get(nextElemIdx).getMotionStartIndex() == -1) {

                // Get element
                BeatElement elem = elemList.get(nextElemIdx);
                elem.setProperties(startElem);
                elem.updateProperties();
            }
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
