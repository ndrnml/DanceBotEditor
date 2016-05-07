package ch.ethz.asl.dancebots.danceboteditor.utils;

/**
 * Created by andrin on 25.10.15.
 */
public interface Encoder {

    int encode(short[] buffer_l, short[] buffer_r, int samples, byte[] mp3buf);

    int flush(byte[] mp3buf);

    void close();
}