package ch.ethz.asl.dancebots.danceboteditor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.model.Song;

/**
 * Created by andrin on 06.07.15.
 */
public class SongListAdapter extends BaseAdapter {

    private Context mContext;

    private ArrayList<Song> mSongList;

    private LayoutInflater mSongElementInflater;

    public SongListAdapter(Context context, ArrayList<Song> songs) {

        // Save current context
        mContext = context;

        // Save song list
        mSongList = songs;

        // Inflate layout
        mSongElementInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mSongList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Create new view from layout
        LinearLayout songLayout = (LinearLayout) mSongElementInflater.inflate
                (R.layout.list_songs, null);

        //get title and artist views
        TextView songView = (TextView)songLayout.findViewById(R.id.song_title);
        TextView artistView = (TextView)songLayout.findViewById(R.id.song_artist);
        TextView dirView = (TextView)songLayout.findViewById(R.id.song_dir);

        // Retrieve song details using position
        Song s = mSongList.get(position);
        String currTitle = s.mTitle;
        String currArtist = s.mArtist;
        String currPah = s.mPath;

        // Display all relevant properties of this song
        songView.setText(currTitle);
        artistView.setText(currArtist);
        dirView.setText(currPah);

        return songLayout;
    }

    public Context getContext() {
        return mContext;
    }

    public ArrayList<Song> getSongList() {
        return mSongList;
    }
}
