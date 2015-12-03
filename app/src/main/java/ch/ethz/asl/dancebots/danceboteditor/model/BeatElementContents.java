package ch.ethz.asl.dancebots.danceboteditor.model;

/**
 * Created by andrin on 03.12.15.
 */
public class BeatElementContents {

    private static BeatElementContents sInstance = null;

    // A static block that sets class fields
    static {
        // Creates a single static instance of PhotoManager
        sInstance = new BeatElementContents();
    }

    private BeatElementContents() {
    }

    public BeatElementContents getInstance() {
        return sInstance;
    }
}
