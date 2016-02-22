package ch.ethz.asl.dancebots.danceboteditor.utils;

/**
 * Created by andrin on 22.02.16.
 */
public class DanceBotConfiguration {

    // Dance Bot hardware constants
    public static final int SAMPLE_FREQUENCY_NOMINAL = 44100;
    public static final int BIT_LENGTH_ONE_NOMINAL = 24;
    public static final int BIT_LENGTH_ZERO_NOMINAL = 8;
    public static final int BIT_LENGTH_RESET_NOMINAL = 40;
    public static final int NUM_BIT_MOTOR = 7;
    public static final short DATA_LEVEL = 26214;

    // Constants for indicating the state of the encoding
    public static final int ENCODE_STATE_FAILED = -1;
    public static final int ENCODE_STATE_STARTED = 0;
    public static final int ENCODE_STATE_COMPLETED = 1;
}
