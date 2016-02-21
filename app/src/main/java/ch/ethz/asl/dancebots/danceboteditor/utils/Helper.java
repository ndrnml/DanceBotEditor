package ch.ethz.asl.dancebots.danceboteditor.utils;

import java.util.concurrent.TimeUnit;

/**
 * Created by andrin on 01.02.16.
 */
public class Helper {

    /**
     * Get song time hh:ss format from milliseconds
     *
     * @param timeInMilliseconds time in milliseconds
     *
     * @return string format mm:ss
     */
    public static String songTimeFormat(int timeInMilliseconds) {
        return String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds),
                TimeUnit.MILLISECONDS.toSeconds(timeInMilliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds)));
    }

}
