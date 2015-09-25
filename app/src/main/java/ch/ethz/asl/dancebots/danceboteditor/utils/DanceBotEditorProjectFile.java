package ch.ethz.asl.dancebots.danceboteditor.utils;

/**
 * Created by andrin on 09.07.15.
 */
public class DanceBotEditorProjectFile {

    public boolean musicFileSelected = false;
    public boolean beatExtractionDone = false;
    public boolean startedEditing = false;

    /**
     * Menu units
     * Make sure the order is the same as in the enum MotionType
     */
    private String[] mMoveStates = new String[]{"Geradeaus", "Drehung", "Wippen", "Vor- und Zurück", "Konstant", "Warten"};
    private String[] mLedStates = new String[]{"Knight Rider", "Zufällig", "Blinken", "SAME_BLINK", "Konstant"};

    private String[] mMoveFrequencies = new String[]{"1/4", "1/3", "1/2", "2/3", "1"};
    private String[] mLedFrequencies = new String[]{"1/4", "1/3", "1/2", "2/3", "3/2", "2", "3", "4"};
    private String[] mVelocities = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private String[] mLights = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
    private String[] mLengths = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

    private DanceBotMusicFile mDBMusicFile;
    private BeatGrid mBeatGrid;
    private ChoreographyManager mChoreoManager;

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

    public String[] getMoveStates() {
        return mMoveStates;
    }

    public String[] getLedStates() {
        return mLedStates;
    }

    public String[] getMoveFrequencies() {
        return mMoveFrequencies;
    }

    public String[] getLedFrequencies() {
        return mLedFrequencies;
    }

    public String[] getVelocities() {
        return mVelocities;
    }

    public String[] getLights() {
        return mLights;
    }

    public String[] getChoreoLengths() {
        return mLengths;
    }
}
