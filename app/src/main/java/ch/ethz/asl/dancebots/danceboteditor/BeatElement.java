package ch.ethz.asl.dancebots.danceboteditor;

import android.graphics.Color;

/**
 * Created by andrin on 28.08.15.
 */
abstract class BeatElement<T extends MotionType> {

    int mBeatPosition;
    int mSamplePosition;
    int mColor;
    T mMotionType;

    abstract void updateProperties();

    String getBeatPositionAsString() {
        return Integer.toString(mBeatPosition);
    }

    int getColor() {
        return mColor;
    }
}
