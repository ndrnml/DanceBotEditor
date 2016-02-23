package ch.ethz.asl.dancebots.danceboteditor.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import ch.ethz.asl.dancebots.danceboteditor.handlers.SoundManager;
import ch.ethz.asl.dancebots.danceboteditor.utils.CompositeSeekBarListener;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorManager;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotMediaPlayer;
import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotMusicStream;
import ch.ethz.asl.dancebots.danceboteditor.view.HorizontalRecyclerViews;

/**
 * This class handles all dance and sound related tasks and features.
 * EditorActivity will only be started if a valid song has been successfully be decoded and
 * beats have been detected.
 */
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
        mProjectManager.setContext(this);
        mProjectManager.initSelectionMenus();

        // Initialize and setup recycler beat element views
        mBeatElementViews = new HorizontalRecyclerViews(EditorActivity.this);

        // Store global reference to HorizontalRecyclerViews beatViews
        mProjectManager.setBeatViews(mBeatElementViews);

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekbar_media_player);

        // Create new media player instance, be sure to pass the current activity to resolve
        // all necessary view elements
        DanceBotMediaPlayer mediaPlayer = new DanceBotMediaPlayer(this);
        mediaPlayer.setMediaPlayerSeekBar(
                (SeekBar) findViewById(R.id.seekbar_media_player),
                (TextView) findViewById(R.id.seekbar_current_time),
                (TextView) findViewById(R.id.seekbar_total_time));

        CompositeSeekBarListener.registerListener(mediaPlayer);

        // Set media player
        mProjectManager.setMediaPlayer(mediaPlayer);

        // Open file in media player
        mProjectManager.getMediaPlayer().openMusicFile(mProjectManager.getDanceBotMusicFile());

        // Update music file information
        // Update Title
        TextView selectedSongTitle = (TextView) findViewById(R.id.id_song_title);
        selectedSongTitle.setText(mProjectManager.getDanceBotMusicFile().getSongTitle());

        // Update Artist view
        TextView selectedSongArtist = (TextView) findViewById(R.id.id_song_artist);
        selectedSongArtist.setText(mProjectManager.getDanceBotMusicFile().getSongArtist());

        // Update Path view
        TextView selectedSongFilePath = (TextView) findViewById(R.id.id_song_path);
        selectedSongFilePath.setText(mProjectManager.getDanceBotMusicFile().getSongPath());

        // Update Duration view
        TextView selectedSongDuration = (TextView) findViewById(R.id.id_song_duration);
        selectedSongDuration.setText(mProjectManager.getDanceBotMusicFile().getDurationReadable());

        // TODO enable album cover images
        /*if (songAlbumArtPath != null) {
            ImageView selectedSongAlbumArt = (ImageView) findViewById(R.id.song_album_art_image);
            selectedSongAlbumArt.setImageDrawable(Drawable.createFromPath(songAlbumArtPath));
        }*/

        // Initialize the beat views, the beat adapters and the dance bot choreography manager
        DanceBotEditorManager.getInstance().initChoreography();

        // TODO: make this more flexible. Maybe a scroll handler for views and one for seek bars
        // TODO: AND don't call it in DanceBotEditorManager
        DanceBotEditorManager.getInstance().initAutomaticScrollHandler();

        // TODO: This is for testing purposes only
        final DanceBotMusicStream streamPlayer = new DanceBotMusicStream(mProjectManager.getDanceBotMusicFile());
        streamPlayer.setDataSource(mProjectManager.getChoreoManager());
        streamPlayer.setMediaPlayerSeekBar(
                (SeekBar) findViewById(R.id.seekbar_media_player),
                (TextView) findViewById(R.id.seekbar_current_time),
                (TextView) findViewById(R.id.seekbar_total_time));

        CompositeSeekBarListener.registerListener(streamPlayer);

        // Add CompositeSeekBarListener to the media seek bar for both media player and media stream
        seekBar.setOnSeekBarChangeListener(CompositeSeekBarListener.getInstance());

        Button streamBnt = (Button) findViewById(R.id.btn_stream);
        streamBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (streamPlayer.isPlaying()) {
                    streamPlayer.pause();
                } else {
                    streamPlayer.play();
                }
            }
        });
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

        // TODO: clean up project files

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
