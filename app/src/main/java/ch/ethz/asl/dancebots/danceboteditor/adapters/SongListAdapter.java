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

    private ArrayList<String> songTitles;
    private ArrayList<String> songArtists;
    private ArrayList<String> songDirs;

    private LayoutInflater songElementInflater;

    public SongListAdapter(Context c, ArrayList<String> titles, ArrayList<String> artists, ArrayList<String> dirs){

        songTitles = titles;
        songArtists = artists;
        songDirs = dirs;

        songElementInflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return songTitles.size();
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
        LinearLayout songLayout = (LinearLayout) songElementInflater.inflate
                (R.layout.song_list_details, null);

        //get title and artist views
        TextView songView = (TextView)songLayout.findViewById(R.id.song_title);
        TextView artistView = (TextView)songLayout.findViewById(R.id.song_artist);
        TextView dirView = (TextView)songLayout.findViewById(R.id.song_dir);

        //get song using position
        String currTitle = songTitles.get(position);
        String currArtist = songArtists.get(position);
        String currDir = songDirs.get(position);

        //get title and artist strings
        songView.setText(currTitle);
        artistView.setText(currArtist);
        dirView.setText(currDir);

        return songLayout;

    }
}
