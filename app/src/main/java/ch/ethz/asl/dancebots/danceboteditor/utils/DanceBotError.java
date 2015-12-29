package ch.ethz.asl.dancebots.danceboteditor.utils;

/**
 * Created by andrin on 27.08.15.
 */
public interface DanceBotError {

    // No errors
    int ERROR = -1;
    int NO_ERROR = 1;

    int MPG123_OK = 0;

    // Errors
    int MPG123_FORMAT_ERROR = 2;
    int BEAT_EXTRACTION_ERR = 3;

    int WRITE_ERROR = 4;
}
