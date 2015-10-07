package ch.ethz.asl.dancebots.danceboteditor.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.lucasr.twowayview.TwoWayView;

import ch.ethz.asl.dancebots.danceboteditor.adapters.BeatElementAdapter;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorProjectFile;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotMusicFile;
import ch.ethz.asl.dancebots.danceboteditor.model.LedBeatElement;
import ch.ethz.asl.dancebots.danceboteditor.model.MotorBeatElement;
import ch.ethz.asl.dancebots.danceboteditor.R;


public class EditorActivity extends Activity {

    // Possible states of the editor
    public enum State {
        NEW, OPENING, DECODING, EDITING, ENCODING, SAVED
    }

    private static final String LOG_TAG = "EDITOR_ACTIVITY";
    private static final int PICK_SONG_REQUEST = 1;
    private DanceBotEditorProjectFile mProjectFile;
    private State mEditorState = State.NEW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // TODO: getActionBar().setDisplayHomeAsUpEnabled(true);

        // TODO: DO all initialization stuff here?

        // TODO: initialize dance bot project file, beat grid, music files etc...
        // Project file initialization
        mProjectFile = DanceBotEditorProjectFile.getInstance();
        mProjectFile.initBeatGrid();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.

        // TODO: DO all initialization stuff here?

        if (mEditorState == State.NEW) {

            mEditorState = State.OPENING;

            // Start intent that loads the editor view and starts pick-song-activity
            Intent mediaLibraryIntent = new Intent(this, MediaLibraryActivity.class);
            startActivityForResult(mediaLibraryIntent, PICK_SONG_REQUEST);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").

        // When a song is selected, start decoding and beat extraction in the background
        // Skip this process, if the beat extraction has already been performed
        // TODO: Check that the beat extraction was performed for the currently selected song
        if (mProjectFile.musicFileSelected && !mProjectFile.beatExtractionDone) {

            Log.v(LOG_TAG, "resumed EditorActivity with a song loaded");

            // Perform beat extraction in async task
            //SoundFileHandlerAsyncTask soundFileHandler = new SoundFileHandlerAsyncTask(EditorActivity.this);
            //soundFileHandler.execute(mProjectFile);

            /**
             * DUMMY DATA CONSTRUCTION
             */
            int NUM_BEATS = 300;
            int SAMPLE_RATE = 44100;
            int DURATION = 180;
            int TOTAL_SAMPLES = DURATION * SAMPLE_RATE;
            int SPACING = TOTAL_SAMPLES / NUM_BEATS;

            mProjectFile.initChoreography();
            for (int i = 0; i < NUM_BEATS; ++i) {
                mProjectFile.getChoreoManager().mMotorBeatElements.add(new MotorBeatElement(getApplicationContext(), i, SPACING*i, mProjectFile.getMotorStates()));
                mProjectFile.getChoreoManager().mLedBeatElements.add(new LedBeatElement(getApplicationContext(), i, SPACING*i, mProjectFile.getLedStates()));
            }
            /**
             * END DUMMY DATA CONSTRUCTION
             */

            // Create the beat adapters
            BeatElementAdapter motorAdapter = new BeatElementAdapter(EditorActivity.this, mProjectFile.getChoreoManager().mMotorBeatElements);
            BeatElementAdapter ledAdapter = new BeatElementAdapter(EditorActivity.this, mProjectFile.getChoreoManager().mLedBeatElements);

            // Attach motor adapter to the horizontal ListView
            TwoWayView motorView = (TwoWayView) findViewById(R.id.motor_element_list);
            motorView.setAdapter(motorAdapter);

            // Attach led adapter
            TwoWayView ledView = (TwoWayView) findViewById(R.id.led_element_list);
            ledView.setAdapter(ledAdapter);

            // TODO remove or change this
            registerForContextMenu(motorView);

            // Set the editor state to decoding (sensitive phase)
            mEditorState = State.DECODING;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_editor, menu);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").

    }

    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")

        // If the editor is currently decoding, editing or encoding ask user to leave
        if (mEditorState == State.DECODING || mEditorState == State.ENCODING) {

            // TODO abort/cancel all async tasks and background threads
        }
    }


    /**
     * Song selection activity is resolved here
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to
        if (requestCode == PICK_SONG_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                String songTitle = data.getStringExtra("TITLE");
                String songArtist = data.getStringExtra("ARTIST");
                String songPath = data.getStringExtra("PATH");
                int songDuration = data.getIntExtra("DURATION", 0); // Duration in ms

                Log.v(LOG_TAG, "title: " + songTitle);
                Log.v(LOG_TAG, "path: " + songPath);

                // Selected music file is attached to the current project file
                DanceBotMusicFile dbMusicFile = new DanceBotMusicFile(songTitle, songArtist, songPath, songDuration);
                mProjectFile.attachMusicFile(dbMusicFile);

                // Notify project file handler that a music file was picked
                mProjectFile.musicFileSelected = true;

                // Update music file information
                // Update Title
                TextView selectedSongTitle = (TextView) findViewById(R.id.id_song_title);
                selectedSongTitle.setText(songTitle);
                // Update Artist
                TextView selectedSongArtist = (TextView) findViewById(R.id.id_song_artist);
                selectedSongArtist.setText(songArtist);
                // Update Path
                TextView selectedSongFilePath = (TextView) findViewById(R.id.id_song_path);
                selectedSongFilePath.setText(songPath);
                // Update Duration
                TextView selectedSongDuration = (TextView) findViewById(R.id.id_song_duration);
                selectedSongDuration.setText(mProjectFile.getDanceBotMusicFile().getDurationReadable());

            } else {

                // resultCode == RESULT_CANCEL
                Log.v(LOG_TAG, "resultCode != RESULT_OK");

                // TODO finish activity
                mEditorState = State.NEW;
                finish();

            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed(); // This has to be removed, REALLY?

        // TODO: ask user to cancel the current project
        Log.v(LOG_TAG, "Back button is pressed.");

        // Popup alert dialog to confirm users decision
        //askExit();

        finish();
    }


    /**
     * Exit app only if user select yes
     */
    private void askExit() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditorActivity.this);

        alertDialog.setPositiveButton(R.string.txt_yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                mEditorState = State.NEW;
                finish();
            }
        });

        alertDialog.setNegativeButton(R.string.txt_no, null);
        alertDialog.setMessage(R.string.alert_ask_exit_txt);
        alertDialog.setTitle(R.string.app_name);
        alertDialog.show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // TODO: Handle all menu actions
        switch (id) {

            case R.id.editor_action_open:

                // TODO: move this to the ask open dialog
                // TODO: This should only be possible if the user want's it -> State == NEW
                Intent mediaLibraryIntent = new Intent(this, MediaLibraryActivity.class);
                startActivityForResult(mediaLibraryIntent, PICK_SONG_REQUEST);

                return true;

            case R.id.editor_action_save:

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     *
     * LOAD NATIVE LIBRARIES AND FUNCTIONS
     */

    static {
        System.loadLibrary("mpg123");
        System.loadLibrary("NativeSoundHandler");
    }
}
