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

import ch.ethz.asl.dancebots.danceboteditor.handlers.SoundManager;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorManager;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotMediaPlayer;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotMusicFile;
import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.view.HorizontalRecyclerViews;


public class EditorActivity extends Activity {

    private static final String LOG_TAG = "EDITOR_ACTIVITY";

    private static final int PICK_SONG_REQUEST = 1;

    private DanceBotEditorManager mProjectManager;
    private HorizontalRecyclerViews mBeatElementViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // TODO: getActionBar().setDisplayHomeAsUpEnabled(true);

        // TODO: DO all initialization stuff here?

        // TODO: initialize dance bot project file, beat grid, music files etc...

        // Project file initialization
        mProjectManager = DanceBotEditorManager.getInstance();

        // Global application context is this (EditorActivity)
        mProjectManager.init(this);
        mProjectManager.initSelectionMenus();

        // Initialize and setup recycler beat element views
        mBeatElementViews = new HorizontalRecyclerViews(EditorActivity.this);

        // Store global reference to HorizontalRecyclerViews beatViews
        mProjectManager.setBeatViews(mBeatElementViews);

        // Create new media player instance, be sure to pass the current activity to resolve
        // all necessary view elements
        mProjectManager.attachMediaPlayer(new DanceBotMediaPlayer(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.

        // TODO: DO all initialization stuff here?

        if (mProjectManager.getEditorState() == DanceBotEditorManager.State.START) {

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
        if (mProjectManager.getEditorState() == DanceBotEditorManager.State.NEW) {

            Log.v(LOG_TAG, "resumed EditorActivity with a song loaded");

            // TODO: Test with 1 thread, compare results
            // Perform beat extraction in async task
            SoundManager.startDecoding(this, mProjectManager.getDanceBotMusicFile(), null, 4);

            // TODO remove or change this (THIS WAS ADDED FOR THE LONG CLICK CAPABILITY)
            //registerForContextMenu(mMotorView);

            // Set the editor state to decoding (sensitive phase)
            mProjectManager.setEditorState(DanceBotEditorManager.State.EDITING);
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
        if (true/*mEditorState == DanceBotEditorManager.State.DECODING || mEditorState == DanceBotEditorManager.State.ENCODING*/) {

            // TODO abort/cancel all async tasks and background threads IMPORTANT!!!!
            //AutomaticScrollHandler ah = new AutomaticScrollHandler();
            //ah.stopListening();
        }
    }


    /**
     * Song selection in media library activity is resolved here
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
                Log.v(LOG_TAG, "duration: " + songDuration);

                // Selected music file is attached to the current project file
                DanceBotMusicFile dbMusicFile = new DanceBotMusicFile(songTitle, songArtist, songPath, songDuration);
                mProjectManager.attachMusicFile(dbMusicFile);

                // Notify project file handler that a music file was picked
                mProjectManager.musicFileSelected = true;

                // Update music file information
                // Update Title
                TextView selectedSongTitle = (TextView) findViewById(R.id.id_song_title);
                selectedSongTitle.setText(songTitle);

                // Update Artist view
                TextView selectedSongArtist = (TextView) findViewById(R.id.id_song_artist);
                selectedSongArtist.setText(songArtist);

                // Update Path view
                TextView selectedSongFilePath = (TextView) findViewById(R.id.id_song_path);
                selectedSongFilePath.setText(songPath);

                // Update Duration view
                TextView selectedSongDuration = (TextView) findViewById(R.id.id_song_duration);
                selectedSongDuration.setText(mProjectManager.getDanceBotMusicFile().getDurationReadable());

                // Update Editor activity state
                mProjectManager.setEditorState(DanceBotEditorManager.State.NEW);

                // Open file in media player
                mProjectManager.getMediaPlayer().openMusicFile(dbMusicFile);

            } else {

                // resultCode == RESULT_CANCEL
                Log.v(LOG_TAG, "resultCode != RESULT_OK");

                // TODO finish activity
                mProjectManager.setEditorState(DanceBotEditorManager.State.EDITING);
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
        //askExit(); // TODO uncomment if needed

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

                mProjectManager.setEditorState(DanceBotEditorManager.State.NEW);
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

                //SaveMP3Handler sv = new SaveMP3Handler(EditorActivity.this);
                //sv.execute(mProjectManager);

                SoundManager.startEncoding(this, mProjectManager.getDanceBotMusicFile(), mProjectManager.getChoreoManager());

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
