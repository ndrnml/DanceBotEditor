package ch.ethz.asl.dancebots.danceboteditor;

import java.util.ArrayList;

/**
 * Created by andrin on 04.09.15.
 */
abstract class DanceBotMotion<T extends MotionType> {

    ArrayList<BeatElement> mBeatElements;
    T mMotionType;
    int mStartIndex;
    int mEndIndex;

    // If underlying beat elements have been changed, update them accordingly
    abstract void updateElements(int startIndex, int endIndex, T type);

}
