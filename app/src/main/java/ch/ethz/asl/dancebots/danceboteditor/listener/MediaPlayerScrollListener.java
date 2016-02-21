package ch.ethz.asl.dancebots.danceboteditor.listener;

/**
 * Created by andrin on 01.02.16.
 */

/**
 * An interface that implements a media player scroll change listener. It listens for media player
 * progress changes. This can be used to communicate the media player progress to scroll views
 * or seek bars.
 */
public interface MediaPlayerScrollListener {

    boolean isPlaying();

    void setSeekBarProgress(int progress);

    int getSeekBarProgress();

    int getCurrentPosition();

    int getTotalTime();

    int getSampleRate();
}