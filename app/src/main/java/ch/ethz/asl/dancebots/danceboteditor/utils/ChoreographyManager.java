package ch.ethz.asl.dancebots.danceboteditor.utils;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import ch.ethz.asl.dancebots.danceboteditor.adapters.BeatElementAdapter;
import ch.ethz.asl.dancebots.danceboteditor.model.BeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.LedBeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.MotorBeatElement;
import ch.ethz.asl.dancebots.danceboteditor.view.HorizontalRecyclerViews;

/**
 * Created by andrin on 31.08.15.
 */
public class ChoreographyManager {

    private static final String LOG_TAG = "CHOREOGRAPHY_MANAGER";

    private Context mContext;

    private Choreography<LedBeatElement> mLedChoregraphy;
    private Choreography<MotorBeatElement> mMotorChoreography;

    private final ChoreographyViewManager mBeatViews;

    /**
     * An interface that defines methods that SoundTask implements. An instance of
     * SoundTask passes itself to an SoundDecodeRunnable instance through the
     * SoundDecodeRunnable constructor, after which the two instances can access each other's
     * variables.
     */
    public interface ChoreographyViewManager {

        /**
         * Sets the current led element view adapter
         * @param ledAdapter
         */
        void setLedElementAdapter(BeatElementAdapter ledAdapter);

        /**
         * Sets the current motor element view adapter
         * @param motorAdapter
         */
        void setMotorElementAdapter(BeatElementAdapter motorAdapter);
    }

    public ChoreographyManager(Context context, HorizontalRecyclerViews beatViews, DanceBotMusicFile musicFile) {

        // Set application context, to load constant colors and strings
        mContext = context;

        // Store the beat view interface object
        mBeatViews = beatViews;

        ArrayList<LedBeatElement> ledElements = initLedBeatElements(musicFile);
        mLedChoregraphy = new Choreography<>(ledElements);

        mBeatViews.setLedElementAdapter(new BeatElementAdapter<>(mContext, mLedChoregraphy.getBeatElements()));

        ArrayList<MotorBeatElement> motorElements = initMotorBeatElements(musicFile);
        mMotorChoreography = new Choreography<>(motorElements);

        mBeatViews.setMotorElementAdapter(new BeatElementAdapter<>(mContext, mMotorChoreography.getBeatElements()));
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
     * Initialize led beat elements after successfully extracting all beats
     * @param musicFile
     */
    public ArrayList<LedBeatElement> initLedBeatElements(DanceBotMusicFile musicFile) {

        ArrayList<LedBeatElement> elems = new ArrayList<>();

        int[] beatBuffer = musicFile.getBeatBuffer();
        int numBeats = beatBuffer.length;

        if (numBeats > 0) {
            for (int i = 0; i < numBeats; ++i) {
                elems.add(new LedBeatElement(mContext, i, beatBuffer[i]));
            }
        } else {
            // TODO some error?
            Log.v(LOG_TAG, "Error: " + beatBuffer.toString() + ", Number of beats: " + numBeats);
        }

        return elems;
    }

    /**
     * Initialize motor beat elements after successfully extracting all beats
     * @param musicFile
     */
    public ArrayList<MotorBeatElement> initMotorBeatElements(DanceBotMusicFile musicFile) {

        ArrayList<MotorBeatElement> elems = new ArrayList<>();

        int[] beatBuffer = musicFile.getBeatBuffer();
        int numBeats = beatBuffer.length;

        if (numBeats > 0) {
            for (int i = 0; i < numBeats; ++i) {
                elems.add(new MotorBeatElement(mContext, i, beatBuffer[i]));
            }
        } else {
            // TODO some error?
            Log.v(LOG_TAG, "Error: " + beatBuffer.toString() + ", Number of beats: " + numBeats);
        }

        return elems;
    }

}