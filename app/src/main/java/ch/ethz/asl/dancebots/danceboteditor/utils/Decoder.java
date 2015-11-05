package ch.ethz.asl.dancebots.danceboteditor.utils;

/**
 * Created by andrin on 25.10.15.
 */
public class Decoder {

    private static final String LOG_TAG = "DECODER";

    private long mSoundFileHandle;

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

    public int getSampleRate() {
        return sampleRate();
    }

    public long getNumberOfSamples() {
        return numberOfSamples();
    }

    public int getNumerOfBeatsDetected() {
        return numBeatsDetected();
    }

    public void dispose()
    {
        if(mSoundFileHandle != 0)
        {
            delete(mSoundFileHandle);
            mSoundFileHandle = 0;
        }
    }

    // Prepare native mp3 decoder
    private native static int initialize();
    // Initialize native decoder handler from selected music file
    private native static long open(String filePath);
    // Decode the currently opened music file
    private native static int decode(long soundFileHandle);
    // Get sample rate from selected song
    private native static int sampleRate();
    // Get total number of samples from selected song
    private native static long numberOfSamples();
    // Get total number of beats detected from selected song
    private native static int numBeatsDetected();
    // Delete all native objects
    private native static int delete(long mSoundFileHandle);

}
