package ch.ethz.asl.dancebots.danceboteditor;

/**
 * Created by andrin on 04.09.15.
 */
public class DanceBotMotorMotion extends DanceBotMotion<MoveType> {

    public DanceBotMotorMotion(int startIndex, int endIndex, MoveType type) {

        mStartIndex = startIndex;
        mEndIndex = endIndex;
        mMotionType = type;
    }

    @Override
    void updateElements(int startIndex, int endIndex, MoveType type) {

    }
}
