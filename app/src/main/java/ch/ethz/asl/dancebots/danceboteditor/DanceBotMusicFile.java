package ch.ethz.asl.dancebots.danceboteditor;

import java.nio.IntBuffer;

/**
 * Created by andrin on 06.07.15.
 */
public class DanceBotMusicFile {

    private String mSongTitle;
    private String mSongPath;

    private int mLengthInSeconds;
    private long mNumberOfSamples;
    private int mBitRate;
    private int mChannels;

    public DanceBotMusicFile() {
        // TODO initialize all stuff
    }

    public DanceBotMusicFile(String songTitle, String songPath) {

        mSongTitle = songTitle;
        mSongPath = songPath;
    }

    public String getSongPath() {
        return mSongPath;
    }
    public String getSongTitle() {
        return mSongTitle;
    }

}
