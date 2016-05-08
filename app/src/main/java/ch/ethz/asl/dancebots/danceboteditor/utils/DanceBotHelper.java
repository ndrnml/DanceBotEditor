package ch.ethz.asl.dancebots.danceboteditor.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.TimeUnit;

/**
 * Created by andrin on 01.02.16.
 */
public class DanceBotHelper {

    private static final String LOG_TAG = DanceBotHelper.class.getSimpleName();

    // DanceBot default directory names
    private static final String MAIN_DIRECTORY = "DanceBot";
    private static final String PROJECT_FILE_EXTENSION = ".proj";
    private static final String MP3_FILE_EXTENSION = ".mp3";
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

    public static File getProjectDirectory(Context context) {

        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOCUMENTS), MAIN_DIRECTORY);

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
            File file = new File(musicDir, fileName + FILE_SUFFIX + MP3_FILE_EXTENSION);

            // Prepare extension number if file already exists
            int numExtension = 1;

            // Create new file name and try again
            while (file.exists()) {

                file = new File(musicDir, fileName + FILE_SUFFIX + numExtension + MP3_FILE_EXTENSION);
                numExtension++;
                //Log.v(LOG_TAG, "file already existed");
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

    public static File saveDanceBotProject(Context context, DanceBotProjectFile projectFile) {

        if (isExternalStorageWritable()) {

            File projectDirectory = getProjectDirectory(context);

            // Save file within this directory
            File file = new File(projectDirectory, projectFile.getProjectName() + FILE_SUFFIX + PROJECT_FILE_EXTENSION);

            // Store actual byte data to mp3 file
            try {
                FileOutputStream fos = new FileOutputStream(file.getPath());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(projectFile);
                oos.close();
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

    public static DanceBotProjectFile loadDanceBotProject(Context context, String filePath) {

        if (isExternalStorageWritable()) {

            DanceBotProjectFile danceBotProjectFile = null;
            try {
                FileInputStream fin = new FileInputStream(filePath);
                ObjectInputStream ois = new ObjectInputStream(fin);
                danceBotProjectFile = (DanceBotProjectFile) ois.readObject();
                ois.close();
                fin.close();

                return danceBotProjectFile;

            } catch (java.io.IOException e) {
                Log.d(LOG_TAG, "Could not write file: ", e);
                return null;
            } catch (ClassNotFoundException e) {
                Log.d(LOG_TAG, "Class not found: ", e);
                return null;
            }

        } else {

            Log.d(LOG_TAG, "Error: external storage is not writable");
            return null;
        }
    }

    public static boolean isProjectFile(File file) {
        String fileName = file.getName();
        String extension = "." + fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        return extension.equals(PROJECT_FILE_EXTENSION);
    }
}
