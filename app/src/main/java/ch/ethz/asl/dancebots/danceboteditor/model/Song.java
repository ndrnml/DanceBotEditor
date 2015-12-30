package ch.ethz.asl.dancebots.danceboteditor.model;

/**
 * Created by andrin on 30.12.15.
 */
public class Song {

    public String mTitle;
    public String mArtist;
    public String mPath;
    public int mDuration;

    public Song(String title, String artist, String path, int duration) {
        mTitle = title;
        mArtist = artist;
        mPath = path;
        mDuration = duration;
    }
}
