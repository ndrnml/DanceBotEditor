package ch.ethz.asl.dancebots.danceboteditor.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

import ch.ethz.asl.dancebots.danceboteditor.model.LedType;
import ch.ethz.asl.dancebots.danceboteditor.model.MoveType;

/**
 * Created by andrin on 09.07.15.
 */
public class DanceBotEditorProjectFile {

    public boolean musicFileSelected = false;
    public boolean beatExtractionDone = false;
    public boolean startedEditing = false;

    private DanceBotMusicFile mDBMusicFile;
    private BeatGrid mBeatGrid;
    private ChoreographyManager mChoreoManager;

    /**
     * MENU STRING UNITS
     * Make sure the order is the same as in the enum MotionType
     */
    private String[] mMoveStatesStrings = new String[]{"Geradeaus", "Drehung", "Wippen", "Vor- und Zurück", "Konstant", "Warten"};
    private String[] mLedStatesStrings = new String[]{"Knight Rider", "Zufällig", "Blinken", "SAME_BLINK", "Konstant"};
    private String[] mMoveFrequenciesStrings = new String[]{"1/4", "1/3", "1/2", "2/3", "1"};
    private String[] mLedFrequenciesStrings = new String[]{"1/4", "1/3", "1/2", "2/3", "3/2", "2", "3", "4"};
    private String[] mVelocitiesStrings = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private String[] mLedLightsStrings = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
    private String[] mChoreoLengthsStrings = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

    /**
     * MENU TYPE UNITS
     */
    private MoveType[] mMoveStates = MoveType.values();
    private LedType[] mLedStates = LedType.values();
    private int[] mChoreoLengths = new int[]{1,2,3,4,5,6,7,8,9,10};

    // Singleton instance
    private static DanceBotEditorProjectFile instance = null;

    /**
     * DanceBotEditorProjectFile is treated as Singleton
     */
    protected DanceBotEditorProjectFile() {
        // Delete default constructor
    }

    /**
     * getInstance() of the Singleton
     * @return
     */
    public static DanceBotEditorProjectFile getInstance() {
        if(instance == null) {
            // If it is the first time the object is accessed create instance
            instance = new DanceBotEditorProjectFile();
        }
        return instance;
    }

    /**
     * Initialize meta container for extracted beats
     */
    public void initBeatGrid() {
        mBeatGrid = new BeatGrid();
    }

    /**
     * Based on the detected beats, create a new choreography manager
     */
    public void initChoreography() {
        mChoreoManager = new ChoreographyManager(mBeatGrid);
    }

    /**
     * TODO comment
     * @param dbMusicFile
     */
    public void attachMusicFile(DanceBotMusicFile dbMusicFile) {
        mDBMusicFile = dbMusicFile;
    }

    /**
     *
     * @return
     */
    public DanceBotMusicFile getDanceBotMusicFile() {
        return mDBMusicFile;
    }

    /**
     *
     * @return
     */
    public BeatGrid getBeatGrid() {
        return mBeatGrid;
    }

    /**
     *
     * @return
     */
    public ChoreographyManager getChoreoManager() {
        return mChoreoManager;
    }

    public String[] getMoveStatesStrings() {
        return mMoveStatesStrings;
    }

    public String[] getLedStatesStrings() {
        return mLedStatesStrings;
    }

    public String[] getMoveFrequenciesStrings() {
        return mMoveFrequenciesStrings;
    }

    public String[] getLedFrequenciesStrings() {
        return mLedFrequenciesStrings;
    }

    public String[] getVelocitiesStrings() {
        return mVelocitiesStrings;
    }

    public String[] getLedLightsStrings() {
        return mLedLightsStrings;
    }

    public String[] getChoreoLengthsStrings() {
        return mChoreoLengthsStrings;
    }

    public MoveType[] getMoveStates() {
        return mMoveStates;
    }
    public LedType[] getLedStates() {
        return mLedStates;
    }
    public int getChoreoLengthAtIdx(int idx) {
        return mChoreoLengths[idx];
    }
}
