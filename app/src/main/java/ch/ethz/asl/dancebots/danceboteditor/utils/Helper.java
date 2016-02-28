package ch.ethz.asl.dancebots.danceboteditor.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

/**
 * Created by andrin on 01.02.16.
 */
public class Helper {

    private static final String LOG_TAG = Helper.class.getSimpleName();

    // DanceBot default directory names
    private static final String MAIN_DIRECTORY = "DanceBot";
    private static final String MUSIC_DIRECTORY = "MusicFiles";
    private static final String PROJECT_DIRECTORY = "ProjectFiles";
    private static final String FILE_EXTENSION_DANCE_BOT = ".proj";
    private static final String FILE_EXTENSION_MP3 = ".mp3";
    private static final String FILE_SUFFIX = "_DANCE";

    /**
     * Get song time hh:ss format from milliseconds
     *
     * @param timeInMilliseconds time in milliseconds
     * @return string format mm:ss
     */
    public static String songTimeFormat(int timeInMilliseconds) {
        return String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds),
                TimeUnit.MILLISECONDS.toSeconds(timeInMilliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds)));
    }


    /**
     * Checks if external storage is available for read and write
     *
     * @return status if external storage is readable and writable
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Get the directory path to the default music storage
     *
     * @return File path to the default music storage location
     */
    public static File getMusicDirectory() {

        // Get the directory for the user's public music directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC), MAIN_DIRECTORY);

        if (!file.mkdirs()) {
            Log.d(LOG_TAG, "Directory not created or already exists");
        }

        return file;
    }

    /**
     * Save music buffer to default DanceBot storage directory
     *
     * @param fileName file name without any extensions
     * @param mp3Buffer byte buffer which will be stored as mp3
     * @return created file or null
     */
    public static File saveToMusicFolder(String fileName, byte[] mp3Buffer) {

        if (isExternalStorageWritable()) {

            // Get music directory
            File musicDir = getMusicDirectory();

            // Save file within this directory
            File file = new File(musicDir, fileName + FILE_SUFFIX + ".mp3");

            // Prepare extension number if file already exists
            int numExtension = 1;

            // Create new file name and try again
            while (file.exists()) {

                file = new File(musicDir, fileName + FILE_SUFFIX + numExtension + ".mp3");
                numExtension++;

                //Log.d(LOG_TAG, "file already existed");
            }

            // Store actual byte data to mp3 file
            try {
                FileOutputStream fos = new FileOutputStream(file.getPath());
                fos.write(mp3Buffer);
                fos.close();

            } catch (java.io.IOException e) {
                Log.d(LOG_TAG, "Exception in file writing", e);
                return null;
            }

            return file;

        } else {

            Log.d(LOG_TAG, "Error: external storage is not writable");
            return null;
        }
    }


}
