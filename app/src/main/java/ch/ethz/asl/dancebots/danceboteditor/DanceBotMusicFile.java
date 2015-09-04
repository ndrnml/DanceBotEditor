package ch.ethz.asl.dancebots.danceboteditor;

import java.nio.IntBuffer;

/**
 * Created by andrin on 06.07.15.
 */
public class DanceBotMusicFile {

    private String mSongTitle;
    private String mSongArtist;
    private String mSongPath;
    private int mDurationInMiliSeconds;
    private long mNumberOfSamples;
    private int mSampleRate;
    private int mChannels;

    public DanceBotMusicFile() {
        // TODO initialize all stuff
    }

    public DanceBotMusicFile(String songTitle, String songArtist, String songPath, int duration) {

        mSongTitle = songTitle;
        mSongArtist = songArtist;
        mSongPath = songPath;
        mDurationInMiliSeconds = duration;
    }

    /**
     * TODO
     * @param rate
     */
    public void setSampleRate(int rate) {
        mSampleRate = rate;
    }

    public void setTotalNumberOfSamples(long samples) {
        mNumberOfSamples = samples;
    }

    public String getSongPath() {
        return mSongPath;
    }
    public String getSongTitle() {
        return mSongTitle;
    }
    public String getDurationReadable() {
        int seconds = (mDurationInMiliSeconds / 1000) % 60;
        int minutes = (mDurationInMiliSeconds / (1000*60));
        if (seconds < 10) {
            return Integer.toString(minutes) + ":0" + Integer.toString(seconds);
        } else {
            return Integer.toString(minutes) + ":" + Integer.toString(seconds);
        }
    }
}
