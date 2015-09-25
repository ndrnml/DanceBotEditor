package ch.ethz.asl.dancebots.danceboteditor.activities;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.adapters.SongListAdapter;

/**
 * Created by andrin on 06.07.15.
 */
public class MediaLibraryActivity extends ListActivity {

    private ArrayList<String> mSongListTitle = new ArrayList<>();
    private ArrayList<String> mSongListArtist = new ArrayList<>();
    private ArrayList<String> mSongListPath = new ArrayList<>();
    private ArrayList<Integer> mSongListDuration = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_media_library);

        // Load song list immediately after activity started
        // TODO: handle large music libraries asynchronously
        getSongList();

        SongListAdapter songLstAdpt = new SongListAdapter(this, mSongListTitle, mSongListArtist, mSongListPath);
        setListAdapter(songLstAdpt);

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

    private void getSongList() {

        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if(musicCursor !=null && musicCursor.moveToFirst()){

            // Get audio file meta data columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int durationColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);
            // int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.SIZE);

            // Add audio information to list
            do {
                //long thisId = musicCursor.getLong(idColumn);
                Uri path = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musicCursor.getString(idColumn));
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                int thisDuration = musicCursor.getInt(durationColumn);

                mSongListTitle.add(thisTitle);
                mSongListArtist.add(thisArtist);
                mSongListPath.add(getRealPathFromURI(this, path));
                mSongListDuration.add(thisDuration);
            }
            while (musicCursor.moveToNext());
        }
    }

    // retrieve absolute path from Uri
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Audio.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
