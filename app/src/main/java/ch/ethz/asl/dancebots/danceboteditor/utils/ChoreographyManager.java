package ch.ethz.asl.dancebots.danceboteditor.utils;

import android.util.Log;

import java.nio.IntBuffer;
import java.util.ArrayList;

import ch.ethz.asl.dancebots.danceboteditor.model.BeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.LedBeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.LedType;
import ch.ethz.asl.dancebots.danceboteditor.model.MotorBeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.MotorType;

/**
 * Created by andrin on 31.08.15.
 */
public class ChoreographyManager {

    private static final String LOG_TAG = "CHOREOGRAPHY_MANAGER";

    public Choreography<LedBeatElement> mLedChoregraphy;
    public Choreography<MotorBeatElement> mMotorChoreography;

    public ChoreographyManager(BeatGrid beatGrid) {

        mLedChoregraphy = new Choreography<>(beatGrid);
        mMotorChoreography = new Choreography<>(beatGrid);
    }

    public void addSequence(BeatElement mBeatElement) {

        if (mBeatElement.getMotionType().getClass() == LedType.class) { // LED_TYPE

            mLedChoregraphy.addSequence((LedBeatElement) mBeatElement);

        } else if (mBeatElement.getMotionType().getClass() == MotorType.class) { // MOVE_TYPE

            mMotorChoreography.addSequence((MotorBeatElement) mBeatElement);

        }
    }

    public void removeSequence(BeatElement mBeatElement) {

        if (mBeatElement.getMotionType().getClass() == LedType.class) { // LED_TYPE

            mLedChoregraphy.removeSequence((LedBeatElement) mBeatElement);

        } else if (mBeatElement.getMotionType().getClass() == MotorType.class) { // MOVE_TYPE

            mMotorChoreography.removeSequence((MotorBeatElement) mBeatElement);

        }
    }

}