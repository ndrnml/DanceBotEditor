package ch.ethz.asl.dancebots.danceboteditor.utils;

import java.nio.IntBuffer;

/**
 * Created by andrin on 04.11.15.
 */
public class NativeSoundHandler {

    private static NativeSoundHandler instance;

    private Decoder mDecoder;
    private Encoder mEncoder;

    protected NativeSoundHandler() {};

    public static NativeSoundHandler getInstance() {
        if(instance == null) {
            // If it is the first time the object is accessed create instance
            instance = new NativeSoundHandler();
        }
        return instance;
    }

    public int init(String filePath) {

        mDecoder = new Decoder();

        return NativeSoundHandlerInit(filePath);
    }

    public Decoder getDecoder() {
        return mDecoder;
    }

    public Encoder getEncoder() {
        return mEncoder;
    }

    /**
     *
     * LOAD NATIVE LIBRARIES AND FUNCTIONS
     */
    // Initialize native sound handler from selected music file
    private native int NativeSoundHandlerInit(String musicFilePath);
    // TODO comment
    private native int NativeExtractBeats(IntBuffer intBuffer, int intBufferSize);
    // Get sample rate from selected song
    private native int NativeGetSampleRate();
    // Get total number of samples from selected song
    private native long NativeGetNumberOfSamples();
    // Get total number of beats detected from selected song
    private native int NativeGetNumBeatsDetected();
}
