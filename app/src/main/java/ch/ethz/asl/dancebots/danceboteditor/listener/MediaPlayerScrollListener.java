package ch.ethz.asl.dancebots.danceboteditor.listener;

/**
 * Created by andrin on 01.02.16.
 */

/**
 * An interface that defines methods that the media player implements. An instance of
 * DanceBotMediaPlayer passes itself to the AutomaticScrollHandler. This is needed to handle
 * communication between scroll views and seek bars correctly.
 */
public interface MediaPlayerScrollListener {

    boolean isPlaying();

    void setSeekBarProgress(int progress);

    int getSeekBarProgress();

    int getCurrentPosition();

    int getTotalTime();

    int getSampleRate();
}