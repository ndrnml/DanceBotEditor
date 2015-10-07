package ch.ethz.asl.dancebots.danceboteditor.utils;

import android.graphics.Color;

import ch.ethz.asl.dancebots.danceboteditor.model.LedType;
import ch.ethz.asl.dancebots.danceboteditor.model.MotorType;

/**
 * Created by andrin on 09.07.15.
 */
public class DanceBotEditorProjectFile {

    public boolean musicFileSelected = false;
    public boolean beatExtractionDone = false;
    public boolean startedEditing = false;

    // Singleton instance
    private static DanceBotEditorProjectFile instance = null;

    private DanceBotMusicFile mDBMusicFile;
    private BeatGrid mBeatGrid;
    private ChoreographyManager mChoreoManager;

    /**
     * MENU STRING UNITS
     * Make sure the order is the same as in the enum MotionType
     */
    private String[] mMotorStatesStrings = new String[]{"Geradeaus", "Drehung", "Wippen", "Vor- und Zurück", "Konstant", "Warten"};
    private String[] mLedStatesStrings = new String[]{"Knight Rider", "Zufällig", "Blinken", "SAME_BLINK", "Konstant"};
    private String[] mMotorFrequenciesStrings = new String[]{"1/4", "1/3", "1/2", "2/3", "1"};
    private String[] mLedFrequenciesStrings = new String[]{"1/4", "1/3", "1/2", "2/3", "3/2", "2", "3", "4"};
    private String[] mVelocitiesStrings = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private String[] mChoreoLengthsStrings = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

    /**
     * MENU TYPE UNITS
     */
    private MotorType[] mMotorStates = MotorType.values(); // Get ENUM values
    private LedType[] mLedStates = LedType.values(); // Get ENUM values
    private int[] mChoreoLengths = new int[]{1,2,3,4,5,6,7,8,9,10};
    private int[] mMotorColors = new int[]{Color.RED, Color.RED, Color.RED, Color.RED, Color.RED, Color.RED};
    private int[] mLedColors = new int[]{Color.RED, Color.RED, Color.RED, Color.RED, Color.RED};

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

    public String[] getMotorStatesStrings() {
        return mMotorStatesStrings;
    }

    public String[] getLedStatesStrings() {
        return mLedStatesStrings;
    }

    public String[] getMotorFrequenciesStrings() {
        return mMotorFrequenciesStrings;
    }

    public String[] getLedFrequenciesStrings() {
        return mLedFrequenciesStrings;
    }

    public String[] getVelocitiesStrings() {
        return mVelocitiesStrings;
    }

    public String[] getChoreoLengthsStrings() {
        return mChoreoLengthsStrings;
    }

    public MotorType[] getMotorStates() {
        return mMotorStates;
    }
    public LedType[] getLedStates() {
        return mLedStates;
    }
    public int getChoreoLengthAtIdx(int idx) {
        return mChoreoLengths[idx];
    }
}
