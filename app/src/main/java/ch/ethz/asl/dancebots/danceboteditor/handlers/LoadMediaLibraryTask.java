package ch.ethz.asl.dancebots.danceboteditor.handlers;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import java.util.ArrayList;

import ch.ethz.asl.dancebots.danceboteditor.adapters.SongListAdapter;
import ch.ethz.asl.dancebots.danceboteditor.model.Song;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotError;
import ch.ethz.asl.dancebots.danceboteditor.utils.Decoder;

/**
 * Created by andrin on 30.12.15.
 */
public class LoadMediaLibraryTask extends AsyncTask<SongListAdapter, Song, Integer> {

    private SongListAdapter mSongListAdapter;

    private ArrayList<Song> mSongList;

    @Override
    protected void onPreExecute() {
        // If device is slow, add spinning wheel here
    }

    @Override
    protected void onPostExecute(Integer integer) {
    }

    /**
     * Update progress performed by background thread
     * @param songs currently processed song
     */
    @Override
    protected void onProgressUpdate(Song... songs) {
        /*
         * Only the main UI thread can modify list adapters. All calls that modify the list adapter
         * must be called in this method
         */
        Song currentSong = songs[0];
        mSongList.add(currentSong);

        // After all data is changed notify the adapter
        mSongListAdapter.notifyDataSetChanged();
    }

    @Override
    protected Integer doInBackground(SongListAdapter... adapters) {

        // Fetch song list adapter
        mSongListAdapter = adapters[0];

        // Load current context
        Context context = mSongListAdapter.getContext();

        // Get references to adapter list
        mSongList = mSongListAdapter.getSongList();

        // Initialize content resolver
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        Uri albumUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor albumCursor = musicResolver.query(albumUri, null, null, null, null);

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
            int albumArtColumn = -1;

            // Check if media resolver cursor is at a valid position
            if (albumCursor != null && albumCursor.moveToFirst()) {
                albumArtColumn = albumCursor.getColumnIndex
                        (MediaStore.Audio.Albums.ALBUM_ART);
            }

            // Add audio information to list
            do {
                // Get URI of external storage
                Uri path = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musicCursor.getString(idColumn));

                // Iterate over all MediaStore columns
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisPath = getRealPathFromURI(context, path);
                int thisDuration = musicCursor.getInt(durationColumn);

                // Iterate over all AlbumsColumns
                String thisAlbumArtPath;
                if (albumArtColumn != -1) {
                    thisAlbumArtPath = musicCursor.getString(albumArtColumn);
                } else {
                    thisAlbumArtPath = null;
                }

                // Check audio format of selected song
                if (correctAudioFormat(thisPath)) {
                    publishProgress(new Song(thisTitle, thisArtist, thisPath, thisDuration, thisAlbumArtPath));
                }
            }
            while (musicCursor.moveToNext());
        }

        return null;
    }

    /**
     * Check if processed song has correct audio format
     * @param thisPath path to the song
     * @return correct format boolean
     */
    private boolean correctAudioFormat(String thisPath) {
        return getMimeType(thisPath).equals("audio/mpeg") || Decoder.checkAudioFormat(thisPath) == DanceBotError.NO_ERROR;
    }

    /**
     * Get the mime type of a file
     * @param url pass the absolute path as String
     * @return the mime type found at the url location
     */
    public static String getMimeType(String url) {

        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);

        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

        if (type != null) {
            return type;
        } else {
            return "";
        }
    }

    /**
     * Retrieve absolute path from URL
     *
     * @param context pass a context for the URL resolver
     * @param contentUri content uri of the file
     * @return absolute path to the file
     */
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Audio.Media.DATA};
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
