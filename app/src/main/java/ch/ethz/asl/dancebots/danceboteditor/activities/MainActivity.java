package ch.ethz.asl.dancebots.danceboteditor.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.handlers.SoundManager;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorManager;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotMusicFile;


public class MainActivity extends Activity {

    private static final String LOG_TAG = "MAIN_ACTIVITY";

    // On activity result identifier
    private static final int PICK_SONG_REQUEST = 1;
    private static final int PICK_PROJECT_REQUEST = 2;

    private DanceBotEditorManager mProjectManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Project file initialization
        mProjectManager = DanceBotEditorManager.getInstance();
        mProjectManager.setContext(this);

        Button newProjectBtn = (Button) findViewById(R.id.btn_start_media_library);
        newProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMediaLibraryActivity();
            }
        });

        Button loadProjectBtn = (Button) findViewById(R.id.btn_load_project);
        loadProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProjectLibraryActivity();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Start the media library activity. This function is coupled to the button.
     */
    public void startMediaLibraryActivity() {
        Intent mediaLibraryIntent = new Intent(this, MediaLibraryActivity.class);
        startActivityForResult(mediaLibraryIntent, PICK_SONG_REQUEST);
    }

    public void startProjectLibraryActivity() {
        Intent projectLibraryIntent = new Intent(this, ProjectLibraryActivity.class);
        startActivityForResult(projectLibraryIntent, PICK_PROJECT_REQUEST);
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

                String songTitle = data.getStringExtra(MediaLibraryActivity.INTENT_MEDIA_TITLE);
                String songArtist = data.getStringExtra(MediaLibraryActivity.INTENT_MEDIA_ARTIST);
                String songPath = data.getStringExtra(MediaLibraryActivity.INTENT_MEDIA_PATH);
                int songDuration = data.getIntExtra(MediaLibraryActivity.INTENT_MEDIA_DURATION, 0); // Duration in ms
                //String songAlbumArtPath = data.getStringExtra("ALBUM_ART_PATH");

                Log.v(LOG_TAG, "title: " + songTitle);
                Log.v(LOG_TAG, "path: " + songPath);
                Log.v(LOG_TAG, "duration: " + songDuration);
                //Log.v(LOG_TAG, "album art: " + songAlbumArtPath);

                // Selected music file is attached to the current project file
                DanceBotMusicFile dbMusicFile = new DanceBotMusicFile(songTitle, songArtist, songPath, songDuration);
                mProjectManager.attachMusicFile(dbMusicFile);

                // Perform beat extraction in async task
                SoundManager.startDecoding(MainActivity.this, mProjectManager.getDanceBotMusicFile(), 1);

            } else {

                // resultCode == RESULT_CANCEL
                Log.v(LOG_TAG, "onActivityResult() ERROR: resultCode != RESULT_OK");
            }
        } else if (requestCode == PICK_PROJECT_REQUEST) {

            if (resultCode == RESULT_OK) {

                Context context = DanceBotEditorManager.getInstance().getContext();
                Intent editorIntent = new Intent(context, EditorActivity.class);
                editorIntent.putExtra(EditorActivity.INTENT_EDITOR_STATE, EditorActivity.INTENT_EDITOR_LOAD);
                editorIntent.putExtra(EditorActivity.INTENT_EDITOR_LOAD_FILE_NAME, data.getStringExtra(ProjectLibraryActivity.INTENT_PROJECT_NAME));
                editorIntent.putExtra(EditorActivity.INTENT_EDITOR_LOAD_FILE_PATH, data.getStringExtra(ProjectLibraryActivity.INTENT_PROJECT_PATH));
                context.startActivity(editorIntent);

                Log.v(LOG_TAG, "Selected: "
                        + data.getStringExtra(ProjectLibraryActivity.INTENT_PROJECT_NAME)
                        + " located at: " + data.getStringExtra(ProjectLibraryActivity.INTENT_PROJECT_PATH));

            } else {
                Log.v(LOG_TAG, "Error: Could not select project. Request code: " + requestCode);
            }
        }
    }


    /**
     * Load native libraries and functions
     */
    static {
        System.loadLibrary("dancebot_module");
        Log.d(LOG_TAG, "Loaded native library: dancebot_module.");
    }
}