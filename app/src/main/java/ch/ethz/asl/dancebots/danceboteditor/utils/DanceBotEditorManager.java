package ch.ethz.asl.dancebots.danceboteditor.utils;

import android.content.Context;

import ch.ethz.asl.dancebots.danceboteditor.handlers.AutomaticScrollHandler;
import ch.ethz.asl.dancebots.danceboteditor.model.BeatElementContents;
import ch.ethz.asl.dancebots.danceboteditor.ui.FloatSelectionMenu;
import ch.ethz.asl.dancebots.danceboteditor.ui.IntegerSelectionMenu;
import ch.ethz.asl.dancebots.danceboteditor.ui.LedTypeSelectionMenu;
import ch.ethz.asl.dancebots.danceboteditor.ui.MotorTypeSelectionMenu;
import ch.ethz.asl.dancebots.danceboteditor.view.HorizontalRecyclerViews;

/**
 * Created by andrin on 09.07.15.
 */
public class DanceBotEditorManager {

    // Possible states of the editor
    public enum State {
        START, NEW, EDITING
    }

    public boolean musicFileSelected = false;
    public boolean beatExtractionDone = false;
    public boolean startedEditing = false;

    // Singleton instance
    private static DanceBotEditorManager instance = null;

    private Context mContext;
    private State mEditorState;
    private DanceBotMusicFile mMusicFile;
    private ChoreographyManager mChoreoManager;
    private DanceBotMediaPlayer mMediaPlayer;
    private HorizontalRecyclerViews mBeatViews;
    private AutomaticScrollHandler mAutomaticScrollHandler;

    /**
     * TODO
     * New Menu implementation
     */
    private LedTypeSelectionMenu mLedTypeMenu;
    private MotorTypeSelectionMenu mMotorTypeMenu;
    private FloatSelectionMenu mLedFrequencyMenu;
    private FloatSelectionMenu mMotorFrequencyMenu;
    private IntegerSelectionMenu mVelocityMenu;
    private IntegerSelectionMenu mChoreoLengthMenu;

    /**
     * DanceBotEditorManager is treated as Singleton
     */
    protected DanceBotEditorManager() {
        // Delete default constructor
    }

    /**
     * getInstance() of the Singleton
     * @return
     */
    public static DanceBotEditorManager getInstance() {
        if(instance == null) {
            // If it is the first time the object is accessed create instance
            instance = new DanceBotEditorManager();
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
        mLedFrequencyMenu = new FloatSelectionMenu(BeatElementContents.getLedFrequencies());
        mMotorFrequencyMenu = new FloatSelectionMenu(BeatElementContents.getMotorFrequencies());

        // Init led type menu
        mLedTypeMenu = new LedTypeSelectionMenu(BeatElementContents.getLedTypes());

        // Init motor type menu
        mMotorTypeMenu = new MotorTypeSelectionMenu(BeatElementContents.getMotorTypes());

        // Init velocity menu
        mVelocityMenu = new IntegerSelectionMenu(BeatElementContents.getVelocityValues());

        // Init choreo length menu
        mChoreoLengthMenu = new IntegerSelectionMenu(BeatElementContents.getLengthValues());
    }


    public void initAutomaticScrollHandler() {
        mAutomaticScrollHandler = new AutomaticScrollHandler(mBeatViews, mMediaPlayer);
    }
    public void notifyAutomaticScrollHandler() {
        mAutomaticScrollHandler.startListening();
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
    public AutomaticScrollHandler getAutomaticScrollHandler() {
        return mAutomaticScrollHandler;
    }
}
