package ch.ethz.asl.dancebots.danceboteditor.utils;

/**
 * Created by andrin on 23.02.16.
 */
public interface StreamPlayback {

    void prepareStreamPlayback();

    int readDataStream(short[] outBuffer, int shortCount);
}
