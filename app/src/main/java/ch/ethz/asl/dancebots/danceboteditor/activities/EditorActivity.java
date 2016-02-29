package ch.ethz.asl.dancebots.danceboteditor.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import ch.ethz.asl.dancebots.danceboteditor.handlers.SoundManager;
import ch.ethz.asl.dancebots.danceboteditor.listener.MediaPlayerListener;
import ch.ethz.asl.dancebots.danceboteditor.utils.CompositeSeekBarListener;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorManager;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotMediaPlayer;
import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotMusicFile;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotMusicStream;
import ch.ethz.asl.dancebots.danceboteditor.utils.Helper;
import ch.ethz.asl.dancebots.danceboteditor.utils.MusicIntentReceiver;
import ch.ethz.asl.dancebots.danceboteditor.view.HorizontalRecyclerViews;

/**
 * This class handles all dance and sound related tasks and features.
 * EditorActivity will only be started if a valid song has been successfully be decoded and
 * beats have been detected.
 */
public class EditorActivity extends Activity {

    private static final String LOG_TAG = "EDITOR_ACTIVITY";

    private DanceBotEditorManager mProjectManager;
    private HorizontalRecyclerViews mBeatElementViews;
    private DanceBotMusicFile mMusicFile;
    private DanceBotMediaPlayer mMediaPlayer;
    private DanceBotMusicStream mMediaStream;
    private SeekBar mSeekBar;
    private MediaPlayerListener mMediaPlayerListener;
    private MusicIntentReceiver mMusicIntentReceiver;

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

        // Get selected music file
        mMusicFile = mProjectManager.getDanceBotMusicFile();

        // Initialize the beat views, the beat adapters and the dance bot choreography manager
        DanceBotEditorManager.getInstance().initChoreography();

        mSeekBar = (SeekBar) findViewById(R.id.seekbar_media_player);
        mSeekBar.setClickable(true);
        mSeekBar.setMax(mMusicFile.getDurationInMilliSecs());
        // Add CompositeSeekBarListener to the media seek bar for both media player and media stream
        mSeekBar.setOnSeekBarChangeListener(CompositeSeekBarListener.getInstance());

        // Set start time of seek bar text view
        TextView seekBarStartTime = (TextView) findViewById(R.id.seekbar_current_time);
        seekBarStartTime.setText(Helper.songTimeFormat(0));

        // Set total duration of seek bar text view
        TextView seekBarTotalTime = (TextView) findViewById(R.id.seekbar_total_time);
        seekBarTotalTime.setText(Helper.songTimeFormat(mMusicFile.getDurationInMilliSecs()));

        // Create new media player instance, be sure to pass the current activity to resolve
        // all necessary view elements
        mMediaPlayer = new DanceBotMediaPlayer(this);
        mMediaPlayer.setDataSource(mMusicFile);
        mMediaPlayer.setMediaPlayerSeekBar((SeekBar) findViewById(R.id.seekbar_media_player));
        mMediaPlayer.setPlayButton((ImageButton) findViewById(R.id.btn_music_player));

        // Initialize media stream player
        mMediaStream = new DanceBotMusicStream(mMusicFile);
        mMediaStream.setStreamSource(mProjectManager.getChoreoManager());
        mMediaStream.setMediaPlayerSeekBar((SeekBar) findViewById(R.id.seekbar_media_player));
        mMediaStream.setPlayButton((ImageButton) findViewById(R.id.btn_stream_player));

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

        // Register all media players to media player change listener
        mMediaPlayerListener = new MediaPlayerListener(mBeatElementViews, mSeekBar, mMusicFile);
        mMediaPlayerListener.registerMediaPlayer(mMediaPlayer);
        mMediaPlayerListener.registerMediaPlayer(mMediaStream);

        // Set the corresponding event listener to the media player
        mMediaPlayer.setEventListener(mMediaPlayerListener);
        mMediaStream.setEventListener(mMediaPlayerListener);

        mMusicIntentReceiver = new MusicIntentReceiver();

        Log.d(LOG_TAG, "onCreate");
    }


    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.

        Log.d(LOG_TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").

        // Restart/Prepare the media player
        mMediaPlayer.onStart();

        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mMusicIntentReceiver, filter);

        Log.d(LOG_TAG, "onResume");
    }


    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").

        unregisterReceiver(mMusicIntentReceiver);

        // Stop if any media player playback
        mMediaPlayer.onStop();
        mMediaStream.onStop();

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

        // Release media player resources
        mMediaPlayer.cleanUp();

        // Cleanup project manager, when activity will be shutdown
        mProjectManager.cleanUp();

        Log.d(LOG_TAG, "onDestroy");
    }


    @Override
    public void onBackPressed() {
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

            case R.id.editor_action_save:

                // Ask if user really wants to save file
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditorActivity.this);

                alertDialog.setPositiveButton(R.string.txt_yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Start saving the project mp3
                        SoundManager.startEncoding(EditorActivity.this, mProjectManager.getDanceBotMusicFile(), mProjectManager.getChoreoManager());
                    }
                });

                alertDialog.setNegativeButton(R.string.txt_no, null);
                alertDialog.setMessage(R.string.string_alert_save_file_message);
                alertDialog.setTitle(R.string.string_alert_save_file_title);
                alertDialog.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}