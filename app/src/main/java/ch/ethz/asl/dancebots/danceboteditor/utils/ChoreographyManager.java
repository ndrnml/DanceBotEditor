package ch.ethz.asl.dancebots.danceboteditor.utils;

import android.content.Context;
import android.util.Log;

import java.nio.IntBuffer;

import ch.ethz.asl.dancebots.danceboteditor.model.BeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.LedBeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.MotorBeatElement;

/**
 * Created by andrin on 31.08.15.
 */
public class ChoreographyManager {

    private static final String LOG_TAG = "CHOREOGRAPHY_MANAGER";

    private Context mContext;

    public Choreography<LedBeatElement> mLedChoregraphy;
    public Choreography<MotorBeatElement> mMotorChoreography;

    public ChoreographyManager(Context context, BeatGrid beatGrid) {

        // Set application context, to load constant colors and strings
        mContext = context;

        mLedChoregraphy = new Choreography<>();
        initLedBeatElements(beatGrid);

        mMotorChoreography = new Choreography<>();
        initMotorBeatElements(beatGrid);
    }

    public void addSequence(BeatElement mBeatElement) {

        if (mBeatElement.getClass() == LedBeatElement.class) { // LED_TYPE

            mLedChoregraphy.addSequence((LedBeatElement) mBeatElement);

        } else if (mBeatElement.getClass() == MotorBeatElement.class) { // MOTOR_TYPE

            mMotorChoreography.addSequence((MotorBeatElement) mBeatElement);

        }
    }

    public void removeSequence(BeatElement mBeatElement) {

        if (mBeatElement.getClass() == LedBeatElement.class) { // LED_TYPE

            mLedChoregraphy.removeSequence((LedBeatElement) mBeatElement);

        } else if (mBeatElement.getClass() == MotorBeatElement.class) { // MOTOR_TYPE

            mMotorChoreography.removeSequence((MotorBeatElement) mBeatElement);

        }
    }

    /**
     * Initialize beat elements after successfully extracting all beats
     * beatGrid.getBeatBuffer() must be NOT null
     * @param beatGrid
     */
    public void initLedBeatElements(BeatGrid beatGrid) {

        IntBuffer beatBuffer = beatGrid.getBeatBuffer();
        int numBeats = beatGrid.getNumOfBeats();

        if (beatBuffer != null && numBeats > 0) {
            for (int i = 0; i < numBeats; ++i) {
                mLedChoregraphy.addBeatElement(new LedBeatElement(mContext, i, beatBuffer.get(i)));
            }
        } else {
            // TODO some error?
            Log.v(LOG_TAG, "Error: " + beatBuffer.toString() + ", Number of beats: " + numBeats);
        }
    }

    public void initMotorBeatElements(BeatGrid beatGrid) {

        IntBuffer beatBuffer = beatGrid.getBeatBuffer();
        int numBeats = beatGrid.getNumOfBeats();

        if (beatBuffer != null && numBeats > 0) {
            for (int i = 0; i < numBeats; ++i) {
                mMotorChoreography.addBeatElement(new MotorBeatElement(mContext, i, beatBuffer.get(i)));
            }
        } else {
            // TODO some error?
            Log.v(LOG_TAG, "Error: " + beatBuffer.toString() + ", Number of beats: " + numBeats);
        }
    }

}