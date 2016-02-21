package ch.ethz.asl.dancebots.danceboteditor.utils;

/**
 * Created by andrin on 01.02.16.
 */
public interface StreamPlayerEvents {

    void onStart(String mime, int sampleRate, int channels, long durationInMs);
    void onPlay();
    void onPlayUpdate(int percentage, long currentMs, long totalMs);
    void onStop();
}
