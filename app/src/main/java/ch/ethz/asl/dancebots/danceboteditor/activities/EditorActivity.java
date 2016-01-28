package ch.ethz.asl.dancebots.danceboteditor.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import ch.ethz.asl.dancebots.danceboteditor.handlers.SoundManager;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorManager;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotMediaPlayer;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotMusicFile;
import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotMusicStream;
import ch.ethz.asl.dancebots.danceboteditor.view.HorizontalRecyclerViews;


public class EditorActivity extends Activity {

    private static final String LOG_TAG = "EDITOR_ACTIVITY";

    // On activity result identifier
    private static final int PICK_SONG_REQUEST = 1;

    private DanceBotEditorManager mProjectManager;
    private HorizontalRecyclerViews mBeatElementViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

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
        // TODO: Shouldn't the player only be created when a valid music file is present?
        mProjectManager.attachMediaPlayer(new DanceBotMediaPlayer(this));

        // TODO: This is for testing purposes only
        Button streamBnt = (Button) findViewById(R.id.btn_stream);
        streamBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProjectManager.getDanceBotMusicFile().getSongPath() != null) {
                    DanceBotMusicStream stream = new DanceBotMusicStream(mProjectManager.getDanceBotMusicFile().getSongPath());
                    stream.play();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.

        if (mProjectManager.getDanceBotMusicFile() == null) {
            // Start intent that loads the editor view and starts pick-song-activity
            Intent mediaLibraryIntent = new Intent(this, MediaLibraryActivity.class);
            startActivityForResult(mediaLibraryIntent, PICK_SONG_REQUEST);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").

        Log.d(LOG_TAG, "onResume");
    }


    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").

        Log.d(LOG_TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")

        Log.d(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is going to be destroyed

        // TODO: Clean up all files
        mProjectManager.cleanUp();

        Log.d(LOG_TAG, "onDestroy");
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
                selectedSongDuration.setText(mProjectManager.getDanceBotMusicFile().getDurationReadable()); // TODO change this line

                // TODO enable album cover images
                /*if (songAlbumArtPath != null) {
                    ImageView selectedSongAlbumArt = (ImageView) findViewById(R.id.song_album_art_image);
                    selectedSongAlbumArt.setImageDrawable(Drawable.createFromPath(songAlbumArtPath));
                }*/

                // Open file in media player
                mProjectManager.getMediaPlayer().openMusicFile(dbMusicFile);

                // TODO: Test with 1 thread, compare results
                // Perform beat extraction in async task
                SoundManager.startDecoding(this, mProjectManager.getDanceBotMusicFile(), null, 1);

            } else {

                // resultCode == RESULT_CANCEL
                Log.v(LOG_TAG, "onActivityResult() ERROR: resultCode != RESULT_OK");

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
        askExit();
    }


    /**
     * Exit app only if user select yes
     */
    private void askExit() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditorActivity.this);

        alertDialog.setPositiveButton(R.string.txt_yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Finish EditorActivity
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

        // Handle all menu options here
        switch (id) {

            /*case R.id.editor_action_open:

                // TODO: move this to the ask open dialog
                // TODO: This should only be possible if the user want's it -> State == NEW
                Intent mediaLibraryIntent = new Intent(this, MediaLibraryActivity.class);
                startActivityForResult(mediaLibraryIntent, PICK_SONG_REQUEST);

                return true;
            */

            case R.id.editor_action_save:

                // Start saving the project mp3
                SoundManager.startEncoding(this.getApplicationContext(), mProjectManager.getDanceBotMusicFile(), mProjectManager.getChoreoManager());

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
