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
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotError;
import ch.ethz.asl.dancebots.danceboteditor.utils.Decoder;

/**
 * Created by andrin on 06.07.15.
 */
public class MediaLibraryActivity extends ListActivity {

    private static final String LOG_TAG = "MEDIA_LIBRARY_ACTIVITY";

    private Runnable mListUpdater;

    private ArrayList<String> mSongListTitle = new ArrayList<>();
    private ArrayList<String> mSongListArtist = new ArrayList<>();
    private ArrayList<String> mSongListPath = new ArrayList<>();
    private ArrayList<Integer> mSongListDuration = new ArrayList<>();

    private SongListAdapter mSongListAdpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_media_library);

        mSongListAdpt = new SongListAdapter(this, mSongListTitle, mSongListArtist, mSongListPath, mSongListDuration);
        setListAdapter(mSongListAdpt);

        mListUpdater = new Runnable() {
            @Override
            public void run() {
                mSongListAdpt.notifyDataSetChanged();
            }
        };

        LoadMediaLibraryTask mediaLibraryTask = new LoadMediaLibraryTask();
        mediaLibraryTask.execute(mSongListAdpt);

        // Load song list immediately after activity started
        // TODO: handle large music libraries asynchronously
        //getSongList();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // Create return results for intent
        Intent returnIntent = new Intent();
        returnIntent.putExtra("TITLE", mSongListTitle.get(position));
        returnIntent.putExtra("ARTIST", mSongListArtist.get(position));
        returnIntent.putExtra("PATH", mSongListPath.get(position));
        returnIntent.putExtra("DURATION", mSongListDuration.get(position));

        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
