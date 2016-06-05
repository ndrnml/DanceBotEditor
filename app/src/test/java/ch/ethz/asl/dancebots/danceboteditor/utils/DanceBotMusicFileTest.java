package ch.ethz.asl.dancebots.danceboteditor.utils;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Author: Andrin Jenal
 * Copyright: ETH Zürich
 */
public class DanceBotMusicFileTest {

    private static DanceBotMusicFile musicFile;

    @BeforeClass
    public static void setUp() {

        musicFile = new DanceBotMusicFile(null, null, null, 0);

        int[] beatBuffer = {1200, 11500, 19000, 25000};
        musicFile.setBeatBuffer(beatBuffer);
    }

    @Test
    public void testBeatIntervalTreeInRange() {
        assertEquals(0, musicFile.getBeatFromSample(900));
        assertEquals(1, musicFile.getBeatFromSample(5000));
        assertEquals(2, musicFile.getBeatFromSample(18000));
        assertEquals(3, musicFile.getBeatFromSample(19500));
    }

    @Test
    public void testOutOfBoundBeatIntervalTree() {
        assertEquals(0, musicFile.getBeatFromSample(-1));
        assertEquals(0, musicFile.getBeatFromSample(0));
        assertEquals(3, musicFile.getBeatFromSample(40000));
    }
}
