package ch.ethz.asl.dancebots.danceboteditor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.nio.IntBuffer;


public class EditorActivity extends Activity {

    private static final String LOG_TAG = "EDITOR_ACTIVITY";

    private static final int PICK_SONG_REQUEST = 1;

    private MusicFile mMusicFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // TODO: getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent mediaLibraryIntent = new Intent(this, MediaLibraryActivity.class);
        startActivityForResult(mediaLibraryIntent, PICK_SONG_REQUEST);

        // TODO: initialize media player

        // TODO: initialize native sound handler

    }

    @Override
    protected void onStart() {
        super.onStart();

        // TODO: DO all initialization stuff here?
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to
        if (requestCode == PICK_SONG_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                String songTitle = data.getStringExtra("TITLE_ARTIST");
                String songPath = data.getStringExtra("PATH");

                Log.v(LOG_TAG, "title: " + songTitle);
                Log.v(LOG_TAG, "path: " + songPath);

                // Selected music file
                mMusicFile = new MusicFile(songTitle, songPath);

                // Update music file information
                TextView selectedSongTitle = (TextView) findViewById(R.id.txt_song_title_id);
                selectedSongTitle.setText(songTitle);
                TextView selectedSongFilePath = (TextView) findViewById(R.id.txt_song_path_id);
                selectedSongFilePath.setText(songPath);

                // TODO: Every time a new song gets selected the native sound handler has to be initialized
                int err = NativeSoundHandlerInit(songPath);
                Log.v(LOG_TAG, "error code: " + err);

                // TODO: open and decode mp3 file

                // TODO: extract beats

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
    // Initialize native sound handler from selected music file
    private native int NativeSoundHandlerInit(String musicFilePath);

    static {
        System.loadLibrary("NativeSoundHandler");
    }
}
