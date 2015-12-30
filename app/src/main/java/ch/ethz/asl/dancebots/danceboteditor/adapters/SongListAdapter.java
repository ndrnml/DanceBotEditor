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

/**
 * Created by andrin on 06.07.15.
 */
public class SongListAdapter extends BaseAdapter {

    private Context mContext;

    private ArrayList<String> mSongTitles;
    private ArrayList<String> mSongArtists;
    private ArrayList<String> mSongPaths;
    private ArrayList<Integer> mSongDurations;

    private LayoutInflater mSongElementInflater;

    public SongListAdapter(Context context, ArrayList<String> titles, ArrayList<String> artists, ArrayList<String> paths, ArrayList<Integer> durations) {

        mContext = context;

        mSongTitles = titles;
        mSongArtists = artists;
        mSongPaths = paths;
        mSongDurations = durations;

        mSongElementInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mSongTitles.size();
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

        //map to song layout
        LinearLayout songLayout = (LinearLayout) mSongElementInflater.inflate
                (R.layout.song_list_details, null);

        //get title and artist views
        TextView songView = (TextView)songLayout.findViewById(R.id.song_title);
        TextView artistView = (TextView)songLayout.findViewById(R.id.song_artist);
        TextView dirView = (TextView)songLayout.findViewById(R.id.song_dir);

        // Retrieve song details using position
        String currTitle = mSongTitles.get(position);
        String currArtist = mSongArtists.get(position);
        String currDir = mSongPaths.get(position);

        // Display all relevant properties of this song
        songView.setText(currTitle);
        artistView.setText(currArtist);
        dirView.setText(currDir);

        return songLayout;
    }

    public Context getContext() {
        return mContext;
    }

    public ArrayList<String> getSongListTitle() {
        return mSongTitles;
    }

    public ArrayList<String> getSongListArtist() {
        return mSongArtists;
    }

    public ArrayList<String> getSongListPath() {
        return mSongPaths;
    }

    public ArrayList<Integer> getSongListDuratoin() {
        return mSongDurations;
    }
}
