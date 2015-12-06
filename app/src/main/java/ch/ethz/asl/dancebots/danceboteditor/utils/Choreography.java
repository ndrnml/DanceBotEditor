package ch.ethz.asl.dancebots.danceboteditor.utils;

import java.util.ArrayList;
import java.util.UUID;

import ch.ethz.asl.dancebots.danceboteditor.model.BeatElement;

/**
 * Created by andrin on 16.10.15.
 */
public class Choreography<T extends BeatElement> {

    // TODO:
    // TODO: Some neat datastructure hash, that maps UUID to (StartIdx, Length)

    private static final String LOG_TAG = "CHOREOGRAPHY";

    private ArrayList<T> mBeatElements;
    private int mNumBeats;

    public Choreography(ArrayList<T> elems) {
        mBeatElements = elems;
        mNumBeats = elems.size();
    }

    /**
     * Add a new dance sequence to the choreography
     * @param choreoID
     * @param startElem
     */
    public void addDanceSequence(UUID choreoID, T startElem) {

        // Set unique dance sequence identifier, to the starting element of the dance sequence
        startElem.setChoreographyID(choreoID);

        // Get start element and length of dance sequence
        int startIdx = startElem.getChoreoStartIdx();
        int choreoLength = startElem.getChoreoLength();

        // Init dance sequence parameters
        int length = 1;
        int nextElemIdx = startIdx + 1;

        T nextElem;

        /*
         * Update element if it does not belong to any choreography and if the current length is
         * less than the total choreography length, and current position is less than total beats
         */
        while ((length < choreoLength) && ((startIdx + length) < mNumBeats)) {

            // If nextElemIdx and length in valid range, fetch next element
            nextElem = mBeatElements.get(nextElemIdx);

            // THIS ENSURES, THAT NO OVERWRITES OF DANCE SEQUENCES OCCUR
            // Check if next element is already assigned to a dance sequence
            if (isNotAssignedToOther(choreoID, nextElem)) {

                // Copy UUID to the next element
                nextElem.setChoreographyID(choreoID);

                // Copy the element properties
                nextElem.setProperties(startElem);

                // Increment the current length
                length += 1;

                // Increment element
                nextElemIdx += 1;

            } else {

                // If the next element already was assigned, stop here.
                break;
            }

        }
    }

    /**
     * Update existing BeatElements and handle (if there exist) remaining BeatElements
     * @param choreoID dance sequence identifier for which elements should be updated
     * @param elem starting element
     */
    public void updateDanceSequence(UUID choreoID, T elem) {

        // Get start index of currently (existing) selected dance sequence
        int startIdx = elem.getChoreoStartIdx();
        T startElem = mBeatElements.get(startIdx);

        // Get old dance sequence length
        int oldDanceSequenceLength = getDanceSequenceLength(choreoID, startElem);
        int newDanceSequenceLength = startElem.getChoreoLength();

        // Overwrite existing elements
        addDanceSequence(choreoID, startElem);

        // If old dance sequence was longer, free the remaining elements
        if (newDanceSequenceLength < oldDanceSequenceLength) {
            removeDanceSequence(choreoID, newDanceSequenceLength);
        }
    }

    /**
     * Compute the length of a dance sequence beginning at startElem
     * @param choreoID unique dance sequence identifier
     * @param startElem start element of the dance sequence
     * @return length of dance sequence beginning at startElem
     */
    private int getDanceSequenceLength(UUID choreoID, T startElem) {

        // Get absolute start index of old start element
        int startIdx = startElem.getChoreoStartIdx();

        // Initialize length
        int length = 1;

        // Compute next element
        int nextElemIdx = startIdx + 1;
        T nextElem = mBeatElements.get(nextElemIdx);

        /*
         * While the following elements have the same choreID they belong to the old dance
         * sequence
         */
        while (nextElemIdx < mNumBeats && nextElem.getChoreographyID() == choreoID) {
            length += 1;
            nextElemIdx += 1;
            nextElem = mBeatElements.get(nextElemIdx);
        }

        // Return the computed length
        return length;
    }

    /**
     * Remove existing dance sequence with unique identifier choreoID, beginning at startIdx
     * @param choreoID unique dance sequence identifier
     * @param startIdx start index for the dance sequence, that is removed
     */
    private void removeDanceSequence(UUID choreoID, int startIdx) {

        // Fetch first element to remove
        int nextElemIdx = startIdx;
        T nextElem = mBeatElements.get(nextElemIdx);

        while (nextElem.getChoreographyID() == choreoID) {

            // Set default properties
            nextElem.setDefaultProperties();

            if (nextElemIdx + 1 < mNumBeats) {
                // Fetch next element
                nextElemIdx += 1;
                nextElem = mBeatElements.get(nextElemIdx);
            } else {
                break;
            }
        }
    }

    public void removeDanceSequence(UUID choreoID, T startElem) {

        int startIdx = startElem.getChoreoStartIdx();
        int choreoLength = startElem.getChoreoLength();

        // At least the clicked element belongs to a choreography
        int length = 1;
        int nextElemIdx = startIdx + 1;

        T nextElem = mBeatElements.get(nextElemIdx);

        // Update element if it does not belong to any choreography and if the current length is
        // less than the total choreography length
        while (nextElem.isSameDanceSequence(startElem) && (length < choreoLength)) {

            // Copy the element properties
            nextElem.setDefaultProperties();

            // Increment the current length
            length += 1;

            // Increment element
            nextElemIdx += 1;
            nextElem = mBeatElements.get(nextElemIdx);
        }
    }

    /**
     * Check if elem is not assigned to any other dance sequence
     * @param choreoID existing dance sequence identifier
     * @param elem the element which is checked
     * @return true if element is not yet assigned
     */
    private boolean isNotAssignedToOther(UUID choreoID, T elem) {
        return ((elem.getChoreographyID() == null) || (elem.getChoreographyID() == choreoID));
    }

    public ArrayList<T> getBeatElements() {
        return mBeatElements;
    }
}
