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
import android.webkit.MimeTypeMap;
import android.widget.ListView;

import java.util.ArrayList;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.adapters.SongListAdapter;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotError;
import ch.ethz.asl.dancebots.danceboteditor.utils.Decoder;

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

        // Check if media resolver cursor is at a valid position
        if (musicCursor != null && musicCursor.moveToFirst()) {

            // Get audio file meta data columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int durationColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);

            // Add audio information to list
            do {
                Uri path = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musicCursor.getString(idColumn));
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                int thisDuration = musicCursor.getInt(durationColumn);

                // TODO change this. Some mp3 files are not listed!
                if (getMimeType(getRealPathFromURI(this, path)).equals("audio/mpeg")/*Decoder.checkAudioFormat(getRealPathFromURI(this, path)) == DanceBotError.NO_ERROR*/) {
                    mSongListTitle.add(thisTitle);
                    mSongListArtist.add(thisArtist);
                    mSongListPath.add(getRealPathFromURI(this, path));
                    mSongListDuration.add(thisDuration);
                }
            }
            while (musicCursor.moveToNext());
        }
    }

    /**
     *
     * @param url
     * @return
     */
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);

        // TODO type can be null
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

        if (type != null) {
            return type;
        } else {
            // TODO This check is really slow
            if (Decoder.checkAudioFormat(url) == DanceBotError.NO_ERROR) {
                return "audio/mpeg";
            } else {
                return "";
            }
        }
    }

    /**
     * Retrieve absolute path from Uri
     * @param context
     * @param contentUri
     * @return
     */
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
