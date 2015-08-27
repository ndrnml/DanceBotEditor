package ch.ethz.asl.dancebots.danceboteditor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class EditorActivity extends Activity {

    private static final String LOG_TAG = "EDITOR_ACTIVITY";

    private static final int PICK_SONG_REQUEST = 1;

    private DanceBotEditorProjectFile mProjectFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // TODO: getActionBar().setDisplayHomeAsUpEnabled(true);

        // Start intent that loads the editor view and starts pick-song-activity
        Intent mediaLibraryIntent = new Intent(this, MediaLibraryActivity.class);
        startActivityForResult(mediaLibraryIntent, PICK_SONG_REQUEST);

        // TODO: initialize media player

        // TODO: initialize dance bot project file, beat grid, music files etc...
        // Project file initialization
        mProjectFile = new DanceBotEditorProjectFile();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.

        // TODO: DO all initialization stuff here?
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
            SoundFileHandlerAsyncTask soundFileHandler = new SoundFileHandlerAsyncTask(EditorActivity.this);
            soundFileHandler.execute(mProjectFile);
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

                // Notify project file handler that a music file was picked
                mProjectFile.musicFileSelected = true;

                String songTitle = data.getStringExtra("TITLE_ARTIST");
                String songPath = data.getStringExtra("PATH");

                Log.v(LOG_TAG, "title: " + songTitle);
                Log.v(LOG_TAG, "path: " + songPath);

                // Selected music file is attached to the current project file
                DanceBotMusicFile dbMusicFile = new DanceBotMusicFile(songTitle, songPath);
                mProjectFile.attachMusicFile(dbMusicFile);

                // Update music file information
                TextView selectedSongTitle = (TextView) findViewById(R.id.txt_song_title_id);
                selectedSongTitle.setText(songTitle);
                TextView selectedSongFilePath = (TextView) findViewById(R.id.txt_song_path_id);
                selectedSongFilePath.setText(songPath);

            } else {

                // TODO
                // resultCode == RESULT_CANCEL
                Log.v(LOG_TAG, "resultCode != RESULT_OK");
            }
        }
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
