package ch.ethz.asl.dancebots.danceboteditor.utils;

/**
 * Created by andrin on 25.10.15.
 */
// TODO
// TODO THIS CLASS SHOULD BE STATIC, NOT?
public class Decoder {

    private static final String LOG_TAG = "DECODER";

    private static long mSoundFileHandle; // TODO: I don't think static is what we want here?!

    public Decoder()
    {
        // Initialize sound handle to "null"
        mSoundFileHandle = 0;

        // Initialize native mp3 decoder
        int result = initialize();

        if(result != DanceBotError.MPG123_OK)
        {
            throw new java.lang.Error("Error: " + result + " initializing native Mp3Decoder");
        }
    }

    public void openFile(String filePath) {

        // Open new sound file
        mSoundFileHandle = open(filePath);

        if (mSoundFileHandle == 0) {
            throw new IllegalArgumentException("couldn't open file: " + filePath);
        }
    }

    public int decode() {
        return decode(mSoundFileHandle);
    }

    // TODO: making this method static is reeeeeally dangerous. What if mSoundFileHandle is not yet initialized?
    public static int transfer(short[] pcmBuffer) {
        return transfer(mSoundFileHandle, pcmBuffer);
    }

    public void dispose()
    {
        if(mSoundFileHandle != 0)
        {
            delete(mSoundFileHandle);
            mSoundFileHandle = 0;
        }
    }

    /* TODO: IS THIS CALL SAFE?
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        dispose();
    }
    */

    // TODO should this method be static, if mSoundFileHandle is static?
    public long getHandle() {
        return mSoundFileHandle;
    }
    public int getSampleRate() {
        return (int)getSampleRate(mSoundFileHandle);
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
    private native static int delete(long mSoundFileHandle);
    // Get sample rate from selected song
    private native static long getSampleRate(long soundFileHandle);
    // Get total number of samples from selected song
    private native static long getNumberOfSamples(long soundFileHandle);
    // Get total number of beats detected from selected song
    private native static int getNumBeatsDetected(long soundFileHandle);
}
