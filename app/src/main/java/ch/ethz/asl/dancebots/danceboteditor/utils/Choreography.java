package ch.ethz.asl.dancebots.danceboteditor.utils;

import android.util.Log;

import java.nio.IntBuffer;
import java.util.ArrayList;

import ch.ethz.asl.dancebots.danceboteditor.model.BeatElement;

/**
 * Created by andrin on 16.10.15.
 */
public class Choreography<T extends BeatElement> {

    private static final String LOG_TAG = "CHOREOGRAPHY";

    //TODO -> change to private
    public ArrayList<T> mBeatElements;

    public Choreography(BeatGrid beatGrid) {

        mBeatElements = new ArrayList<>();

        initBeatElements(beatGrid);
    }

    public void addSequence(T startElem) {

        addElements(mBeatElements, startElem);
    }

    public void removeSequence(T startElem) {

        removeElements(mBeatElements, startElem);
    }

    private void removeElements(ArrayList<T> elemList, T startElem) {

        int startIdx = startElem.getChoreoStartIdx();
        int choreoLength = startElem.getChoreoLength();

        // At least the clicked element belongs to a choreography
        int length = 1;
        int nextElemIdx = startIdx + 1;

        T nextElem = elemList.get(nextElemIdx);

        // Update element if it does not belong to any choreography and if the current length is
        // less than the total choreography length
        while (nextElem.isSameChoreography(startElem) && (length < choreoLength)) {

            // Copy the element properties
            nextElem.setDefaultProperties();

            // Increment the current length
            length += 1;

            // Increment element
            nextElemIdx += 1;
            nextElem = elemList.get(nextElemIdx);
        }

    }

    private void addElements(ArrayList<T> elemList, T startElem) {

        int startIdx = startElem.getChoreoStartIdx();
        int choreoLength = startElem.getChoreoLength();

        int length = 1;
        int nextElemIdx = startIdx + 1;

        T nextElem = elemList.get(nextElemIdx);

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

    private boolean isNotAssigned(T elem) {
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
                //mBeatElements.add(new MotorBeatElement(i, beatBuffer.get(i), ));
            }
        } else {
            // TODO some error?
            Log.v(LOG_TAG, "Error: " + beatBuffer.toString() + ", Number of beats: " + numBeats);
        }
    }

}
