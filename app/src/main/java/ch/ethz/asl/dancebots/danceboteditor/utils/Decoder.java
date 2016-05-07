package ch.ethz.asl.dancebots.danceboteditor.utils;

/**
 * Created by andrin on 25.10.15.
 */
public interface Decoder {

    void openFile(String filePath);

    int decode();

    int transfer(short[] pcmBuffer);

    int checkAudioFormat(String filePath);

    void close();

    long getHandle();

    int getSampleRate();

    long getNumberOfSamples();
}
