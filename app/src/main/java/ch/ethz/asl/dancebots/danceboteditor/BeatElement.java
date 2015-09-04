package ch.ethz.asl.dancebots.danceboteditor;

import android.graphics.Color;

/**
 * Created by andrin on 28.08.15.
 */
public class BeatElement {

    private int mSamplePosition;
    private String mName;
    private Color mColor;
    private DanceBotMotion mMotion;

    public BeatElement(int pos, String name) {

        mSamplePosition = pos;
        mName = name;
    }

    public String getName() {
        return mName;
    }
}
