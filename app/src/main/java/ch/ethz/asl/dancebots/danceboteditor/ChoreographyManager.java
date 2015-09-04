package ch.ethz.asl.dancebots.danceboteditor;

import java.nio.IntBuffer;
import java.util.ArrayList;

/**
 * Created by andrin on 31.08.15.
 */
public class ChoreographyManager {

    //TODO -> change to private
    public ArrayList<BeatElement> mMotorBeatElements;

    private ArrayList<DanceBotMotorMotion> mMotorMotions;

    public ChoreographyManager(BeatGrid beatGrid) {

        mMotorBeatElements = new ArrayList<>();
        mMotorMotions = new ArrayList<>();

        initBeatElements(beatGrid);
    }

    /**
     * Initialize beat elements after successfully extracting all beats
     * beatGrid.getBeatBuffer() must be NOT null
     * @param beatGrid
     */
    private void initBeatElements(BeatGrid beatGrid) {

        IntBuffer beatBuffer = beatGrid.getBeatBuffer();
        int numBeats = beatGrid.getNumOfBeats();
        int i = 0;

        if (beatBuffer != null) {
            while (i < numBeats) {

                mMotorBeatElements.add(new MotorBeatElement(i, beatBuffer.get(i), MoveType.WAIT));
            }
        } else {
            // TODO some error?
        }
    }

}
