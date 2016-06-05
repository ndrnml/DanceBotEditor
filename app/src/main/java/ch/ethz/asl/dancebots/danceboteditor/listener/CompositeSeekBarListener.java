package ch.ethz.asl.dancebots.danceboteditor.listener;

import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrin on 20.11.15.
 */
public class CompositeSeekBarListener implements SeekBar.OnSeekBarChangeListener {

    private static CompositeSeekBarListener sInstance = null;

    private static List<SeekBar.OnSeekBarChangeListener> registeredListeners = new ArrayList<>();

    // A static block that sets class fields
    static {
        // Creates a single static instance of PhotoManager
        sInstance = new CompositeSeekBarListener();
    }

    private CompositeSeekBarListener() {}

    public static void registerListener(SeekBar.OnSeekBarChangeListener listener) {
        registeredListeners.add(listener);
    }

    public static SeekBar.OnSeekBarChangeListener getInstance() {
        return sInstance;
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