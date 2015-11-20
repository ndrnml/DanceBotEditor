package ch.ethz.asl.dancebots.danceboteditor.utils;

import android.annotation.SuppressLint;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by andrin on 20.11.15.
 */
class CompositeSeekBarListener implements SeekBar.OnSeekBarChangeListener {

    // A single instance of PhotoManager, used to implement the singleton pattern
    private static CompositeSeekBarListener sInstance = null;

    private static List<SeekBar.OnSeekBarChangeListener> registeredListeners = new ArrayList<SeekBar.OnSeekBarChangeListener>();

    // A static block that sets class fields
    static {
        // Creates a single static instance of PhotoManager
        sInstance = new CompositeSeekBarListener();
    }

    /**
     * Constructs the work queues and thread pools used to download and decode images.
     */
    private CompositeSeekBarListener() {

    }
    public static void registerListener (SeekBar.OnSeekBarChangeListener listener) {
        registeredListeners.add(listener);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        for(SeekBar.OnSeekBarChangeListener listener : registeredListeners) {
            listener.onProgressChanged(seekBar, progress, fromUser);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        for(SeekBar.OnSeekBarChangeListener listener : registeredListeners) {
            listener.onStartTrackingTouch(seekBar);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        for(SeekBar.OnSeekBarChangeListener listener : registeredListeners) {
            listener.onStopTrackingTouch(seekBar);
        }
    }
}