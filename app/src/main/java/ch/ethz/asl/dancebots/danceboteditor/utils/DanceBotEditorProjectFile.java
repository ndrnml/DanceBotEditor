package ch.ethz.asl.dancebots.danceboteditor.utils;

import android.content.Context;
import android.util.Pair;

import java.util.ArrayList;
import java.util.EnumSet;

import ch.ethz.asl.dancebots.danceboteditor.model.LedType;
import ch.ethz.asl.dancebots.danceboteditor.model.MotorType;
import ch.ethz.asl.dancebots.danceboteditor.ui.FloatSelectionMenu;
import ch.ethz.asl.dancebots.danceboteditor.ui.IntegerSelectionMenu;
import ch.ethz.asl.dancebots.danceboteditor.ui.LedTypeSelectionMenu;
import ch.ethz.asl.dancebots.danceboteditor.ui.MotorTypeSelectionMenu;
import ch.ethz.asl.dancebots.danceboteditor.view.HorizontalRecyclerViews;

/**
 * Created by andrin on 09.07.15.
 */
public class DanceBotEditorProjectFile {

    // Possible states of the editor
    public enum State {
        START, NEW, EDITING
    }

    public boolean musicFileSelected = false;
    public boolean beatExtractionDone = false;
    public boolean startedEditing = false;

    // Singleton instance
    private static DanceBotEditorProjectFile instance = null;

    private Context mContext;
    private State mEditorState;
    private DanceBotMusicFile mMusicFile;
    private ChoreographyManager mChoreoManager;
    private DanceBotMediaPlayer mMediaPlayer;
    private HorizontalRecyclerViews mBeatViews;

    /**
     * TODO
     * New Menu implementation
     */
    private final int VELOCITY_MIN = 10;
    private final int VELOCITY_MAX = 100;
    private final int VELOCITY_STEP = 10;
    private final int CHOREO_LENGTH_MIN = 1;
    private final int CHOREO_LENGTH_MAX = 50;
    private final int CHOREO_LENGTH_STEP = 1;
    private LedTypeSelectionMenu mLedTypeMenu;
    private MotorTypeSelectionMenu mMotorTypeMenu;
    private FloatSelectionMenu mLedFrequencyMenu;
    private FloatSelectionMenu mMotorFrequencyMenu;
    private IntegerSelectionMenu mVelocityMenu;
    private IntegerSelectionMenu mChoreoLengthMenu;

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
     * First init. This one is really important. It sets the application context, which is needed
     * for a few objects, since some properties are constant encoded.
     * This is not a pretty solution, but I have no clue how to make it better. I know, I am not a
     * good programmer yet, but maybe once, I will understand the concepts.
     */
    public void init(Context context) {
        setContext(context);
    }

    /**
     * Init menus
     */
    public void initSelectionMenus() {

        // Init frequency menus
        mLedFrequencyMenu = new FloatSelectionMenu(generateLedFrequencies());
        mMotorFrequencyMenu = new FloatSelectionMenu(generateMotorFrequencies());

        // Init led type menu
        mLedTypeMenu = new LedTypeSelectionMenu(new ArrayList<>(EnumSet.allOf(LedType.class)));

        // Init motor type menu
        mMotorTypeMenu = new MotorTypeSelectionMenu(new ArrayList<>(EnumSet.allOf(MotorType.class)));

        // Init velocity menu
        mVelocityMenu = new IntegerSelectionMenu(generateIntegersInRange(VELOCITY_MIN, VELOCITY_MAX, VELOCITY_STEP));

        // Init choreo length menu
        mChoreoLengthMenu = new IntegerSelectionMenu(generateIntegersInRange(CHOREO_LENGTH_MIN, CHOREO_LENGTH_MAX, CHOREO_LENGTH_STEP));
    }


    /**
     * Based on the detected beats, create a new choreography manager
     */
    public void initChoreography() {
        mChoreoManager = new ChoreographyManager(mContext, mBeatViews, mMusicFile);
    }

    /**
     * TODO comment
     * @param dbMusicFile
     */
    public void attachMusicFile(DanceBotMusicFile dbMusicFile) {
        mMusicFile = dbMusicFile;
    }

    public void attachMediaPlayer(DanceBotMediaPlayer mediaPlayer) {
        mMediaPlayer = mediaPlayer;
    }

    /**
     * Generate led frequencies
     */
    private ArrayList<Pair<Integer,Integer>> generateLedFrequencies() {

        // "1/4", "1/3", "1/2", "2/3", "1"
        ArrayList<Pair<Integer, Integer>> frequencies = new ArrayList<>();
        frequencies.add(new Pair<>(1, 4));
        frequencies.add(new Pair<>(1, 3));
        frequencies.add(new Pair<>(2, 3));
        frequencies.add(new Pair<>(1, 1));

        return frequencies;
    }
    /**
     * Generate led frequencies
     */
    private ArrayList<Pair<Integer,Integer>> generateMotorFrequencies() {

        // "1/4", "1/3", "1/2", "2/3", "3/2", "2", "3", "4"
        ArrayList<Pair<Integer, Integer>> frequencies = new ArrayList<>();
        frequencies.add(new Pair<>(1, 4));
        frequencies.add(new Pair<>(1, 3));
        frequencies.add(new Pair<>(1, 2));
        frequencies.add(new Pair<>(2, 3));
        frequencies.add(new Pair<>(3, 2));
        frequencies.add(new Pair<>(2, 1));
        frequencies.add(new Pair<>(3, 1));
        frequencies.add(new Pair<>(4, 1));

        return frequencies;
    }

    private ArrayList<Integer> generateIntegersInRange(int min, int max, int step) {

        ArrayList<Integer> collection = new ArrayList<>();

        int number_of_elements = (max / step);
        for (int i = 0; i < number_of_elements; ++i) {
            collection.add(min + (i * step));
        }

        return collection;
    }

    ///////////
    // SETTERS
    ///////////
    // TODO
    // TODO: make beatgrid, choreography setters
    // TODO
    public void setBeatViews(HorizontalRecyclerViews beatViews) {
        mBeatViews = beatViews;
    }
    public void setEditorState(State s) {
        mEditorState = s;
    }
    public void setContext(Context c) {
        mContext = c;
    }
    ///////////
    // GETTERS
    ///////////
    public Context getContext() {
        return mContext;
    }
    public DanceBotMusicFile getDanceBotMusicFile() {
        return mMusicFile;
    }
    public ChoreographyManager getChoreoManager() {
        return mChoreoManager;
    }
    public State getEditorState() {
        return mEditorState;
    }
    public LedTypeSelectionMenu getLedTypeMenu() {
        return mLedTypeMenu;
    }
    public MotorTypeSelectionMenu getMotorTypeMneu() {
        return mMotorTypeMenu;
    }
    public FloatSelectionMenu getLedFrequencyMenu() {
        return mLedFrequencyMenu;
    }

    public FloatSelectionMenu getMotorFrequencyMenu() {
        return mMotorFrequencyMenu;
    }

    public IntegerSelectionMenu getVelocityMenu() {
        return mVelocityMenu;
    }

    public IntegerSelectionMenu getChoreoLengthMenu() {
        return mChoreoLengthMenu;
    }
    public DanceBotMediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }
}
