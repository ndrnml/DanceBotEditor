package de.mpg123;

import android.util.Log;

import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotError;
import ch.ethz.asl.dancebots.danceboteditor.utils.Decoder;

/**
 * Created by andrin on 25.10.15.
 */
public class MPG123Decoder implements Decoder {

    private static final String LOG_TAG = "DECODER";

    // Pointer of the sound file handle used by the native decoder implementation
    private long mSoundFileHandle;

    public MPG123Decoder()
    {
        // Initialize sound handle to "null"
        mSoundFileHandle = 0;

        // Initialize native mp3 decoder
        int result = initialize();

        if(result != DanceBotError.MPG123_OK)
        {
            throw new Error("Error: " + result + " initializing native Mp3Decoder");
        }
    }

    @Override
    public void openFile(String filePath) {

        // Open new sound file
        mSoundFileHandle = open(filePath);

        if (mSoundFileHandle == 0) {
            throw new IllegalArgumentException("couldn't open file: " + filePath);
        }
    }

    @Override
    public int decode() {
        return decode(mSoundFileHandle);
    }

    /**
     * Transfer method to fill the java buffer with the native decoded pcm audio channel
     *
     * Attention: This implementation is not so nice, at it uses a tremendously high amount
     * of memory. If possible change this to a deocder stream
     *
     * @param pcmBuffer java short buffer that will be filled with pcm bytes
     * @return number of samples (shorts) transferred
     */
    @Override
    public int transfer(short[] pcmBuffer) {
        return transfer(mSoundFileHandle, pcmBuffer);
    }

    /**
     * Check the audio format of a file at a given path
     *
     * @param filePath file path to the file
     * @return audio format check result
     */
    @Override
    public int checkAudioFormat(String filePath) {

        // Initialize mpg123 library for this thread
        /*int result = initialize();

        if (result != DanceBotError.MPG123_OK) {
            throw new Error("Error: " + result + " initializing native Mp3Decoder");
        }*/

        // Check (audio) file format
        int err = checkFormat(filePath);

        if (err == DanceBotError.MPG123_OK) {
            return DanceBotError.NO_ERROR;
        } else {
            return DanceBotError.MPG123_FORMAT_ERROR;
        }
    }

    /**
     * This function is really important.
     * It cleans up all internally (native) used data structures and objects.
     */
    @Override
    public void close() {
        if (mSoundFileHandle != 0) {
            cleanUp(mSoundFileHandle);
            mSoundFileHandle = 0;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        //cleanUp();
        Log.d(LOG_TAG, "Decoder finalize()");
        super.finalize();
    }

    public long getHandle() {
        return mSoundFileHandle;
    }
    public int getSampleRate() {
        return (int) getSampleRate(mSoundFileHandle);
    }
    public long getNumberOfSamples() {
        return getNumberOfSamples(mSoundFileHandle);
    }

    // Prepare native mp3 decoder
    private native static int initialize();
    // Initialize native decoder handler from selected music file
    private native static long open(String filePath);
    // Decode the currently opened music file
    private native static int decode(long soundFileHandle);
    private native static int transfer(long soundFileHandle, short[] pcmBuffer);
    // Delete native sound file
    private native static int cleanUp(long mSoundFileHandle);
    // Check audio format
    private native static int checkFormat(String filePath);

    // Get sample rate from selected song
    private native static long getSampleRate(long soundFileHandle);
    // Get total number of samples from selected song
    private native static long getNumberOfSamples(long soundFileHandle);
}
