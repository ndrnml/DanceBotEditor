package ch.ethz.asl.dancebots.danceboteditor.model;

import java.util.UUID;

/**
 * Created by andrin on 06.12.15.
 */
public class DanceSequence<T extends BeatElement> {

    private UUID mSequenceID;
    private T mStartElement;
    private int mLength;

    public DanceSequence() {
        setDefaultProperties();
    }

    public DanceSequence(UUID choreoID, T startElem, int danceSequenceLength) {
        mSequenceID = choreoID;
        mStartElement = startElem;
        mLength = danceSequenceLength;
    }

    public void setDefaultProperties() {
        mSequenceID = null;
        mStartElement = null;
        mLength = -1;
    }

    public void updateProperties(UUID choreoID, T startElem, int danceSequenceLength) {
        mSequenceID = choreoID;
        mStartElement = startElem;
        mLength = danceSequenceLength;
    }

    public UUID getChoreographyID() {
        return mSequenceID;
    }

    public T getStartElement() {
        return mStartElement;
    }

    public int getLength() {
        return mLength;
    }

}
