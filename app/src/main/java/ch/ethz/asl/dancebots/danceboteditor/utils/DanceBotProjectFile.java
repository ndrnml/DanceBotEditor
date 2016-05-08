package ch.ethz.asl.dancebots.danceboteditor.utils;

import java.io.Serializable;
import java.util.ArrayList;

import ch.ethz.asl.dancebots.danceboteditor.model.Choreography;
import ch.ethz.asl.dancebots.danceboteditor.model.LedBeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.MotorBeatElement;

/**
 * Created by andrin on 08.05.16. Hello
 */
public class DanceBotProjectFile implements Serializable {

    private final String mProjectName;

    private DanceBotMusicFile mMusicFile;
    private Choreography<LedBeatElement> mLedChoregraphy;
    private Choreography<MotorBeatElement> mMotorChoreography;
    private ArrayList<MotorBeatElement> mMotorElements;
    private ArrayList<LedBeatElement> mLedElements;

    public DanceBotProjectFile(String name) {
        mProjectName = name;
    }

    public void saveProject(
            DanceBotMusicFile musicFile,
            Choreography<LedBeatElement> ledChoreography,
            Choreography<MotorBeatElement> motorChoreography,
            ArrayList<LedBeatElement> ledElements,
            ArrayList<MotorBeatElement> motorElements) {

        mMusicFile = musicFile;
        mLedChoregraphy = ledChoreography;
        mMotorChoreography = motorChoreography;
        mLedElements = ledElements;
        mMotorElements = motorElements;
    }

    public DanceBotMusicFile loadMusicFile() {
        return mMusicFile;
    }

    public Choreography<LedBeatElement> loadLedChoreography() {
        return mLedChoregraphy;
    }

    public Choreography<MotorBeatElement> loadMotorChoreography() {
        return mMotorChoreography;
    }

    public ArrayList<MotorBeatElement> loadMotorElements() {
        return mMotorElements;
    }

    public ArrayList<LedBeatElement> loadLedElements() {
        return mLedElements;
    }

    public String getProjectName() {
        return mProjectName;
    }
}
