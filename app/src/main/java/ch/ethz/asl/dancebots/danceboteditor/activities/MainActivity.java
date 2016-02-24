package ch.ethz.asl.dancebots.danceboteditor.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.handlers.SoundManager;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorManager;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotMusicFile;


public class MainActivity extends Activity {

    private static final String LOG_TAG = "MAIN_ACTIVITY";

    // On activity result identifier
    private static final int PICK_SONG_REQUEST = 1;

    private DanceBotEditorManager mProjectManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Project file initialization
        mProjectManager = DanceBotEditorManager.getInstance();
        mProjectManager.setContext(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
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
     * @param view calling view
     */
    public void startMediaLibraryActivity(View view) {
/*        Intent editorIntent = new Intent(this, EditorActivity.class);
        startActivity(editorIntent);
*/
        // Start intent that loads the editor view and starts pick-song-activity
        Intent mediaLibraryIntent = new Intent(this, MediaLibraryActivity.class);
        startActivityForResult(mediaLibraryIntent, PICK_SONG_REQUEST);
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
                String songAlbumArtPath = data.getStringExtra("ALBUM_ART_PATH");

                Log.v(LOG_TAG, "title: " + songTitle);
                Log.v(LOG_TAG, "path: " + songPath);
                Log.v(LOG_TAG, "duration: " + songDuration);
                Log.v(LOG_TAG, "album art: " + songAlbumArtPath);

                // Selected music file is attached to the current project file
                DanceBotMusicFile dbMusicFile = new DanceBotMusicFile(songTitle, songArtist, songPath, songDuration);
                mProjectManager.attachMusicFile(dbMusicFile);

                // Perform beat extraction in async task
                SoundManager.startDecoding(MainActivity.this, mProjectManager.getDanceBotMusicFile(), null, 1);

            } else {

                // resultCode == RESULT_CANCEL
                Log.v(LOG_TAG, "onActivityResult() ERROR: resultCode != RESULT_OK");

                finish();
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
