package ch.ethz.asl.dancebots.danceboteditor.utils;

/**
 * Created by andrin on 27.08.15.
 */
public interface DanceBotError {

    // No errors
    int NO_ERROR = -1;

    int MPG123_OK = 0;

    // Errors
    int DECODING_ERR = 1;
    int BEAT_EXTRACTION_ERR = 2;

}
