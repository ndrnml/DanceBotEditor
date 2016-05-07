package ch.ethz.asl.dancebots.danceboteditor.utils;

import java.nio.IntBuffer;

import ch.ethz.asl.dancebots.danceboteditor.model.BeatElement;

/**
 * Created by andrin on 25.10.15.
 */
public interface BeatExtractor {

    int extract(long handle, IntBuffer buffer, long startSample, long endSample);

    int getNumberOfProcessedSamples(long handle);

    int close(long handle);
}
