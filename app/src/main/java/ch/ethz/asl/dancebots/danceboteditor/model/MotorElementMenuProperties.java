package ch.ethz.asl.dancebots.danceboteditor.model;

/**
 * Created by andrin on 06.12.15.
 */
public class MotorElementMenuProperties {

    private int mMotionTypeIdx;
    private int mFrequencyIdx;
    private int mVelocityLeftIdx;
    private int mVelocityRightIdx;

    private MotorType mMotorType;
    private float mFrequencyVal;
    private int mLeftVelocityVal;
    private int mRightVelocityVal;

    public MotorElementMenuProperties(
            int motionTypeIdx,
            int frequencyIdx,
            int velocityLeftIdx,
            int velocityRightIdx) {
        mMotionTypeIdx = motionTypeIdx;
        mFrequencyIdx = frequencyIdx;
        mVelocityLeftIdx = velocityLeftIdx;
        mVelocityRightIdx = velocityRightIdx;
    }

}
