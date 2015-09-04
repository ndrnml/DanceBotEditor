package ch.ethz.asl.dancebots.danceboteditor;

import java.util.ArrayList;

/**
 * Created by andrin on 31.08.15.
 */
public class ChoreographyManager {

    private ArrayList<BeatElement> mBeatElements;
    private ArrayList<DanceBotMotion> mDanceBotMotions;

    public ChoreographyManager() {
        mDanceBotMotions = new ArrayList<>();
    }

    private void addMotion(DanceBotMotion motion) {
        mDanceBotMotions.add(motion);
    }

    private void removeMotion(DanceBotMotion motion) {
        mDanceBotMotions.remove(motion);
    }
}
