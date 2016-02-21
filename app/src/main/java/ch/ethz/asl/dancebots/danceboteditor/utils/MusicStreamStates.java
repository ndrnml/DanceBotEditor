package ch.ethz.asl.dancebots.danceboteditor.utils;

/**
 * Created by andrin on 21.02.16.
 */
public class MusicStreamStates {

    public static final int READY_TO_PLAY = 1;
    public static final int PLAYING = 2;
    public static final int STOPPED = 3;

    private int mState = STOPPED;

    public void setState(int s) {
        mState = s;
    }

    public int getState() {
        return mState;
    }

    public synchronized boolean isReadyToPlay() {
        return mState == READY_TO_PLAY;
    }

    public synchronized boolean isPlaying() {
        return mState == PLAYING;
    }

    public synchronized boolean isStopped() {
        return mState == STOPPED;
    }

}
