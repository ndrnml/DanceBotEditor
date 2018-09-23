package ch.ethz.asl.dancebots.danceboteditor.activities;

import android.Manifest;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ListView;

import java.util.ArrayList;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.adapters.SongListAdapter;
import ch.ethz.asl.dancebots.danceboteditor.handlers.LoadMediaLibraryTask;
import ch.ethz.asl.dancebots.danceboteditor.model.Song;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotError;
import ch.ethz.asl.dancebots.danceboteditor.utils.Decoder;

/**
 * Created by andrin on 06.07.15.
 */
public class MediaLibraryActivity extends ListActivity {

    private static final String LOG_TAG = "MEDIA_LIBRARY_ACTIVITY";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    public static final String INTENT_MEDIA_TITLE = "TITLE";
    public static final String INTENT_MEDIA_ARTIST = "ARTIST";
    public static final String INTENT_MEDIA_PATH = "PATH";
    public static final String INTENT_MEDIA_DURATION = "DURATION";

    private ArrayList<Song> mSongList = new ArrayList<>();

    private void searchMediaLibrary() {
        // Create song list view adapter, initialized with empty arrays
        SongListAdapter mSongListAdpt = new SongListAdapter(this, mSongList);
        setListAdapter(mSongListAdpt);

        // Execute asynchronous audio file loading task
        LoadMediaLibraryTask mediaLibraryTask = new LoadMediaLibraryTask();
        mediaLibraryTask.execute(mSongListAdpt);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set list view
        setContentView(R.layout.activity_media_library);

        // Check permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            searchMediaLibrary();
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // Create return results for intent
        Intent returnIntent = new Intent();
        Song s = mSongList.get(position);
        returnIntent.putExtra(INTENT_MEDIA_TITLE, s.mTitle);
        returnIntent.putExtra(INTENT_MEDIA_ARTIST, s.mArtist);
        returnIntent.putExtra(INTENT_MEDIA_PATH, s.mPath);
        returnIntent.putExtra(INTENT_MEDIA_DURATION, s.mDuration);
        // TODO: enable album image
        //returnIntent.putExtra("ALBUM_ART_PATH", s.mAlbumArtPath);

        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.v(LOG_TAG, "Permission is granted: " + permissions[0]);
                        searchMediaLibrary();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }

    }
}
