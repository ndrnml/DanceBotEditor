package ch.ethz.asl.dancebots.danceboteditor.activities;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

    private ArrayList<Song> mSongList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set list view
        setContentView(R.layout.activity_media_library);

        // Create song list view adapter, initialized with empty arrays
        SongListAdapter mSongListAdpt = new SongListAdapter(this, mSongList);
        setListAdapter(mSongListAdpt);

        // Execute asynchronous audio file loading task
        LoadMediaLibraryTask mediaLibraryTask = new LoadMediaLibraryTask();
        mediaLibraryTask.execute(mSongListAdpt);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // Create return results for intent
        Intent returnIntent = new Intent();
        Song s = mSongList.get(position);
        returnIntent.putExtra("TITLE", s.mTitle);
        returnIntent.putExtra("ARTIST", s.mArtist);
        returnIntent.putExtra("PATH", s.mPath);
        returnIntent.putExtra("DURATION", s.mDuration);
        // TODO: enable album image
        //returnIntent.putExtra("ALBUM_ART_PATH", s.mAlbumArtPath);

        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
